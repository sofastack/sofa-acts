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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author qingqin
 * @version $Id: MethodUtilsTest.java, v 0.1 2019年01月12日 下午3:13 qingqin Exp $
 */
public class ObjectCompareUtilTest {
    /**
     * Test method for {@link ObjectCompareUtil#compareByFields(Object, Object)}.
     */
    @Test
    public void testCompareByFields() {

        TradeException r1 = new TradeException("r1");
        TradeException r2 = new TradeException("r2");

        boolean result = ObjectCompareUtil.compareByFields(r1, r2);
        Assert.assertFalse(result);

        //=============================================================

        r1 = new TradeException("normal", new BigDecimal(10000.00));
        r2 = new TradeException("normal", new BigDecimal(10000.01));

        result = ObjectCompareUtil.compareByFields(r1, r2);
        Assert.assertFalse(result);

    }

    @Test
    public void testMatchObj() {

        TradeException r1 = new TradeException("123", new BigDecimal(10000.00));

        Double[] doubles = { 0.026 };
        r1.setDoubles(doubles);

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> tempMap = new HashMap<String, String>();
        list.add(tempMap);

        r1.setList(list);

        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
        List<Integer> tempList = new ArrayList<Integer>();
        tempList.add(1);
        map.put("tempList", tempList);

        r1.setMap(map);

        Set<Date> set = new HashSet<Date>();
        set.add(new Date());
        r1.setSet(set);

        TradeException.Msg msg = new TradeException.Msg();
        msg.setMessage("hello");
        r1.setMsg(msg);

        TradeException r2 = new TradeException("$key", new BigDecimal(10000.00));

        r2.setDoubles(doubles);
        r2.setList(list);
        r2.setMap(map);
        r2.setSet(set);
        TradeException.Msg msg1 = new TradeException.Msg();
        msg1.setMessage("^h.*");
        r2.setMsg(msg1);

        Map<String, Object> varParaMap = new HashMap<String, Object>();
        varParaMap.put("key", 123);

        Map<String, Map<String, String>> flagMap = new HashMap<String, Map<String, String>>();
        Map<String, String> fileds = new HashMap<String, String>();
        fileds.put("message", "R");
        flagMap.put("com.alipay.test.acts.utils.TradeException$Msg", fileds);

        boolean result = ObjectCompareUtil.matchObj(r1, r2, flagMap, varParaMap);
        Assert.assertTrue(result);

    }

}
