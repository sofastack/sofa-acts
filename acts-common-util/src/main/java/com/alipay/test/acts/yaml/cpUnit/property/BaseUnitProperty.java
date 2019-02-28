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

import com.alipay.test.acts.cache.ActsCacheData;
import com.alipay.test.acts.object.ActsObjectUtil;
import com.alipay.test.acts.object.comparer.UnitComparer;
import com.alipay.test.acts.object.enums.UnitFlagEnum;
import com.alipay.test.acts.object.manager.ObjectCompareManager;

import org.apache.commons.lang.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * db field
 * 
 * @author baishuo.lp
 * @version $Id: BaseUnitProperty.java, v 0.1 2015年8月12日 下午4:40:26 baishuo.lp Exp $
 */
public class BaseUnitProperty {

    protected String  keyName;

    protected String  flagCode;

    protected Object  expectValue;

    protected Object  baseValue;

    protected String  baseFlagCode;

    protected String  dbColumnType;

    protected String  dbColumnComment;

    protected String  keyPath;

    protected boolean isUnique       = true;

    protected boolean compareSuccess = true;

    protected Object  oldExpectValue = null;

    protected Object  actualValue    = null;

    public BaseUnitProperty(String keyName, String keyPath, Object expectValue) {
        this.keyName = keyName;
        this.expectValue = expectValue;
        this.keyPath = keyPath;
    }

    protected BaseUnitProperty() {
    }

