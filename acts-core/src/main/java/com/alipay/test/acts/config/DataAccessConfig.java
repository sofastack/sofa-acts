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

public class DataAccessConfig {

    private String   dataSourceName;
    /** Table name */
    private String   tableName;
    /** The primary keys of the table */
    private String[] keys;
    /** Field names used when comparing data */
    private String[] selectKeys;

    /**
     * Constructor.
     *
     * @param tableName the table name
     * @param keys the keys
     * @param selectKeys the select keys
     */
    public DataAccessConfig(String tableName, String[] keys, String[] selectKeys) {

        this.tableName = tableName;
        this.keys = keys;
        this.selectKeys = selectKeys;
    }

    /**
     * Gets table name.
     *
     * @return the table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets table name.
     *
     * @param tableName the table name
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Gets data source name.
     *
     * @return the data source name
     */
    public String getDataSourceName() {
        return dataSourceName;
    }

    /**
     * Sets data source name.
     *
     * @param dataSourceName the data source name
     */
    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    /**
     * Get keys string [ ].
     *
     * @return the string [ ]
     */
    public String[] getKeys() {
        return keys;
    }

    /**
     * Sets keys.
     *
     * @param keys the keys
     */
    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    /**
     * Get select keys string [ ].
     *
     * @return the string [ ]
     */
    public String[] getSelectKeys() {
        return selectKeys;
    }

    /**
     * Sets select keys.
     *
     * @param selectKeys the select keys
     */
    public void setSelectKeys(String[] selectKeys) {
        this.selectKeys = selectKeys;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        String message = "tableName" + this.tableName;
        if (this.keys.length > 0) {
            message += ",keys:";
            for (String key : this.keys) {
                message += key + "/";
            }
        }
        if (this.selectKeys.length > 0) {
            message += ",selectKeys为：";
            for (String key : this.selectKeys) {
                message += key + "/";
            }
        }
        return message;
    }
}
