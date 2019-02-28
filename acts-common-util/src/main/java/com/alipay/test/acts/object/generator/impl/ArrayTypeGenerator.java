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
import com.alipay.test.acts.object.generator.ObjectGenerator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

/**
 *
 * @author baishuo.lp
 * @version $Id: ArrayTypeGenerator.java, v 0.1 2015年8月14日 下午6:32:45 baishuo.lp Exp $
 */
public class ArrayTypeGenerator implements ObjectGenerator {

    private static final Log LOG = LogFactory.getLog(ArrayTypeGenerator.class);

    @Override
    public boolean isSimpleType() {
        return false;
    }

    @Override
    public Object generateFieldObject(Class<?> clz, String fieldName, String referedCSVValue) {
        return Array.newInstance(clz.getComponentType(), 0);
    }

    @Override
    public String generateObjectValue(Object obj, String csvPath, boolean isSimple) {
        String collectionString = "";

        if (Array.getLength(obj) == 0) {
            collectionString = "@element_empty@";
            return collectionString;
        }

        if (isSimple) {
            for (int i = 0; i < Array.getLength(obj); i++) {
                collectionString = collectionString + String.valueOf(Array.get(obj, i)) + ";";
            }
        } else {
            String reCsvPath = StringUtils.substringAfterLast(csvPath, "/");
            collectionString = reCsvPath + "@";
            for (int i = 0; i < Array.getLength(obj); i++) {
                try {
                    int index = CSVHelper.insertObjDataAndReturnIndex(Array.get(obj, i), csvPath);
                    collectionString = collectionString + String.valueOf(index) + ";";
                } catch (Exception e) {
                    LOG.error("Cann't convert array to string!", e);
                    return null;
                }
            }
        }
        collectionString = collectionString.substring(0, collectionString.length() - 1);
        return collectionString;
    }

    @Override
    public Class<?> getItemClass(Type collectionItemType, Class<?> clz) {
        return clz.getComponentType();
    }

    @Override
    public void setObjectValue(Object collectionObject, Object value, String originalValue,
                               int index) {
        Array.set(collectionObject, index, value);
    }
}
