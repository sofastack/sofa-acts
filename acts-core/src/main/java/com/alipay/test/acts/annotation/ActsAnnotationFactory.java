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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alipay.test.acts.annotation.acts.AfterCheck;
import com.alipay.test.acts.annotation.acts.AfterClean;
import com.alipay.test.acts.annotation.acts.AfterPrepare;
import com.alipay.test.acts.annotation.acts.AfterTable;
import com.alipay.test.acts.annotation.acts.BeforeCheck;
import com.alipay.test.acts.annotation.acts.BeforeClean;
import com.alipay.test.acts.annotation.acts.BeforePrepare;
import com.alipay.test.acts.annotation.acts.BeforeTable;
import com.alipay.test.acts.annotation.acts.Executor;
import com.alipay.test.acts.component.db.DBDatasProcessor;
import com.alipay.test.acts.template.ActsTestBase;

/**
 * 
 * @author tianzhu.wtzh
 * @version $Id: ActsAnnotationFactory.java, v 0.1 2016年5月12日 上午11:09:18 tianzhu.wtzh Exp $
 */
public class ActsAnnotationFactory {

    /** Currently registered annotation method*/
    protected Map<String, List<IActsMethod>> annoationMethods;

    /** Data processor */
    public DBDatasProcessor                  dbDatasProcessor;

    /** IActsMethod builder factory */
    protected ActsMethodBuilder              actsMethodBuilder = new ActsMethodBuilder();

    /**
     * Factory constructor
     * @param annoationMethods
     * @param dbDatasProcessor
     */
    public ActsAnnotationFactory(Map<String, List<IActsMethod>> annoationMethods,
                                 DBDatasProcessor dbDatasProcessor) {
        this.annoationMethods = annoationMethods;
        this.dbDatasProcessor = dbDatasProcessor;
    }

    /**
     * scan and get @BeforeClean, @AfterClean, @BeforeCheck, @AfterCheck
     * @BeforePrepare, @AfterPrepare, @BeforeTable, @AfterTable
     */
    public void initAnnotationMethod(Set<Method> allMethod, ActsTestBase template) {

        for (Method method : allMethod) {
            addActsMethod(method, AfterClean.class, annoationMethods, template);
            addActsMethod(method, BeforeClean.class, annoationMethods, template);
            addActsMethod(method, BeforeCheck.class, annoationMethods, template);
            addActsMethod(method, AfterCheck.class, annoationMethods, template);
            addActsMethod(method, BeforePrepare.class, annoationMethods, template);
            addActsMethod(method, AfterPrepare.class, annoationMethods, template);
            addActsMethod(method, Executor.class, annoationMethods, template);

            //@BeforeTable, @AfterTable
            if (method.isAnnotationPresent(BeforeTable.class)) {

                this.dbDatasProcessor.getBeforeVTableExecuteMethodList().add(
                    new IVTableGroupCmdMethodImpl(template, method));
            }

            if (method.isAnnotationPresent(AfterTable.class)) {

                this.dbDatasProcessor.getAfterVTableExecuteMethodList().add(
                    new IVTableGroupCmdMethodImpl(template, method));
            }
        }

    }

    /**
     * add method
     * @param m
     * @param clsz
     */
    private void addActsMethod(Method m, Class<? extends Annotation> clsz,
                               Map<String, List<IActsMethod>> annoationMethods,
                               ActsTestBase template) {

        if (!annoationMethods.containsKey(clsz.getSimpleName())) {

            List<IActsMethod> methodList = new LinkedList<IActsMethod>();
            annoationMethods.put(clsz.getSimpleName(), methodList);
        }

        addActsMethod(annoationMethods.get(clsz.getSimpleName()), m, clsz, template);
    }

    /**
     * Add annotation method
     * @param methodList
     * @param m
     * @param clsz
     */
    private void addActsMethod(List<IActsMethod> methodList, Method m,
                               Class<? extends Annotation> clsz, ActsTestBase template) {
        if (m.isAnnotationPresent(clsz)) {
            Annotation annotaion = m.getAnnotation(clsz);

            IActsMethod iActsMethod = actsMethodBuilder.buildActsMethod(m, template, clsz,
                annotaion);
            int i = 0;
            for (i = 0; i < methodList.size(); i++) {
                if (methodList.get(i).getOrder() > iActsMethod.getOrder()) {
                    break;
                }
            }

            methodList.add(i, iActsMethod);
        }
    }

    /**
     * Getter method for property <tt>annoationMethods</tt>.
     * 
     * @return property value of annoationMethods
     */
    public Map<String, List<IActsMethod>> getAnnoationMethods() {
        return annoationMethods;
    }

    /**
     * Setter method for property <tt>annoationMethods</tt>.
     * 
     * @param annoationMethods value to be assigned to property annoationMethods
     */
    public void setAnnoationMethods(Map<String, List<IActsMethod>> annoationMethods) {
        this.annoationMethods = annoationMethods;
    }

    /**
     * Getter method for property <tt>dbDatasProcessor</tt>.
     * 
     * @return property value of dbDatasProcessor
     */
    public DBDatasProcessor getDbDatasProcessor() {
        return dbDatasProcessor;
    }

    /**
     * Setter method for property <tt>dbDatasProcessor</tt>.
     * 
     * @param dbDatasProcessor value to be assigned to property dbDatasProcessor
     */
    public void setDbDatasProcessor(DBDatasProcessor dbDatasProcessor) {
        this.dbDatasProcessor = dbDatasProcessor;
    }

    /**
     * Getter method for property <tt>actsMethodBuilder</tt>.
     * 
     * @return property value of actsMethodBuilder
     */
    public ActsMethodBuilder getActsMethodBuilder() {
        return actsMethodBuilder;
    }

    /**
     * Setter method for property <tt>actsMethodBuilder</tt>.
     * 
     * @param actsMethodBuilder value to be assigned to property actsMethodBuilder
     */
    public void setActsMethodBuilder(ActsMethodBuilder actsMethodBuilder) {
        this.actsMethodBuilder = actsMethodBuilder;
    }

}
