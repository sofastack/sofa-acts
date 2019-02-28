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
 * Expected exception
 * @author tantian.wc
 * @version $Id: VirtualException.java, v 0.1 2015年10月13日 上午10:24:39 tantian.wc Exp $
 */
public class VirtualException extends TestUnit {

    /** Exception object */
    private VirtualObject expectException;

    /**
     * Constructor.
     */
    public VirtualException() {
        this.expectException = new VirtualObject();
    }

    /**
     * Constructor.
     *
     * @param expectException the expect exception
     */
    public VirtualException(VirtualObject expectException) {
        this.expectException = new VirtualObject(expectException);
    }

    /**
     * Gets virtual object.
     *
     * @return the virtual object
     */
    public VirtualObject getVirtualObject() {

        return expectException;
    }

    /**
     * Gets exception class.
     *
     * @return the exception class
     */
    public String getExceptionClass() {
        if (expectException == null) {
            return null;
        }
        return expectException.getObjClass();
    }

    /**
     * Gets expect exception object.
     *
     * @return the expect exception object
     */
    public Object getExpectExceptionObject() {
        if (expectException == null) {
            return null;
        }
        return expectException.getObject();
    }

    /**
     * Sets expect exception.
     *
     * @param virtualObject the virtual object
     */
    public void setExpectException(VirtualObject virtualObject) {
        this.expectException = virtualObject;
    }

    /**
     * Gets expect exception.
     *
     * @return the expect exception
     */
    public VirtualObject getExpectException() {

        return expectException;
    }

    @Deprecated
    public void setExceptionClass(String exceptionClass) {
    }

    @Deprecated
    public void setExpectExceptionObject(Object expectException) {
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VirtualException [expectException=" + expectException + "]";
    }

}
