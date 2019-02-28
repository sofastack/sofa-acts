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
package com.alipay.test.acts.model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * virtual table
 * @author tantian.wc
 * @version $Id: VirtualTable.java, v 0.1 2015年10月13日 上午10:29:18 tantian.wc Exp $
 */
public class VirtualTable extends TestNode {

    /** if DO, the class name of the DO */
    private String                                          dataObjClazz;

    /** table name */
    private String                                          tableName;

    /** description of table template */
    private String                                          tableBaseDesc;

    /** table rowRecords */
    private List<Map<String, Object>>                       tableData;

    /** flags */
    private Map<String /* fieldName*/, String /* Flag */> flags;

    /**
     * Constructor.
     */
    public VirtualTable() {
    }

    /**
     * Constructor.
     *
     * @param dataObjClazz the data obj clazz
     */
    public VirtualTable(String dataObjClazz) {
        this.dataObjClazz = dataObjClazz;
    }

    /**
     * Constructor.
     *
     * @param tableName the table name
     * @param tableBaseDesc the table base desc
     */
    public VirtualTable(String tableName, String tableBaseDesc) {
        this.tableName = tableName;
        this.tableBaseDesc = tableBaseDesc;
    }

    /**
     * Constructor.
     *
     * @param dataObject the data object
     */
    public VirtualTable(Object dataObject) {
        addRow(dataObject);
    }

    /**
     * 获取表字段对应的flag，兼容表字段名称大小写
     * @param fieldName
     * @return flag标识
     */
    public String getFlagByFieldNameIgnoreCase(String fieldName) {
        if (this.flags == null || this.flags.isEmpty()) {
            return null;
        }

        String currentFlag = this.flags.get(fieldName); // 尝试原始字段名称
        if (currentFlag == null) {
            currentFlag = this.flags.get(fieldName.toLowerCase()); // 尝试原始字段小写
            if (currentFlag == null) {
                currentFlag = this.flags.get(fieldName.toUpperCase()); // 尝试原始字段大写
            }
        }

        return currentFlag;

    }

    /**
     * Add one row by map
     * @param row
     * @return
     */
    public VirtualTable addRow(Map<String, Object> row) {
        if (tableData == null) {
            tableData = new ArrayList<Map<String, Object>>();
        }
        this.tableData.add(row);
        return this;
    }

    /**
     * Add one row by DO
     * @param dataObject
     * @return
     */
    public VirtualTable addRow(Object dataObject) {
        if (dataObjClazz == null) {
            dataObjClazz = dataObject.getClass().getName();
        }
        if (tableData == null) {
            tableData = new ArrayList<Map<String, Object>>();
        }
        Map<String, Object> rowData = new LinkedHashMap<String, Object>();
        List<Field> tmpFields = new ArrayList<Field>();
        for (@SuppressWarnings("rawtypes")
        Class clazz = dataObject.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Field f : clazz.getDeclaredFields()) {
                tmpFields.add(f);
            }
        }

        List<Field> fields = filterSyntheticFields(tmpFields);
        fields = filterStaticFields(fields);
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                rowData.put(field.getName(), field.get(dataObject));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        tableData.add(rowData);
        return this;
    }

    /**
     * Gets row.
     *
     * @param rowNum the row num
     * @return the row
     */
    public Map<String, Object> getRow(int rowNum) {
        if (tableData == null || tableData.size() < rowNum + 1) {
            return null;
        }
        return tableData.get(rowNum);
    }

    /**
     * Set the specified line
     * 
     * @param rowNum
     * @param row
     * @return
     */
    public VirtualTable setRow(int rowNum, Map<String, Object> row) {
        Map<String, Object> oldRow = getRow(rowNum);
        if (oldRow == null) {
            return this;
        }
        tableData.set(rowNum, row);
        return this;
    }

    private List<Field> filterSyntheticFields(List<Field> tmpFields) {
        // 过滤 synthetic 属性
        List<Field> fieldsList = new ArrayList<Field>();
        for (Field field : tmpFields) {
            if (!field.isSynthetic()) {
                fieldsList.add(field);
            }
        }
        return fieldsList;
    }

    private List<Field> filterStaticFields(List<Field> tmpFields) {
        List<Field> fieldsList = new ArrayList<Field>();
        for (Field field : tmpFields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                fieldsList.add(field);
            }
        }
        return fieldsList;
    }

    /**
     * Gets data obj clazz.
     *
     * @return the data obj clazz
     */
    public String getDataObjClazz() {
        return dataObjClazz;
    }

    /**
     * Sets data obj clazz.
     *
     * @param dataObjClazz the data obj clazz
     */
    public void setDataObjClazz(String dataObjClazz) {
        this.dataObjClazz = dataObjClazz;
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
     * Gets table data.
     *
     * @return the table data
     */
    public List<Map<String, Object>> getTableData() {
        return tableData;
    }

    /**
     * Sets table data.
     *
     * @param tableData the table data
     */
    public void setTableData(List<Map<String, Object>> tableData) {
        this.tableData = tableData;
    }

    /**
     * Gets flags.
     *
     * @return the flags
     */
    public Map<String, String> getFlags() {
        return flags;
    }

    /**
     * Sets flags.
     *
     * @param flags the flags
     */
    public void setFlags(Map<String, String> flags) {
        this.flags = flags;
    }

    /**
     * Gets table base desc.
     *
     * @return the table base desc
     */
    public String getTableBaseDesc() {
        return tableBaseDesc;
    }

    /**
     * Sets table base desc.
     *
     * @param tableBaseDesc the table base desc
     */
    public void setTableBaseDesc(String tableBaseDesc) {
        this.tableBaseDesc = tableBaseDesc;
    }

    /**
     * Sets flag.
     *
     * @param key the key
     * @param flag the flag
     * @return the flag
     */
    public VirtualTable setFlag(String key, String flag) {
        if (this.flags == null) {
            this.flags = new LinkedHashMap<String, String>();
        }
        this.flags.put(key, flag);
        return this;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VirtualTable [dataObjClazz=" + dataObjClazz + ", tableName=" + tableName
               + ", tableBaseDesc=" + tableBaseDesc + ", tableData=" + tableData + ", flags="
               + flags + "]";
    }

}
