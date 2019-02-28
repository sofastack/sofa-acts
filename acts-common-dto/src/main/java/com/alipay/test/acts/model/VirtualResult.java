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
 * Expected result
 * @author tantian.wc
 * @version $Id: VirtualResult.java, v 0.1 2015年10月13日 上午10:29:05 tantian.wc Exp $
 */
public class VirtualResult extends TestUnit {

    /** result object */
    private VirtualObject result;

    /**
     * Constructor.
     */
    public VirtualResult() {
        this.result = new VirtualObject();
    }

    /**
     * Gets virtual object.
     *
     * @return the virtual object
     */
    public VirtualObject getVirtualObject() {
        return result;
    }

    /**
     * Constructor.
     *
     * @param resultObj the result obj
     */
    public VirtualResult(Object resultObj) {

        this.result = new VirtualObject(resultObj);

    }

    /**
     * Gets result clazz.
     *
     * @return the result clazz
     */
    public String getResultClazz() {
        if (result == null) {
            return null;
        }
        return result.getObjClass();
    }

    /**
     * Sets result clazz.
     *
     * @param resultClazz the result clazz
     */
    public void setResultClazz(String resultClazz) {
        if (this.result == null) {
            this.result = new VirtualObject();

        }
        result.setObjClass(resultClazz);
    }

    /**
     * Gets result obj.
     *
     * @return the result obj
     */
    public Object getResultObj() {
        if (result == null) {
            return null;
        }
        return result.getObject();
    }

    /**
     * Gets result.
     *
     * @return the result
     */
    public VirtualObject getResult() {
        return result;
    }

    /**
     * Sets result.
     *
     * @param result the result
     */
    public void setResult(VirtualObject result) {
        this.result = result;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VirtualResult [result=" + result + "]";
    }

}
