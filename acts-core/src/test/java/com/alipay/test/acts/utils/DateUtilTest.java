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

import java.util.Date;

/**
 *
 * @author qingqin
 * @version $Id: DateUtilTest.java, v 0.1 2019年01月08日 下午2:47 qingqin Exp $
 */
public class DateUtilTest {

    @Test
    public void testFormat() throws Exception {
        String dataStr = "20180101";

        Date date = DateUtil.parse(dataStr, DateUtil.SHORT_FORMAT);
        Assert.assertEquals(date.getYear(), 118);

        String res = DateUtil.format(date, DateUtil.SHORT_FORMAT);
        Assert.assertEquals(res, dataStr);

        res = DateUtil.formatCurrent(DateUtil.SHORT_FORMAT);
        Assert.assertEquals(res.length(), 8);

        res = DateUtil.format(dataStr, DateUtil.SHORT_FORMAT, DateUtil.WEB_FORMAT);
        Assert.assertEquals(res, "2018-01-01");

        res = DateUtil.formatShort(date);
        Assert.assertEquals(res, dataStr);

        res = DateUtil.formatShort("2018-01-01", DateUtil.WEB_FORMAT);
        Assert.assertEquals(res, dataStr);

        res = DateUtil.formatWeb(date);
        Assert.assertEquals(res, "2018-01-01");

        res = DateUtil.formatWeb("20180101", DateUtil.SHORT_FORMAT);
        Assert.assertEquals(res, "2018-01-01");

        res = DateUtil.formatMonth(date);
        Assert.assertEquals(res, "201801");

        res = DateUtil.formatTime(date);
        Assert.assertEquals(res, "000000");

        res = DateUtil.getTimestamp(3);
        Assert.assertEquals(res.length(), 17);

    }
}