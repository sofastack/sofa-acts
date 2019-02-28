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
package com.alipay.test.acts.object.manager;

import com.alipay.test.acts.log.ActsLogUtil;
import com.alipay.test.acts.object.generator.ObjectGenerator;
import com.alipay.test.acts.object.generator.impl.ArrayListTypeGenerator;
import com.alipay.test.acts.object.generator.impl.ArrayTypeGenerator;
import com.alipay.test.acts.object.generator.impl.BigDecimalTypeGenerator;
import com.alipay.test.acts.object.generator.impl.CurrencyTypeGenerator;
import com.alipay.test.acts.object.generator.impl.DateTypeGenerator;
import com.alipay.test.acts.object.generator.impl.EnumTypeGenerator;
import com.alipay.test.acts.object.generator.impl.ListTypeGenerator;
import com.alipay.test.acts.object.generator.impl.MapTypeGenerator;
import com.alipay.test.acts.object.generator.impl.SetTypeGenerator;
import com.alipay.test.acts.object.generator.impl.StringTypeGenerator;
import com.alipay.test.acts.util.CSVApisUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author guangming.zhong
 * @version $Id: ObjectTypeControler.java, v 0.1 2011-12-12 上午09:29:59 guangming.zhong Exp $
 */
public class ObjectTypeManager {

    private static final Log                   LOG              = LogFactory
                                                                    .getLog(ObjectTypeManager.class);

    private final Map<String, ObjectGenerator> objectHandlers   = new HashMap<String, ObjectGenerator>();

    private final Set<String>                  simpleHandlerSet = new HashSet<String>();

    private final List<String>                 simpleClassType  = new ArrayList<String>();

    public ObjectTypeManager() {

        objectHandlers.put("enum", new EnumTypeGenerator());
        objectHandlers.put(java.lang.String.class.getName(), new StringTypeGenerator());
        objectHandlers.put(java.util.Date.class.getName(), new DateTypeGenerator());

        objectHandlers.put(java.util.List.class.getName(), new ListTypeGenerator());
        objectHandlers.put(java.util.ArrayList.class.getName(), new ArrayListTypeGenerator());
        objectHandlers.put(java.util.Map.class.getName(), new MapTypeGenerator());
        objectHandlers.put(java.util.HashMap.class.getName(), new MapTypeGenerator());
        objectHandlers.put(java.util.Set.class.getName(), new SetTypeGenerator());
        objectHandlers.put(java.util.HashSet.class.getName(), new SetTypeGenerator());
        objectHandlers.put("ARRAY", new ArrayTypeGenerator());
        objectHandlers.put(java.util.Currency.class.getName(), new CurrencyTypeGenerator());
        objectHandlers.put(java.math.BigDecimal.class.getName(), new BigDecimalTypeGenerator());

        for (Entry<String, ObjectGenerator> entry : objectHandlers.entrySet()) {
            if (entry.getValue().isSimpleType()) {
                simpleHandlerSet.add(entry.getKey());
            }
        }

        simpleClassType.add("java.lang.Integer");
        simpleClassType.add("java.lang.Float");
        simpleClassType.add("java.lang.Double");
        simpleClassType.add("java.lang.Long");
        simpleClassType.add("java.lang.Short");
        simpleClassType.add("java.lang.Byte");
        simpleClassType.add("java.lang.Boolean");
        simpleClassType.add("java.lang.Character");
        simpleClassType.add("java.util.Properties");
    }

    public boolean isSimpleType(Class<?> clz) {
        boolean result = false;

        String simpleKey = null;
        if (clz.isEnum() || clz.getName().toLowerCase().contains("enum")) {
            simpleKey = "enum";
        } else {
            simpleKey = clz.getName();
        }

        if (clz.isPrimitive() || simpleHandlerSet.contains(simpleKey)
            || simpleClassType.contains(simpleKey)) {
            result = true;
        }
        return result;
    }

    public Object getSimpleObject(Class<?> clz, String value, String fieldName, String className) {
        ObjectGenerator handler = null;

        if (clz.isPrimitive() || simpleClassType.contains(className)) {
            return getPrimitiveObject(value, className);
        }

        if (clz.isEnum()) {
            handler = objectHandlers.get("enum");
        } else {
            handler = objectHandlers.get(clz.getName());
        }

        Object result = null;
        if (handler != null) {
            result = handler.generateFieldObject(clz, fieldName, value);
        }
        return result;
    }

