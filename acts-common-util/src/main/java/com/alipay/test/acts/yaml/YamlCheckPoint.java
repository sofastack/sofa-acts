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

import com.alipay.test.acts.cache.ActsCacheData;
import com.alipay.test.acts.yaml.cpUnit.BaseCPUnit;
import com.alipay.test.acts.yaml.cpUnit.DataBaseCPUnit;
import com.alipay.test.acts.yaml.cpUnit.GroupDataBaseCPUnit;
import com.alipay.test.acts.yaml.cpUnit.ListDataBaseCPUnit;
import com.alipay.test.acts.yaml.cpUnit.ListObjectCPUnit;
import com.alipay.test.acts.yaml.cpUnit.MessageCPUnit;
import com.alipay.test.acts.yaml.cpUnit.ObjectCPUnit;
import com.alipay.test.acts.yaml.enums.CPUnitTypeEnum;
import org.junit.Assert;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * checkpoint model (CP)
 * 
 * @author baishuo.lp
 * @version $Id: YamlCheckPoint.java, v 0.1 2015年8月12日 上午11:09:37 baishuo.lp Exp $
 */
public class YamlCheckPoint {

    /** name of checkpoint*/
    private String                        checkPointName;

    /** map of checkpoints*/
    private final Map<String, BaseCPUnit> checkPointUnitMap = new LinkedHashMap<String, BaseCPUnit>();

    @SuppressWarnings("unchecked")
    public YamlCheckPoint(String checkPointName, Map<String, Object> checkPointData) {
        this.checkPointName = checkPointName;
        for (Entry<String, Object> entry : checkPointData.entrySet()) {
            String unitName = entry.getKey();
            CPUnitTypeEnum unitType = ActsCacheData.getCPUnitType(unitName);
            BaseCPUnit unit = null;
            if (unitType == null) {
                Assert.fail(unitName + "Checkpoint does not exist, please add it manually");
            }
            if (entry.getValue() != null) {
                switch (unitType) {
                    case DATABASE:
                        Object value = entry.getValue();
                        if (value instanceof Map) {
                            unit = new DataBaseCPUnit(unitName,
                                (Map<String, Object>) entry.getValue());
                        } else if (value instanceof List) {
                            unit = new ListDataBaseCPUnit(unitName, (List<Object>) value);
                        } else {
                            Assert.fail(unitName + "incorrect cp format");
                        }
                        break;
                    case GROUP:
                        unit = new GroupDataBaseCPUnit(unitName,
                            (Map<String, Object>) entry.getValue());
                        break;
                    case OBJECT:
                        if (entry.getValue() instanceof Map) {
                            unit = new ObjectCPUnit(unitName,
                                (Map<String, Object>) entry.getValue());
                        } else if (entry.getValue() instanceof List) {
                            unit = new ListObjectCPUnit(unitName,
                                (List<Map<String, Object>>) entry.getValue());
                        } else if (entry.getValue() != null) {
                            Assert.fail("format of prepare-object unknown exception");
                        }
                        break;
                    case MESSAGE:
                        unit = new MessageCPUnit(unitName, (List<Object>) entry.getValue());
                        break;
                }
            }
            this.checkPointUnitMap.put(unitName, unit);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map dump() {
        Map dumpMap = new LinkedHashMap();
        for (Entry<String, BaseCPUnit> entry : this.checkPointUnitMap.entrySet()) {
            String unitName = entry.getKey();
            BaseCPUnit cpUnit = entry.getValue();
            dumpMap.put(unitName, cpUnit.dump());
        }
        return dumpMap;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "YamlCheckPoint [checkPointName=" + checkPointName + ", checkPointUnitMap="
               + checkPointUnitMap + "]";
    }

    /**
     * Getter method for property <tt>checkPointName</tt>.
     * 
     * @return property value of checkPointName
     */
    public String getCheckPointName() {
        return checkPointName;
    }

    /**
     * Setter method for property <tt>checkPointName</tt>.
     * 
     * @param checkPointName value to be assigned to property checkPointName
     */
    public void setCheckPointName(String checkPointName) {
        this.checkPointName = checkPointName;
    }

    /**
     * Getter method for property <tt>checkPointUnitMap</tt>.
     * 
     * @return property value of checkPointUnitMap
     */
    public Map<String, BaseCPUnit> getCheckPointUnitMap() {
        return checkPointUnitMap;
    }

}
