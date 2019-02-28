/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.test.acts.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alipay.test.acts.util.BaseDataUtil;
import com.alipay.test.acts.util.DeepCopyUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alipay.test.acts.collector.sqllog.SqlLogCollector;
import com.alipay.test.acts.model.PrepareData;
import com.alipay.test.acts.model.VirtualDataSet;
import com.alipay.test.acts.model.VirtualEventSet;
import com.alipay.test.acts.model.VirtualObject;
import com.alipay.test.acts.model.VirtualResult;
import com.alipay.test.acts.model.VirtualTable;
import com.alipay.test.acts.runtime.ActsRuntimeContext;

import com.alipay.test.acts.utils.config.ConfigrationFactory;

/**
 * Case Collection Tool
 * 
 * @author chao.gao
 * @author hongling.xiang
 * @version $Id: CaseResultCollectUtil.java, v 0.1 2015年10月27日 下午3:58:22
 *          hongling.xiang Exp $
 */
public class CaseResultCollectUtil {

    /** log */
    private static final Log                      log               = LogFactory
                                                                        .getLog(CaseResultCollectUtil.class);

    /** sql log file */
    private static final String                   SQL_LOG_PATH_NAME = "./logs/acts-sql.log";

    /** case ID mapping with case data */
    private static final Map<String, PrepareData> caseDatas         = new HashMap<String, PrepareData>();

    /**
     * Save the original request<code>PrepareData</code>,
     * to avoid the user rewriting <code>beforeTest</code>to set another value for <code>PrepareData</code>
     * 
     * @param caseId
     * @param prepareData
     */
    public static void holdOriginalRequest(String caseId, PrepareData prepareData) {

        if (null == prepareData || !isCollectCaseResultOpen()) {
            return;
        }
        caseDatas.put(caseId, DeepCopyUtils.deepCopy(prepareData));
    }

    /**
     * 
     * @param actsRuntimeContext
     * @param events
     * @param file
     * @param clsLoader
     */
    public static void holdProcessData(ActsRuntimeContext actsRuntimeContext,
                                       Map<String, List<Object>> events, File file,
                                       ClassLoader clsLoader) {

        String caseId = actsRuntimeContext.getCaseId();
        Object actualResultObj = actsRuntimeContext.getResultObj();

        if (!isCollectCaseResultOpen()) {
            return;
        }

        // Then load it from yaml file to avoid changing the parameter object after running
        Map<String, PrepareData> AllPrepareDataWithArgs = new HashMap<String, PrepareData>();
        if (file.exists() && !file.isDirectory()) {
            AllPrepareDataWithArgs = BaseDataUtil.loadFromYaml(file, clsLoader);
        } else if (file.exists() && file.isDirectory()) {
            AllPrepareDataWithArgs = BaseDataUtil.loadFromYamlByCase(file, clsLoader, null);
        }

        PrepareData newPrepareData = AllPrepareDataWithArgs.get(caseId);

        if (null == newPrepareData) {
            return;
        }

        try {
            if (null != actualResultObj) {
                VirtualResult expectResult = buildVirtualObject(actualResultObj, clsLoader);
                newPrepareData.setExpectResult(expectResult);
            }

            if (!CollectionUtils.isEmpty(events)) {
                newPrepareData.setExpectEventSet(buildExpEvents(events, clsLoader));
            }

            File logfile = new File(SQL_LOG_PATH_NAME);
            if (logfile.exists()) {
                collectSqlLog(caseId, newPrepareData);
            }

            caseDatas.put(caseId, newPrepareData);

        } catch (Throwable t) {
            log.warn("Collecting case result-unknown exception parsing SQL, caseId=" + caseId, t);
        }

    }

    /**
     * Collect SQL logs to generate table data
     * 
     * 
     * 
     */
    public static void collectSqlLog(String caseId, PrepareData rootPrepareData) {
        PrepareData prepareData = rootPrepareData;

        Map<String, List<List<String>>> passedCaseSqlLog = SqlLogCollector.collectConcernedSqlLog(
            SQL_LOG_PATH_NAME, caseId);

        //format: tested method Id||component Id
        Map<String, List<List<String>>> singlePassedCaseSqlLog = new HashMap<String, List<List<String>>>();
        for (String key : passedCaseSqlLog.keySet()) {

            //Filter the data corresponding to the target caseId to avoid parsing dirty data
            if (StringUtils.contains(key, caseId)) {
                singlePassedCaseSqlLog.put(key, passedCaseSqlLog.get(key));
            }
        }

        for (String key : singlePassedCaseSqlLog.keySet()) {

            List<List<String>> curCaseSqlLog = singlePassedCaseSqlLog.get(key);
            if (CollectionUtils.isEmpty(curCaseSqlLog)) {
                break;
            }

            // Analyze sql log of the case to get table data
            List<VirtualTable> caseVirtualTables = SqlLogCollector.parseSqlLog(curCaseSqlLog);

            caseVirtualTables = mergeAllSameTables(caseVirtualTables);

            if (CollectionUtils.isEmpty(caseVirtualTables)) {
                break;
            }

            VirtualDataSet expectDataSet = new VirtualDataSet();
            expectDataSet.addTables(caseVirtualTables);

            //put collected data
            String[] keyStrs = key.split("\\|");
            for (int i = 0; i < keyStrs.length; i++) {
                if (i == (keyStrs.length - 1)) {
                    prepareData.setExpectDataSet(expectDataSet);
                } else {
                    prepareData = getComponentPreparedata(prepareData, keyStrs[i]);
                }
            }
        }

    }

