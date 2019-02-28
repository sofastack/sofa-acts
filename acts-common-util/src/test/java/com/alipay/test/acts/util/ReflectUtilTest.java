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
package com.alipay.test.acts.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by yanzhu on 2019/1/13.
 */
public class ReflectUtilTest {

    private String    test;

    public char       test1;

    public double     test2;

    public long       test3;

    public Date       test4;

    public short      test5;

    public byte       test6;

    public float      test7;

    public boolean    test8;

    public Currency   test9;

    public BigDecimal test10;

    @Test
    public void testcanInstantiate() {
        Assert.assertTrue(!ReflectUtil.canInstantiate(ReflectUtilTest.class));
    }

    @Test
    public void testInstantiateClass() throws InstantiationException, IllegalAccessException,
                                      ClassNotFoundException {
        Assert.assertTrue(ReflectUtil.instantiateClass(ReflectUtil.class).toString()
            .contains("com.alipay.test.acts.util.ReflectUtil"));
    }

    @Test
    public void testGetField() throws NoSuchFieldException {

        Assert.assertEquals(ReflectUtil.getField(ReflectUtilTest.class, "test").toString(),
            "private java.lang.String com.alipay.test.acts.util.ReflectUtilTest.test");

        try {
            Assert.assertEquals(ReflectUtil.getField(ReflectUtilTest.class, "yyyy").toString(),
                "private java.lang.String com.alipay.test.acts.util.ReflectUtilTest.test");

        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("yyyy"));
        }
    }

    @Test
    public void test1GetField() throws NoSuchFieldException {
        ReflectUtilTest test = new ReflectUtilTest();
        Assert.assertEquals(ReflectUtil.getField(test, "test").toString(),
            "private java.lang.String com.alipay.test.acts.util.ReflectUtilTest.test");

        try {
            Assert.assertEquals(ReflectUtil.getField(test, "yyyy").toString(),
                "private java.lang.String com.alipay.test.acts.util.ReflectUtilTest.test");

        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("yyyy"));
        }
    }

    @Test
    public void testFieldValue() {
        ReflectUtilTest test = new ReflectUtilTest();
        ReflectUtil.setFieldValue(test, "test", "123");
        Assert.assertEquals(ReflectUtil.getFieldValue(test, "test"), "123");
    }

    @Test
    public void testValueByCorrectType() {
        HashMap<String, Object> test = new HashMap<String, Object>();
        for (Field field : ReflectUtilTest.class.getFields()) {

            Object tt = ReflectUtil.valueByCorrectType(null, field, "2");
            test.put(field.toString(), tt);
        }

        Assert.assertEquals(
            test.get("public double com.alipay.test.acts.util.ReflectUtilTest.test2").toString(),
            "2.0");
    }

    @Test
    public void testFindMethod() {
        Assert.assertEquals(ReflectUtil.findMethod(ReflectUtilTest.class, "testValueByCorrectType")
            .getName().toString(), "testValueByCorrectType");
    }

    @Test
    public void test1findMethod() {
        Class<?>[] parameterTypes = { Class.class, String.class };
        Assert.assertEquals(ReflectUtil.findMethod(ReflectUtil.class, "getField", parameterTypes)
            .getName().toString(), "getField");
    }

    /***
     * Test method for {@link ReflectUtil#invokeMethod(Object, String, Object[])} invokeMethod}
     */
    @Test
    public void testInvokeMethod() {
        ReflectUtilTest test = new ReflectUtilTest();
        Assert.assertNull(ReflectUtil.invokeMethod(test, "say", null));

        try {
            Assert.assertNull(ReflectUtil.invokeMethod(test, "say1", null));
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("say1"));
        }
    }

    public String say() {
        return null;
    }

    public String syso(String i, String j, String k) {
        return i + j + k;
    }

    /***
     * Test method for {@link ReflectUtil#invokeMethod(Class, String, Object...)} invokeMethod}
     */
    @Test
    public void testInvokeMethodN() {
        Assert.assertEquals(ReflectUtil.invokeMethod(ReflectUtilTest.class, "syso", "1", "2", "3"),
            "123");

        try {
            Assert.assertEquals(
                ReflectUtil.invokeMethod(ReflectUtilTest.class, "syso1", "1", "2", "3"), "123");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("syso1"));
        }
    }

    /***
     * Test method for {@link ReflectUtil#invokeMethod(Class, String, Class[], Object[])} invokeMethod}
     */
    @Test
    public void testInvokeMethodT() {
        Assert.assertEquals(
            ReflectUtil.invokeMethod(ReflectUtilTest.class, "syso", new Class[] { String.class,
                    String.class, String.class }, new Object[] { "1", "2", "3" }), "123");

        try {
            Assert
                .assertEquals(
                    ReflectUtil.invokeMethod(ReflectUtilTest.class, "syso1", new Class[] {
                            String.class, String.class, String.class }, new Object[] { "1", "2",
                            "3" }), "123");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("syso1"));
        }
    }

    /***
     * Test method for {@link ReflectUtil#invokeMethod(Object, String, Class[], Object[])}
     */
    @Test
    public void testInvokeMethodO() {
        Assert.assertEquals(
            ReflectUtil.invokeMethod(new ReflectUtilTest(), "syso", new Class[] { String.class,
                    String.class, String.class }, new Object[] { "1", "2", "3" }), "123");
        try {
            Assert
                .assertEquals(
                    ReflectUtil.invokeMethod(new ReflectUtilTest(), "syso1", new Class[] {
                            String.class, String.class, String.class }, new Object[] { "1", "2",
                            "3" }), "123");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("syso1"));
        }
    }

}
