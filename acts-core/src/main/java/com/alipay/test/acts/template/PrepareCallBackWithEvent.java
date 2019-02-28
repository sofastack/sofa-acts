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
package com.alipay.test.acts.template;

import java.util.List;

import com.alipay.test.acts.model.VirtualArgs;
import com.alipay.test.acts.model.VirtualDataSet;
import com.alipay.test.acts.model.VirtualEventSet;

/**
 * CallBack with message check
 * 
 * @author fenglei.fl
 * @version $Id: PrepareCallBackWithEvent.java, v 0.1 2015年1月12日 下午12:17:48
 *          fenglei.fl Exp $
 */
public class PrepareCallBackWithEvent implements PrepareCallBack {
    /**
     * Prepare expect event set virtual event set.
     *
     * @param args the args
     * @param depDataSet the dep data set
     * @param expectResult the expect result
     * @return the virtual event set
     */
    public VirtualEventSet prepareExpectEventSet(VirtualArgs args, VirtualDataSet depDataSet,
                                                 Object expectResult) {
        return null;
    }

    /**
     * Prepare dep data set virtual data set.
     *
     * @return the virtual data set
     */
    @Override
    public VirtualDataSet prepareDepDataSet() {
        return null;
    }

    /**
     * Prepare args virtual args.
     *
     * @param depDataSet the dep data set
     * @return the virtual args
     */
    @Override
    public VirtualArgs prepareArgs(VirtualDataSet depDataSet) {
        return null;
    }

    /**
     * Prepare expect data set virtual data set.
     *
     * @param args the args
     * @param depDataSet the dep data set
     * @return the virtual data set
     */
    @Override
    public VirtualDataSet prepareExpectDataSet(VirtualArgs args, VirtualDataSet depDataSet) {
        return null;
    }

    /**
     * Prepare expect result object.
     *
     * @param args the args
     * @param depDataSet the dep data set
     * @return the object
     */
    @Override
    public Object prepareExpectResult(VirtualArgs args, VirtualDataSet depDataSet) {
        return null;
    }

    /**
     * Prepare expect invoke out args virtual args.
     *
     * @return the virtual args
     */
    @Override
    public VirtualArgs prepareExpectInvokeOutArgs() {
        return null;
    }

    /**
     * Prepare component list.
     *
     * @return the list
     */
    public List<String> prepareComponent() {
        return null;
    }

    /**
     * Execute component list.
     *
     * @return the list
     */
    public List<String> executeComponent() {
        return null;
    }

    /**
     * Check component list.
     *
     * @return the list
     */
    public List<String> checkComponent() {
        return null;
    }

    /**
     * Clear component list.
     *
     * @return the list
     */
    public List<String> clearComponent() {
        return null;
    }

}
