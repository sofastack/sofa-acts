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
package com.alipay.test.acts.utils.config.impl;

import java.util.Map;
import java.util.HashMap;

import com.alipay.test.acts.utils.config.Configration;

/**
 * 
 * @author tianzhu.wtzh
 * @version $Id: ConfigrationImpl.java, v 0.1 2015年10月20日 上午11:53:41 tianzhu.wtzh Exp $
 */
public class ConfigrationImpl implements Configration {

    private Map<String, String> atsConfigMap = new HashMap<String, String>();

    /**
     * Constructor.
     */
    public ConfigrationImpl() {
    }

    /**
     * Gets config.
     *
     * @return the config
     */
    public Map<String, String> getConfig() {
        //Clone the map to ensure security
        Map<String, String> map = new HashMap<String, String>();
        map.putAll(this.atsConfigMap);
        return map;
    }

    /**
     * Sets config.
     *
     * @param map the map
     */
    public void setConfig(Map<String, String> map) {
        this.atsConfigMap.putAll(map);
    }

    /**
     * Gets property value.
     *
     * @param key the key
     * @return the property value
     */
    public String getPropertyValue(String key) {
        return this.atsConfigMap.get(key);
    }

    /**
     * Set configuration properties
     *
     * @param key
     * @param value
     */
    public void setProperty(String key, String value) {
        this.atsConfigMap.put(key, value);
    }

    /**
     * Gets property value.
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the property value
     */
    public String getPropertyValue(String key, String defaultValue) {
        return this.atsConfigMap.get(key) == null ? defaultValue : this.atsConfigMap.get(key);
    }
}
