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
package com.alipay.test.acts.collector.sqllog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alipay.test.acts.constant.ActsPathConstants;
import com.alipay.test.acts.model.VirtualTable;
import com.alipay.test.acts.util.BaseDataUtil;
import com.alipay.test.acts.util.FileUtil;

/**
 * sql log analysis factory
 * 
 * @author hongling.xiang
 * @version $Id: SqlLogParseFactory.java, v 0.1 2015年10月26日 上午11:14:19
 *          hongling.xiang Exp $
 */
public class SqlLogParseFactory {

    /* log* */
    private static final Log                       log       = LogFactory
                                                                 .getLog(SqlLogParseFactory.class);

    /** SQL type and corresponding parser */
    private static final Map<String, SqlLogParser> sqlParser = new HashMap<String, SqlLogParser>();

    static {
        sqlParser.put(SqlTypeEnum.INSERT_SQL.getCode(), new InsertSqlLogParser());
        sqlParser.put(SqlTypeEnum.UPDATE_SQL.getCode(), new UpdateSqlLogParser());
    }

    /**
     * parse SQL to generate VirtualTable object
     * 
     * @param sqlType
     * @param sqlExecLog Preprocessed data
     * @return
     */
    public static VirtualTable genVirtualTable(String sqlType, List<String> sqlExecLog) {

        SqlLogParser sqlLogParser = sqlParser.get(sqlType);

        // obtain tableName
        String tableName = sqlLogParser.parseTableName(sqlExecLog.get(0));
        if (StringUtils.isBlank(tableName) || !isAppTable(tableName)) {
            return null;
        }

        List<Map<String, Object>> tableDatas = sqlLogParser.parseGenTableDatas(sqlExecLog.get(0),
            parseSqlParamValue(sqlExecLog.get(1), sqlExecLog.get(2)),
            Arrays.asList(sqlExecLog.get(2).split(", ")));
        for (Map<String, Object> tableData : tableDatas) {
            for (String key : tableData.keySet()) {
                tableData.put(key, String.valueOf(tableData.get(key)));
            }
        }
        if (CollectionUtils.isEmpty(tableDatas)) {
            return null;
        }

        Map<String, String> fieldsFlag = fetchFieldFlagsFromDbModel(tableName);
        if (CollectionUtils.isEmpty(fieldsFlag)) {
            //If user does not define this table template, parse sql and get
            fieldsFlag = sqlLogParser
                .parseTableFlags(sqlExecLog.get(0), tableDatas.get(0).keySet());
        }

        VirtualTable table = new VirtualTable();
        table.setTableName(tableName);
        table.setFlags(fieldsFlag);
        table.setTableData(tableDatas);
        table.setTableBaseDesc(tableName);

        return table;
    }

    /**
     * Get table field tags from DB table templates
     * 
     * @param tableName
     * @return
     */
    private static Map<String, String> fetchFieldFlagsFromDbModel(String tableName) {

        File folder = FileUtil.getTestResourceFile(ActsPathConstants.DB_DATA_PATH);
        if (!folder.exists()) {
            return null;
        }

        //Compatible processing
        String dbModelFullPath = folder.getAbsolutePath();
        if (StringUtils.contains(dbModelFullPath, "\\")) {
            dbModelFullPath = StringUtils.replace(dbModelFullPath, "\\", "/");
        }
        String dbModelRootPath = dbModelFullPath.substring(0,
            dbModelFullPath.indexOf("model/dbModel"));
        VirtualTable virtualTable = null;
        try {
            virtualTable = BaseDataUtil.getVirtualTableFromBase(tableName, tableName,
                dbModelRootPath, System.getProperty("file.encoding"));
        } catch (Throwable t) {
            log.warn("query DB model exception!");
        }

        if (null == virtualTable) {
            return null;
        }

        return virtualTable.getFlags();
    }

    /**
     * parse
     * 
     * @param sqlParamValueStr
     * @param sqlParamTypeStr
     * @return
     */
    public static List<String> parseSqlParamValue(String sqlParamValueStr, String sqlParamTypeStr) {

        String[] paramTypes = sqlParamTypeStr.split(", ");

        String[] paramValues = sqlParamValueStr.split(", ");

        if (paramValues.length == paramTypes.length) {
            return Arrays.asList(paramValues);
        }

        if ((paramValues.length == (paramTypes.length - 1)) && sqlParamValueStr.endsWith(" ")) {
            sqlParamValueStr = sqlParamValueStr + " ";

        }

        // FIXME try to resolve map problem.
        String[] newParamValues = sqlParamValueStr.split(", ");
        List<String> newArray = new ArrayList<String>();

        for (int i = 0; i < newParamValues.length; i++) {
            String currentStr = newParamValues[i];
            boolean needConnect = false;
            if (StringUtils.contains(newParamValues[i], "{")) {
                needConnect = true;
                i++;
            }
            while (!StringUtils.contains(newParamValues[i], "}") && needConnect) {
                currentStr += (newParamValues[i] + ", ");
                i++;
            }
            if (StringUtils.contains(newParamValues[i], "}")) {
                currentStr += (", " + newParamValues[i]);
            }
            newArray.add(currentStr);
        }
        log.debug("parse result:" + newArray);
        return newArray;
    }

    private static boolean isAppTable(String tableName) {

        boolean isApptable = !StringUtils.contains(tableName.toLowerCase(), "seq_")
                             && !StringUtils.contains(tableName.toLowerCase(), "sequence_")
                             && !StringUtils.contains(tableName.toLowerCase(), "_sequence")
                             && !StringUtils.contains(tableName.toLowerCase(), "_seq");

        if (isApptable) {
            return true;
        } else {
            log.warn(tableName + "is not Apptable !!!");
            return false;
        }

    }

}
