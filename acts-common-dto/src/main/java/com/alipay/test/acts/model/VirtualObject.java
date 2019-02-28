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

import java.util.LinkedHashMap;
import java.util.Map;

import com.alipay.test.acts.setter.FlagSetter;

/**
 * virtual object
 * @author tantian.wc
 * @version $Id: VirtualObject.java, v 0.1 2015年10月12日 下午9:13:04 tantian.wc Exp $
 */
public class VirtualObject extends TestNode {

    /** desc  */
    public String                           description;
    /** class name */
    public String                           objClass;
    /** name of csv template */
    public String                           objBaseName;
    /** desc */
    public String                           objBaseDesc;
    /** Object instance */
    public Object                           object;
    /** flag,<class, <field name, flag value>> */
    public Map<String, Map<String, String>> flags = new LinkedHashMap<String, Map<String, String>>();

    /**
     * Constructor.
     */
    public VirtualObject() {

    }

    /**
     * Constructor.
     *
     * @param obj the obj
     */
    public VirtualObject(Object obj) {
        if (obj != null) {
            this.objClass = obj.getClass().getName();
        } else {
            this.objClass = null;
        }
        this.object = obj;

    }

    /**
     * Constructor.
     *
     * @param obj the obj
     * @param desc the desc
     */
    public VirtualObject(Object obj, String desc) {
        if (obj != null) {
            this.objClass = obj.getClass().getName();
            this.objBaseName = obj.getClass().getSimpleName();
        }
        this.object = obj;
        this.objBaseDesc = desc;
    }

    /**
     * set can be called repeatedly through setters  getFlagSetter(A.class).set("a","Y").set("b","N");
     * @param clazz
     * @return
     */
    public FlagSetter getFlagSetter(Class<?> clazz) {
        if (flags == null) {
            flags = new LinkedHashMap<String, Map<String, String>>();
        }
        if (flags.get(clazz.getName()) == null) {
            flags.put(clazz.getName(), new LinkedHashMap<String, String>());
        }

        return FlagSetter.getFlagSetter(clazz.getName(), flags.get(clazz.getName()));
    }

    /**
     * Gets obj class.
     *
     * @return the obj class
     */
    public String getObjClass() {
        return objClass;
    }

    /**
     * Sets obj class.
     *
     * @param objClass the obj class
     */
    public void setObjClass(String objClass) {
        this.objClass = objClass;
    }

    /**
     * Gets obj base name.
     *
     * @return the obj base name
     */
    public String getObjBaseName() {
        return objBaseName;
    }

    /**
     * Sets obj base name.
     *
     * @param objBaseName the obj base name
     */
    public void setObjBaseName(String objBaseName) {
        this.objBaseName = objBaseName;
    }

    /**
     * Gets obj base desc.
     *
     * @return the obj base desc
     */
    public String getObjBaseDesc() {
        return objBaseDesc;
    }

    /**
     * Sets obj base desc.
     *
     * @param objBaseDesc the obj base desc
     */
    public void setObjBaseDesc(String objBaseDesc) {
        this.objBaseDesc = objBaseDesc;
    }

    /**
     * Gets object.
     *
     * @return the object
     */
    public Object getObject() {
        return object;
    }

    /**
     * Sets object.
     *
     * @param object the object
     */
    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * Gets flags.
     *
     * @return the flags
     */
    public Map<String, Map<String, String>> getFlags() {
        return flags;
    }

    /**
     * Sets flags.
     *
     * @param flags the flags
     */
    public void setFlags(Map<String, Map<String, String>> flags) {
        this.flags = flags;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VirtualObject [description=" + description + ", objClass=" + objClass
               + ", objBaseName=" + objBaseName + ", objBaseDesc=" + objBaseDesc + ", object="
               + object + ", flags=" + flags + "]";
    }

}
