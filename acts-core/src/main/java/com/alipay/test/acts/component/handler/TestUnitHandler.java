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
package com.alipay.test.acts.component.handler;

import com.alipay.test.acts.component.components.ActsComponentUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alipay.test.acts.component.event.EventContextHolder;
import com.alipay.test.acts.exception.ActsTestException;
import com.alipay.test.acts.model.*;
import com.alipay.test.acts.runtime.ActsRuntimeContext;
import com.alipay.test.acts.runtime.ActsRuntimeContextThreadHold;
import com.alipay.test.acts.runtime.ComponentsActsRuntimeContextThreadHold;
import com.alipay.test.acts.utils.CaseResultCollectUtil;
import com.alipay.test.acts.utils.DetailCollectUtils;
import com.alipay.test.acts.utils.ObjectCompareUtil;
import com.alipay.test.acts.utils.ObjectUtil;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Handle each test data
 *
 * @author tantian.wc
 * @version $Id: TestUnitHandler.java, v 0.1 2015年10月21日 下午7:43:31 tantian.wc
 *          Exp $
 */
public class TestUnitHandler {

    protected final Log    log              = LogFactory.getLog(this.getClass());

    /** Store scanned objects to prevent loops */
    protected List<Object> scanList;

    /** Runtime context */
    ActsRuntimeContext     actsRuntimeContext;

    /**Store objects that currently need to be compared with topic and eventcode */
    public Object          storeExpEventObj = null;

    /**
     * Constructor.
     */
    public TestUnitHandler() {
        this.actsRuntimeContext = ActsRuntimeContextThreadHold.getContext();
    }

    /**
     * Constructor.
     *
     * @param actsRuntimeContext the acts runtime context
     */
    public TestUnitHandler(ActsRuntimeContext actsRuntimeContext) {

        this.actsRuntimeContext = actsRuntimeContext;
    }

    /**
     * Prepare DB data
     *
     * @param extMapInfo
     * @param groupIds
     */
    public void prepareDepData(Map<String, String> extMapInfo, String... groupIds) {
        actsRuntimeContext.getDbDatasProcessor().updateDataSource(extMapInfo);
        try {

            if (null != actsRuntimeContext.getPrepareData().getDepDataSet()
                && null != actsRuntimeContext.getPrepareData().getDepDataSet().getVirtualTables()) {

                DetailCollectUtils.appendAndLog("Preparing DB data:", log);
                for (VirtualTable table : actsRuntimeContext.getPrepareData().getDepDataSet()
                    .getVirtualTables()) {
                    if ((ArrayUtils.isEmpty(groupIds) && StringUtils.isEmpty(table.getNodeGroup()))
                        || ArrayUtils.contains(groupIds, table.getNodeGroup())) {
                        log.info(table.getTableName());
                    }
                }
                replaceTableParam(actsRuntimeContext.getPrepareData().getDepDataSet()
                    .getVirtualTables(), groupIds);
                actsRuntimeContext.getDbDatasProcessor().importDepDBDatas(
                    actsRuntimeContext.getPrepareData().getDepDataSet().getVirtualTables(),
                    groupIds);

            } else {
                log.info("None DB preparation");
            }
        } catch (Exception e) {
            throw new ActsTestException(
                "Unknown exception while preparing DB data. DB actual parameters:"
                        + actsRuntimeContext.getPreparedDbData().toString(), e);
        }
    }

