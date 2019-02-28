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
import com.alipay.test.acts.yaml.enums.CPUnitTypeEnum;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * CP unit of group data
 * 
 * @author baishuo.lp
 * @version $Id: GroupDataBaseCPUnit.java, v 0.1 2015年8月12日 上午11:23:52 baishuo.lp Exp $
 */
public class GroupDataBaseCPUnit extends BaseCPUnit {

    /** column names list */
    private final String[]             conditionKeys;

    private final String               orderBy;

    private final List<DataBaseCPUnit> dataList = new ArrayList<DataBaseCPUnit>();

    @SuppressWarnings("unchecked")
    public GroupDataBaseCPUnit(String unitName, Map<String, Object> rawData) {
        this.unitName = unitName.substring(6);
        this.description = "" + rawData.get("__desc");
        rawData.remove("__desc");
        this.unitType = CPUnitTypeEnum.GROUP;
        this.targetCSVPath = ActsPathConstants.DB_DATA_PATH + this.unitName + ".csv";
        this.conditionKeys = ((String) rawData.get("__conditionKeys")).split(",");
        this.orderBy = (String) rawData.get("__orderBy");
        String tableName = unitName.substring(6);
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) rawData.get(tableName);

        for (Map<String, Object> data : dataList) {
            DataBaseCPUnit dataBaseUnit = new DataBaseCPUnit(tableName, data);
            this.dataList.add(dataBaseUnit);
        }
    }

    /**
     * Getter method for property <tt>conditionKeys</tt>.
     * 
     * @return property value of conditionKeys
     */
    public String[] getConditionKeys() {
        return conditionKeys;
    }

    /**
     * Getter method for property <tt>dataList</tt>.
     * 
     * @return property value of dataList
     */
    public List<DataBaseCPUnit> getDataList() {
        return dataList;
    }

    /**
     * Getter method for property <tt>orderBy</tt>.
     * 
     * @return property value of orderBy
     */
    public String getOrderBy() {
        return orderBy;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GroupDataBaseCPUnit [unitType=" + unitType + ", conditionKeys="
               + Arrays.toString(conditionKeys) + ", orderBy=" + orderBy + ", dataList=" + dataList
               + ", unitName=" + unitName + "]";
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object dump() {
        Map dumpMap = new TreeMap();
        dumpMap.put("__conditionKeys", StringUtils.join(this.conditionKeys, ","));
        dumpMap.put("__orderBy", this.orderBy);
        List listData = new ArrayList();
        for (DataBaseCPUnit dbUnit : this.dataList) {
            listData.add(dbUnit.dump());
        }
        dumpMap.put(this.unitName, listData);
        return dumpMap;
    }
}
