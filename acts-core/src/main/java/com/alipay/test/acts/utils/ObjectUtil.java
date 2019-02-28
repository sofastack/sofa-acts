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
package com.alipay.test.acts.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.aop.framework.AopProxyUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.enums.EnumUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author jie.peng
 * @version $Id: ObjectUtil.java, v 0.1 2015-04-07 jie.peng 下午08:32:28  Exp $
 */
@SuppressWarnings("unchecked")
public class ObjectUtil {

    private static final Log log = LogFactory.getLog(ObjectUtil.class);

    /**
     * Parse the target object from a string
     * 
     * @param source Source string to be parsed, The format is:billKeyName=testData|status=O</br>
     * @param obj
     * @return
     */
    public static Object restoreObject(String source, Object obj) {

        if (StringUtils.isBlank(source)) {
            return obj;
        }
        String[] keyValues = StringUtils.split(source, "|");
        Class clazz = obj.getClass();
        for (String keyValue : keyValues) {

            String[] detail = keyValue.split("=", 2);
            String methodName = "set" + StringUtils.capitalize(detail[0].trim());

            Method method;
            try {
                method = clazz.getMethod(methodName, String.class);
                method.setAccessible(true);
                method.invoke(obj, detail[1]);
            } catch (Exception e) {
                log.error("An exception occurred when parsing domain objects,source=" + source, e);
            }
        }
        return obj;
    }

    /**
     * 
     * 
     * @param methodName
     * @param testedObj
     * @return
     */
    public static Method findMethod(String methodName, Object testedObj) {

        Class<?>[] clazzes = AopProxyUtils.proxiedUserInterfaces(testedObj);

        for (Class<?> clazz : clazzes) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (StringUtils.equals(method.getName(), methodName)) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * Parse the target object from a string
     * 
     * @param source Source string to be parsed, The format is:billKeyName=testData|status=O</br>
     * @param obj
     * @return
     */
    public static Object load(String source, Object obj) {

        if (StringUtils.isBlank(source)) {
            return obj;
        }
        String[] keyValues = StringUtils.split(source, "|");
        for (String keyValue : keyValues) {
            String[] detail = keyValue.split("=", 2);
            setValue(obj, detail[0].trim(), detail[1]);
        }
        return obj;
    }

    /**
     * Writes the specified value for the specified property <code>propertyName</code> of the specified object <code>obj</code>
     * 
     * @param obj
     * @param propertyName
     * @param value
     */
    public static void setValue(Object obj, String propertyName, String value) {
        if (null == obj) {
            return;
        }
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (!StringUtils.equals(propertyName, field.getName())
                || field.getClass().isPrimitive()) {
                continue;
            }
            field.setAccessible(true);
            try {
                Class propertyClazz = field.getType();
                if (propertyClazz.isInstance(new String())) {
                    field.set(obj, value);
                } else if (propertyClazz.isInstance(Boolean.TRUE)) {
                    field.set(obj, Boolean.valueOf(value));
                } else if (propertyClazz.isEnum()) {
                    //no checked
                    field.set(obj, EnumUtils.getEnum(propertyClazz, value));
                } else if (propertyClazz.isInstance(new Integer(1))) {
                    field.set(obj, new Integer(value));
                } else if ("int".equals(propertyClazz.getName())) {
                    field.set(obj, Integer.parseInt(value));
                } else if (propertyClazz.isInstance(new HashSet<String>())) {
                    Set<String> propertyvalue = new HashSet<String>();
                    for (int i = 0; i < value.length(); i++) {
                        propertyvalue.add(value + String.valueOf(i));
                    }
                    field.set(obj, propertyvalue);
                }
            } catch (Exception e) {

            }
        }
    }

    /**
     * Parse the list from a string description
     * @param source
     * @return
     */
    public static List<String> resolveList(String source) {
        List<String> list = new ArrayList<String>();
        if (StringUtils.isBlank(source)) {
            return list;
        }
        String[] elements = StringUtils.split(source, "|");
        for (String each : elements) {
            list.add(each);
        }
        return list;
    }

    /**
     * Parse the list from the string description, the separator can be customized
     * 
     * @param source
     * @param separater 
     * @return
     */
    public static List<String> resolveList(String source, String separater) {
        List<String> list = new ArrayList<String>();
        if (StringUtils.isBlank(source)) {
            return list;
        }
        String[] elements = StringUtils.split(source, separater);
        for (String each : elements) {
            list.add(each);
        }
        return list;
    }

    /**
     * Copy the corresponding attribute from the object to map, the key of the map is the attribute name
     * @param obj
     * @param params
     * @return
     */

    public static Map<String, String> copyProperties(Object obj, String... params) {
        Map<String, String> result = new HashMap<String, String>();
        if (null == obj || null == params || params.length == 0) {
            return result;
        }

        Class clazz = obj.getClass();
        for (String param : params) {
            String methodName = "get" + StringUtils.capitalize(param);

            Method method;
            try {
                method = clazz.getMethod(methodName);
                method.setAccessible(true);
                Object invokeResult = method.invoke(obj);

                if (invokeResult instanceof String) {
                    result.put(param, (String) invokeResult);
                } else {
                    result.put(param, invokeResult.toString());
                }

            } catch (Exception e) {
                log.error("Exception obtaining Object attribute, current param=" + param, e);
            }
        }
        return result;
    }

    /**
     * The object's serialized string
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        try {
            return JSONObject.toJSONString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }

    public static <T> T getDOByClass(List<Object> objs, Class<T> clazz) {
        for (Object obj : objs) {
            if (obj.getClass() == clazz) {
                return (T) obj;
            }
        }
        return null;
    }
}
