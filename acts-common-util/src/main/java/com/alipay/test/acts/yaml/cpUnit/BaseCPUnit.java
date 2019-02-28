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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CP base unit
 * 
 * @author baishuo.lp
 * @version $Id: BaseCPUnit.java, v 0.1 2015年8月12日 上午11:15:53 baishuo.lp Exp $
 */
public abstract class BaseCPUnit {

    /** name of unit */
    protected String                    unitName;

    /** desc and number of name */
    protected String                    description;

    /** path of targeted csv */
    protected String                    targetCSVPath;

    /** map of customize rule */
    protected final Map<String, Object> specialMap = new LinkedHashMap<String, Object>();

    /** type of CP unit */
    protected CPUnitTypeEnum            unitType;

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BaseCPUnit [unitName=" + unitName + ", description=" + description
               + ", targetCSVPath=" + targetCSVPath + ", specialMap=" + specialMap + ", unitType="
               + unitType + "]";
    }

    /**
     * generate a block map corresponding to the yaml file
     * 
     * @return
     */
    public abstract Object dump();

    /**
     * Getter method for property <tt>unitName</tt>.
     * 
     * @return property value of unitName
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * Setter method for property <tt>unitName</tt>.
     * 
     * @param unitName value to be assigned to property unitName
     */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    /**
     * Getter method for property <tt>specialMap</tt>.
     * 
     * @return property value of specialMap
     */
    public Map<String, Object> getSpecialMap() {
        return specialMap;
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
     * Getter method for property <tt>unitType</tt>.
     * 
     * @return property value of unitType
     */
    public CPUnitTypeEnum getUnitType() {
        return unitType;
    }
}
