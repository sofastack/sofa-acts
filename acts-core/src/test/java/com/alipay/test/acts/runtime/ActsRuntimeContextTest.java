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
package com.alipay.test.acts.runtime;

import com.alipay.test.acts.model.PrepareData;
import com.alipay.test.acts.model.VirtualArgs;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author qingqin
 * @version $Id: runtime.java, v 0.1 2019年01月12日 下午10:00 qingqin Exp $
 */
public class ActsRuntimeContextTest {

    @Test
    public void testActsRuntimeContext() {
        ActsRuntimeContext actsRuntimeContext = new ActsRuntimeContext("caseid", null, null, null,
            null, null);

        Assert.assertEquals(actsRuntimeContext.getCaseId(), "caseid");
    }

    @Test
    public void testGetArg() {
        ActsRuntimeContext actsRuntimeContext = new ActsRuntimeContext("caseid", null, null, null,
            null, null);

        Assert.assertEquals(actsRuntimeContext.getArg(0), null);

        PrepareData prepareData = new PrepareData();

        VirtualArgs virtualArgs = new VirtualArgs();
        virtualArgs.addArg(1L, "");

        prepareData.setArgs(virtualArgs);

        actsRuntimeContext.setPrepareData(prepareData);

        Assert.assertEquals(actsRuntimeContext.getArg(0).getObject(), 1L);
    }

    @Test
    public void testSetArg() {
        PrepareData prepareData = new PrepareData();
        ActsRuntimeContext actsRuntimeContext = new ActsRuntimeContext("caseid", prepareData, null,
            null, null, null);

        Assert.assertEquals(actsRuntimeContext.getArg(0), null);

        actsRuntimeContext.setArg(0, 1L);

        Assert.assertEquals(actsRuntimeContext.getArg(0).getObject(), 1L);
    }

    @Test
    public void testGetArgValue() {
        ActsRuntimeContext actsRuntimeContext = new ActsRuntimeContext("caseid", null, null, null,
            null, null);

        Assert.assertEquals(actsRuntimeContext.getArgValue(0), null);

        PrepareData prepareData = new PrepareData();

        VirtualArgs virtualArgs = new VirtualArgs();
        virtualArgs.addArg("hello", "");

        prepareData.setArgs(virtualArgs);
        actsRuntimeContext.setPrepareData(prepareData);

        Assert.assertEquals(actsRuntimeContext.getArgValue(0), "hello");
    }

    @Test
    public void testGetUserDefParams() {
        PrepareData prepareData = new PrepareData();
        ActsRuntimeContext actsRuntimeContext = new ActsRuntimeContext("caseid", prepareData, null,
            null, null, null);

        Assert.assertEquals(actsRuntimeContext.getUserDefParams("hello"), null);
    }

    @Test
    public void testSetUserDefParams() {
        PrepareData prepareData = new PrepareData();
        ActsRuntimeContext actsRuntimeContext = new ActsRuntimeContext("caseid", prepareData, null,
            null, null, null);

        actsRuntimeContext.setUserDefParams("hello", "hello");

        Assert.assertEquals(actsRuntimeContext.getUserDefParams("hello"), "hello");
    }

    @Test
    public void testSetException() {
        PrepareData prepareData = new PrepareData();
        ActsRuntimeContext actsRuntimeContext = new ActsRuntimeContext("caseid", prepareData, null,
            null, null, null);

        Assert.assertEquals(actsRuntimeContext.getException().getObject(), null);

        actsRuntimeContext.setException(new RuntimeException());

        Assert.assertEquals(actsRuntimeContext.getException().getObject().getClass(),
            RuntimeException.class);

    }

    @Test
    public void testGetException() {
        PrepareData prepareData = new PrepareData();
        ActsRuntimeContext actsRuntimeContext = new ActsRuntimeContext("caseid", prepareData, null,
            null, null, null);

        Assert.assertEquals(actsRuntimeContext.getException().getObject(), null);

    }

    @Test
    public void testGetExpectResult() {
        PrepareData prepareData = new PrepareData();
        ActsRuntimeContext actsRuntimeContext = new ActsRuntimeContext("caseid", prepareData, null,
            null, null, null);

        Assert.assertEquals(actsRuntimeContext.getExpectResult(), null);

    }

    @Test
    public void testSetExpectResult() {
        PrepareData prepareData = new PrepareData();
        ActsRuntimeContext actsRuntimeContext = new ActsRuntimeContext("caseid", prepareData, null,
            null, null, null);

        actsRuntimeContext.setExpectResult("test");

        Assert.assertEquals(actsRuntimeContext.getExpectResult(), "test");
    }

    @Test
    public void testRefreshDataParam() {
        ActsRuntimeContext actsRuntimeContext = new ActsRuntimeContext("caseid", null, null, null,
            null, null);

        actsRuntimeContext.refreshDataParam();
    }

}