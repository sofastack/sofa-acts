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

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;

/**
 *
 * @modify qingqin
 * @version $Id: ActsComponentInvoker.java, v 0.1 2018年12月07日 上午1:12 qingqin Exp $
 */
public class ActsComponentInvoker {
    public Object componentObject;
    public Method targetMethod;

    public Object execute(String command) throws Exception {
        Object[] params = genParam(command);
        return targetMethod.invoke(componentObject, params);
    }

    /**
     *
     * @param command
     * @return
     */
    public Object[] genParam(String command) {
        Object[] params = null;
        if (StringUtils.contains(command, "?")) {
            params = new Object[targetMethod.getParameterTypes().length];
            String query = StringUtils.substringAfter(command, "?");
            if (StringUtils.isNotBlank(query)) {
                String[] pairs = StringUtils.split(query, "&");

                int index = 0;
                for (String pair : pairs) {
                    if (StringUtils.isBlank(pair)) {
                        continue;
                    }
                    Object value = StringUtils.substringAfter(pair, "=");
                    params[index] = value;
                    index++;
                }
            }
        }
        return params;
    }

    public Object getComponentObject() {
        return componentObject;
    }

    public void setComponentObject(Object componentObject) {
        this.componentObject = componentObject;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(Method targetMethod) {
        this.targetMethod = targetMethod;
    }
}