    public String getSimpleObjValue(Class<?> clz, Object obj, String fieldName) {
        ObjectGenerator handler = null;

        if (clz.isPrimitive() || simpleClassType.contains(clz.getName())) {
            return String.valueOf(obj);
        }

        if (clz.isEnum()) {
            handler = objectHandlers.get("enum");
        } else {
            handler = objectHandlers.get(clz.getName());
        }

        String result = null;
        if (handler != null) {
            result = handler.generateObjectValue(obj, null, true);
        }
        return result;
    }

    /*
     * java primitive object
     */
    protected Object getPrimitiveObject(String value, String primitiveType) {

        Object result = null;

        if (StringUtils.equals("int", primitiveType)
            || StringUtils.equals("java.lang.Integer", primitiveType)) {
            result = Integer.parseInt(value);
        } else if (StringUtils.equals("float", primitiveType)
                   || StringUtils.equals("java.lang.Float", primitiveType)) {
            result = Float.parseFloat(value);
        } else if (StringUtils.equals("double", primitiveType)
                   || StringUtils.equals("java.lang.Double", primitiveType)) {
            result = Double.parseDouble(value);
        } else if (StringUtils.equals("long", primitiveType)
                   || StringUtils.equals("java.lang.Long", primitiveType)) {
            result = Long.parseLong(value);
        } else if (StringUtils.equals("short", primitiveType)
                   || StringUtils.equals("java.lang.Short", primitiveType)) {
            result = Short.parseShort(value);
        } else if (StringUtils.equals("byte", primitiveType)
                   || StringUtils.equals("java.lang.Byte", primitiveType)) {
            result = Byte.parseByte(value);
        } else if (StringUtils.equals("boolean", primitiveType)
                   || StringUtils.equals("java.lang.Boolean", primitiveType)) {
            result = Boolean.parseBoolean(value);
        } else if (StringUtils.equals("char", primitiveType)
                   || StringUtils.equals("java.lang.Character", primitiveType)) {
            result = value.charAt(0);
        } else {
            ActsLogUtil
                .error(LOG, String.format("Failed to parse %s . return null", primitiveType));
        }

        return result;
    }

    /*
     * object collection type
     */
    public boolean isCollectionType(Class<?> clz) {
        boolean result = false;

        String collectionKey = null;
        if (clz.isArray()) {
            collectionKey = "ARRAY";
        } else {
            collectionKey = clz.getName();
        }

        if (!simpleHandlerSet.contains(collectionKey) && objectHandlers.containsKey(collectionKey)) {
            result = true;
        }
        return result;
    }

    /*
     * map or collection
     */
    public Class<?> getCollectionItemClass(Type genericType, Class<?> clz) {

        ObjectGenerator generator = getObjectGenerator(clz);

        Class<?> result = null;

        if (generator != null) {
            if (Map.class.isAssignableFrom(clz)) {
                result = CSVApisUtil.getClass(genericType, 1);

            } else {
                result = CSVApisUtil.getClass(genericType, 0);

            }
        }
        return result;
    }

    public Object getCollectionObject(Class<?> clz) {
        ObjectGenerator generator = getObjectGenerator(clz);

        Object result = null;
        if (generator != null) {
            result = generator.generateFieldObject(clz, null, null);
        }
        return result;
    }

    public void setCollectionObjectValue(Object collectionObject, Object value,
                                         String originalValue, int index, Class<?> clz) {
        ObjectGenerator generator = getObjectGenerator(clz);

        if (generator != null) {
            generator.setObjectValue(collectionObject, value, originalValue, index);
        }
    }

    public String getCollectionObjectString(Class<?> clz, Object collectionObject,
                                            boolean isSimple, String csvPath) {

        ObjectGenerator generator = getObjectGenerator(clz);

        String result = null;
        if (generator != null) {
            result = generator.generateObjectValue(collectionObject, csvPath, isSimple);
        }
        return result;
    }

    private ObjectGenerator getObjectGenerator(Class<?> clz) {
        ObjectGenerator handler = null;
        if (clz.isArray()) {
            handler = objectHandlers.get("ARRAY");
        } else {
            handler = objectHandlers.get(clz.getName());
        }
        return handler;
    }
}
