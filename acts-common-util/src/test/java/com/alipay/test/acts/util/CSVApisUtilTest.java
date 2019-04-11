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

import com.alipay.test.acts.model.PrepareData;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.Test;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.math.BigDecimal;

/**
 *
 * @author qingqin
 * @version $Id: CSVApisUtilTest.java, v 0.1 2019年01月14日 下午9:29 qingqin Exp $
 */
public class CSVApisUtilTest {
    private int aa;

    /***
     * Test method for {@link CSVApisUtil#paraClassToCscFile(String, ClassLoader, String)}
     */
    @Test
    public void testParaClassToCscFile() {
        try {
            Set<String> set = CSVApisUtil.paraClassToCscFile(PrepareData.class.getName(), this
                .getClass().getClassLoader(), getFileFromURL("/PrepareData/").getAbsolutePath());

            Assert.assertTrue(set.size() >= 0);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ClassNotFoundException);
        }
    }

    private File getFileFromURL(String path) {
        File folder = null;
        try {
            folder = new ClassPathResource(path).getFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }
        } catch (IOException e) {
            Assert.fail("error! ", e);
        }
        return folder;
    }

    /***
     * Test method for {@link CSVApisUtil#addSimpleValue(String, Class, List)}
     */
    @Test
    public void testAddSimpleValue() {
        List<String> values = new ArrayList<String>(1);

        // Date
        CSVApisUtil.addSimpleValue("", Date.class, values);
        Assert.assertEquals(values.get(0), "D");
        Assert.assertEquals(values.get(1), "today");
        values.clear();

        // Currency
        CSVApisUtil.addSimpleValue("", Currency.class, values);
        Assert.assertEquals(values.get(0), "Y");
        Assert.assertEquals(values.get(1), "CNY");
        values.clear();

        // CurrencyValue
        CSVApisUtil.addSimpleValue("currencyValue", Class.class, values);
        Assert.assertEquals(values.get(0), "Y");
        Assert.assertEquals(values.get(1), "156");
        values.clear();

        // Integer
        CSVApisUtil.addSimpleValue("", Integer.class, values);
        Assert.assertEquals(values.get(0), "Y");
        Assert.assertEquals(values.get(1), "0");
        values.clear();

        // BigDecimal
        CSVApisUtil.addSimpleValue("", BigDecimal.class, values);
        Assert.assertEquals(values.get(0), "Y");
        Assert.assertEquals(values.get(1), "0.001");
        values.clear();

        // Else
        CSVApisUtil.addSimpleValue("", Number.class, values);
        Assert.assertNotEquals(values, null);
        Assert.assertEquals(values.get(0), "Y");
        Assert.assertEquals(values.get(1), "null");
        values.clear();
    }

    /***
     * Test method for {@link CSVApisUtil#cutCsvName(String)}
     */
    @Test
    public void testCutCsvName() {
        String str = CSVApisUtil.cutCsvName(getFileFromURL().getAbsolutePath());
        Assert.assertEquals(str, "BusinessActionContext.csv");
    }

    /***
     * Test method for {@link CSVApisUtil#doProcess(Class, Class, Set, String, Set)}
     */
    @Test
    public void testDoProcess() {

    }

    /***
     * Test method for {@link CSVApisUtil#findTargetClsFields(Class)}
     */
    @Test
    public void testFindTargetClsFields() {
        Map<String, Field> map = CSVApisUtil.findTargetClsFields(CSVApisUtilTest.class);
        Assert.assertEquals(map.size(), 1);
        Assert.assertEquals(map.get("aa").getName(), "aa");
    }

    /***
     * Test method for {@link CSVApisUtil#getGenericCsvFileName(Class, Class, String)}
     */
    @Test
    public void testGetGenericCsvFileName() {
        String res = CSVApisUtil.getGenericCsvFileName(Map.class, Object.class, getFileFromURL()
            .getAbsolutePath());
        Assert.assertTrue(res.contains("Map_Object.csv"));
    }

    private File getFileFromURL() {
        File folder = null;
        try {
            folder = new ClassPathResource("BusinessActionContext/BusinessActionContext.csv")
                .getFile();
        } catch (IOException e) {
            Assert.fail("error! ", e);
        }
        return folder;
    }

    /***
     * Test method for {@link CSVApisUtil#getParameRawCls(Type)}
     */
    @Test
    public void testGetParameRawCls() {
        Map<String, String> map = new HashMap<String, String>(1);
        Type res = CSVApisUtil.getParameRawCls(map.getClass().getGenericSuperclass());
        Assert.assertEquals(res, AbstractMap.class);
    }

    /***
     * Test method for {@link CSVApisUtil#getTypeCount(Type)}
     */
    @Test
    public void testGetTypeCount() {

        Map<String, String> map = new HashMap<String, String>(1);
        int i = CSVApisUtil.getTypeCount(map.getClass().getGenericSuperclass());
        Assert.assertEquals(i, 2);

        List<String> list = new ArrayList<String>(1);
        i = CSVApisUtil.getTypeCount(list.getClass().getGenericSuperclass());
        Assert.assertEquals(i, 1);

        i = CSVApisUtil.getTypeCount(int.class);
        Assert.assertEquals(i, 0);
    }

    /***
     * Test method for {@link CSVApisUtil#HandMutiParameType(Type, int)}
     */
    @Test
    public void testHandMutiParameType() {
        //class
        Type res = CSVApisUtil.HandMutiParameType(int.class, 0);
        Assert.assertEquals(res, int.class);

        res = CSVApisUtil.HandMutiParameType(int.class, 1);
        Assert.assertNotEquals(res, int.class);

        //ParameterizedType
        Map<String, String> map = new HashMap<String, String>(1);

        res = CSVApisUtil.HandMutiParameType(map.getClass().getGenericSuperclass(), 0);
        Assert.assertEquals(((TypeVariableImpl) res).getName(), "K");

        res = CSVApisUtil.HandMutiParameType(map.getClass().getGenericSuperclass(), 1);
        Assert.assertEquals(((TypeVariableImpl) res).getName(), "V");

        List<String> list = new ArrayList<String>(1);

        res = CSVApisUtil.HandMutiParameType(list.getClass().getGenericSuperclass(), 0);
        Assert.assertEquals(((TypeVariableImpl) res).getName(), "E");

        res = CSVApisUtil.HandMutiParameType(list.getClass().getGenericSuperclass(), 1);
        Assert.assertEquals(res, null);

    }

    /***
     * Test method for {@link CSVApisUtil#isWrapClass(Class)}
     */
    @Test
    public void testIsWrapClass() {
        Boolean res = CSVApisUtil.isWrapClass(Boolean.class);
        Assert.assertTrue(res);

        res = CSVApisUtil.isWrapClass(List.class);
        Assert.assertFalse(res);
    }
}