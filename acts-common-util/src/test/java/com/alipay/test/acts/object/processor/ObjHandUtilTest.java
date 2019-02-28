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
package com.alipay.test.acts.object.processor;

import com.alipay.test.acts.model.VirtualList;
import com.alipay.test.acts.model.VirtualMap;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yanzhu on 2019/1/15.
 */
public class ObjHandUtilTest {

    @Test
    public void testIsSubListConvert() {
        Assert.assertTrue(ObjHandUtil.isSubListConvert(
            getFileFromURL("BusinessActionContext/Map_Object.csv").getPath(),
            "com.alipay.test.acts.model.VirtualMap"));
    }

    @Test
    public void testHandListConvert() {
        VirtualList obj = new VirtualList();
        ArrayList<String> test = new ArrayList<String>();
        test.add("1");
        obj.setVirtualList(test);
        Assert.assertEquals(
            "[[1]]",
            ObjHandUtil.handListConvert(obj,
                getFileFromURL("BusinessActionContext/List_Object.csv").getPath()).toString());
    }

    @Test
    public void testHandMapConvert() {
        VirtualMap obj = new VirtualMap();
        obj.setMapKey("test");
        obj.setMapValue("test");
        Assert.assertEquals(ObjHandUtil.handMapConvert(obj).toString(), "{test=test}");
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
