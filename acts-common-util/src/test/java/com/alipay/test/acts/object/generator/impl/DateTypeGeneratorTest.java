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

import java.util.Date;

/**
 * Created by yanzhu on 2019/1/15.
 */
public class DateTypeGeneratorTest {

    @Test
    public void testGenerateObjectValue() {

        DateTypeGenerator dateTypeGenerator = new DateTypeGenerator();
        Date date = new Date();
        Assert.assertEquals("today", dateTypeGenerator.generateObjectValue(date, null, false));
    }

    @Test
    public void testGenerateFieldObject() {

        DateTypeGenerator dateTypeGenerator = new DateTypeGenerator();
        Date date = new Date();
        long aftertime = (date.getTime() / 1000) + 60 * 60 * 24;
        date.setTime(aftertime * 1000);
        Assert.assertTrue(Math.abs(date.getTime()
                                   - ((Date) dateTypeGenerator.generateFieldObject(Date.class,
                                       null, "today+1")).getTime()) < 5000);

    }

    @Test
    public void testGetItemClass() {
        DateTypeGenerator stringTypeGenerator = new DateTypeGenerator();
        Object o = stringTypeGenerator.getItemClass(null, null);
        Assert.assertEquals(o, null);
    }
}
