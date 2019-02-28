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
 * CP unit of list data
 * 
 * @author baishuo.lp
 * @version $Id: GroupDataBaseCPUnit.java, v 0.1 2015年8月12日 上午11:23:52 baishuo.lp Exp $
 */
public class ListDataBaseCPUnit extends BaseCPUnit {

    private final List<DataBaseCPUnit> dataList = new ArrayList<DataBaseCPUnit>();

    @SuppressWarnings("unchecked")
    public ListDataBaseCPUnit(String unitName, List<Object> rawData) {
        this.unitName = unitName;
        this.unitType = CPUnitTypeEnum.DATABASE;

        for (Object data : rawData) {
            DataBaseCPUnit dataBaseUnit = new DataBaseCPUnit(unitName, (Map<String, Object>) data);
            this.dataList.add(dataBaseUnit);
        }
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ListDataBaseCPUnit [dataList=" + dataList + ", unitName=" + unitName
               + ", unitType=" + unitType + "]";
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object dump() {
        List listData = new ArrayList();
        for (DataBaseCPUnit dbUnit : this.dataList) {
            listData.add(dbUnit.dump());
        }
        return listData;
    }
}
