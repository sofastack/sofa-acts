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

import java.util.LinkedHashMap;

/**
 * Created by yanzhu on 2019/1/13.
 */
public class FileUtilTest {

    @Test
    public void testGetTestResourceFile() {
        Assert.assertEquals(FileUtil.getTestResourceFile("test").getName().toString(), "test");
    }

    @Test
    public void testGetTestResourceFileByRootPath() {
        Assert.assertEquals(FileUtil.getTestResourceFileByRootPath("test").getName().toString(),
            "test");

    }

    @Test
    public void testGetRelativePath() {

        String str = FileUtil.getRelativePath("BusinessActionContext/Map_Object.csv",
            "BusinessActionContext/");
        Assert.assertEquals(str, "BusinessActionContext/Map_Object.csv");
    }

    @Test
    public void testReadFile() {

        String str = FileUtil.readFile("BusinessActionContext/Map_Object.csv");
        Assert.assertNotNull(str);
    }

    @Test
    public void testReadYaml() {
        LinkedHashMap<?, ?> map = FileUtil.readYaml(FileOperateUtilsTest
            .getFileFromURL("yaml/DeleteMessageActsTest.deleteMessage.yaml"));
        Assert.assertTrue(map.size() > 0);
    }
}
