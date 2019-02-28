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

import java.util.ArrayList;
import java.util.List;

/**
 * Virtual database objects
 * @author tantian.wc
 * @version $Id: VirtualDataSet.java, v 0.1 2015年10月13日 上午10:20:40 tantian.wc Exp $
 */
public class VirtualDataSet extends TestUnit {

    /** List of database tables */
    private List<VirtualTable> virtualTables = new ArrayList<VirtualTable>();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static VirtualDataSet getInstance() {
        return new VirtualDataSet();
    }

    /**
     * Add a series of DOs and automatically generate virtualTables
     * @param dataObjects
     * @return
     */
    public VirtualDataSet addDataObjects(List<Object> dataObjects) {
        if (this.virtualTables == null) {
            this.virtualTables = new ArrayList<VirtualTable>();
        }

        for (Object dataObject : dataObjects) {
            VirtualTable virtualTable = new VirtualTable(dataObject);
            this.virtualTables.add(virtualTable);
        }
        return this;

    }

    /**
     * Add a series of VirtualTables
     * @param virtualTables
     * @return
     */
    public VirtualDataSet addTables(List<VirtualTable> virtualTables) {
        if (this.virtualTables == null) {
            this.virtualTables = new ArrayList<VirtualTable>();
        }

        for (VirtualTable virtualTable : virtualTables) {
            this.virtualTables.add(virtualTable);

        }
        return this;
    }

    /**
     * Add a VirtualTable
     * @param virtualTable
     * @return
     */
    public VirtualDataSet addVirtualTable(VirtualTable virtualTable) {
        if (this.virtualTables == null) {
            this.virtualTables = new ArrayList<VirtualTable>();
        }
        this.virtualTables.add(virtualTable);
        return this;
    }

    /**
     * Get VirtualTable by table name
     * @param tableName
     * @return
     */
    public VirtualTable getVirtualTableByName(String tableName) {
        if (virtualTables != null && tableName != null) {
            for (VirtualTable virtualTable : virtualTables) {
                if (tableName.equalsIgnoreCase(virtualTable.getTableName())) {
                    return virtualTable;
                }
            }
        }
        return null;
    }

    /**
     * Gets virtual tables.
     *
     * @return the virtual tables
     */
    public List<VirtualTable> getVirtualTables() {
        return virtualTables;
    }

    /**
     * Sets virtual tables.
     *
     * @param virtualTables the virtual tables
     */
    public void setVirtualTables(List<VirtualTable> virtualTables) {
        this.virtualTables = virtualTables;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VirtualDataSet [virtualTables=" + virtualTables + "]";
    }

}
