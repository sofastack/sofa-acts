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

import com.alipay.test.acts.constant.ActsYamlConstants;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author baishuo.lp
 * @version $Id: YamlTestCase.java, v 0.1 2015年8月12日 上午11:06:27 baishuo.lp Exp $
 */
public class YamlTestCase {

    /** case ID*/
    private String                            caseId;

    /** case desc*/
    private String                            description;

    /** CP list*/
    private final Map<String, YamlCheckPoint> checkPointMap = new LinkedHashMap<String, YamlCheckPoint>();

    @SuppressWarnings("unchecked")
    public YamlTestCase(String caseId, Map<String, Object> testCaseData) {
        this.caseId = caseId;
        this.description = (String) testCaseData.get("__desc");
        testCaseData.remove("__desc");

        if (this.caseId.equals(ActsYamlConstants.COMMONKEY)) {
            //load common data
            YamlCheckPoint checkPoint = new YamlCheckPoint(ActsYamlConstants.COMMONKEY,
                testCaseData);
            this.checkPointMap.put(ActsYamlConstants.COMMONKEY, checkPoint);
        } else {
            //load cases data
            for (Entry<String, Object> entry : testCaseData.entrySet()) {
                String checkPointName = entry.getKey();
                Map<String, Object> checkPointData = (Map<String, Object>) entry.getValue();
                YamlCheckPoint checkPoint = new YamlCheckPoint(checkPointName, checkPointData);
                this.checkPointMap.put(checkPointName, checkPoint);
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map dump() {
        Map dumpMap = new LinkedHashMap();
        if (this.caseId.equals(ActsYamlConstants.COMMONKEY)) {
            dumpMap.putAll(this.checkPointMap.get(ActsYamlConstants.COMMONKEY).dump());
        } else {
            for (Entry<String, YamlCheckPoint> entry : this.checkPointMap.entrySet()) {
                String unitName = entry.getKey();
                YamlCheckPoint cpUnit = entry.getValue();
                dumpMap.put(unitName, cpUnit.dump());
            }
        }
        return dumpMap;
    }

    public YamlCheckPoint getCheckPoint(String checkPointName) {
        return checkPointMap.get(checkPointName);
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "YamlTestCase [caseId=" + caseId + ", description=" + description
               + ", checkPointMap=" + checkPointMap + "]";
    }

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
     * Getter method for property <tt>description</tt>.
     * 
     * @return property value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter method for property <tt>description</tt>.
     * 
     * @param description value to be assigned to property description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter method for property <tt>checkPointList</tt>.
     * 
     * @return property value of checkPointList
     */
    public Map<String, YamlCheckPoint> getCheckPointMap() {
        return checkPointMap;
    }

}
