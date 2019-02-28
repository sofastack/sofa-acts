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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Map
 * @author zhiyuan.lzy
 * @version $Id: MapTypeHandler.java, v 0.1 2014年12月16日 下午2:52:48 zhiyuan.lzy Exp $
 */
@SuppressWarnings("unchecked")
public class MapTypeGenerator implements ObjectGenerator {

    private static final Log    LOG       = LogFactory.getLog(MapTypeGenerator.class);

    private static final String SEPERATOR = ":";

    @Override
    public boolean isSimpleType() {
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object generateFieldObject(Class<?> clz, String fieldName, String referedCSVValue) {
        return new HashMap();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public String generateObjectValue(Object obj, String csvPath, boolean isSimple) {
        String collectionString = "";

        if (((Map) obj).size() == 0) {
            collectionString = "@element_empty@";
            return collectionString;
        }

        if (isSimple) {
            for (Object o : ((Map) obj).keySet()) {
                collectionString = collectionString + String.valueOf(o) + ":"
                                   + String.valueOf(((Map) obj).get(o)) + ";";
            }
        } else {
            String reCsvPath = StringUtils.substringAfterLast(csvPath, "/");
            String tempCollectionString = reCsvPath + "@";
            for (Object o : ((Map) obj).keySet()) {
                try {
                    int index = CSVHelper.insertObjDataAndReturnIndex(((Map) obj).get(o), csvPath);
                    collectionString = collectionString + String.valueOf(o) + ":"
                                       + tempCollectionString + String.valueOf(index) + ";";
                } catch (Exception e) {
                    ActsLogUtil.fail(LOG, "Failed to convert Map to String!", e);
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

        if (genericType == null) {
            ActsLogUtil.fail(LOG, "Failed to parse type!");
        }

        Type[] typeArguments = genericType.getActualTypeArguments();
        if (typeArguments.length != 2) {
            ActsLogUtil
                .fail(LOG, "Failed to parse, typeArguments.length = " + typeArguments.length);
        }
        try {
            return (Class<?>) typeArguments[1];
        } catch (Exception e) {
            throw new RuntimeException("Not support T type!");
        }

    }

    @SuppressWarnings("rawtypes")
    @Override
    public void setObjectValue(Object collectionObject, Object value, String originalValue,
                               int index) {
        String[] valueParts = originalValue.split(SEPERATOR, 2);
        if (valueParts.length == 0) {
            ActsLogUtil.fail(LOG, "Invalid format!");
        }

        if (collectionObject instanceof java.util.Map) {
            ((Map) collectionObject).put(valueParts[0], valueParts[1]);
        } else {
            ActsLogUtil.fail(LOG,
                "Type of [" + collectionObject + "] is " + collectionObject.getClass()
                        + ", but List is required!");
        }
    }
}
