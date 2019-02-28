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

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alipay.test.acts.annotation.acts.AfterCheck;
import com.alipay.test.acts.annotation.acts.AfterClean;
import com.alipay.test.acts.annotation.acts.AfterPrepare;
import com.alipay.test.acts.annotation.acts.BeforeCheck;
import com.alipay.test.acts.annotation.acts.BeforeClean;
import com.alipay.test.acts.annotation.acts.BeforePrepare;
import com.alipay.test.acts.annotation.acts.Executor;
import com.alipay.test.acts.component.handler.TestUnitHandler;
import com.alipay.test.acts.runtime.ActsRuntimeContext;
import com.alipay.test.acts.template.ActsTestBase;

/**
 * executor method, do execute with prepare、execute、check and clean
 * if you has any problem, please contact with xiuzhu.hp in ali-wang-wang 
 * @author hupeng
 *
 */
public class ActsExecutorMethod extends ActsMethodImpl {

    protected final Log log = LogFactory.getLog(this.getClass());

    /**
     * constructor
     * @param method
     * @param instance
     * @param executor
     */
    public ActsExecutorMethod(Method method, Object instance, Executor executor) {
        super(method, instance);
        this.group = executor.group();
        this.setOrder(executor.Order());
    }

    /**
     * Invoke.
     *
     * @param actsRuntimeContext the acts runtime context
     */
    @Override
    public void invoke(ActsRuntimeContext actsRuntimeContext) {
        log.info("=============================[" + this.group
                 + " begin]=============================\r\n");

        /* conversion */
        ActsTestBase testbase = (ActsTestBase) this.instance;

        TestUnitHandler testUnitHandler = testbase.getTestUnitHandler();

        try {

            log.info("=============================[" + this.group
                     + " prepare begin]=============================\r\n");

            /* do prepare */
            try {

                testbase.invokeIActsMethods(BeforePrepare.class, actsRuntimeContext);
                testUnitHandler.clearDepData(null, this.group);
                testUnitHandler.prepareDepData(null, this.group);

            } finally {
                testbase.invokeIActsMethods(AfterPrepare.class, actsRuntimeContext);
            }

            log.info("=============================[" + this.group
                     + " prepare end]=============================\r\n");

            log.info("=============================[" + this.group
                     + " invoke begin]=============================\r\n");

            /* do the method with @Executor on it */
            super.invoke(actsRuntimeContext);

            log.info("=============================[" + this.group
                     + " invoke end]=============================\r\n");

            log.info("=============================[" + this.group
                     + " check begin]=============================\r\n");

            /* do check */
            testbase.invokeIActsMethods(BeforeCheck.class, actsRuntimeContext);

            try {
                testUnitHandler.checkExpectDbData(null, this.group);
            } finally {
                testbase.invokeIActsMethods(AfterCheck.class, actsRuntimeContext);
            }

            log.info("=============================[" + this.group
                     + " check end]=============================\r\n");

        } finally {
            log.info("=============================[" + this.group
                     + " clean begin]=============================\r\n");

            /* do clean */
            try {
                testbase.invokeIActsMethods(BeforeClean.class, actsRuntimeContext);
                testUnitHandler.clearDepData(null, this.group);
                testUnitHandler.clearExpectDBData(null, this.group);
            } finally {

                try {
                    testbase.invokeIActsMethods(AfterClean.class, actsRuntimeContext);
                } finally {
                    log.info("=====================[" + this.group
                             + " clean end]======================\r\n");
                }
            }

        }
        log.info("=====================[" + this.group + " end]======================\r\n");

    }
}
