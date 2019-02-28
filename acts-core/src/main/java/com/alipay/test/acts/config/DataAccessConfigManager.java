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
package com.alipay.test.acts.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.InitializingBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;

public class DataAccessConfigManager implements InitializingBean {
    protected static final Log                     log              = LogFactory
                                                                        .getLog(DataAccessConfigManager.class);

    /** jdbc connection config */
    protected static Map<String, DataAccessConfig> configMap        = new HashMap<String, DataAccessConfig>();

    public static Map<String, List<String>>        dataSourceMap    = new HashMap<String, List<String>>();
    public static Map<String, List<String>>        extDataSourceMap = new HashMap<String, List<String>>();
    /**
     *  key: class name
    *   value: DO object
    *    */
    protected static Map<String, Object>           DAOMap           = new HashMap<String, Object>();

    /**
     * Find table name string.
     *
     * @param className the class name
     * @return the string
     */
    public String findTableName(String className) {

        Iterator<String> it = configMap.keySet().iterator();

        while (it.hasNext()) {
            if (StringUtils.equals(className, it.next())) {
                return configMap.get(className).getTableName();
            }
        }

        return null;

    }

    /**
     * Find data source name string.
     *
     * @param targetTableName the target table name
     * @return the string
     */
    public static String findDataSourceName(String targetTableName) {
        boolean findExtDateSourceFlag = true;
        boolean findDateSourceFlag = true;
        String result = null;
        String tableName = targetTableName.toLowerCase();
        for (String key : extDataSourceMap.keySet()) {
            //Default returns the last data source
            result = key;
            for (String tableTmp : extDataSourceMap.get(key)) {
                String table = tableTmp.toLowerCase();
                if (table.startsWith("*") && !table.endsWith("*")) {
                    if (tableName.endsWith(table.replace("*", ""))) {
                        return key;
                    }
                    findExtDateSourceFlag = false;
                } else if (!table.startsWith("*") && table.endsWith("*")) {
                    if (tableName.startsWith(table.replace("*", ""))) {
                        return key;
                    }
                    findExtDateSourceFlag = false;
                } else if (table.startsWith("*") && table.endsWith("*")) {
                    if (tableName.contains(table.replace("*", ""))) {
                        return key;
                    }
                    findExtDateSourceFlag = false;
                } else {
                    if (tableName.equals(table)) {
                        return key;
                    }
                    findExtDateSourceFlag = false;
                }
            }
        }
        for (String key : dataSourceMap.keySet()) {
            result = key;
            for (String tableTmp : dataSourceMap.get(key)) {
                String table = tableTmp.toLowerCase();
                if (table.startsWith("*") && !table.endsWith("*")) {
                    if (tableName.endsWith(table.replace("*", ""))) {
                        return key;
                    }
                    findDateSourceFlag = false;
                } else if (!table.startsWith("*") && table.endsWith("*")) {
                    if (tableName.startsWith(table.replace("*", ""))) {
                        return key;
                    }
                    findDateSourceFlag = false;
                } else if (table.startsWith("*") && table.endsWith("*")) {
                    if (tableName.contains(table.replace("*", ""))) {
                        return key;
                    }
                    findDateSourceFlag = false;
                } else {
                    if (tableName.equals(table)) {
                        return key;
                    }
                    findDateSourceFlag = false;
                }
            }
        }

        /**regular expression matching.
         * in order to be compatible with the old format,the original matching method is not deleted
         */
        for (String key : extDataSourceMap.keySet()) {
            //Default returns the last data source
            result = key;
            for (String tableTmp : extDataSourceMap.get(key)) {
                String table = tableTmp.toLowerCase();
                try {
                    Pattern pattern = Pattern.compile(tableName);
                    Matcher matcher = pattern.matcher(table);
                    if (matcher.find() && StringUtils.equals(table, matcher.group())) {
                        return key;
                    }
                } catch (Exception e) {
                    // regex has problem on format, ignore and continue
                }
            }
        }
        findExtDateSourceFlag = false;

        for (String key : dataSourceMap.keySet()) {
            result = key;
            for (String tableTmp : dataSourceMap.get(key)) {
                String table = tableTmp.toLowerCase();
                try {
                    Pattern pattern = Pattern.compile(tableName);
                    Matcher matcher = pattern.matcher(table);
                    if (matcher.find() && StringUtils.equals(table, matcher.group())) {
                        return key;
                    }
                } catch (Exception e) {
                    // regex has problem on format, ignore and continue
                }
            }
        }
        findDateSourceFlag = false;

        if (!(findExtDateSourceFlag || findDateSourceFlag)) {
            if (log.isInfoEnabled()) {
                log.info("Not found data source for table: " + tableName
                         + ". Use default data source(last data source): " + result);
            }
        }
        return result;
    }

    /**
     * Find data access config data access config.
     *
     * @param className the class name
     * @return the data access config
     */
    public DataAccessConfig findDataAccessConfig(String className) {
        Iterator<String> it = configMap.keySet().iterator();

        while (it.hasNext()) {
            if (StringUtils.equals(className, it.next())) {
                return configMap.get(className);
            }
        }

        return null;

    }

    /**
     * Gets dao.
     *
     * @param className the class name
     * @return the dao
     */
    public Object getDAO(String className) {

        Iterator<String> it = DAOMap.keySet().iterator();

        while (it.hasNext()) {
            if (StringUtils.equals(className, it.next())) {
                return DAOMap.get(className);
            }
        }

        return null;
    }

    /**
     * Update ext data source map.
     *
     * @param dataSourceName the data source name
     * @param tableName the table name
     */
    public static void updateExtDataSourceMap(String dataSourceName, String tableName) {
        if (!extDataSourceMap.containsKey(dataSourceName)) {
            List<String> tables = new ArrayList<String>();
            extDataSourceMap.put(dataSourceName, tables);
        }
        extDataSourceMap.get(dataSourceName).add(tableName);
    }

    /**
     * Clear ext data source map.
     */
    public static void clearExtDataSourceMap() {
        extDataSourceMap.clear();
    }

    /**
     * After properties set.
     *
     * @throws Exception the exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {

    }

}
