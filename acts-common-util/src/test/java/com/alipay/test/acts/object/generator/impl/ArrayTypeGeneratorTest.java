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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * Created by yanzhu on 2019/1/15.
 */
public class ArrayTypeGeneratorTest {

    @Test
    public void testGenerateObjectValue() {
        ArrayTypeGenerator arrayTypeGenerator = new ArrayTypeGenerator();
        String test[] = { "test" };
        Assert.assertEquals(
            arrayTypeGenerator.generateObjectValue(test, "BusinessActionContext/List_Object.csv",
                true).toString(), "test");
    }

    @Test
    public void testGetItemClass() {
        ArrayTypeGenerator arrayListTypeGenerator = new ArrayTypeGenerator();

        ArrayList<String> test = new ArrayList<String>();

        Class<?> cls = arrayListTypeGenerator.getItemClass(test.getClass().getGenericSuperclass(),
            String.class);
        Assert.assertEquals(cls, null);

        Class<?> cls1 = arrayListTypeGenerator.getItemClass(
            test.getClass().getGenericInterfaces()[0], String.class);
        Assert.assertEquals(cls1, null);
    }
}
