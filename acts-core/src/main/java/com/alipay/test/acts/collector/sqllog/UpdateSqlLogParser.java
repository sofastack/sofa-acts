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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.alipay.test.acts.runtime.ActsRuntimeContextThreadHold;

import org.apache.commons.lang.StringUtils;

/**
 * update-sql log parser
 * 
 * @author hongling.xiang
 * @version $Id: UpdateSqlLogParser.java, v 0.1 2015年10月26日 上午11:11:03 hongling.xiang Exp $
 */
public class UpdateSqlLogParser implements SqlLogParser {

    /** insert sql regular expression */
    private static final Pattern fieldSetParttner = Pattern
                                                      .compile("([\\s\\S]*)in \\(([\\s\\S]*)\\)");

    /**
     * @see com.alipay.test.acts.collector.sqllog.SqlLogParser#parseGenTableDatas(java.lang.String, java.util.List, java.util.List)
     */
    @Override
    public List<Map<String, Object>> parseGenTableDatas(String sql, List<String> paramValue,
                                                        List<String> paramType) {

        String querySql = genSql(sql, paramValue, paramType);

        String tableName = parseTableName(sql);
        //Sql query execution
        return ActsRuntimeContextThreadHold.getContext().getDbDatasProcessor()
            .queryForList(tableName, querySql);

    }

    /**
     * generate select-sql
     * @param sql
     * @param paramValue
     * @return
     */
    public String genSql(String sql, List<String> paramValue, List<String> paramType) {
        //parse field in set, where
        String setFieldStr = sql.substring(sql.indexOf(" set ") + 5, sql.indexOf(" where "));
        String[] setFields = setFieldStr.trim().split(",");
        if (null == setFields || setFields.length == 0) {
            return null;
        }

        String condtionPart = sql.substring(sql.indexOf(" where ")).trim();

        int needUpdateFieldNum = 0;
        for (String field : setFields) {
            if (StringUtils.contains(field, "?")) {
                needUpdateFieldNum++;
            }
        }
        while (condtionPart.contains("?")) {
            condtionPart = StringUtils.replace(condtionPart, "?",
                "'" + paramValue.get(needUpdateFieldNum) + "'", 1);
            ++needUpdateFieldNum;
        }

        String tableName = parseTableName(sql);
        StringBuffer querySql = new StringBuffer("select * from ");
        querySql.append(tableName);
        querySql.append(" " + condtionPart);
        querySql.append("\r\n");
        return querySql.toString();
    }

    /** 
     * @see com.alipay.test.acts.collector.sqllog.SqlLogParser#parseTableName(java.lang.String)
     */
    @Override
    public String parseTableName(String sql) {

        String sqlPart = sql.substring(0, sql.indexOf(" set ")).trim();
        String[] sqlSegments = sqlPart.split(" ");

        return sqlSegments[sqlSegments.length - 1].trim();
    }

    /** 
     * @see com.alipay.test.acts.collector.sqllog.SqlLogParser#parseTableFlags(java.lang.String, java.util.Set)
     */
    @Override
    public Map<String, String> parseTableFlags(String sql, Set<String> tableFields) {

        Map<String, String> fieldFlag = new HashMap<String, String>();

        //where condition
        Set<String> conFieldSet = new HashSet<String>();
        String condtionFieldStr = sql.substring(sql.indexOf("where") + 5).trim();
        List<String> conFields = Arrays.asList(condtionFieldStr.toLowerCase().split(" and "));
        for (int i = 0; i < conFields.size(); i++) {
            //example: id in (?,?,?)
            if (fieldSetParttner.matcher(conFields.get(i)).find() && conFields.get(i).contains("?")) {
                //Remove brackets
                String newConField = StringUtils.replace(conFields.get(i), "(", "");
                newConField = StringUtils.replace(newConField, ")", "");
                conFieldSet.add(StringUtils.substring(newConField, 0, newConField.indexOf("in"))
                    .trim());
                continue;
            }

            if (conFields.get(i).contains("?") && conFields.get(i).contains("=")) {
                //Remove  brackets
                String newConField = StringUtils.replace(conFields.get(i), "(", "");
                conFields.set(i, StringUtils.replace(newConField, ")", ""));
                conFieldSet.add(StringUtils.substring(newConField, 0, newConField.indexOf("="))
                    .trim());
            }
        }

        //Set a tag for the field
        for (String field : tableFields) {
            if (conFieldSet.contains(field)) {
                fieldFlag.put(field, "C");
                continue;
            }
            fieldFlag.put(field, "Y");
        }

        return fieldFlag;
    }

}
