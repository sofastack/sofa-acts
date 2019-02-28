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

import com.alipay.test.acts.object.generator.ObjectGenerator;

import java.lang.reflect.Type;

/**
 * Money
 *
 * @author zhiyuan.lzy
 * @version $Id: MoneyTypeHandler.java, v 0.1 2014年12月16日 下午2:53:08 zhiyuan.lzy Exp $
 */
public class MoneyTypeGenerator implements ObjectGenerator {

    @Override
    public boolean isSimpleType() {
        return true;
    }

    @Override
    public Object generateFieldObject(Class<?> clz, String fieldName, String referedCSVValue) {
        return null;
    }

    @Override
    public String generateObjectValue(Object obj, String csvPath, boolean isSimple) {
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
