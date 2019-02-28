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

import com.alipay.test.acts.log.ActsLogUtil;
import com.alipay.test.acts.object.generator.ObjectGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * BigDecimal
 * @author xiaoleicxl
 * @version $Id: BigDecimalTypeGenerator.java, v 0.1 2015年12月9日 下午12:48:14 xiaoleicxl Exp $
 */
public class BigDecimalTypeGenerator implements ObjectGenerator {

    private static final Log LOG = LogFactory.getLog(BigDecimalTypeGenerator.class);

    @Override
    public boolean isSimpleType() {
        return true;
    }

    @Override
    public Object generateFieldObject(Class<?> clz, String fieldName, String referedCSVValue) {
        BigDecimal num = null;
        try {
            num = new BigDecimal(referedCSVValue);
        } catch (Exception e) {
            ActsLogUtil.fail(LOG, "Cann't parse value, and return null!", e);
        }
        return num;
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
