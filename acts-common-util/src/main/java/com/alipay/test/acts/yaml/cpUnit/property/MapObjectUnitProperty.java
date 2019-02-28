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
package com.alipay.test.acts.yaml.cpUnit.property;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author baishuo.lp
 * @version $Id: ListObjectUnitProperty.java, v 0.1 2015年8月12日 下午4:40:26 baishuo.lp Exp $
 */
public class MapObjectUnitProperty extends BaseUnitProperty {

    private String                              targetCSVPath;

    private Class<?>                            classType;

    private final Map<String, BaseUnitProperty> objectMap = new LinkedHashMap<String, BaseUnitProperty>();

    @SuppressWarnings("unchecked")
    public MapObjectUnitProperty(String keyName, String keyPath, String parentCSVPath,
                                 Map<String, BaseUnitProperty> value) {
        super(keyName, keyPath, null);
        if (value != null) {
            for (String key : value.keySet()) {

                BaseUnitProperty property = value.get(key);

                this.objectMap.put(key, property);
            }

        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object genObject(ClassLoader classLoader) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (String key : this.objectMap.keySet()) {
            BaseUnitProperty property = this.objectMap.get(key);
            if (property instanceof ObjectUnitProperty) {
                ObjectUnitProperty childUnit = (ObjectUnitProperty) property;
                childUnit.setClassType(this.classType);
                map.put(key, childUnit.genObject(classLoader));
            } else {
                map.put(key, property.getExpectValue());
            }
        }
        return map;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MapObjectUnitProperty [objectMap=" + objectMap + ", keyName=" + keyName
               + ", flagCode=" + flagCode + ", keyPath=" + keyPath + "]";
    }

    /**
     * Getter method for property <tt>objectList</tt>.
     * 
     * @return property value of objectList
     */
    public Map<String, BaseUnitProperty> getObjectMap() {
        return objectMap;
    }

    /**
     * Getter method for property <tt>targetCSVPath</tt>.
     * 
     * @return property value of targetCSVPath
     */
    public String getTargetCSVPath() {
        return targetCSVPath;
    }

    /**
     * Setter method for property <tt>targetCSVPath</tt>.
     * 
     * @param targetCSVPath value to be assigned to property targetCSVPath
     */
    public void setTargetCSVPath(String targetCSVPath) {
        this.targetCSVPath = targetCSVPath;
    }

    /**
     * Getter method for property <tt>classType</tt>.
     * 
     * @return property value of classType
     */
    public Class<?> getClassType() {
        return classType;
    }

    /**
     * Setter method for property <tt>classType</tt>.
     * 
     * @param classType value to be assigned to property classType
     */
    public void setClassType(Class<?> classType) {
        this.classType = classType;
    }

}
