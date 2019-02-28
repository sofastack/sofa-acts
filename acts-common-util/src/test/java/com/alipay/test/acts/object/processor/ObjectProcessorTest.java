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

import com.alipay.test.acts.yaml.cpUnit.property.BaseUnitProperty;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by yanzhu on 2019/1/15.
 */
public class ObjectProcessorTest {

    @Test
    public void testGenObject() throws Exception {
        //map
        ObjectProcessor objectProcessor = new ObjectProcessor(this.getClass().getClassLoader(),
            getFileFromURL("BusinessActionContext/Map_Object.csv").getPath(), "1", "UTF-8");

        Assert.assertEquals("{1=null}", objectProcessor.genObject().toString());

        //list
        objectProcessor = new ObjectProcessor(getFileFromURL("PrepareData/VirtualArgs.csv")
            .getPath(), "1", "UTF-8");

        Assert.assertNotNull(objectProcessor.genObject().toString());

        //object
        objectProcessor = new ObjectProcessor(this.getClass().getClassLoader(), getFileFromURL(
            "PrepareData/PrepareData.csv").getPath(), "1", "UTF-8");

        Assert.assertNotNull(objectProcessor.genObject());

        //exception
        try {
            objectProcessor = new ObjectProcessor(this.getClass().getClassLoader(), getFileFromURL(
                "BusinessActionContext/BusinessActionContext.csv").getPath(), "1", "UTF-8");
            objectProcessor.genObject();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains(
                "Failed to load class based on qualified name"));
        }

    }

    /**
     * Test method for {@link ObjectProcessor#ObjectProcessor}
     */
    @Test
    public void testObjectProcessor() throws Exception {
        ObjectProcessor objectProcessor = new ObjectProcessor(getFileFromURL(
            "PrepareData/PrepareData.csv").getPath(), "1");

        Assert.assertNotNull(objectProcessor.genObject().toString());

        objectProcessor = new ObjectProcessor(getFileFromURL("PrepareData/PrepareData.csv")
            .getPath(), "1", "UTF-8");

        Assert.assertNotNull(objectProcessor.genObject().toString());

    }

    private File getFileFromURL(String path) {
        File folder = null;
        try {
            folder = new ClassPathResource(path).getFile();
        } catch (IOException e) {
            Assert.fail("error! ", e);
        }
        return folder;
    }

    @Test
    public void testGenerateSimpleCollection() {
        //map
        ObjectProcessor objectProcessor = new ObjectProcessor(this.getClass().getClassLoader(),
            getFileFromURL("BusinessActionContext/Map_Object.csv").getPath(), "1", "UTF-8");

        BaseUnitProperty baseUnitProperty = new BaseUnitProperty("", "", "");

        Object o = objectProcessor.generateSimpleCollection(baseUnitProperty, null, null, "",
            "{\"test\":\"test\"}");
        Assert.assertEquals(o.toString(), "{test=test}");
    }
}
