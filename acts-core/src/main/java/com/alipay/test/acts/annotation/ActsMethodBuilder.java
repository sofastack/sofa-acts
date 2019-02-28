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
package com.alipay.test.acts.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.alipay.test.acts.annotation.acts.Executor;

/**
 * 
 * @author tianzhu.wtzh
 * @version $Id: ActsMethodBuilder.java, v 0.1 2016年5月12日 上午11:15:15 tianzhu.wtzh Exp $
 */
public class ActsMethodBuilder {

    /**
     * Build acts method i acts method.
     *
     * @param method the method
     * @param instance the instance
     * @param annotationClass the annotation class
     * @param annotation the annotation
     * @return the i acts method
     */
    public IActsMethod buildActsMethod(Method method, Object instance,
                                       Class<? extends Annotation> annotationClass,
                                       Annotation annotation) {

        if (annotationClass.equals(Executor.class)) {
            return new ActsExecutorMethod(method, instance, (Executor) annotation);
        }

        return new ActsMethodImpl(method, instance);
    }
}
