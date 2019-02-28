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

import com.alipay.test.acts.yaml.enums.CPUnitTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CP unit of object list
 * 
 * @author baishuo.lp
 * @version $Id: DataBaseCPUnit.java, v 0.1 2015年8月12日 上午11:23:52 baishuo.lp Exp $
 */
public class ListObjectCPUnit extends BaseCPUnit {

    private List<ObjectCPUnit> attributeList = new ArrayList<ObjectCPUnit>();

    /**
     * yaml
     * 
     * @param unitName
     * @param rawData
     */
    public ListObjectCPUnit(String unitName, List<Map<String, Object>> rawData) {
        this.unitName = unitName;
        this.unitType = CPUnitTypeEnum.OBJECT;
        for (Map<String, Object> data : rawData) {
            ObjectCPUnit objectCPUnit = new ObjectCPUnit(unitName, data);
            this.attributeList.add(objectCPUnit);
        }
    }

    /**
     * message
     *
     * @param msgCPUnit
     */
    public ListObjectCPUnit(MessageCPUnit msgCPUnit) {
        this.unitName = msgCPUnit.getUnitName();
        this.unitType = CPUnitTypeEnum.OBJECT;
        this.attributeList = msgCPUnit.getAttributeList();
        this.targetCSVPath = msgCPUnit.getTargetCSVPath();
    }

    /**
     * Getter method for property <tt>attributeList</tt>.
     * 
     * @return property value of attributeList
     */
    public List<ObjectCPUnit> getAttributeList() {
        return attributeList;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ListObjectCPUnit [unitType=" + unitType + ", attributeList=" + attributeList
               + ", unitName=" + unitName + "]";
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Object dump() {
        List objList = new ArrayList();
        for (ObjectCPUnit cpUnit : this.attributeList) {
            objList.add(cpUnit.dump());
        }
        return objList;
    }
}
