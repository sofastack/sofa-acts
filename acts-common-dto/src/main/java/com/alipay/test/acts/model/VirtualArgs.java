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
import java.util.Iterator;
import java.util.List;

/**
 * Virtual Args
 * @author tantian.wc
 * @version $Id: VirtualArgs.java, v 0.1 2015年10月13日 上午10:20:27 tantian.wc Exp $
 */
public class VirtualArgs extends TestUnit {

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static VirtualArgs getInstance() {
        return new VirtualArgs();
    }

    /** Ordered input list */
    public List<VirtualObject> inputArgs;

    /**
     * Constructor.
     */
    public VirtualArgs() {
        super();
    }

    /**
     * Add a arg
     * @param obj
     * @return
     */
    public VirtualArgs addArg(Object obj) {
        if (inputArgs == null) {
            inputArgs = new ArrayList<VirtualObject>();
        }
        inputArgs.add(new VirtualObject(obj));

        return this;
    }

    /**
     * Add one arg
     * @param obj parameter object
     * @param desc if the obj is obtained from base, need to set desc
     * @return
     */
    public VirtualArgs addArg(Object obj, String desc) {
        if (inputArgs == null) {
            inputArgs = new ArrayList<VirtualObject>();
        }
        inputArgs.add(new VirtualObject(obj, desc));

        return this;
    }

    /**
     * Add one arg
     * @param vo parameter object
     * @return
     */
    public VirtualArgs addArg(VirtualObject vo) {
        if (inputArgs == null) {
            inputArgs = new ArrayList<VirtualObject>();
        }
        inputArgs.add(vo);

        return this;
    }

    /**
     * Gets arg.
     *
     * @param i the i
     * @return the arg
     */
    public Object getArg(int i) {

        return inputArgs.get(i).getObject();

    }

    /**
     * Gets arg types.
     *
     * @return the arg types
     */
    public List<String> getArgTypes() {
        List<String> argTypes = new ArrayList<String>();

        if (null != inputArgs) {
            for (VirtualObject virtualObject : inputArgs) {
                argTypes.add(virtualObject.getObjClass());
            }
        }
        return argTypes;
    }

    /**
     * Gets input args.
     *
     * @return the input args
     */
    public List<Object> getInputArgs() {
        if (inputArgs == null) {
            return null;
        }
        List<Object> args = new ArrayList<Object>();
        for (VirtualObject virtualObject : inputArgs) {
            args.add(virtualObject.getObject());
        }
        return args;
    }

    /**
     * Gain virtual input args list.
     *
     * @return the list
     */
    public List<VirtualObject> gainVirtualInputArgs() {
        if (inputArgs == null) {
            return null;
        }
        List<VirtualObject> args = new ArrayList<VirtualObject>();
        for (VirtualObject virtualObject : inputArgs) {
            args.add(virtualObject);
        }
        return args;
    }

    /**
     * Gets virtual objects.
     *
     * @return the virtual objects
     */
    public List<VirtualObject> getVirtualObjects() {
        return inputArgs;
    }

    /**
     * Sets arg.
     *
     * @param index the index
     * @param obj the obj
     */
    public void setArg(int index, Object obj) {
        if (inputArgs == null) {
            inputArgs = new ArrayList<VirtualObject>();
        }
        inputArgs.set(index, new VirtualObject(obj));

    }

    /**
     * Sets arg.
     *
     * @param index the index
     * @param obj the obj
     * @param desc the desc
     */
    public void setArg(int index, Object obj, String desc) {
        VirtualObject voToAdd = new VirtualObject(obj, desc);
        setArg(index, voToAdd);

    }

    /**
     * Sets input args.
     *
     * @param inputArgs the input args
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setInputArgs(List<Object> inputArgs) {
        List args = new ArrayList();
        for (Iterator localIterator = inputArgs.iterator(); localIterator.hasNext();) {
            Object arg = localIterator.next();
            if (VirtualObject.class.isInstance(arg)) {
                args.add(arg);
            } else {
                args.add(new VirtualObject(arg));
            }
        }
        this.inputArgs = args;
    }

    /**
     * Sets virtual input args.
     *
     * @param inputArgs the input args
     */
    public void setVirtualInputArgs(List<VirtualObject> inputArgs) {
        List<VirtualObject> args = new ArrayList<VirtualObject>();
        for (Object arg : inputArgs) {
            args.add((VirtualObject) arg);
        }
        this.inputArgs = args;
    }

    /**
     * Sets input args.
     *
     * @param inputArgs the input args
     * @param desc the desc
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setInputArgs(List<Object> inputArgs, List<String> desc) {
        List args = new ArrayList();
        if (inputArgs == null) {
            inputArgs = null;
            return;
        }
        for (int i = 0; i < inputArgs.size(); i++) {
            args.add(new VirtualObject(inputArgs.get(i), desc.get(i)));
        }
        this.inputArgs = args;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VirtualArgs [inputArgs=" + inputArgs + "]";
    }

}
