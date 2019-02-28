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
package com.alipay.test.acts.helper;

import com.alipay.test.acts.model.PrepareData;
import com.alipay.test.acts.model.VirtualObject;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

/**
 *
 * @author qingqin
 * @version $Id: CSVHelperTest.java, v 0.1 2019年01月18日 下午10:41 qingqin Exp $
 */
public class CSVHelperTest {

    private class Demo {
        public Date     date;

        public Currency currency;
    }

    @Test
    public void testAddSimpleValue() {

        Field field = null;
        Method method = null;

        List<String> values = new ArrayList<String>(1);

        try {
            method = CSVHelper.class.getDeclaredMethod("addSimpleValue", Class.class, Field.class,
                List.class);
            method.setAccessible(true);

            //string
            field = PrepareData.class.getDeclaredField("description");
            method.invoke(null, PrepareData.class, field, values);

            Assert.assertNotEquals(values, null);
            Assert.assertEquals(values.get(0), "Y");
            Assert.assertEquals(values.get(1), "");
            values.clear();

            //int
            field = Integer.class.getDeclaredField("value");
            method.invoke(null, Integer.class, field, values);

            Assert.assertNotEquals(values, null);
            Assert.assertEquals(values.get(0), "Y");
            Assert.assertEquals(values.get(1), "0");
            values.clear();

            //double
            field = Double.class.getDeclaredField("value");
            method.invoke(null, Double.class, field, values);

            Assert.assertNotEquals(values, null);
            Assert.assertEquals(values.get(0), "Y");
            Assert.assertEquals(values.get(1), "0.0");
            values.clear();

            //boolean
            field = Boolean.class.getDeclaredField("value");
            method.invoke(null, Boolean.class, field, values);

            Assert.assertNotEquals(values, null);
            Assert.assertEquals(values.get(0), "Y");
            Assert.assertEquals(values.get(1), "false");
            values.clear();

            //char
            field = Character.class.getDeclaredField("value");
            method.invoke(null, Character.class, field, values);

            Assert.assertNotEquals(values, null);
            Assert.assertEquals(values.get(0), "Y");
            Assert.assertEquals(values.get(1), "A");
            values.clear();

            //date
            field = Demo.class.getDeclaredField("date");
            method.invoke(null, Demo.class, field, values);

            Assert.assertNotEquals(values, null);
            Assert.assertEquals(values.get(0), "D");
            Assert.assertEquals(values.get(1), "today");
            values.clear();

            //currency
            field = Demo.class.getDeclaredField("currency");
            method.invoke(null, Demo.class, field, values);

            Assert.assertNotEquals(values, null);
            Assert.assertEquals(values.get(0), "Y");
            Assert.assertEquals(values.get(1), "CNY");
            values.clear();

        } catch (Exception e) {
            /*
             * if the specified object is null and the method is an instance method.
             */
            Assert.fail("failed", e);
        }

    }

    @Test
    public void testGetCsvFileName() {
        String path = getFileFromURL("Map_Object/VirtualObject.csv").getPath();

        String name = CSVHelper.getCsvFileName(VirtualObject.class, path);

        Assert.assertEquals(name.contains("VirtualObject.csv"), true);
    }

    private File getFileFromURL(String s) {
        File folder = null;
        try {
            folder = new ClassPathResource(s).getFile();
        } catch (IOException e) {
            Assert.fail("error! ", e);
        }
        return folder;
    }

}