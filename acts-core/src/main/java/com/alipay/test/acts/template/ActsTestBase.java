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
package com.alipay.test.acts.template;

import com.alipay.test.acts.annotation.ActsAnnotationFactory;
import com.alipay.test.acts.collector.sqllog.SqlLogCollector;
import com.alipay.test.acts.component.components.ActsComponentUtil;
import com.alipay.test.acts.util.BaseDataUtil;
import com.alipay.test.acts.utils.*;

import org.apache.commons.io.FileUtils;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import com.alipay.test.acts.annotation.IActsMethod;
import com.alipay.test.acts.annotation.TestBean;
import com.alipay.test.acts.annotation.acts.*;
import com.alipay.test.acts.collector.CaseResultCollector;
import com.alipay.test.acts.component.db.DBDatasProcessor;
import com.alipay.test.acts.component.event.EventContextHolder;
import com.alipay.test.acts.component.handler.TestUnitHandler;
import com.alipay.test.acts.exception.ActsTestException;
import com.alipay.test.acts.model.PrepareData;
import com.alipay.test.acts.runtime.ActsRuntimeContext;
import com.alipay.test.acts.runtime.ActsRuntimeContextThreadHold;
import com.alipay.test.acts.utils.config.ConfigrationFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.util.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.collections.Lists;
import org.testng.internal.ClassHelper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Acts test base
 *
 * @author yuanren.syr
 * @version $Id: JATest.java, v 0.1 2015年8月17日 上午10:09:57 yuanren.syr Exp $
 */
public class ActsTestBase extends AbstractTestNGSpringContextTests {
    protected final static Log            log              = LogFactory.getLog(ActsTestBase.class);

    /*** sql log **/
    private static final Log              sqlLog           = LogFactory.getLog("acts-sql-logger");

    /** Used to collect logs */
    protected static final String[]       SUFFIX           = new String[] { "NormalTest",
            "FuncExceptionTest"                           };

    /** Data preparation template */
    protected PrepareTemplate             prepareTemplate  = new PrepareTemplateImpl();

    public TestUnitHandler                testUnitHandler;

    /** data processing */
    public static DBDatasProcessor        dbDatasProcessor;

    /** tested method */
    public static String                  testedMethodName;
    /** caseId and data preparation*/
    protected Map<String, PrepareData>    prepareDatas     = new HashMap<String, PrepareData>();

    public String                         testDataFilePath;

    public String                         testDataFolderPath;

    public ActsRuntimeContext             actsRuntimeContext;

    /** Deprecated */
    public static boolean                 invokModel;

    /** annotationMethods map */
    public Map<String, List<IActsMethod>> annoationMethods = new HashMap<String, List<IActsMethod>>();

    @BeforeClass
    protected void setUp() throws Exception {

        try {
            if (dbDatasProcessor == null) {
                dbDatasProcessor = new DBDatasProcessor(applicationContext);
            }
            dbDatasProcessor.initDataSource();

            //parameterization init
            String componentPackage = this.getClass().getPackage().getName().split(".acts.")[0]
                                      + ".acts.component";
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            ActsComponentUtil.init(componentPackage, loader);

            //annotation init
            ActsAnnotationFactory annotation = new ActsAnnotationFactory(annoationMethods,
                dbDatasProcessor);
            Set<Method> allMethod = ClassHelper.getAvailableMethods(this.getClass());
            annotation.initAnnotationMethod(allMethod, this);

        } catch (Exception e) {
            log.info("Exception raised during setup process");
            throw new RuntimeException(e);
        }

    }

    /**
     * Get test data
     * <ol>
     * <li>Prepare Datas</li>
     * <li>Mock Interfaces</li>
     * </ol>
     */
    public void getTestData() {

        File file = new File(getDataFilePath());

        File folder = new File(getDataFolderPath());

        if (file.exists()) {
            prepareDatas = loadFromYaml(file, this.getClass().getClassLoader());

        } else if (folder.exists() && folder.isDirectory()) {
            prepareDatas = loadFromYamlFolder(folder, this.getClass().getClassLoader());

        }

    }

