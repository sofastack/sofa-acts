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

public class ActsCaseContextHolder {

    private static ThreadLocal<ActsCaseContext> actsCaseContextLocal = new ThreadLocal<ActsCaseContext>();

    public static ActsCaseContext get() {
        return actsCaseContextLocal.get();
    }

    public static boolean exists() {
        return actsCaseContextLocal.get() != null;
    }

    public static void set(ActsCaseContext context) {
        actsCaseContextLocal.set(context);
    }

    public static void clear() {
        actsCaseContextLocal.remove();
    }

    public static void addUniqueMap(String key, Object value) {
        actsCaseContextLocal.get().getUniqueMap().put(key, value);
    }

    public static void clearUniqueMap() {
        actsCaseContextLocal.get().getUniqueMap().clear();
    }

}
