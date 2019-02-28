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

import com.alipay.test.acts.annotation.TestComponent;
import com.alipay.test.acts.utils.config.ConfigrationFactory;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  parameterization component util
 *
 * @modify qingqin
 * @version $Id: ActsComponentUtil.java, v 0.1 2018年12月07日 上午1:15 qingqin Exp $
 */
public class ActsComponentUtil {
    private final static Log                        log = LogFactory
                                                            .getLog(ActsComponentUtil.class);

    public static Map<String, ActsComponentInvoker> actsComponents;

    public static void init(String componentPackage, ClassLoader loader) {
        if (actsComponents != null) {
            return;
        }
        actsComponents = new LinkedHashMap<String, ActsComponentInvoker>();
        //load components
        ImmutableSet<ClassInfo> classes = null;
        try {
            classes = ClassPath.from(loader).getTopLevelClasses();
            if (classes != null) {
                for (final ClassPath.ClassInfo info : classes) {
                    if (info.getName().startsWith(componentPackage)) {
                        String name = info.getName();
                        Class<?> clazz = Class.forName(name);
                        Method[] methods = clazz.getMethods();
                        for (Method method : methods) {

                            if (method.isAnnotationPresent(TestComponent.class)) {
                                ActsComponentInvoker actsComponentInvoker = new ActsComponentInvoker();
                                actsComponentInvoker.setTargetMethod(method);
                                actsComponentInvoker.setComponentObject(clazz.newInstance());
                                actsComponents.put(
                                    (method.getAnnotation(TestComponent.class)).id(),
                                    actsComponentInvoker);
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("acts components init error!", e);
        }
        /***
         * try again
         */
        if (actsComponents.isEmpty()) {
            try {
                String components = ConfigrationFactory.getConfigration().getPropertyValue(
                    "param_components");
                if (StringUtils.isBlank(components)) {
                    return;
                }
                for (String classQualifyName : components.split(",")) {

                    if (StringUtils.isEmpty((classQualifyName = classQualifyName.trim()))) {
                        continue;
                    }
                    Class<?> cl = Class.forName(classQualifyName);
                    Method[] methods = cl.getMethods();

                    for (Method method : methods) {
                        if (method.isAnnotationPresent(TestComponent.class)) {
                            ActsComponentInvoker componentInvoker = new ActsComponentInvoker();
                            componentInvoker.setComponentObject(cl.newInstance());
                            componentInvoker.setTargetMethod(method);
                            actsComponents.put((method.getAnnotation(TestComponent.class)).id(),
                                componentInvoker);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("acts components init error!", e);
            }
        }
    }

    public static Object run(String command) {
        String id = "";

        if (StringUtils.contains(command, "?")) {
            id = StringUtils.substringBetween(command, "@", "?");
        } else {
            id = StringUtils.substringAfter(command, "@");
        }
        try {
            return actsComponents.get(id).execute(command);
        } catch (Exception e) {
            log.error("execute acts component error!", e);
        }
        return null;
    }

    public static void clear() {
        actsComponents = null;
    }
}