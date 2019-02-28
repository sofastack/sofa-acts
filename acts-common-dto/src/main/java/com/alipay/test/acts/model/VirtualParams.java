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

import java.util.HashMap;
import java.util.Map;

/**
 * Custom parameter object
 * @author tantian.wc
 * @version $Id: VirtualArgs.java, v 0.1 2015年10月13日 上午10:20:27 tantian.wc Exp $
 */
public class VirtualParams extends TestUnit {

    /** Custom parameter */
    public Map<String, VirtualObject> params = new HashMap<String, VirtualObject>();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static VirtualParams getInstance() {
        return new VirtualParams();
    }

    /**
     * Constructor.
     */
    public VirtualParams() {
        super();
    }

    /**
     * Gets params.
     *
     * @return the params
     */
    public Map<String, VirtualObject> getParams() {
        return params;
    }

    /**
     * Sets params.
     *
     * @param params the params
     */
    public void setParams(Map<String, VirtualObject> params) {
        this.params = params;
    }

    /**
     * Add param.
     *
     * @param key the key
     * @param obj the obj
     */
    public void addParam(String key, Object obj) {
        params.put(key, new VirtualObject(obj));
    }

    /**
     * Gets by para name.
     *
     * @param paraName the para name
     * @return the by para name
     */
    public Object getByParaName(final String paraName) {
        if (params == null || null == paraName) {
            return null;
        }
        return params.get(paraName);
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VirtualParams [params=" + params + "]";
    }

}