    /**
     * Test method execution
     */
    public void execute() {
        try {
            List<Object> inputParams = actsRuntimeContext.getPrepareData().getArgs().getInputArgs();
            replaceAllParam(inputParams, actsRuntimeContext.getParamMap());

            Object[] paramObjs = null;
            if (inputParams != null) {
                paramObjs = inputParams.toArray(new Object[0]);
            }

            if (actsRuntimeContext.getTestedMethod() != null) {
                Object resultObj = null;
                try {
                    DetailCollectUtils.appendAndLog("Start to invoke method:"
                                                    + actsRuntimeContext.getTestedMethod()
                                                        .getName() + " parameters:", log);
                    if (inputParams == null) {
                        log.info("null");
                    } else {
                        DetailCollectUtils.appendDetail(inputParams.toString());
                        for (Object obj : inputParams) {
                            if (!(obj instanceof HttpServletRequest || obj instanceof HttpSession || obj instanceof HttpServletResponse)) {
                                try {
                                    log.info(ObjectUtil.toJson(obj));
                                } catch (Exception e) {
                                    log.info(obj.toString());
                                }
                            }

                        }
                    }
                    resultObj = actsRuntimeContext.getTestedMethod().invoke(
                        actsRuntimeContext.getTestedObj(), paramObjs);

                    //将结果及消息放入组件Map
                    putResultToMap();
                    try {
                        DetailCollectUtils.appendAndLog(
                            "Invocation result: " + ObjectUtil.toJson(resultObj), log);
                    } catch (Exception e) {
                        DetailCollectUtils.appendAndLog("Invocation result: " + resultObj, log);
                    }

                } catch (Exception e) {
                    try {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        log.info("\r\n" + sw.toString() + "\r\n");
                    } catch (Exception e2) {
                        log.info("bad getErrorInfoFromException");
                    }
                    DetailCollectUtils.appendAndLog("Exception Invocation" + e.getCause(), log);
                    actsRuntimeContext.setExceptionObj(e.getCause());
                }
                actsRuntimeContext.setResultObj(resultObj);

            } else {
                log.info("Test method not found, interrupt invocation");
            }
        } catch (Exception e) {
            throw new ActsTestException("unknown exception while invocation", e);
        }

    }

    /**
     * Abnormal result check
     */
    public void checkException() {

        // Abnormal contrast
        if (actsRuntimeContext.getPrepareData().getExpectException() != null
            && actsRuntimeContext.getPrepareData().getExpectException().getExpectException() != null
            && actsRuntimeContext.getPrepareData().getExpectException().getExpectException()
                .getObject() != null) {
            if (actsRuntimeContext.getExceptionObj() != null) {
                log.info("Checking Exception");
                ObjectCompareUtil.varParaMap = actsRuntimeContext.paramMap;
                Object expectedExp = actsRuntimeContext.getExceptionObj();
                VirtualException ve = actsRuntimeContext.getPrepareData().getExpectException();
                Object actualExp = ve.getExpectExceptionObject();
                ObjectCompareUtil.compare(expectedExp, actualExp, ve.getVirtualObject().flags,
                    actsRuntimeContext.paramMap);

            } else {
                throw new ActsTestException("None Exception raised during invocation");

            }
        } else {
            if (actsRuntimeContext.getExceptionObj() != null) {
                throw new ActsTestException("unknown exception raised during invocation",
                    (Throwable) actsRuntimeContext.getExceptionObj());
            }
            log.info("None exception to check");
        }

    }

    /**
     * DB result check
     *
     * @param extMapInfo
     * @param groupIds
     */
    public void checkExpectDbData(HashMap<String, String> extMapInfo, String... groupIds) {
        try {

            ComponentsActsRuntimeContextThreadHold.setContext(this.actsRuntimeContext);
            actsRuntimeContext.getDbDatasProcessor().updateDataSource(extMapInfo);
            if (null != actsRuntimeContext.getPrepareData().getExpectDataSet()
                && actsRuntimeContext.getPrepareData().getExpectDataSet().getVirtualTables() != null) {
                log.info("Checking DB, tables checked");
                for (VirtualTable virtualTable : actsRuntimeContext.getPrepareData()
                    .getExpectDataSet().getVirtualTables()) {
                    if ((ArrayUtils.isEmpty(groupIds) && StringUtils.isEmpty(virtualTable
                        .getNodeGroup()))
                        || ArrayUtils.contains(groupIds, virtualTable.getNodeGroup())) {
                        log.info(virtualTable.getTableName());
                    }
                }
                replaceTableParam(actsRuntimeContext.getPrepareData().getExpectDataSet()
                    .getVirtualTables());
                actsRuntimeContext.getDbDatasProcessor().compare2DBDatas(
                    actsRuntimeContext.getPrepareData().getExpectDataSet().getVirtualTables(),
                    groupIds);
            } else {
                log.info("None DB expectation");
            }
        } catch (Exception e) {
            throw new ActsTestException("unknown exception while checking DB", e);
        }
    }

