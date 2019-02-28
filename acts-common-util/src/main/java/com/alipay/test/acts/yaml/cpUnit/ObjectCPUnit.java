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
package com.alipay.test.acts.yaml.cpUnit;

import com.alipay.test.acts.constant.ActsPathConstants;
import com.alipay.test.acts.yaml.cpUnit.property.BaseUnitProperty;
import com.alipay.test.acts.yaml.cpUnit.property.ListObjectUnitProperty;
import com.alipay.test.acts.yaml.cpUnit.property.ObjectUnitProperty;
import com.alipay.test.acts.yaml.enums.CPUnitTypeEnum;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * CP of object
 * 
 * @author baishuo.lp
 * @version $Id: DataBaseCPUnit.java, v 0.1 2015年8月12日 上午11:23:52 baishuo.lp Exp $
 */
public class ObjectCPUnit extends BaseCPUnit {

    private final ObjectUnitProperty            property;

    private final Map<String, BaseUnitProperty> attributeMap;

    public ObjectCPUnit(String unitName, Map<String, Object> rawData) {
        this.unitName = unitName;
        this.description = "" + rawData.get("__desc");
        this.unitType = CPUnitTypeEnum.OBJECT;
        this.targetCSVPath = ActsPathConstants.OBJECT_DATA_PATH + this.unitName + "/"
                             + this.unitName + ".csv";
        for (Entry<String, Object> entry : rawData.entrySet()) {
            String keyName = entry.getKey();
            if (keyName.startsWith("$")) {
                this.specialMap.put(keyName, entry.getValue());
                rawData.remove(keyName);
            }
        }
        this.property = new ObjectUnitProperty(this.unitName, this.unitName, this.targetCSVPath,
            rawData);
        this.attributeMap = this.property.getAttributeMap();
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ObjectCPUnit [unitType=" + unitType + ", attributeMap=" + attributeMap
               + ", unitName=" + unitName + ", description=" + description + ", targetCSVPath="
               + targetCSVPath + ", specialMap=" + specialMap + "]";
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Object dump() {
        Map dumpMap = new LinkedHashMap();
        dumpMap.put("__desc", this.description);
        for (Entry<String, BaseUnitProperty> entry : this.attributeMap.entrySet()) {
            BaseUnitProperty property = entry.getValue();
            if (property instanceof ObjectUnitProperty) {
                Map objMap = (Map) ((ObjectUnitProperty) entry.getValue()).dump(entry.getKey());
                dumpMap.putAll(objMap);
            } else if (property instanceof ListObjectUnitProperty) {
                Map objMap = (Map) ((ListObjectUnitProperty) entry.getValue()).dump(entry.getKey());
                dumpMap.putAll(objMap);
            } else {
                dumpMap.putAll((Map) entry.getValue().dump());
            }
        }
        return dumpMap;
    }

    /**
     * Getter method for property <tt>attributeMap</tt>.
     * 
     * @return property value of attributeMap
     */
    public Map<String, BaseUnitProperty> getAttributeMap() {
        return attributeMap;
    }

    /**
     * Getter method for property <tt>property</tt>.
     * 
     * @return property value of property
     */
    public ObjectUnitProperty getProperty() {
        return property;
    }
}
