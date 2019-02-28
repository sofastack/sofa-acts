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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author qingqin
 * @version $Id: ObjectUtilTest.java, v 0.1 2019年01月08日 下午3:31 qingqin Exp $
 */
public class ObjectUtilTest {

    @Test
    public void testRestoreObject() {
        TradeException tradeException = new TradeException();

        ObjectUtil.restoreObject("code=01", tradeException);
        Assert.assertEquals(tradeException.getCode(), "01");
    }

    @Test
    public void testFindMethod() {
        TradeException tradeException = new TradeException();

        Method method = ObjectUtil.findMethod("getCode", tradeException);
        Assert.assertEquals(method, null);
    }

    @Test
    public void testLoad() {
        TradeException tradeException = new TradeException();

        ObjectUtil.load("code=01", tradeException);
        Assert.assertEquals(tradeException.getCode(), "01");
    }

    @Test
    public void testSetValue() {
        TradeException tradeException = new TradeException();

        ObjectUtil.setValue(tradeException, "code", "01");
        Assert.assertEquals(tradeException.getCode(), "01");
    }

    @Test
    public void testResolveList() {
        List<String> list = ObjectUtil.resolveList("code|01", "|");
        Assert.assertEquals(list.get(1), "01");
    }

    @Test
    public void testCopyProperties() {

        TradeException tradeException = new TradeException();
        tradeException.setCode("01");

        Map<String, String> map = ObjectUtil
            .copyProperties(tradeException, new String[] { "code" });
        Assert.assertEquals(map.get("code"), tradeException.getCode());
    }

    @Test
    public void testToJson() {
        TradeException tradeException = new TradeException();
        tradeException.setCode("01");

        String str = ObjectUtil.toJson(tradeException);
        Assert.assertTrue(str.contains("\"code\":\"01\""));
    }

    @Test
    public void testGetDOByClass() {
        TradeException tradeException = new TradeException();
        tradeException.setCode("01");
        List<Object> list = new ArrayList<Object>(1);
        list.add(tradeException);
        list.add(new Date());

        TradeException t = ObjectUtil.getDOByClass(list, TradeException.class);
        Assert.assertEquals(t, tradeException);
    }

}