    /**
     * Resultcheck
     */
    public void checkExpectResult() {
        try {
            if (actsRuntimeContext.getPrepareData().getExpectResult() != null
                && actsRuntimeContext.getPrepareData().getExpectResult().getResult().getObject() != null) {
                DetailCollectUtils.appendAndLog("Checking invocation result:", log);
                VirtualObject expect = actsRuntimeContext.getPrepareData().getExpectResult()
                    .getVirtualObject();
                Object actual = actsRuntimeContext.getResultObj();
                try {

                    log.info("\nexpect:" + ObjectUtil.toJson(expect.getObject()) + "\nactual:"
                             + ObjectUtil.toJson(actual));
                } catch (Exception e) {
                    log.error("\nexpect:" + expect + "\nactual:" + actual);

                }
                ObjectCompareUtil.compare(actual, expect.getObject(), expect.getFlags(),
                    actsRuntimeContext.paramMap);
            } else {
                log.info("None result expectation");
            }
        } catch (Exception e) {
            throw new ActsTestException("unknown exception while checking invocation result", e);
        }
    }

    /**
     * Message check
     *
     * @param groupIds
     */
    public void checkExpectEvent(String... groupIds) {
        try {
            log.info("Checking Events");
            if (!actsRuntimeContext.getPrepareData().getExpectEventSet().getVirtualEventObjects()
                .isEmpty()) {

                Map<String, List<Object>> uEventList = EventContextHolder.getBizEvent();
                for (VirtualEventObject virtualEventObject : actsRuntimeContext.getPrepareData()
                    .getExpectEventSet().getVirtualEventObjects()) {
                    if ((ArrayUtils.isEmpty(groupIds) && StringUtils.isEmpty(virtualEventObject
                        .getNodeGroup()))
                        || ArrayUtils.contains(groupIds, virtualEventObject.getNodeGroup())) {
                        Map<String, List<Object>> events = EventContextHolder.getBizEvent();
                        DetailCollectUtils.appendAndLog("Actually intercepted message list is:"
                                                        + ObjectUtil.toJson(events), log);
                        String expEventCode = virtualEventObject.getEventCode();
                        String expTopic = virtualEventObject.getTopicId();
                        Object expPayLoad = virtualEventObject.getEventObject().getObject();
                        String flag = virtualEventObject.getIsExist();
                        String key = expEventCode + "|" + ((expTopic == null) ? "" : expTopic);

                        replaceAllParam(expPayLoad, actsRuntimeContext.getParamMap());

                        if (StringUtils.equals(flag, "N")) {
                            if (StringUtils.isBlank(expEventCode)) {
                                Assert.assertTrue(events == null || events.isEmpty(),
                                    "Event detected, but none expectation");
                            } else if (events.get(key) != null) {
                                Assert.assertTrue(false,
                                    "Unexpected event found, with " + "topicId: "
                                            + virtualEventObject.getTopicId() + " eventCode"
                                            + virtualEventObject.getEventCode() + " ");
                            }
                        } else if (StringUtils.equals(flag, "Y")) {
                            List<Object> payLoads = events.get(key);
                            boolean found = false;
                            if (payLoads == null || payLoads.isEmpty()) {
                                Assert.assertTrue(
                                    false,
                                    "Specified event not found, with topic:"
                                            + virtualEventObject.getTopicId() + " eventCode:"
                                            + virtualEventObject.getEventCode() + "");
                            } else {
                                for (Object obj : payLoads) {
                                    if (ObjectCompareUtil.matchObj(obj, expPayLoad,
                                        virtualEventObject.getEventObject().getFlags(),
                                        actsRuntimeContext.getParamMap())) {
                                        found = true;
                                        break;
                                    }
                                }
                                storeExpEventObj = expPayLoad;
                            }
                            if (!found) {
                                Assert.assertTrue(
                                    false,
                                    "cannot find event matching the expected payload" + "topicId: "
                                            + virtualEventObject.getTopicId() + " eventCode"
                                            + virtualEventObject.getEventCode()
                                            + "\nThe actual message list is:"
                                            + ObjectUtil.toJson(payLoads)
                                            + "\nThe expected message is:"
                                            + ObjectUtil.toJson(storeExpEventObj) + "\n"
                                            + "The error message is："
                                            + ObjectCompareUtil.getReportStr().toString() + "\n");
                                DetailCollectUtils.appendAndLogColoredError(
                                    "The message check fails, and the failure message is:"
                                            + "topicId: " + virtualEventObject.getTopicId()
                                            + " eventCode" + virtualEventObject.getEventCode()
                                            + "\nThe actual message list is:"
                                            + ObjectUtil.toJson(payLoads)
                                            + "\nThe expected message is:"
                                            + ObjectUtil.toJson(storeExpEventObj) + "\n"
                                            + "The error message is："
                                            + ObjectCompareUtil.getReportStr().toString() + "\n",
                                    log);
                            }
                        }

                    }

                }
            } else {
                log.info("Skip event check in rpc mode");
            }

        } catch (Exception e) {
            throw new ActsTestException("unknow exception raised while cheking events", e);
        }

    }

