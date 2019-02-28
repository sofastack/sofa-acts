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
package com.alipay.test.acts.context;

import org.junit.Assert;

public class ActsSuiteContextHolder {

    //thread context
    private static ThreadLocal<ActsSuiteContext> actsSuiteContextLocal = new ThreadLocal<ActsSuiteContext>();

    /**
     * get
     *
     * @return
     */
    public static ActsSuiteContext get() {
        Assert.assertNotNull("case context", actsSuiteContextLocal.get());
        return actsSuiteContextLocal.get();
    }

    /**
     * check
     *
     * @return true如果当前是否存在上下文，否则false
     */
    public static boolean exists() {
        return actsSuiteContextLocal.get() != null;
    }

    /**
     * set
     *
     * @param context
     */
    public static void set(ActsSuiteContext context) {
        actsSuiteContextLocal.set(context);
    }

    /**
     * clear
     */
    public static void clear() {
        actsSuiteContextLocal.remove();
    }
}
