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

import org.testng.annotations.Test;

/**
 *
 * @author qingqin
 * @version $Id: ActsRuntimeContextThreadHoldTest.java, v 0.1 2019年01月13日 下午2:23 qingqin Exp $
 */
public class ActsRuntimeContextThreadHoldTest {

    @Test
    public void testGetContext() {
        ActsRuntimeContextThreadHold.getContext();
    }

    @Test
    public void testSetContext() {
        ActsRuntimeContext actsRuntimeContext = new ActsRuntimeContext();
        ActsRuntimeContextThreadHold.setContext(actsRuntimeContext);
    }
}