    /**
     * clean up data already prepared
     *
     * @param extMapInfo
     * @param groupIds
     */
    public void clearDepData(Map<String, String> extMapInfo, String... groupIds) {
        actsRuntimeContext.getDbDatasProcessor().updateDataSource(extMapInfo);
        try {
            if (null != actsRuntimeContext.getPrepareData().getDepDataSet()) {
                DetailCollectUtils
                    .appendAndLog(
                        "====================Cleaning up DB data preparations=============================",
                        log);
                replaceTableParam(actsRuntimeContext.getPrepareData().getDepDataSet()
                    .getVirtualTables(), groupIds);
                actsRuntimeContext.getDbDatasProcessor().cleanDBDatas(
                    actsRuntimeContext.getPrepareData().getDepDataSet().getVirtualTables(),
                    groupIds);

            } else {
                log.info("None DB preparation to clean");
            }
        } catch (Exception e) {
            throw new ActsTestException("Unknown exception raised while cleaning DB preparations",
                e);
        }
    }

    /**
     * Clean up expected data
     *
     * @param extMapInfo
     * @param groupIds
     */
    public void clearExpectDBData(Map<String, String> extMapInfo, String... groupIds) {
        actsRuntimeContext.getDbDatasProcessor().updateDataSource(extMapInfo);
        try {
            if (null != actsRuntimeContext.getPrepareData().getExpectDataSet()) {
                log.info("Cleaning up DB expectation data");
                replaceTableParam(actsRuntimeContext.getPrepareData().getExpectDataSet()
                    .getVirtualTables(), groupIds);
                actsRuntimeContext.getDbDatasProcessor().cleanDBDatas(
                    actsRuntimeContext.getPrepareData().getExpectDataSet().getVirtualTables(),
                    groupIds);

            } else {
                log.info("None DB expectation to clean up");
            }
        } catch (Exception e) {
            throw new ActsTestException("unknown exception raised while cleaning DB expectations",
                e);
        }
    }

