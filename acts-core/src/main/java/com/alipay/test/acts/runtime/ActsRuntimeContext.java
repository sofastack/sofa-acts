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
package com.alipay.test.acts.runtime;

import com.alipay.test.acts.component.db.DBDatasProcessor;
import com.alipay.test.acts.model.*;
import com.alipay.test.acts.template.ActsTestBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The Acts framework runtime context, you can set the data, get the return value
 *
 * @author tantian.wc
 * @version $Id: ActsRuntimeContext.java, v 0.1 2015年10月13日 下午1:42:21 tantian.wc
 *          Exp $
 */
public class ActsRuntimeContext {
    protected final Log                     log                  = LogFactory.getLog(this
                                                                     .getClass());

    /** caseId */
    public String                           caseId;
    /** test data */
    public PrepareData                      prepareData;
    /** runtime context */
    public Map<String, Object>              componentContext;
    /** data processor */
    public DBDatasProcessor                 dbDatasProcessor;
    /** Tested method */
    public Method                           testedMethod;
    /** Tested object */
    public Object                           testedObj;
    /** Return result，after execute, it will be generated */
    public Object                           resultObj;
    /** Expectations of exception results */
    public Object                           exceptionObj;

    /** Component context list, all components */
    public List<ActsTestBase>               actsComponents       = new ArrayList<ActsTestBase>();
    /** parameter list，can be specified by $ */
    public Map<String, Object>              paramMap             = new LinkedHashMap<String, Object>();

    /** store list of command components */
    public List<String>                     prepareCommandList   = new ArrayList<String>();
    public List<String>                     clearCommandList     = new ArrayList<String>();
    public List<String>                     checkCommandList     = new ArrayList<String>();
    public List<String>                     defaultCommandList   = new ArrayList<String>();

    /**store List of preparData components */
    public List<ActsTestBase>               BeforeClearPreList   = new ArrayList<ActsTestBase>();
    public List<ActsTestBase>               AfterClearPreList    = new ArrayList<ActsTestBase>();
    public List<ActsTestBase>               BeforeCheckPreList   = new ArrayList<ActsTestBase>();
    public List<ActsTestBase>               AfterCheckPreList    = new ArrayList<ActsTestBase>();
    public List<ActsTestBase>               BeforePreparePreList = new ArrayList<ActsTestBase>();
    public List<ActsTestBase>               AfterPreparePreList  = new ArrayList<ActsTestBase>();

    public Map<String, Map<String, Object>> componentsResultMap  = new LinkedHashMap<String, Map<String, Object>>();

    /**
     * Constructor.
     */
    public ActsRuntimeContext() {

    }

    /**
     * Constructor.
     *
     * @param caseId the case id
     * @param prepareData the prepare data
     * @param componentContext the component context
     * @param testedMethod the tested method
     * @param testedObj the tested obj
     * @param dbDatasProcessor the db datas processor
     */
    public ActsRuntimeContext(String caseId, PrepareData prepareData,
                              Map<String, Object> componentContext, Method testedMethod,
                              Object testedObj, DBDatasProcessor dbDatasProcessor) {
        super();
        this.caseId = caseId;
        this.prepareData = prepareData;
        this.componentContext = componentContext;
        this.dbDatasProcessor = dbDatasProcessor;
        this.testedMethod = testedMethod;
        this.testedObj = testedObj;

    }

    /**
     * Get the ith parameter
     *
     * @param i
     * @return
     */
    public VirtualObject getArg(int i) {
        if (prepareData == null || prepareData.getArgs() == null
            || prepareData.getArgs().getVirtualObjects() == null) {
            return null;
        }
        return prepareData.getArgs().getVirtualObjects().get(i);
    }

    /**
     * Get the ith parameter
     *
     * @param i
     * @return
     */
    public Object getArgValue(int i) {
        if (prepareData == null || prepareData.getArgs() == null
            || prepareData.getArgs().getVirtualObjects() == null) {
            return null;
        }
        return prepareData.getArgs().getInputArgs().get(i);
    }

    /**
     * Get the specified custom input
     *
     * @param
     * @return
     */
    public Object getUserDefParams(String virParsName) {
        if (prepareData == null || prepareData.getVirtualParams() == null
            || prepareData.getVirtualParams().getParams() == null) {
            return null;
        }

        VirtualObject virRet = (VirtualObject) prepareData.getVirtualParams().getByParaName(
            virParsName);
        if (null == virRet) {
            return null;
        }
        return virRet.getObject();
    }

