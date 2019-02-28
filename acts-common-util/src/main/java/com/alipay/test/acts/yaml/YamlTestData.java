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
package com.alipay.test.acts.yaml;

import com.alipay.test.acts.api.LogApis;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author baishuo.lp
 * @version $Id: YamlTestCase.java, v 0.1 2015年8月12日 上午11:06:27 baishuo.lp Exp $
 */
public class YamlTestData {

    /** TestCase Map*/
    private final Map<String, YamlTestCase> testCaseMap = new LinkedHashMap<String, YamlTestCase>();

    /** file path*/
    private String                          filePath;

    @SuppressWarnings("unchecked")
    public YamlTestData(File yamlFile) {
        this.filePath = yamlFile.getAbsolutePath();

        /** read Yaml*/
        InputStream is = null;
        try {
            is = new FileInputStream(yamlFile);
        } catch (FileNotFoundException e) {
            LogApis.fail("File not found: " + filePath);
        }
        if (is == null) {
            LogApis.fail("failed to read file : " + filePath);
        }
        InputStreamReader reader = new InputStreamReader(is);
        Iterator<Object> iterator = new Yaml().loadAll(reader).iterator();
        while (iterator.hasNext()) {
            LinkedHashMap<?, ?> rawData = (LinkedHashMap<?, ?>) iterator.next();
            for (Entry<?, ?> entry : rawData.entrySet()) {
                String caseId = (String) entry.getKey();
                Map<String, Object> testCaseData = (Map<String, Object>) entry.getValue();
                YamlTestCase testCase = new YamlTestCase(caseId, testCaseData);
                this.testCaseMap.put(caseId, testCase);
            }
        }
    }

    public YamlTestCase getTestCase(String caseId) {
        YamlTestCase testCase = this.testCaseMap.get(caseId);
        return testCase;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "YamlTestData [testCaseMap=" + testCaseMap + ", filePath=" + filePath + "]";
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public String dump() {
        List caseList = new ArrayList();
        for (String caseId : this.getTestCaseMap().keySet()) {
            YamlTestCase testCase = this.getTestCase(caseId);
            Map caseData = testCase.dump();
            Map caseMap = new HashMap();
            caseMap.put(caseId, caseData);
            caseList.add(caseMap);
        }
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(options).dumpAll(caseList.iterator());
    }

    /**
     * Getter method for property <tt>testCaseMap</tt>.
     * 
     * @return property value of testCaseMap
     */
    public Map<String, YamlTestCase> getTestCaseMap() {
        return testCaseMap;
    }

    /**
     * Getter method for property <tt>filePath</tt>.
     * 
     * @return property value of filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Setter method for property <tt>filePath</tt>.
     * 
     * @param filePath value to be assigned to property filePath
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