    /**
     * this method is deprecated, and compatibility reserved for fixing problems
     *
     * @param key
     * @return
     */
    public static PrepareData getComponentPreparedata(PrepareData prepareData, String key) {
        //do nothing
        return prepareData;
    }

    /***
     * Merge the same table
     * 
     * @param caseVirtualTables
     * @return
     */
    public static List<VirtualTable> mergeAllSameTables(List<VirtualTable> caseVirtualTables) {
        List<VirtualTable> result = new ArrayList<VirtualTable>();

        Set<String> distinctTableNames = getDistinctTableNames(caseVirtualTables);

        for (String tableName : distinctTableNames) {
            List<VirtualTable> sameTables = filterByTableName(caseVirtualTables, tableName);
            VirtualTable vt = combineSameTables(sameTables);
            result.add(vt);
        }
        return result;
    }

    /***
     * Merge into a single table
     * 
     * @param caseVirtualTables
     * @return
     */
    private static VirtualTable combineSameTables(List<VirtualTable> caseVirtualTables) {
        VirtualTable result = new VirtualTable();
        if (caseVirtualTables != null && caseVirtualTables.size() > 0) {
            result = DeepCopyUtils.deepCopy(caseVirtualTables.get(0));
        }
        result.getTableData().clear();
        for (VirtualTable vt : caseVirtualTables) {
            result.getTableData().addAll(vt.getTableData());
        }

        return result;
    }

    /***
     * Get a unique set of table names
     * 
     * @param caseVirtualTables
     * @return
     */
    private static Set<String> getDistinctTableNames(List<VirtualTable> caseVirtualTables) {
        Set<String> distinctTableNames = new HashSet<String>();
        for (VirtualTable vt : caseVirtualTables) {
            distinctTableNames.add(vt.getTableName());
        }
        return distinctTableNames;

    }

    /***
     * Get the table set according to the table name
     * 
     * @param caseVirtualTables
     * @param tableName
     * @return
     */
    private static List<VirtualTable> filterByTableName(List<VirtualTable> caseVirtualTables,
                                                        String tableName) {
        List<VirtualTable> result = new ArrayList<VirtualTable>();
        for (VirtualTable vt : caseVirtualTables) {
            if (StringUtils.equalsIgnoreCase(vt.getTableName(), tableName)) {
                result.add(vt);
            }
        }

        return DeepCopyUtils.deepCopy(result);

    }

    /**
     * Building expected result Objects
     * @param actualResultObj
     * @param clsLoader
     * @return
     */
    private static VirtualResult buildVirtualObject(Object actualResultObj, ClassLoader clsLoader) {

        VirtualObject virtualObject = new VirtualObject(actualResultObj, actualResultObj.getClass()
            .getSimpleName());
        virtualObject.setDescription(actualResultObj.getClass().getSimpleName());

        VirtualResult virtualResult = new VirtualResult();
        virtualResult.setResult(virtualObject);

        return virtualResult;
    }

    /**
     * Construct expected events
     * 
     * @param events
     * @return
     */
    public static VirtualEventSet buildExpEvents(Map<String, List<Object>> events,
                                                 ClassLoader clsLoader) {

        VirtualEventSet virtualEventSet = new VirtualEventSet();
        for (String key : events.keySet()) {
            String[] keys = key.split("\\|");
            List<Object> payloads = events.get(key);
            for (Object payload : payloads) {
                virtualEventSet.addEventObject(payload, keys[0], (keys.length == 2) ? keys[1] : "");

            }
        }

        return virtualEventSet;
    }

    /**
     * Get all use case data
     * 
     * @return
     */
    public static Map<String, PrepareData> getAllCaseDatas() {
        return caseDatas;
    }

    /**
     * Whether the component result switch is on
     * 
     * @return
     */
    public static boolean isCollectCaseResultOpen() {

        String isCollectCaseResult = ConfigrationFactory.getConfigration().getPropertyValue(
            "collect_case_result");

        if (StringUtils.isBlank(isCollectCaseResult)) {
            return false;
        }

        return StringUtils.equalsIgnoreCase(isCollectCaseResult.trim(), Boolean.TRUE.toString());
    }

    /**
     * Whether the component result switch is on
     * 
     * @return
     */
    public static boolean isCollectComponentResultOpen() {

        String isCollectComponentResult = ConfigrationFactory.getConfigration().getPropertyValue(
            "collect_case_component_result");

        if (StringUtils.isBlank(isCollectComponentResult)) {
            return true;
        }

        return StringUtils.equalsIgnoreCase(isCollectComponentResult.trim(),
            Boolean.TRUE.toString());
    }

}
