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

import com.alipay.test.acts.log.ActsLogUtil;
import com.alipay.test.acts.object.ActsObjectUtil;
import com.alipay.test.acts.yaml.cpUnit.ListObjectCPUnit;
import com.alipay.test.acts.yaml.cpUnit.ObjectCPUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author baishuo.lp
 * @version $Id: ListObjectUnitProperty.java, v 0.1 2015年8月12日 下午4:40:26 baishuo.lp Exp $
 */
public class ListObjectUnitProperty extends BaseUnitProperty {
    private static final Log       logger     = LogFactory.getLog(ListObjectUnitProperty.class);

    private String                 targetCSVPath;

    private Class<?>               classType;

    private List<BaseUnitProperty> objectList = new ArrayList<BaseUnitProperty>();

    @SuppressWarnings("unchecked")
    public ListObjectUnitProperty(String keyName, String keyPath, String parentCSVPath,
                                  List<Object> value) {
        super(keyName, keyPath, null);
        for (int i = 0; i < value.size(); i++) {
            Object obj = value.get(i);
            String currentKeyPath = this.keyPath + "." + i;
            BaseUnitProperty property;
            if (obj instanceof Map) {
                Map<String, Object> mapObj = (Map<String, Object>) obj;
                if (mapObj.get("__desc") != null) {

                    property = new ObjectUnitProperty(keyName, currentKeyPath, parentCSVPath,
                        mapObj);
                } else {

                    property = new BaseUnitProperty(keyName, currentKeyPath, value);
                }
            } else if (obj instanceof List) {
                List<Object> listObj = (List<Object>) obj;
                property = new ListObjectUnitProperty(keyName, currentKeyPath, parentCSVPath,
                    listObj);
            } else {
                property = new BaseUnitProperty(keyName, currentKeyPath, value);
            }
            this.objectList.add(property);
        }
    }

    public ListObjectUnitProperty(ListObjectCPUnit unit) {
        super(unit.getUnitName(), unit.getUnitName(), null);
        Assert.assertTrue("size of attributeList must be more than 0", unit.getAttributeList()
            .size() > 0);
        List<BaseUnitProperty> objectList = new ArrayList<BaseUnitProperty>();
        for (ObjectCPUnit objUnit : unit.getAttributeList()) {
            ObjectUnitProperty property = objUnit.getProperty();
            objectList.add(property);
        }
        this.targetCSVPath = unit.getTargetCSVPath();
        this.objectList = objectList;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object genObject(ClassLoader classLoader) {
        List list = new ArrayList();
        for (BaseUnitProperty property : this.getObjectList()) {
            Assert.assertTrue("only supported List<Complex Type>",
                property instanceof ObjectUnitProperty);
            ObjectUnitProperty childUnit = (ObjectUnitProperty) property;
            childUnit.setClassType(this.classType);
            list.add(childUnit.genObject(classLoader));
        }
        return list;
    }

    /**
     * 
     * @param object
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void compare(Object object) {
        List actualList = (List) object;
        if (actualList.size() != this.objectList.size()) {
            ActsLogUtil.error(logger, this.keyName + ":length is different, expect:"
                                      + this.objectList.size() + "，actual:" + actualList.size());
        }
        for (int i = 0; i < actualList.size(); i++) {
            ObjectUnitProperty childProperty = (ObjectUnitProperty) this.objectList.get(i);
            childProperty.compare(actualList.get(i));
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object dump(String fieldName) {
        Map dumpMap = new LinkedHashMap();
        List objlist = new ArrayList();
        boolean isComplexList = false;
        boolean needDump = false;
        String expect = "";
        for (BaseUnitProperty property : this.objectList) {
            //notice: it is terrible if type of element in list is list
            if (property instanceof ObjectUnitProperty) {
                isComplexList = true;
                ObjectUnitProperty objProperty = (ObjectUnitProperty) property;
                Map obj = (Map) objProperty.dump(fieldName);
                objlist.add(obj.get(fieldName));
                if (obj.size() != 0) {
                    needDump = true;
                }
            } else {
                Object obj = ((Map) property.dump()).get(fieldName);
                objlist.add(obj);
                expect += (";" + obj);
            }
        }
        if (!isComplexList) {
            needDump = !ActsObjectUtil.easyCompare(expect.substring(1), this.baseValue);
        }

        if (needDump && isComplexList) {
            dumpMap.put(fieldName, objlist);
        } else if (needDump) {
            dumpMap.put(fieldName, expect.substring(1));
        }
        return dumpMap;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ListObjectUnitProperty [objectList=" + objectList + ", keyName=" + keyName
               + ", flagCode=" + flagCode + ", keyPath=" + keyPath + "]";
    }

    /**
     * Getter method for property <tt>objectList</tt>.
     * 
     * @return property value of objectList
     */
    public List<BaseUnitProperty> getObjectList() {
        return objectList;
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
