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
package com.alipay.test.acts.component.components;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author qingqin
 * @version $Id: ActsComponentUtilTest.java, v 0.1 2019年01月14日 上午11:42 qingqin Exp $
 */
public class ActsComponentUtilTest {

    /***
     * Test method for {@link ActsComponentUtil#init(String, ClassLoader)}
     */
    @Test
    public void testInit_Run_Clear() {

        ActsComponentUtil.init("com.alipay.test.acts.component", this.getClass().getClassLoader());

        String object = (String) ActsComponentUtil.run("@test?param=123");
        Assert.assertEquals(object, "123");

        ActsComponentUtil.clear();
    }
}