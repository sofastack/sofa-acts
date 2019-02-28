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
package com.alipay.test.acts.setter;

import java.util.Map;

/**
 * Set object flag's tool class
 * @author tantian.wc
 * @version $Id: FlagSetter.java, v 0.1 2015年10月13日 上午10:30:38 tantian.wc Exp $
 */
public class FlagSetter {

    /** Current class name  */
    String              clazzName;
    /** Flag of the field in the current class */
    Map<String, String> fieldFlags;

    /**
     *
     * @param clazzName
     * @param fieldFlags
     */
    public FlagSetter(String clazzName, Map<String, String> fieldFlags) {
        super();
        this.clazzName = clazzName;
        this.fieldFlags = fieldFlags;
    }

    /**
     * Get FlagSetter instance
     * @param clazzName
     * @param fieldFlags
     * @return
     */
    public static FlagSetter getFlagSetter(String clazzName, Map<String, String> fieldFlags) {
        FlagSetter flagSetter = new FlagSetter(clazzName, fieldFlags);
        return flagSetter;
    }

    /**
     * Set flag setter.
     *
     * @param field the field
     * @param value the value
     * @return the flag setter
     */
    public FlagSetter set(String field, String value) {
        fieldFlags.put(field, value);
        return this;
    }

    /**
     * Gets field flags.
     *
     * @return the field flags
     */
    public Map<String, String> getFieldFlags() {
        return fieldFlags;
    }

    /**
     * Sets field flags.
     *
     * @param fieldFlags the field flags
     */
    public void setFieldFlags(Map<String, String> fieldFlags) {
        this.fieldFlags = fieldFlags;
    }

    /**
     * Gets clazz name.
     *
     * @return the clazz name
     */
    public String getClazzName() {
        return clazzName;
    }

    /**
     * Sets clazz name.
     *
     * @param clazzName the clazz name
     */
    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }
}
