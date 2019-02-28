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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import com.alipay.test.acts.constant.ActsYamlConstants;
import com.alipay.test.acts.object.generator.ObjectGenerator;
import org.junit.Assert;

import java.lang.reflect.Type;

/**
 *
 * @author baishuo.lp
 * @version $Id: StringTypeHandler.java, v 0.1 2015年8月14日 下午5:10:35 baishuo.lp Exp $
 */
public class StringTypeGenerator implements ObjectGenerator {

    private final static String lineSeparator = SystemUtils.LINE_SEPARATOR;

    @Override
    public boolean isSimpleType() {
        return true;
    }

    @Override
    public Object generateFieldObject(Class<?> clz, String fieldName, String referedCSVValue) {
        Assert.assertEquals(clz, String.class);
        if (StringUtils.equals("''", referedCSVValue)) {
            return "";
        }
        if (referedCSVValue.contains(ActsYamlConstants.LINESEPARATOR)) {
            referedCSVValue = referedCSVValue.replace(ActsYamlConstants.LINESEPARATOR,
                lineSeparator);
        }

        return referedCSVValue.trim();
    }

    @Override
    public String generateObjectValue(Object obj, String csvPath, boolean isSimple) {
        if (StringUtils.isEmpty(((String) obj))) {
            return "''";
        }

        return String.valueOf(obj);
    }

    @Override
    public Class<?> getItemClass(Type collectionItemType, Class<?> clz) {
        return null;
    }

    @Override
    public void setObjectValue(Object collectionObject, Object value, String originalValue,
                               int index) {
    }

}
