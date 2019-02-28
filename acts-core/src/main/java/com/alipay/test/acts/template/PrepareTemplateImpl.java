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
package com.alipay.test.acts.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.alipay.test.acts.config.DataAccessConfig;
import com.alipay.test.acts.config.DataAccessConfigManager;
import com.alipay.test.acts.model.PrepareData;
import com.alipay.test.acts.model.VirtualArgs;
import com.alipay.test.acts.model.VirtualDataSet;
import com.alipay.test.acts.model.VirtualEventSet;
import com.alipay.test.acts.model.VirtualResult;
import com.alipay.test.acts.model.VirtualTable;

public class PrepareTemplateImpl implements PrepareTemplate {

    protected DataAccessConfigManager dataAccessConfigManager;

    /**
     * Prepare prepare data.
     *
     * @param callback the callback
     * @return the prepare data
     */
    @Override
    public PrepareData prepare(PrepareCallBack callback) {
        PrepareData data = new PrepareData();
        // Prepare db data
        VirtualDataSet depDataSet = callback.prepareDepDataSet();
        fillTableName(depDataSet);
        data.setDepDataSet(depDataSet);
        // Prepare for input
        VirtualArgs args = callback.prepareArgs(depDataSet);
        data.setArgs(args);
        // expected db data
        VirtualDataSet expectDataSet = callback.prepareExpectDataSet(args, depDataSet);
        fillTableName(expectDataSet);
        data.setExpectDataSet(expectDataSet);
        // Prepare expected results
        Object expectResultObj = callback.prepareExpectResult(args, depDataSet);
        VirtualResult result = new VirtualResult(expectResultObj);
        data.setExpectResult(result);

        if (callback instanceof PrepareCallBackWithEvent) {
            VirtualEventSet virtualEventSet = ((PrepareCallBackWithEvent) callback)
                .prepareExpectEventSet(args, depDataSet, expectResultObj);
            data.setExpectEventSet(virtualEventSet);
        }

        return data;
    }

    /**
     * Convert object properties to column names
     * 
     * @param fieldName
     * @return
     */
    protected String convertFieldName2ColumnName(String fieldName) {

        if (fieldName.contains("_")) {
            return fieldName;
        }
        String[] strs = splitByCharacterType(fieldName, true);
        for (int i = 0; i < strs.length; i++) {
            strs[i] = strs[i];
        }
        String fieldColumnName = StringUtils.join(strs, "_");
        return fieldColumnName;

    }

    /**
     * Get tableName by DAO, fill in VirtualTable
     * @param dataSet
     */
    private void fillTableName(VirtualDataSet dataSet) {
        if (dataSet == null) {
            return;
        }
        for (VirtualTable virtualTable : dataSet.getVirtualTables()) {

            if (virtualTable.getTableName() == null) {
                Map<String, String> flagMap = virtualTable.getFlags();
                if (flagMap != null) {
                    List<String> keyList = new ArrayList<String>();
                    for (String flagKey : flagMap.keySet()) {
                        keyList.add(flagKey);

                    }
                    for (String key : keyList) {
                        String tmpFlag = flagMap.get(key);
                        flagMap.remove(key);
                        flagMap.put(convertFieldName2ColumnName(key), tmpFlag);

                    }

                }

                List<Map<String, Object>> tableData = new ArrayList<Map<String, Object>>();
                for (Map<String, Object> row : virtualTable.getTableData()) {
                    List<String> keyList = new ArrayList<String>();
                    for (String rowKey : row.keySet()) {
                        keyList.add(rowKey);
                    }
                    for (String key : keyList) {
                        Object tmpData = row.get(key);
                        row.remove(key);
                        row.put(convertFieldName2ColumnName(key), tmpData);

                    }
                    tableData.add(row);
                }
                virtualTable.setTableData(tableData);
                virtualTable.setFlags(flagMap);
                DataAccessConfig dataAccessConfig = dataAccessConfigManager
                    .findDataAccessConfig(virtualTable.getDataObjClazz());
                if (dataAccessConfig != null) {
                    virtualTable.setTableName(dataAccessConfig.getTableName());
                }
            }
        }
    }

    private static String[] splitByCharacterType(String str, boolean camelCase) {
        if (str == null) {
            return null;
        } else if (str.length() == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            char[] c = str.toCharArray();
            ArrayList list = new ArrayList();
            int tokenStart = 0;
            int currentType = Character.getType(c[tokenStart]);

            for (int pos = tokenStart + 1; pos < c.length; ++pos) {
                int type = Character.getType(c[pos]);
                if (type != currentType) {
                    if (camelCase && type == 2 && currentType == 1) {
                        int newTokenStart = pos - 1;
                        if (newTokenStart != tokenStart) {
                            list.add(new String(c, tokenStart, newTokenStart - tokenStart));
                            tokenStart = newTokenStart;
                        }
                    } else {
                        list.add(new String(c, tokenStart, pos - tokenStart));
                        tokenStart = pos;
                    }

                    currentType = type;
                }
            }

            list.add(new String(c, tokenStart, c.length - tokenStart));
            return ((String[]) list.toArray(new String[list.size()]));
        }
    }

    /**
     * Gets data access config manager.
     *
     * @return the data access config manager
     */
    public DataAccessConfigManager getDataAccessConfigManager() {
        return dataAccessConfigManager;
    }

    /**
     * Sets data access config manager.
     *
     * @param dataAccessConfigManager the data access config manager
     */
    public void setDataAccessConfigManager(DataAccessConfigManager dataAccessConfigManager) {
        this.dataAccessConfigManager = dataAccessConfigManager;
    }
}
