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
package com.alipay.test.acts.context;

import com.alipay.test.acts.driver.enums.SuiteFlag;
import com.alipay.test.acts.yaml.YamlTestData;
import com.alipay.test.acts.yaml.cpUnit.DataBaseCPUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Acts case context
 * 
 * @author baishuo.lp
 * @version $Id: ActsCaseContext.java, v 0.1 2015年8月16日 下午11:04:53 baishuo.lp Exp $
 */
public class ActsCaseContext {

    //caseID
    private String                     caseId;

    //case desc
    private String                     caseDesc;

    private SuiteFlag                  suiteFlag;

    //input args
    private Map<String, Object>        parameterMap;

    private final Map<String, Object>  uniqueMap              = new HashMap<String, Object>();

    private final List<String>         logData                = new ArrayList<String>();

    private final List<DataBaseCPUnit> preCleanContent        = new ArrayList<DataBaseCPUnit>();

    private final List<String>         processErrorLog        = new ArrayList<String>();

    private boolean                    needLoadCommonSection  = false;

    private boolean                    needCompareTableLength = true;

    private String                     yamlPath;

    private YamlTestData               yamlTestData;

    /**
     * Getter method for property <tt>caseId</tt>.
     * 
     * @return property value of caseId
     */
    public String getCaseId() {
        return caseId;
    }

    /**
     * Setter method for property <tt>caseId</tt>.
     * 
     * @param caseId value to be assigned to property caseId
     */
    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    /**
     * Getter method for property <tt>parameterMap</tt>.
     * 
     * @return property value of parameterMap
     */
    public Map<String, Object> getParameterMap() {
        return parameterMap;
    }

    /**
     * Setter method for property <tt>parameterMap</tt>.
     * 
     * @param parameterMap value to be assigned to property parameterMap
     */
    public void setParameterMap(Map<String, Object> parameterMap) {
        this.parameterMap = parameterMap;
    }

    /**
     * Getter method for property <tt>needLoadCommonSection</tt>.
     * 
     * @return property value of needLoadCommonSection
     */
    public boolean isNeedLoadCommonSection() {
        return needLoadCommonSection;
    }

    /**
     * Setter method for property <tt>needLoadCommonSection</tt>.
     * 
     * @param needLoadCommonSection value to be assigned to property needLoadCommonSection
     */
    public void setNeedLoadCommonSection(boolean needLoadCommonSection) {
        this.needLoadCommonSection = needLoadCommonSection;
    }

    /**
     * Getter method for property <tt>logData</tt>.
     * 
     * @return property value of logData
     */
    public List<String> getLogData() {
        return logData;
    }

    /**
     * Getter method for property <tt>preCleanContent</tt>.
     * 
     * @return property value of preCleanContent
     */
    public List<DataBaseCPUnit> getPreCleanContent() {
        return preCleanContent;
    }

    /**
     * Getter method for property <tt>caseDesc</tt>.
     * 
     * @return property value of caseDesc
     */
    public String getCaseDesc() {
        return caseDesc;
    }

    /**
     * Setter method for property <tt>caseDesc</tt>.
     * 
     * @param caseDesc value to be assigned to property caseDesc
     */
    public void setCaseDesc(String caseDesc) {
        this.caseDesc = caseDesc;
    }

    /**
     * Getter method for property <tt>suiteFlag</tt>.
     * 
     * @return property value of suiteFlag
     */
    public SuiteFlag getSuiteFlag() {
        return suiteFlag;
    }

    /**
     * Setter method for property <tt>suiteFlag</tt>.
     * 
     * @param suiteFlag value to be assigned to property suiteFlag
     */
    public void setSuiteFlag(SuiteFlag suiteFlag) {
        this.suiteFlag = suiteFlag;
    }

    /**
     * Getter method for property <tt>uniqueMap</tt>.
     * 
     * @return property value of uniqueMap
     */
    public Map<String, Object> getUniqueMap() {
        return uniqueMap;
    }

    /**
     * Getter method for property <tt>processErrorLog</tt>.
     * 
     * @return property value of processErrorLog
     */
    public List<String> getProcessErrorLog() {
        return processErrorLog;
    }

    /**
     * Getter method for property <tt>needCompareTableLength</tt>.
     * 
     * @return property value of needCompareTableLength
     */
    public boolean isNeedCompareTableLength() {
        return needCompareTableLength;
    }

    /**
     * Setter method for property <tt>needCompareTableLength</tt>.
     * 
     * @param needCompareTableLength value to be assigned to property needCompareTableLength
     */
    public void setNeedCompareTableLength(boolean needCompareTableLength) {
        this.needCompareTableLength = needCompareTableLength;
    }

    /**
     * Getter method for property <tt>yamlTestData</tt>.
     * 
     * @return property value of yamlTestData
     */
    public YamlTestData getYamlTestData() {
        return yamlTestData;
    }

    /**
     * Setter method for property <tt>yamlTestData</tt>.
     * 
     * @param yamlTestData value to be assigned to property yamlTestData
     */
    public void setYamlTestData(YamlTestData yamlTestData) {
        this.yamlTestData = yamlTestData;
    }

    /**
     * Getter method for property <tt>yamlPath</tt>.
     * 
     * @return property value of yamlPath
     */
    public String getYamlPath() {
        return yamlPath;
    }

    /**
     * Setter method for property <tt>yamlPath</tt>.
     * 
     * @param yamlPath value to be assigned to property yamlPath
     */
    public void setYamlPath(String yamlPath) {
        this.yamlPath = yamlPath;
    }

}
