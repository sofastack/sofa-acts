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
package com.alipay.test.acts.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.testng.collections.Lists;

import org.apache.commons.lang.StringUtils;

/**
 * Method Utils 
 * 
 * @author yuanren.syr
 * @version $Id: MethodUtils.java, v 0.1 2015年8月17日 下午5:45:32 yuanren.syr Exp $
 */
public class MethodUtils {

    /**
     * filter method by prototype
     * 
     * @param methods
     * @return
     */
    public static List<Method> filterMethod(List<Method> methods, Class<?>[] paramTypes,
                                            Class<?> retType) {
        List<Method> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(methods)) {
            return result;
        }
        for (Method method : methods) {
            if (Arrays.equals(method.getParameterTypes(), paramTypes)
                && method.getReturnType() == retType) {
                result.add(method);
            }
        }
        return result;
    }

    /**
     * find Methods by MethodName
     * 
     * @param clazz
     * @param methodNames
     * @return
     */
    public static List<Method> findMethodsByName(Class<?> clazz, String... methodNames) {
        if (clazz == null || methodNames == null || methodNames.length == 0) {
            return null;
        }
        List<Method> result = Lists.newArrayList();
        for (Method method : clazz.getMethods()) {
            for (String methodName : methodNames) {
                if (StringUtils.equals(method.getName(), methodName)) {
                    result.add(method);
                }
            }
        }
        return result;
    }
}
