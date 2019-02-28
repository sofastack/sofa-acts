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
package com.alipay.test.acts.collector.sqllog;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alipay.test.acts.util.ReflectUtil;

import org.apache.commons.lang.StringUtils;

/**
 * insert sql log resolver
 * 
 * @author chao.gao
 * @author hongling.xiang
 * @version $Id: InsertSqlLogParser.java, v 0.1 2015-10-26- 11:10:26 a.m
 *          hongling.xiang Exp $
 */
public class InsertSqlLogParser implements SqlLogParser {

    @Override
    public List<Map<String, Object>> parseGenTableDatas(String sql, List<String> paramValue,
                                                        List<String> paramType) {

        List<Map<String, Object>> tableDatas = new ArrayList<Map<String, Object>>();
        Map<String, Object> tableRow = new HashMap<String, Object>();

        String tableNameFields = sql.substring(sql.indexOf(" into ") + 6, sql.indexOf(" values"))
            .trim();
        String tableFields = tableNameFields.substring(tableNameFields.indexOf("(") + 1,
            tableNameFields.indexOf(")")).trim();

        String tableValues = StringUtils.substring(sql, sql.indexOf(" values") + 7).trim();

        //field name, value, and type order
        int index = 0;
        String[] tableFieldsArray = tableFields.split(",");
        String[] tableValuesArray = tableValues.substring(1, tableValues.length() - 1).split(",");

        Object[] tableValuesObj = new Object[tableValuesArray.length];
        for (int i = 0; i < tableValuesArray.length; i++) {
            String fieldValue = tableValuesArray[i].trim();
            if (StringUtils.equals(fieldValue, "?") || StringUtils.equals(fieldValue, "null")) {
                if (StringUtils.isBlank(paramType.get(index))) {
                    tableValuesObj[i] = null;
                } else if (StringUtils.equalsIgnoreCase("", paramValue.get(index))) {
                    tableValuesObj[i] = StringUtils.EMPTY;
                } else if (StringUtils.equalsIgnoreCase("null", paramValue.get(index))) {
                    tableValuesObj[i] = null;
                } else {
                    Class<?> clazz = ReflectUtil.getClassForName(paramType.get(index).trim());
                    Object obj = ReflectUtil.valueByCorrectType(null, clazz, paramValue.get(index)
                        .trim());
                    tableValuesObj[i] = obj;
                }

                index++;
            }
            // field value is a sql function
            if (StringUtils.equals(fieldValue, "sysdate")) {
                tableValuesObj[i] = new Date();
            }
            tableRow.put(tableFieldsArray[i].trim(), tableValuesObj[i]);
        }

        if (null != tableRow) {
            tableDatas.add(tableRow);
        }

        return tableDatas;
    }

    /**
     * @see com.alipay.test.acts.collector.sqllog.SqlLogParser#parseTableName(java.lang.String)
     */
    @Override
    public String parseTableName(String sql) {
        String tableNameFields = sql.substring(sql.indexOf(" into") + 5, sql.indexOf(" values"))
            .trim();
        return tableNameFields.substring(0, tableNameFields.indexOf("(")).trim();
    }

    /**
     * @see com.alipay.test.acts.collector.sqllog.SqlLogParser#parseTableFlags(java.lang.String,
     *      java.util.Set)
     */
    @Override
    public Map<String, String> parseTableFlags(String sql, Set<String> tableFields) {

        Map<String, String> fieldFlag = new HashMap<String, String>();

        for (String field : tableFields) {
            if (StringUtils.equalsIgnoreCase(field, "ID")) {
                fieldFlag.put(field, "C");
                continue;
            }
            fieldFlag.put(field, "Y");
        }

        return fieldFlag;
    }

}
