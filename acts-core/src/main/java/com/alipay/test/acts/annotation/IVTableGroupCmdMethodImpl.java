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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author xiuzhu.hp
 */
public class IVTableGroupCmdMethodImpl implements IVTableGroupCmdMethod {

    private static final Log log = LogFactory.getLog(IVTableGroupCmdMethodImpl.class);

    private Object           instance;

    private Method           targetMethod;

    /**
     * Constructor.
     */
    public IVTableGroupCmdMethodImpl() {
    }

    /**
     * Constructor.
     *
     * @param instance the instance
     * @param targetMethod the target method
     */
    public IVTableGroupCmdMethodImpl(Object instance, Method targetMethod) {
        this.instance = instance;
        this.targetMethod = targetMethod;
    }

    /**
     * Invoke.
     *
     * @param tableName the table name
     * @param groupId the group id
     */
    @Override
    public void invoke(String tableName, String groupId) {

        try {
            if (this.targetMethod.getParameterTypes().length == 0) {
                this.targetMethod.setAccessible(true);
                this.targetMethod.invoke(instance, new Object[] {});
                return;
            }

            if (this.targetMethod.getParameterTypes().length == 2
                && this.targetMethod.getParameterTypes()[0].equals(String.class)
                && this.targetMethod.getParameterTypes()[1].equals(String.class)) {
                this.targetMethod.setAccessible(true);
                this.targetMethod.invoke(instance, new Object[] { tableName, groupId });
                return;
            }

        } catch (IllegalAccessException e) {
            if (log.isInfoEnabled()) {
                log.info("error", e);
            }
        } catch (IllegalArgumentException e) {
            if (log.isInfoEnabled()) {
                log.info("error", e);
            }
        } catch (InvocationTargetException e) {
            if (log.isInfoEnabled()) {
                log.info("error", e);
            }
        }
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public Object getInstance() {
        return instance;
    }

    /**
     * Sets instance.
     *
     * @param instance the instance
     */
    public void setInstance(Object instance) {
        this.instance = instance;
    }

    /**
     * Gets target method.
     *
     * @return the target method
     */
    public Method getTargetMethod() {
        return targetMethod;
    }

    /**
     * Sets target method.
     *
     * @param targetMethod the target method
     */
    public void setTargetMethod(Method targetMethod) {
        this.targetMethod = targetMethod;
    }

}
