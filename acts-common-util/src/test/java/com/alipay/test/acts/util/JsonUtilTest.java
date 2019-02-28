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

import java.util.HashMap;

/**
 * Created by yanzhu on 2019/1/13.
 */
public class JsonUtilTest {

    @Test
    public void testToPrettyString() {
        HashMap<String, String> test = new HashMap<String, String>();
        test.put("test", "test");
        Assert.assertEquals(JsonUtil.toPrettyString(test), "{\n" + "\t\"test\":\"test\"\n" + "}");
    }

    @Test
    public void testGenObjectFromJsonString() {
        Assert.assertEquals(
            JsonUtil.genObjectFromJsonString("{\n" + "\t\"test\":\"test\"\n" + "}", Object.class)
                .toString(), "{\"test\":\"test\"}");
    }

}
