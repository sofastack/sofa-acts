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
package com.alipay.test.acts.object.generator.impl;

import com.alipay.test.acts.helper.CSVHelper;
import com.alipay.test.acts.log.ActsLogUtil;
import com.alipay.test.acts.object.generator.ObjectGenerator;
import com.alipay.test.acts.object.manager.ObjectTypeManager;
import com.google.common.reflect.TypeToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashSet;
import java.util.Set;

/**
 * Set
 * @author xiaoleicxl
 * @version $Id: SetTypeGenerator.java, v 0.1 2015年12月9日 下午12:47:41 xiaoleicxl Exp $
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class SetTypeGenerator implements ObjectGenerator {

    private static final Log LOG = LogFactory.getLog(SetTypeGenerator.class);

    @Override
    public boolean isSimpleType() {
        return false;
    }

    @Override
    public Object generateFieldObject(Class<?> clz, String fieldName, String referedCSVValue) {
        return new HashSet();
    }

    @Override
    public String generateObjectValue(Object obj, String csvPath, boolean isSimple) {
        String collectionString = "";

        if (((Set) obj).size() == 0) {
            collectionString = "@element_empty@";
            return collectionString;
        }

        if (isSimple) {
            for (Object o : (Set) obj) {
                collectionString = collectionString + o + ";";
            }
        } else {
            collectionString = csvPath + "@";
            for (Object o : (Set) obj) {

                try {
                    int index = CSVHelper.insertObjDataAndReturnIndex(o, csvPath);
                    collectionString = collectionString + index + ";";
                } catch (Exception e) {
                    ActsLogUtil.fail(LOG, "Cann't convert array to string!");
                }
            }
        }
        collectionString = collectionString.substring(0, collectionString.length() - 1);
        return collectionString;
    }

    @Override
    public Class<?> getItemClass(Type collectionItemType, Class<?> clz) {
        ParameterizedType genericType = null;
        if (collectionItemType instanceof ParameterizedType) {
            genericType = (ParameterizedType) collectionItemType;
        }

        Assert.assertNotNull("Failed to parse type!", genericType);

        Type[] typeArguments = genericType.getActualTypeArguments();

        Assert.assertEquals("Failed to parse, typeArguments.length = " + typeArguments.length,
            typeArguments.length, 1);

        if (typeArguments[0] instanceof ParameterizedType) {
            ObjectTypeManager subMng = new ObjectTypeManager();
            TypeToken<?> genericTypeToken = TypeToken.of(typeArguments[0]);
            Class innCls = genericTypeToken.getRawType();
            if (subMng.isSimpleType(innCls)) {
                return genericTypeToken.getRawType();
            } else if (subMng.isCollectionType(innCls)) {
                return innCls;
            }
        } else if (typeArguments[0] instanceof TypeVariable) {

            return Object.class;
        } else if (typeArguments[0] instanceof WildcardType) {

            return Object.class;
        } else {
            return (Class<?>) typeArguments[0];
        }

        return null;
    }

    @Override
    public void setObjectValue(Object collectionObject, Object value, String originalValue,
                               int index) {
        if (collectionObject instanceof java.util.Set) {
            ((Set) collectionObject).add(value);
        } else {
            ActsLogUtil.fail(LOG,
                "Type of [" + collectionObject + "] is " + collectionObject.getClass()
                        + ", but Set is required!");
        }
    }
}