    /**
     * Set the i-th parameter to obj
     *
     * @param i
     * @param obj
     * @return
     */
    public void setArg(int i, Object obj) {
        if (prepareData == null) {
            return;
        }
        if (prepareData.getArgs() == null || prepareData.getArgs().getVirtualObjects() == null) {
            prepareData.setArgs(VirtualArgs.getInstance());
            if (prepareData.getArgs().getVirtualObjects() == null) {
                prepareData.getArgs().setInputArgs(new ArrayList<Object>());
            }
            prepareData.getArgs().getVirtualObjects().add(new VirtualObject(obj));
        } else {
            prepareData.getArgs().getVirtualObjects().set(i, new VirtualObject(obj));
        }
    }

    /**
     * Set the specified custom input
     *
     * @param
     * @param obj
     * @return
     */
    public void setUserDefParams(String virParsName, Object obj) {
        if (prepareData == null) {
            return;
        }
        if (prepareData.getArgs() == null || prepareData.getVirtualParams().getParams() == null) {
            prepareData.setVirtualParams(VirtualParams.getInstance());
            prepareData.getVirtualParams().addParam(virParsName, obj);
        } else {
            prepareData.getVirtualParams().addParam(virParsName, obj);
        }
    }

    /**
     * get exception VirtualObject
     *
     * @return
     */
    public VirtualObject getException() {
        if (prepareData == null || prepareData.getExpectException() == null
            || prepareData.getExpectException().getExpectException() == null) {
            return null;
        }
        return prepareData.getExpectException().getVirtualObject();
    }

    /**
     * set exception
     *
     * @param e
     */
    public void setException(Throwable e) {
        if (prepareData == null) {
            return;
        }
        if (prepareData.getExpectException() == null
            || prepareData.getExpectException().getExpectException() == null) {
            prepareData.setExpectException(new VirtualException());
        }

        prepareData.getExpectException().getExpectException().setObject(e);
        prepareData.getExpectException().getVirtualObject().getFlagSetter(Throwable.class)
            .set("stackTrace", "N").set("cause", "N");
        prepareData.getExpectException().getVirtualObject().getFlagSetter(e.getClass())
            .set("stackTrace", "N").set("cause", "N");
    }

    /**
     * Get current desired result
     *
     * @return
     */
    public Object getExpectResult() {
        if (prepareData == null || prepareData.getExpectResult() == null) {
            return null;
        }
        return prepareData.getExpectResult().getResultObj();
    }

    /**
     * Set the current desired result
     *
     * @return
     */
    public Boolean setExpectResult(Object objToSet) {
        if (prepareData == null) {
            return false;
        }

        VirtualResult virRt = prepareData.getExpectResult();
        if (virRt == null) {
            virRt = new VirtualResult();
        }

        if (null == virRt.getVirtualObject()) {
            VirtualObject virObj = new VirtualObject();
            virObj.setObject(objToSet);
        } else {
            virRt.getVirtualObject().setObject(objToSet);
        }
        prepareData.setExpectResult(virRt);

        return true;
    }

    /**
     * Update the variables in the table
     */
    public void refreshDataParam() {
        if (prepareData == null) {
            return;
        }
        if (prepareData.getDepDataSet() != null
            && prepareData.getDepDataSet().getVirtualTables() != null) {
            refreshTableParam(prepareData.getDepDataSet().getVirtualTables());
        }
        if (prepareData.getExpectDataSet() != null
            && prepareData.getExpectDataSet().getVirtualTables() != null) {
            refreshTableParam(prepareData.getExpectDataSet().getVirtualTables());
        }

    }

