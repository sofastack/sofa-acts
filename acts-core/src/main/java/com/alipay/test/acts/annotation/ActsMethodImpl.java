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

import com.alipay.test.acts.runtime.ActsRuntimeContext;

/**
 * 
 * @author tianzhu.wtzh
 * @version $Id: ActsMethodImpl.java, v 0.1 2016年5月12日 上午11:25:31 tianzhu.wtzh Exp $
 */
public class ActsMethodImpl implements IActsMethod {

    private static final Log log   = LogFactory.getLog(ActsMethodImpl.class);

    private Method           invoker;

    protected Object         instance;

    protected String         group;

    /* the invoke order */
    private int              order = 0;

    /**
     * Constructor.
     */
    public ActsMethodImpl() {
    }

    /**
     * Constructor.
     *
     * @param method the method
     * @param instance the instance
     */
    public ActsMethodImpl(Method method, Object instance) {
        this.invoker = method;
        this.instance = instance;
    }

    /**
     * Invoke.
     *
     * @param actsRuntimeContext the acts runtime context
     */
    @Override
    public void invoke(ActsRuntimeContext actsRuntimeContext) {

        try {
            if (this.invoker.getParameterTypes().length == 0) {
                this.invoker.setAccessible(true);
                this.invoker.invoke(instance, new Object[] {});
                return;
            }

            if (this.invoker.getParameterTypes()[0].equals(ActsRuntimeContext.class)) {
                this.invoker.setAccessible(true);
                this.invoker.invoke(instance, new Object[] { actsRuntimeContext });
                return;
            }

        } catch (IllegalAccessException e) {
            if (log.isInfoEnabled()) {
                log.info("error ", e);
            }
        } catch (IllegalArgumentException e) {
            if (log.isInfoEnabled()) {
                log.info("error ", e);
            }
        } catch (InvocationTargetException e) {
            if (log.isInfoEnabled()) {
                log.info("error ", e);
            }
        }
    }

    /**
     * Gets invoker.
     *
     * @return the invoker
     */
    public Method getInvoker() {
        return invoker;
    }

    /**
     * Sets invoker.
     *
     * @param invoker the invoker
     */
    public void setInvoker(Method invoker) {
        this.invoker = invoker;
    }

    /**
     * Gets order.
     *
     * @return the order
     */
    @Override
    public int getOrder() {
        return this.order;
    }

    /**
     * Sets order.
     *
     * @param order the order
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Gets group.
     *
     * @return the group
     */
    @Override
    public String getGroup() {
        return this.group;
    }

}
