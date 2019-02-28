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

import com.alipay.test.acts.yaml.YamlTestData;

import java.util.List;

/**
 * 
 * @author baishuo.lp
 * @version $Id: ActsSuiteContext.java, v 0.1 2015年8月16日 下午11:04:53 baishuo.lp Exp $
 */
public class ActsSuiteContext {

    private String       className;

    private String       methodName;

    private String       csvFolderPath;

    private String       csvFilePath;

    private List<String> parameterKeyList;

    private String       yamlPath;

    private YamlTestData yamlTestData;

    /**
     * Getter method for property <tt>className</tt>.
     * 
     * @return property value of className
     */
    public String getClassName() {
        return className;
    }

    /**
     * Setter method for property <tt>className</tt>.
     * 
     * @param className value to be assigned to property className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Getter method for property <tt>methodName</tt>.
     * 
     * @return property value of methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Setter method for property <tt>methodName</tt>.
     * 
     * @param methodName value to be assigned to property methodName
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Getter method for property <tt>csvFolderPath</tt>.
     * 
     * @return property value of csvFolderPath
     */
    public String getCsvFolderPath() {
        return csvFolderPath;
    }

    /**
     * Setter method for property <tt>csvFolderPath</tt>.
     * 
     * @param csvFolderPath value to be assigned to property csvFolderPath
     */
    public void setCsvFolderPath(String csvFolderPath) {
        this.csvFolderPath = csvFolderPath;
    }

    /**
     * Getter method for property <tt>csvFilePath</tt>.
     * 
     * @return property value of csvFilePath
     */
    public String getCsvFilePath() {
        return csvFilePath;
    }

    /**
     * Setter method for property <tt>csvFilePath</tt>.
     * 
     * @param csvFilePath value to be assigned to property csvFilePath
     */
    public void setCsvFilePath(String csvFilePath) {
        this.csvFilePath = csvFilePath;
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
     * Getter method for property <tt>parameterKeyList</tt>.
     * 
     * @return property value of parameterKeyList
     */
    public List<String> getParameterKeyList() {
        return parameterKeyList;
    }

    /**
     * Setter method for property <tt>parameterKeyList</tt>.
     * 
     * @param parameterKeyList value to be assigned to property parameterKeyList
     */
    public void setParameterKeyList(List<String> parameterKeyList) {
        this.parameterKeyList = parameterKeyList;
    }

}
