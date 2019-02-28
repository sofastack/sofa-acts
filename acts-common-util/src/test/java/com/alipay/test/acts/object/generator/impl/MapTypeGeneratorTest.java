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

import com.alipay.test.acts.model.VirtualMap;
import com.alipay.test.acts.object.generator.impl.MapTypeGenerator;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanzhu on 2019/1/15.
 */
public class MapTypeGeneratorTest {

    @Test
    public void testGenerateObjectValue() {
        MapTypeGenerator mapTypeGenerator = new MapTypeGenerator();
        HashMap<String, String> test = new HashMap<String, String>();
        test.put("test", "test");
        Assert.assertEquals(
            mapTypeGenerator.generateObjectValue(test,
                getFileFromURL("BusinessActionContext/Map_Object.csv").getPath(), true).toString(),
            "test:test");

        mapTypeGenerator = new MapTypeGenerator();
        Map<String, VirtualMap> map = new HashMap<String, VirtualMap>(1);
        map.put("map", new VirtualMap());
        String str = mapTypeGenerator.generateObjectValue(map,
            getFileFromURL("Map_Object/Map_Object.csv").getPath(), false).toString();
        Assert.assertEquals(str.contains("map:Map_Object.csv@"), true);
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

    @Test
    public void testGetItemClass() {
        MapTypeGenerator mapTypeGenerator = new MapTypeGenerator();

        HashMap<String, String> test = new HashMap<String, String>();

        try {
            mapTypeGenerator.getItemClass(test.getClass().getGenericSuperclass(), String.class);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof RuntimeException);
        }

    }
}
