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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author qingqin
 * @version $Id: MethodUtilsTest.java, v 0.1 2019年01月08日 下午3:13 qingqin Exp $
 */
public class MethodUtilsTest {

    @Test
    public void testFilterMethod() {
        List<Method> list = MethodUtils.filterMethod(
            Arrays.asList(MethodUtilsTest.class.getMethods()), new Class[] {}, void.class);
        Assert.assertEquals(list.size(), 5);
    }

    @Test
    public void testFindMethodsByName() {

        List list = MethodUtils.findMethodsByName(MethodUtilsTest.class, "testFindMethodsByName");
        Assert.assertEquals(list.size(), 1);
    }

}