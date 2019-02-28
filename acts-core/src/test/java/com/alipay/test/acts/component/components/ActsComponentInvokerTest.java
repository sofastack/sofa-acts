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

import java.lang.reflect.Method;

/**
 * 
 * @author zhiyuan.lzy
 * @version $Id: ActsComponentInvokerTest.java, v 0.1 2016年1月25日 下午2:46:39 zhiyuan.lzy Exp $
 */
public class ActsComponentInvokerTest {

    /**
     * Test method for {@link ActsComponentInvoker#genParam(String)}.
     */
    @Test
    public void testGenParam() {

        ActsComponentInvoker invoker = new ActsComponentInvoker();

        MethodModel model = new MethodModel();
        Method declaredMethod = null;
        try {
            declaredMethod = model.getClass().getDeclaredMethod("method", String.class);
        } catch (Exception e) {
            Assert.fail("failed", e);
        }
        invoker.setTargetMethod(declaredMethod);
        invoker.setComponentObject(model);

        Object[] params = invoker.genParam("@method?a=1");
        Assert.assertEquals(params.length, 1);

        try {
            Object result = invoker.execute("@method?a=1");
            Assert.assertEquals(result, 1);
        } catch (Exception e) {
            Assert.fail("failed", e);
        }
    }

}