    /**
     * Acts DataProvider
     *
     * @param method
     * @return
     * @throws IOException
     */
    @DataProvider(name = "ActsDataProvider")
    public Iterator<Object[]> getDataProvider(Method method) throws IOException {
        try {
            testedMethodName = method.getName();
            getTestData();
            if (CollectionUtils.isEmpty(prepareDatas)) {
                return null;
            }
            List<Object[]> prepareDataList = Lists.newArrayList();
            String rexStr = ConfigrationFactory.getConfigration().getPropertyValue("test_only");
            if (StringUtils.isBlank(rexStr)) {
                rexStr = ".*";
            } else {
                rexStr = rexStr + ".*";
            }
            log.info("Run cases matching regex: [" + rexStr + "]");
            Pattern pattern = Pattern.compile(rexStr);
            // 排序
            TreeMap<String, PrepareData> treeMap = new TreeMap<String, PrepareData>(
                new Comparator<String>() {
                    @Override
                    public int compare(String str1, String str2) {
                        return str1.compareTo(str2);
                    }
                });
            treeMap.putAll(prepareDatas);
            for (String caseId : treeMap.keySet()) {
                if (prepareDatas.get(caseId).getDescription() != null) {
                    Matcher matcher = pattern.matcher(prepareDatas.get(caseId).getDescription());
                    if (!matcher.find()) {
                        log.info("[" + prepareDatas.get(caseId).getDescription()
                                 + "] does not match [" + rexStr + "], skip it");
                        continue;
                    }
                }
                String desc = prepareDatas.get(caseId).getDescription();
                desc = (desc == null) ? "" : desc;
                Object[] args = new Object[] { caseId, desc, prepareDatas.get(caseId) };
                prepareDataList.add(args);
            }
            return prepareDataList.iterator();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get tested object
     *
     * @return
     */
    protected Object getTestedObj() {
        List<Field> fields = AnnotationUtils.findFields(getClass(), TestBean.class);
        if (fields == null || fields.size() != 1) {
            log.error("Not find the tested class,Please check if the tested bean has added @TestBean annotation. If there is no publish "
                      + "service bean, please add XAutoware annotation");
            throw new RuntimeException(
                "Not find the tested class,Please check if the tested bean has added @TestBean annotation. If there is no publish "
                        + "service bean, please add XAutoware annotation");
        }
        Object testedObj;
        try {
            fields.get(0).setAccessible(true);
            testedObj = fields.get(0).get(this);
        } catch (Exception e) {
            log.error("Not find the tested bean, please check the tested bean has been injected.");
            throw new RuntimeException();
        }

        if (testedObj == null) {
            log.error("Not find the tested class, please check the tested bean has been injected, such as the bean name is correct.");
            throw new RuntimeException(
                "Not find the tested class, please check the tested bean has been injected, such as the bean name is correct.");
        }
        return testedObj;
    }

    /**
     * get tested interface
     *
     * @return
     */
    protected String getTestedInterface() {
        List<Field> fields = AnnotationUtils.findFields(getClass(), TestBean.class);
        if (fields == null || fields.size() != 1) {
            return null;
        }

        return fields.get(0).getType().getSimpleName();
    }

    /**
     * Get data driver file address
     *
     * @return
     */
    protected String getDataFilePath() {
        List<Field> fields = AnnotationUtils.findFields(getClass(), TestBean.class);
        if (fields == null || fields.size() != 1) {
            throw new ActsTestException(
                "Specified method not found, check wether service has started, or add XAutoWire annotations");
        }
        try {
            fields.get(0).setAccessible(true);
        } catch (Exception e) {
            return null;
        }
        String fileName = this.getClass().getSimpleName() + "." + testedMethodName + ".yaml";
        String relativePath = "src/test/java/"
                              + this.getClass().getPackage().getName().replace(".", "/") + "/";
        String fileFullName = relativePath + fileName;
        this.testDataFilePath = fileFullName;
        return fileFullName;
    }

    /**
     * Get data driver file address. One yaml file per case
     *
     * @return
     */
    protected String getDataFolderPath() {
        List<Field> fields = AnnotationUtils.findFields(getClass(), TestBean.class);
        if (fields == null || fields.size() != 1) {
            throw new ActsTestException(
                "Specified method not found, check wether service has started, or add XAutoWire annotations");
        }
        try {
            fields.get(0).setAccessible(true);
        } catch (Exception e) {
            return null;
        }
        String folderName = this.getClass().getSimpleName();
        String relativePath = "src/test/java/"
                              + this.getClass().getPackage().getName().replace(".", "/") + "/";
        String folderFullName = relativePath + folderName;
        this.testDataFolderPath = folderFullName;
        return folderFullName;
    }

    /**
     * Universal<code>beforeActsTest</code>，can be overridden in subclasses
     *
     * @param actsRuntimeContext
     */
    public void beforeActsTest(ActsRuntimeContext actsRuntimeContext) {

    }

    /**
     * Universal<code>afterActsTest</code>, can be overridden in subclasses
     *
     * @param actsRuntimeContext
     */
    public void afterActsTest(ActsRuntimeContext actsRuntimeContext) {

    }

    /**
     * Initialize the ACTS context
     *
     * @param caseId
     * @param prepareData
     * @param isComponent
     */
    public void initRuntimeContext(String caseId, PrepareData prepareData, boolean isComponent) {
        Object testedObj = getTestedObj();
        Method testedMethod = this.findMethod(testedMethodName, testedObj, isComponent);

        testedMethod.setAccessible(true);
        actsRuntimeContext = new ActsRuntimeContext(caseId, prepareData,
            new HashMap<String, Object>(), testedMethod, testedObj, dbDatasProcessor);

    }

    /**
     * Initialize the handle object
     */
    public void initTestUnitHandler() {
        this.testUnitHandler = new TestUnitHandler(actsRuntimeContext);
    }

    /**
     * Perform tests
     *
     * @param caseId
     * @param prepareData
     */
    public void runTest(String caseId, PrepareData prepareData) {

        DetailCollectUtils.appendAndLog(
            "=============================Start excuting TestCase caseId:" + caseId + " "
                    + prepareData.getDescription() + "=================", log);

        initRuntimeContext(caseId, prepareData, false);
        initTestUnitHandler();
        ActsRuntimeContextThreadHold.setContext(actsRuntimeContext);
        actsRuntimeContext.componentContext.put("caseFileFullName", testDataFilePath);
        CaseResultCollectUtil.holdOriginalRequest(caseId, prepareData);
        DetailCollectUtils.appendDetail(ObjectUtil.toJson(prepareData));

        try {
            if (sqlLog.isInfoEnabled()) {
                sqlLog.info(SqlLogCollector.START_FLAG + caseId);
            }
            // before all tests, the method will be executed
            initComponentsBeforeTest(actsRuntimeContext);
            beforeActsTest(actsRuntimeContext);
            process(actsRuntimeContext);

        } finally {
            // After all tests, the method will be executed
            afterActsTest(actsRuntimeContext);
            // clean up thread variable
            EventContextHolder.clear();
        }
    }

    /**
     * Collecting use case execution results
     */
    @AfterClass
    protected void collectCaseResult() {
        try {
            if (ActsRuntimeContextThreadHold.getContext() == null) {
                logger.warn("Can not write result!Because actsRuntimeContext is null.");
                return;
            }
            String caseFilePath = (String) ActsRuntimeContextThreadHold.getContext().componentContext
                .get("caseFileFullName");

            DetailCollectUtils.saveBuffer(caseFilePath);

            if (!CaseResultCollectUtil.isCollectCaseResultOpen()) {
                return;
            }

            CaseResultCollector.saveCaseResult(caseFilePath,
                CaseResultCollectUtil.getAllCaseDatas());
        } catch (Exception e) {
            log.warn("AfterClass excution failure", e);
        }
    }

    /**
     *
     *
     * @param actsRuntimeContext
     */
    public void process(ActsRuntimeContext actsRuntimeContext) {

        try {
            clear(actsRuntimeContext);
            prepare(actsRuntimeContext);
            execute(actsRuntimeContext);
            check(actsRuntimeContext);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            DetailCollectUtils
                .appendDetail("The cause of the exception occurred during the process:"
                              + sw.toString());
            log.error("An exception occurred during the process", e);
            throw new ActsTestException(e.getCause());

        } finally {

            try {
                if (sqlLog.isInfoEnabled()) {
                    sqlLog.info(SqlLogCollector.FINISH_FLAG + actsRuntimeContext.getCaseId());
                }
                File file = new File(getYamlPath());
                File folder = new File(getDataFolderPath());

                ClassLoader clsLoader = this.getClass().getClassLoader();
                // Save data during use case execution
                if (file.exists()) {
                    CaseResultCollectUtil.holdProcessData(actsRuntimeContext,
                        EventContextHolder.getBizEvent(), file, clsLoader);
                } else if (folder.exists() && folder.isDirectory()) {
                    CaseResultCollectUtil.holdProcessData(actsRuntimeContext,
                        EventContextHolder.getBizEvent(), folder, clsLoader);
                }

                try {
                    invokeIActsMethods(Executor.class, actsRuntimeContext);
                } finally {
                    //clear
                    clear(actsRuntimeContext);
                }

            } catch (ActsTestException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Only obtain the yaml file address corresponding to the test script
     *
     * @return
     */
    protected String getYamlPath() {
        String fileName = this.getClass().getSimpleName() + "." + testedMethodName + ".yaml";

        String relativePath = "src/test/java/"
                              + this.getClass().getPackage().getName().replace(".", "/") + "/";
        String fileFullName = relativePath + fileName;
        this.testDataFilePath = fileFullName;
        return fileFullName;
    }

    /**
     * Initialize execution components and custom parameters
     *
     * @param actsRuntimeContext
     */
    public void initComponentsBeforeTest(ActsRuntimeContext actsRuntimeContext) {
        testUnitHandler.prepareUserPara();
    }

    /**
     *
     *
     * @param actsRuntimeContext
     * @throws ActsTestException
     */
    public void prepare(ActsRuntimeContext actsRuntimeContext) throws ActsTestException {

        log.info("=============================[acts prepare begin]=============================\r\n");

        invokeIActsMethods(BeforePrepare.class, actsRuntimeContext);

        testUnitHandler.prepareDepData(null);

        invokeIActsMethods(AfterPrepare.class, actsRuntimeContext);
        log.info("=============================[acts prepare end]=============================\r\n");
    }

    /**
     *
     *
     * @param actsRuntimeContext
     * @throws ActsTestException
     */
    public void execute(ActsRuntimeContext actsRuntimeContext) throws ActsTestException {
        log.info("=============================[acts execute begin]=============================\r\n");
        testUnitHandler.execute();
        log.info("=============================[acts execute end]=============================\r\n");
    }

    /**
     *
     *
     * @param actsRuntimeContext
     * @throws ActsTestException
     */
    public void check(ActsRuntimeContext actsRuntimeContext) throws ActsTestException {

        log.info("=============================[acts check begin]=============================\r\n");

        invokeIActsMethods(BeforeCheck.class, actsRuntimeContext);

        testUnitHandler.checkException();
        testUnitHandler.checkExpectDbData(null);
        testUnitHandler.checkExpectEvent();
        testUnitHandler.checkExpectResult();

        invokeIActsMethods(AfterCheck.class, actsRuntimeContext);

        log.info("=============================[acts check end]=============================\r\n");
    }

    /**
     *
     *
     * @param actsRuntimeContext
     * @throws ActsTestException
     */
    public void clear(ActsRuntimeContext actsRuntimeContext) throws ActsTestException {
        log.info("=============================[acts clear begin]=============================\r\n");
        invokeIActsMethods(BeforeClean.class, actsRuntimeContext);

        testUnitHandler.clearDepData(null);
        testUnitHandler.clearExpectDBData(null);

        invokeIActsMethods(AfterClean.class, actsRuntimeContext);
        log.info("=============================[acts clear end]=============================\r\n");
    }

    /**
     * Obtain the tested method of the tested object
     *
     * @param methodName
     * @param testedObj
     * @return
     */
    protected Method findMethod(String methodName, Object testedObj, boolean isComponent) {

        if (isComponent) {
            Method[] scriptMethods = this.getClass().getDeclaredMethods();
            for (Method method : scriptMethods) {
                if (method.isAnnotationPresent(Test.class)) {
                    methodName = method.getName();
                }
            }
        }
        Class<?>[] clazzes;
        try {
            clazzes = AopProxyUtils.proxiedUserInterfaces(testedObj);
        } catch (Exception e) {
            clazzes = new Class<?>[] { testedObj.getClass() };
        }
        if (clazzes != null) {
            for (Class<?> clazz : clazzes) {
                while (clazz != null && !clazz.equals(Object.class)) {
                    Method[] methods = clazz.getDeclaredMethods();
                    if (methods != null) {
                        for (Method method : methods) {
                            if (method != null
                                && StringUtils.equalsIgnoreCase(method.getName(), methodName)) {
                                return method;
                            }
                        }
                    }
                    clazz = clazz.getSuperclass();
                }
            }
        }
        // Not found in the interface, then try to find directly in the object
        Method[] publicMethods = testedObj.getClass().getMethods();
        for (Method method : publicMethods) {
            if (StringUtils.equalsIgnoreCase(method.getName(), methodName)) {
                return method;
            }
        }
        return null;
    }

    /**
     * Save data in yaml
     *
     * @param prepareDatas
     * @param file
     */
    public void storeToYaml(Map<String, PrepareData> prepareDatas, File file) {
        BaseDataUtil.storeToYaml(prepareDatas, file);

    }

    /**
     * Save data in yaml.
     * A case corresponds to a yaml file.
     * @param prepareDatas
     * @param
     */
    public void storeToYamlFolder(Map<String, PrepareData> prepareDatas, File folder) {
        BaseDataUtil.storeToYamlByCase(prepareDatas, folder, null);

    }

    /**
     * Read from yaml file
     *
     * @param file
     * @param classLoader
     * @return
     */
    public Map<String, PrepareData> loadFromYaml(File file, ClassLoader classLoader) {
        if (file.exists()) {
            String str;
            try {
                str = FileUtils.readFileToString(file);
            } catch (Exception e) {
                Assert.assertTrue(false, "Failed to read yaml,  Exception:");
                log.error(e.getMessage());
                return null;
            }
            return BaseDataUtil.loadFromYaml(str, classLoader);
        }
        return null;
    }

    /**
     * Read from yaml file
     * A case corresponds to a yaml file.
     * @param
     * @param classLoader
     * @return
     */
    public Map<String, PrepareData> loadFromYamlFolder(File folder, ClassLoader classLoader) {
        if (folder.exists() && folder.isDirectory()) {
            return BaseDataUtil.loadFromYamlByCase(folder, classLoader, null);
        }
        return null;
    }

    /**
     * Setter method for property <tt>dbDatasProcessor</tt>.
     *
     * @param dbDatasProcessor
     *            value to be assigned to property dbDatasProcessor
     */
    public void setDbDatasProcessor(DBDatasProcessor dbDatasProcessor) {
        this.dbDatasProcessor = dbDatasProcessor;
    }

    /**
     *
     * @param clsz
     * @param actsRuntimeContext
     */
    public void invokeIActsMethods(Class<? extends Annotation> clsz,
                                   ActsRuntimeContext actsRuntimeContext) {
        List<IActsMethod> list = this.annoationMethods.get(clsz.getSimpleName());

        if (list == null) {
            return;
        }

        for (IActsMethod method : list) {
            method.invoke(actsRuntimeContext);
        }
    }

    /**
     * Gets prepare template.
     *
     * @return the prepare template
     */
    public PrepareTemplate getPrepareTemplate() {
        return prepareTemplate;
    }

    /**
     * Sets prepare template.
     *
     * @param prepareTemplate the prepare template
     */
    public void setPrepareTemplate(PrepareTemplate prepareTemplate) {
        this.prepareTemplate = prepareTemplate;
    }

    /**
     * Gets test unit handler.
     *
     * @return the test unit handler
     */
    public TestUnitHandler getTestUnitHandler() {
        return testUnitHandler;
    }

    /**
     * Sets test unit handler.
     *
     * @param testUnitHandler the test unit handler
     */
    public void setTestUnitHandler(TestUnitHandler testUnitHandler) {
        this.testUnitHandler = testUnitHandler;
    }

    /**
     * Gets tested method name.
     *
     * @return the tested method name
     */
    public String getTestedMethodName() {
        return testedMethodName;
    }

    /**
     * Sets tested method name.
     *
     * @param testedMethodName the tested method name
     */
    public void setTestedMethodName(String testedMethodName) {
        this.testedMethodName = testedMethodName;
    }

    /**
     * Gets prepare datas.
     *
     * @return the prepare datas
     */
    public Map<String, PrepareData> getPrepareDatas() {
        return prepareDatas;
    }

    /**
     * Sets prepare datas.
     *
     * @param prepareDatas the prepare datas
     */
    public void setPrepareDatas(Map<String, PrepareData> prepareDatas) {
        this.prepareDatas = prepareDatas;
    }

    /**
     * Gets test data file path.
     *
     * @return the test data file path
     */
    public String getTestDataFilePath() {
        return testDataFilePath;
    }

    /**
     * Sets test data file path.
     *
     * @param testDataFilePath the test data file path
     */
    public void setTestDataFilePath(String testDataFilePath) {
        this.testDataFilePath = testDataFilePath;
    }

    /**
     * Gets acts runtime context.
     *
     * @return the acts runtime context
     */
    public ActsRuntimeContext getActsRuntimeContext() {
        return actsRuntimeContext;
    }

    /**
     * Sets acts runtime context.
     *
     * @param actsRuntimeContext the acts runtime context
     */
    public void setActsRuntimeContext(ActsRuntimeContext actsRuntimeContext) {
        this.actsRuntimeContext = actsRuntimeContext;
    }

    /**
     * Is invok model boolean.
     *
     * @return the boolean
     */
    public static boolean isInvokModel() {
        return invokModel;
    }

    /**
     * Sets invok model.
     *
     * @param invokModel the invok model
     */
    public static void setInvokModel(boolean invokModel) {
        ActsTestBase.invokModel = invokModel;
    }

}
