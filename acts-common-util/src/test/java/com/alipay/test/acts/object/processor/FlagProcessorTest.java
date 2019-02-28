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

import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author zhiyuan.lzy
 * @version $Id: FlagProcessorTest.java, v 0.1 2016年2月1日 下午4:48:11 zhiyuan.lzy Exp $
 */
public class FlagProcessorTest {

    /**
     * Test method for {@link FlagProcessor#genFlag(String, String)}.
     */
    @Test
    public void testGenFlag() {
        FlagProcessor processor = new FlagProcessor();

        File folder = getFileFromURL();

        String csvPath = folder.getAbsolutePath();
        Map<String, Map<String, String>> map = processor.genFlag(csvPath, "UTF-8");

        Assert.assertNotEquals(map, null);
        Assert.assertEquals(map.size(), 2);

    }

    private File getFileFromURL() {
        File folder = null;
        try {
            folder = new ClassPathResource("BusinessActionContext/BusinessActionContext.csv")
                .getFile();
        } catch (IOException e) {
            Assert.fail("error! ", e);
        }
        return folder;
    }
}
