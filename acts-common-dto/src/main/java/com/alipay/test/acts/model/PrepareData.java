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

/**
 * Test data, one for each case
 * @author tantian.wc
 * @version $Id: PrepareData.java, v 0.1 2015年10月13日 上午10:17:03 tantian.wc Exp $
 */
public class PrepareData {

    /**description */
    private String           description;

    /**request parameters  */
    private VirtualArgs      args;

    /**database preparation  */
    private VirtualDataSet   depDataSet;

    /**database expectation  */
    private VirtualDataSet   expectDataSet;

    /**response expectation  */
    private VirtualResult    expectResult;

    /**message expectation  */
    private VirtualEventSet  expectEventSet;

    /**exception expectation  */
    private VirtualException expectException;

    /**context parameters  */
    private VirtualParams    virtualParams;

    /**default constructor
     * set all object members to not null
     * */
    public PrepareData() {
        this(new VirtualArgs(), new VirtualDataSet(), new VirtualDataSet(), new VirtualResult(),
            new VirtualEventSet(), new VirtualException(), new VirtualParams());
    }

    private PrepareData(VirtualArgs virtualArgs, VirtualDataSet virtualDataSet,
                        VirtualDataSet virtualDataSet2, VirtualResult virtualResult,
                        VirtualEventSet virtualEventSet, VirtualException virtualException,
                        VirtualParams virtualParams) {
        this.args = virtualArgs;
        this.depDataSet = virtualDataSet;
        this.expectDataSet = virtualDataSet2;
        this.expectResult = virtualResult;
        this.expectEventSet = virtualEventSet;
        this.expectException = virtualException;
        this.virtualParams = virtualParams;
    }

    /**
     * Getter method for property <tt>description</tt>.
     * 
     * @return property value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter method for property <tt>description</tt>.
     * 
     * @param description value to be assigned to property description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets args.
     *
     * @return the args
     */
    public VirtualArgs getArgs() {
        return args;
    }

    /**
     * Sets args.
     *
     * @param args the args
     */
    public void setArgs(VirtualArgs args) {
        this.args = args;
    }

    /**
     * Gets dep data set.
     *
     * @return the dep data set
     */
    public VirtualDataSet getDepDataSet() {
        return depDataSet;
    }

    /**
     * Sets dep data set.
     *
     * @param depDataSet the dep data set
     */
    public void setDepDataSet(VirtualDataSet depDataSet) {
        this.depDataSet = depDataSet;
    }

    /**
     * Gets expect data set.
     *
     * @return the expect data set
     */
    public VirtualDataSet getExpectDataSet() {
        return expectDataSet;
    }

    /**
     * Sets expect data set.
     *
     * @param expectDataSet the expect data set
     */
    public void setExpectDataSet(VirtualDataSet expectDataSet) {
        this.expectDataSet = expectDataSet;
    }

    /**
     * Gets expect result.
     *
     * @return the expect result
     */
    public VirtualResult getExpectResult() {
        return expectResult;
    }

    /**
     * Sets expect result.
     *
     * @param expectResult the expect result
     */
    public void setExpectResult(VirtualResult expectResult) {
        this.expectResult = expectResult;
    }

    /**
     * Gets expect exception.
     *
     * @return the expect exception
     */
    public VirtualException getExpectException() {
        return expectException;
    }

    /**
     * Sets expect exception.
     *
     * @param expectException the expect exception
     */
    public void setExpectException(VirtualException expectException) {
        this.expectException = expectException;
    }

    /**
     * Gets expect event set.
     *
     * @return the expect event set
     */
    public VirtualEventSet getExpectEventSet() {
        return expectEventSet;
    }

    /**
     * Sets expect event set.
     *
     * @param expectEventSet the expect event set
     */
    public void setExpectEventSet(VirtualEventSet expectEventSet) {
        this.expectEventSet = expectEventSet;
    }

    /**
     * Gets virtual params.
     *
     * @return the virtual params
     */
    public VirtualParams getVirtualParams() {
        return virtualParams;
    }

    /**
     * Sets virtual params.
     *
     * @param virtualParams the virtual params
     */
    public void setVirtualParams(VirtualParams virtualParams) {
        this.virtualParams = virtualParams;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PrepareData [description=" + description + "]";
    }

}