    /**
     * Replace by fields.
     *
     * @param obj the obj
     * @param varParaMap the var para map
     */
    public void replaceByFields(Object obj, Map<String, Object> varParaMap) {
        try {
            if (hasReplaced(obj)) {
                return;
            }
            Class<?> objType = obj.getClass();
            if (ObjectCompareUtil.isComparable(objType)) {
                if (obj instanceof String) {
                    if (((String) obj).startsWith("$")) {
                        String key = ((String) obj).replace("$", "");
                        if (varParaMap != null && varParaMap.get(key) != null) {
                            if (!(varParaMap.get(key) instanceof String)) {
                                return;
                            }
                            Field[] fieldsOfString = String.class.getDeclaredFields();
                            for (Field field : fieldsOfString) {
                                field.setAccessible(true);
                                field.set(obj, field.get(varParaMap.get(key)));

                            }
                        }
                    } else if (((String) obj).startsWith("@")) {
                        //parse variables in components
                        String str = (String) obj;
                        String callString = str;
                        if (StringUtils.contains(str, "$")) {
                            String query = StringUtils.substringAfter(str, "?");
                            callString = StringUtils.substringBefore(str, "?") + "?";
                            if (StringUtils.isNotBlank(query)) {
                                String[] pairs = StringUtils.split(query, "&");
                                for (String pair : pairs) {
                                    if (StringUtils.isBlank(pair)) {
                                        continue;
                                    }
                                    Object value = StringUtils.substringAfter(pair, "=");
                                    replaceByFields(value, varParaMap);
                                    callString = callString
                                                 + StringUtils.substringBefore(pair, "=") + "="
                                                 + value + "&";

                                }
                                callString = StringUtils.substring(callString, 0,
                                    callString.length() - 1);

                            }

                        }
                        //execute Parameterization
                        String rs = (String) ActsComponentUtil.run(callString);
                        log.info("parameterization invoke:" + callString + " result:" + rs);
                        Field[] fieldsOfString = String.class.getDeclaredFields();
                        for (Field field : fieldsOfString) {
                            field.setAccessible(true);
                            field.set(obj, field.get(rs));
                        }
                    }
                }
                return;
            } else if (objType.isArray()) {

                Object[] objArray = (Object[]) obj;
                if (objArray.length == 0) {
                    return;
                }
                for (int i = 0; i < objArray.length; i++) {
                    replaceByFields(objArray[i], varParaMap);
                }
                return;
            } else if (obj instanceof Map) {
                Map<Object, Object> objMap = (Map) obj;
                if (objMap.size() == 0) {
                    return;
                }
                for (Entry<Object, Object> entry : objMap.entrySet()) {
                    Object targetVal = entry.getValue();
                    replaceByFields(targetVal, varParaMap);
                }
            } else if (obj instanceof List) {
                List objList = (List) obj;

                if (objList.size() == 0) {
                    return;
                }
                for (int j = 0; j < objList.size(); j++) {

                    replaceByFields(objList.get(j), varParaMap);

                }
            } else {

                List<Field> fields = new ArrayList<Field>();

                for (Class<?> c = objType; c != null; c = c.getSuperclass()) {
                    for (Field field : c.getDeclaredFields()) {
                        int modifiers = field.getModifiers();
                        if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)
                            && !fields.contains(field)) {
                            fields.add(field);
                        }
                    }
                }
                for (Field field : fields) {
                    try {
                        field.setAccessible(true);
                        Object objTarget = field.get(obj);
                        replaceByFields(objTarget, varParaMap);

                    } catch (IllegalArgumentException e) {
                        return;
                    } catch (IllegalAccessException e) {
                        return;
                    } catch (Exception e) {
                        return;
                    }
                }
            }
        } catch (Exception e) {
            return;
        }
    }

    /**
     * Replace table param.
     *
     * @param virtualTables the virtual tables
     * @param groupIds the group ids
     */
    public void replaceTableParam(List<VirtualTable> virtualTables, String... groupIds) {
        for (VirtualTable virtualTable : virtualTables) {
            if ((ArrayUtils.isEmpty(groupIds) && StringUtils.isEmpty(virtualTable.getNodeGroup()))
                || ArrayUtils.contains(groupIds, virtualTable.getNodeGroup())) {
                for (Map<String, Object> row : virtualTable.getTableData()) {
                    for (String key : row.keySet()) {
                        if (String.valueOf(row.get(key)).startsWith("$")) {

                            if (actsRuntimeContext.getParamMap().containsKey(
                                String.valueOf(row.get(key)).replace("$", ""))) {
                                row.put(
                                    key,
                                    actsRuntimeContext.getParamMap().get(
                                        String.valueOf(row.get(key)).replace("$", "")));
                            }
                        } else if (String.valueOf(row.get(key)).startsWith("@")) {
                            //parse variables in components
                            String str = String.valueOf(row.get(key));
                            String callString = str;
                            if (StringUtils.contains(str, "$")) {
                                String query = StringUtils.substringAfter(str, "?");
                                callString = StringUtils.substringBefore(str, "?") + "?";
                                if (StringUtils.isNotBlank(query)) {
                                    String[] pairs = StringUtils.split(query, "&");
                                    for (String pair : pairs) {
                                        if (StringUtils.isBlank(pair)) {
                                            continue;
                                        }
                                        Object value = StringUtils.substringAfter(pair, "=");
                                        replaceByFields(value, actsRuntimeContext.getParamMap());
                                        callString = callString
                                                     + StringUtils.substringBefore(pair, "=") + "="
                                                     + value + "&";

                                    }
                                    callString = StringUtils.substring(callString, 0,
                                        callString.length() - 1);

                                }

                            }
                            //执行组件化参数
                            String rs = (String) ActsComponentUtil.run(callString);
                            log.info("parameterization invoke:" + callString + " result:" + rs);
                            row.put(key, rs);
                        }
                    }
                }
            }
        }
    }

    /**
     * whether object has been replaced by a variable
     *
     * @param target
     * @return
     */
    public boolean hasReplaced(Object target) {
        for (Object object : scanList) {
            // if the reference is the same
            if (target == object) {
                return true;
            }
        }
        scanList.add(target);
        return false;

    }

    /**
     * Replace all param.
     *
     * @param obj the obj
     * @param varParaMap the var para map
     */
    public void replaceAllParam(Object obj, Map<String, Object> varParaMap) {

        List<Object> newObj = new ArrayList<Object>();
        if (obj instanceof List) {
            for (Object o : (List<?>) obj) {
                if (o instanceof HttpServletRequest || o instanceof HttpSession
                    || o instanceof HttpServletResponse)
                    continue;
                else {
                    newObj.add(o);
                }
            }
        } else {
            newObj.add(obj);
        }
        scanList = new ArrayList<Object>();
        replaceByFields(newObj, varParaMap);
    }

    /**
     * Gets acts runtime context.
     *
     * @return the acts runtime context
     */
    public ActsRuntimeContext getActsRuntimeContext() {
        return actsRuntimeContext;

    }

    /**
     * Sets acts runtime context.
     *
     * @param actsRuntimeContext the acts runtime context
     */
    public void setActsRuntimeContext(ActsRuntimeContext actsRuntimeContext) {
        this.actsRuntimeContext = actsRuntimeContext;
    }

    /**
     * Replace custom input
     *
     */
    public void prepareUserPara() {
        if (actsRuntimeContext.getPrepareData().getVirtualParams() != null) {
            Map<String, VirtualObject> userParams = actsRuntimeContext.getPrepareData()
                .getVirtualParams().getParams();
            replaceAllParam(userParams, actsRuntimeContext.getParamMap());
            for (Entry<String, VirtualObject> entry : userParams.entrySet()) {
                actsRuntimeContext.paramMap.put(entry.getKey(), entry.getValue().getObject());

            }

            actsRuntimeContext.getPrepareData().getVirtualParams().setParams(userParams);
        }
    }

    /**
     * clean up component list
     */
    public void clearComponentsList() {
        this.actsRuntimeContext.actsComponents.clear();
        this.actsRuntimeContext.AfterCheckPreList.clear();
        this.actsRuntimeContext.AfterClearPreList.clear();
        this.actsRuntimeContext.AfterPreparePreList.clear();
        this.actsRuntimeContext.BeforeCheckPreList.clear();
        this.actsRuntimeContext.BeforeClearPreList.clear();
        this.actsRuntimeContext.BeforePreparePreList.clear();
    }

    /**
     * Put the result object in the Map
     *
     * @param
     */
    private void putResultToMap() {

        VirtualResult res = new VirtualResult(this.actsRuntimeContext.getResultObj());
        VirtualEventSet event = CaseResultCollectUtil.buildExpEvents(
            EventContextHolder.getBizEvent(), this.getClass().getClassLoader());
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("virtualResult", res);
        resultMap.put("virtualEventSet", event);
        this.actsRuntimeContext.getComponentsResultMap().put(
            this.getActsRuntimeContext().getCaseId(), resultMap);

    }

}
