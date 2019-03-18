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

import com.alipay.yaml.DumperOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author qingqin
 * @version $Id: PropertiesUtilTest.java, v 0.1 2019年01月08日 下午4:10 qingqin Exp $
 */
public class PropertiesUtilTest {

    @Test
    public void testConvert2String() {
        Properties properties = new Properties();
        properties.put("cdd", "boy");
        String str = PropertiesUtil.convert2String(properties, false);
        Assert.assertEquals(str, "cdd=boy"
                                 + DumperOptions.LineBreak.getPlatformLineBreak().getString());

        Map<String, String> map = new HashMap<String, String>();
        map.put("cdd", "boy");
        str = PropertiesUtil.convert2String(map, false);
        Assert.assertEquals(str, "cdd=boy"
                                 + DumperOptions.LineBreak.getPlatformLineBreak().getString());
    }

    @Test
    public void testRestoreFromString() {
        Properties properties = PropertiesUtil.restoreFromString("cdd=boy"
                                                                 + DumperOptions.LineBreak
                                                                     .getPlatformLineBreak()
                                                                     .getString(), "UTF-8");
        Assert.assertEquals(properties.get("cdd"), "boy");
    }

    @Test
    public void testRestoreMap() {
        Map<String, String> map = PropertiesUtil
            .restoreMap("cdd=boy" + DumperOptions.LineBreak.getPlatformLineBreak().getString());
        Assert.assertEquals(map.get("cdd"), "boy");
    }

    @Test
    public void testToProperties() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("cdd", "boy");

        Properties properties = PropertiesUtil.toProperties(map);
        Assert.assertEquals(properties.get("cdd"), "boy");
    }

}