    private void refreshTableParam(List<VirtualTable> tables) {
        for (VirtualTable table : tables) {
            if (table == null || table.getTableData() == null) {
                return;
            }
            for (Map<String, Object> row : table.getTableData()) {
                if (row == null) {
                    continue;
                }
                for (String key : row.keySet()) {
                    if (String.valueOf(row.get(key)).contains("$")) {
                        String paramName = String.valueOf(row.get(key)).replace("$", "");
                        if (paramMap.containsKey(paramName)) {
                            log.info("alter param [" + paramName + "] to "
                                     + paramMap.get(paramName));
                            row.put(key, paramMap.get(paramName));
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets prepare data.
     *
     * @return the prepare data
     */
    public PrepareData getPrepareData() {
        return prepareData;
    }

    /**
     * Sets prepare data.
     *
     * @param prepareData the prepare data
     */
    public void setPrepareData(PrepareData prepareData) {
        this.prepareData = prepareData;
    }

    /**
     * Gets component context.
     *
     * @return the component context
     */
    public Map<String, Object> getComponentContext() {
        return componentContext;
    }

    /**
     * Sets component context.
     *
     * @param componentContext the component context
     */
    public void setComponentContext(Map<String, Object> componentContext) {
        this.componentContext = componentContext;
    }

    /**
     * Gets db datas processor.
     *
     * @return the db datas processor
     */
    public DBDatasProcessor getDbDatasProcessor() {
        return dbDatasProcessor;
    }

    /**
     * Sets db datas processor.
     *
     * @param dbDatasProcessor the db datas processor
     */
    public void setDbDatasProcessor(DBDatasProcessor dbDatasProcessor) {
        this.dbDatasProcessor = dbDatasProcessor;
    }

    /**
     * Gets tested method.
     *
     * @return the tested method
     */
    public Method getTestedMethod() {
        return testedMethod;
    }

    /**
     * Sets tested method.
     *
     * @param testedMethod the tested method
     */
    public void setTestedMethod(Method testedMethod) {
        this.testedMethod = testedMethod;
    }

    /**
     * Gets tested obj.
     *
     * @return the tested obj
     */
    public Object getTestedObj() {
        return testedObj;
    }

    /**
     * Sets tested obj.
     *
     * @param testedObj the tested obj
     */
    public void setTestedObj(Object testedObj) {
        this.testedObj = testedObj;
    }

    /**
     * Gets result obj.
     *
     * @return the result obj
     */
    public Object getResultObj() {
        return resultObj;
    }

    /**
     * Sets result obj.
     *
     * @param resultObj the result obj
     */
    public void setResultObj(Object resultObj) {
        this.resultObj = resultObj;
    }

    /**
     * Gets exception obj.
     *
     * @return the exception obj
     */
    public Object getExceptionObj() {
        return exceptionObj;
    }

    /**
     * Sets exception obj.
     *
     * @param exceptionObj the exception obj
     */
    public void setExceptionObj(Object exceptionObj) {
        this.exceptionObj = exceptionObj;
    }

    /**
     * Gets param map.
     *
     * @return the param map
     */
    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    /**
     * Add one param.
     *
     * @param paraName the para name
     * @param paraObj the para obj
     */
    public void addOneParam(String paraName, Object paraObj) {
        paramMap.put(paraName, paraObj);
    }

    /**
     * Gets param by name.
     *
     * @param paraName the para name
     * @return the param by name
     */
    public Object getParamByName(String paraName) {
        if (null == paraName) {
            return null;
        }

        return paramMap.get(paraName);
    }

    /**
     * Gets param by name with generic.
     *
     * @param <T>  the type parameter
     * @param paraName the para name
     * @return the param by name with generic
     */
    @SuppressWarnings("unchecked")
    public <T> T getParamByNameWithGeneric(String paraName) {
        if (null == paraName) {
            return null;
        }
        return (T) paramMap.get(paraName);
    }

    /**
     * Sets param map.
     *
     * @param paramMap the param map
     */
    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    /**
     * Gets case id.
     *
     * @return the case id
     */
    public String getCaseId() {
        return caseId;
    }

    /**
     * Sets case id.
     *
     * @param caseId the case id
     */
    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    /**
     * Gets acts components.
     *
     * @return the acts components
     */
    public List<ActsTestBase> getActsComponents() {
        return actsComponents;
    }

    /**
     * Sets acts components.
     *
     * @param actsComponents the acts components
     */
    public void setActsComponents(List<ActsTestBase> actsComponents) {
        this.actsComponents = actsComponents;
    }

    /**
     * Getter method for property <tt>componentsResultMap</tt>.
     *
     * @return property value of componentsResultMap
     */
    public Map<String, Map<String, Object>> getComponentsResultMap() {
        return componentsResultMap;
    }

    /**
     * Setter method for property <tt>componentsResultMap</tt>.
     *
     * @param componentsResultMap value to be assigned to property componentsResultMap
     */
    public void setComponentsResultMap(Map<String, Map<String, Object>> componentsResultMap) {
        this.componentsResultMap = componentsResultMap;
    }

    /**
     * Get all the input
     */
    public List<Object> getInputParams() {
        if (null == prepareData || null == prepareData.getArgs()) {
            return null;
        }
        List<Object> retObjList = new ArrayList<Object>();
        for (VirtualObject tempVObj : prepareData.getArgs().getVirtualObjects()) {
            retObjList.add(tempVObj.getObject());
        }
        return retObjList;
    }

    /**
     * Get input by location
     */
    public Object getInputParamByPos(int i) {
        if (null == prepareData.getArgs() || null == prepareData.getArgs().getVirtualObjects()) {
            return null;
        }
        if (i >= prepareData.getArgs().inputArgs.size()) {
            return null;
        }
        return prepareData.getArgs().getVirtualObjects().get(i).getObject();
    }

    /**
     * Add
     */
    public void addInputParam(Object obj) {
        if (null == prepareData.getArgs()) {
            return;
        }
        VirtualObject vObj = new VirtualObject();
        vObj.setObject(obj);
        prepareData.getArgs().addArg(vObj);
    }

    /**
     * Get db prepared data
     */
    public List<VirtualTable> getPreparedDbData() {
        if (null == prepareData.getDepDataSet()
            || null == prepareData.getDepDataSet().getVirtualTables()) {
            return null;
        }
        return prepareData.getDepDataSet().getVirtualTables();
    }
}