    public void compare(Object object) {
        UnitFlagEnum flag = UnitFlagEnum.getByCode(this.flagCode);
        UnitComparer comparer;
        if (flag == UnitFlagEnum.CUSTOM) {
            comparer = ActsCacheData.getCustomComparer(this.flagCode);
        } else {
            comparer = ObjectCompareManager.getComparerManager().get(
                UnitFlagEnum.getByCode(this.flagCode));
        }
        if (!comparer.compare(this.expectValue, object, this.flagCode)) {
            this.setCompareSuccess(false);
            this.setActualValue(object);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object dump() {
        Object exp = this.expectValue;
        if (!this.compareSuccess) {
            if (this.isUnique) {

                exp = this.oldExpectValue;
            } else {

                exp = this.actualValue;
            }
        }

        Map dumpMap = new LinkedHashMap();
        if (!StringUtils.equals(this.baseFlagCode, this.flagCode)) {
            dumpMap.put(this.keyName + "[" + this.flagCode + "]", exp);
        } else if (!ActsObjectUtil.easyCompare(this.baseValue, exp)) {
            dumpMap.put(this.keyName, exp);
        }
        return dumpMap;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BaseUnitProperty [keyName=" + keyName + ", flagCode=" + flagCode + ", expectValue="
               + expectValue + ", baseValue=" + baseValue + ", baseFlagCode=" + baseFlagCode
               + ", dbColumnType=" + dbColumnType + ", dbColumnComment=" + dbColumnComment
               + ", keyPath=" + keyPath + "]";
    }

    /**
     * Getter method for property <tt>keyName</tt>.
     * 
     * @return property value of keyName
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * Setter method for property <tt>keyName</tt>.
     * 
     * @param keyName value to be assigned to property keyName
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    /**
     * Getter method for property <tt>flagCode</tt>.
     * 
     * @return property value of flagCode
     */
    public String getFlagCode() {
        return flagCode;
    }

    /**
     * Setter method for property <tt>flagCode</tt>.
     * 
     * @param flagCode value to be assigned to property flagCode
     */
    public void setFlagCode(String flagCode) {
        this.flagCode = flagCode;
    }

    /**
     * Getter method for property <tt>expectValue</tt>.
     * 
     * @return property value of expectValue
     */
    public Object getExpectValue() {
        return expectValue;
    }

    /**
     * Setter method for property <tt>expectValue</tt>.
     * 
     * @param expectValue value to be assigned to property expectValue
     */
    public void setExpectValue(Object expectValue) {
        this.expectValue = expectValue;
    }

    /**
     * Getter method for property <tt>keyPath</tt>.
     * 
     * @return property value of keyPath
     */
    public String getKeyPath() {
        return keyPath;
    }

    /**
     * Setter method for property <tt>keyPath</tt>.
     * 
     * @param keyPath value to be assigned to property keyPath
     */
    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }

    /**
     * Getter method for property <tt>baseValue</tt>.
     * 
     * @return property value of baseValue
     */
    public Object getBaseValue() {
        return baseValue;
    }

    /**
     * Setter method for property <tt>baseValue</tt>.
     * 
     * @param baseValue value to be assigned to property baseValue
     */
    public void setBaseValue(Object baseValue) {
        this.baseValue = baseValue;
    }

    /**
     * Getter method for property <tt>dbColumnType</tt>.
     * 
     * @return property value of dbColumnType
     */
    public String getDbColumnType() {
        return dbColumnType;
    }

    /**
     * Setter method for property <tt>dbColumnType</tt>.
     * 
     * @param dbColumnType value to be assigned to property dbColumnType
     */
    public void setDbColumnType(String dbColumnType) {
        this.dbColumnType = dbColumnType;
    }

    /**
     * Getter method for property <tt>dbColumnComment</tt>.
     * 
     * @return property value of dbColumnComment
     */
    public String getDbColumnComment() {
        return dbColumnComment;
    }

    /**
     * Setter method for property <tt>dbColumnComment</tt>.
     * 
     * @param dbColumnComment value to be assigned to property dbColumnComment
     */
    public void setDbColumnComment(String dbColumnComment) {
        this.dbColumnComment = dbColumnComment;
    }

    /**
     * Getter method for property <tt>baseFlagCode</tt>.
     * 
     * @return property value of baseFlagCode
     */
    public String getBaseFlagCode() {
        return baseFlagCode;
    }

    /**
     * Setter method for property <tt>baseFlagCode</tt>.
     * 
     * @param baseFlagCode value to be assigned to property baseFlagCode
     */
    public void setBaseFlagCode(String baseFlagCode) {
        this.baseFlagCode = baseFlagCode;
    }

    /**
     * Getter method for property <tt>isUnique</tt>.
     * 
     * @return property value of isUnique
     */
    public boolean isUnique() {
        return isUnique;
    }

    /**
     * Setter method for property <tt>isUnique</tt>.
     * 
     * @param isUnique value to be assigned to property isUnique
     */
    public void setUnique(boolean isUnique) {
        this.isUnique = isUnique;
    }

    /**
     * Getter method for property <tt>oldExpectValue</tt>.
     * 
     * @return property value of oldExpectValue
     */
    public Object getOldExpectValue() {
        return oldExpectValue;
    }

    /**
     * Setter method for property <tt>oldExpectValue</tt>.
     * 
     * @param oldExpectValue value to be assigned to property oldExpectValue
     */
    public void setOldExpectValue(Object oldExpectValue) {
        this.oldExpectValue = oldExpectValue;
    }

    /**
     * Getter method for property <tt>actualValue</tt>.
     * 
     * @return property value of actualValue
     */
    public Object getActualValue() {
        return actualValue;
    }

    /**
     * Setter method for property <tt>actualValue</tt>.
     * 
     * @param actualValue value to be assigned to property actualValue
     */
    public void setActualValue(Object actualValue) {
        this.actualValue = actualValue;
    }

    /**
     * Getter method for property <tt>compareSuccess</tt>.
     * 
     * @return property value of compareSuccess
     */
    public boolean isCompareSuccess() {
        return compareSuccess;
    }

    /**
     * Setter method for property <tt>compareSuccess</tt>.
     * 
     * @param compareSuccess value to be assigned to property compareSuccess
     */
    public void setCompareSuccess(boolean compareSuccess) {
        this.compareSuccess = compareSuccess;
    }

}
