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
package com.alipay.test.acts.util;

import au.com.bytecode.opencsv.CSVWriter;
import com.alipay.test.acts.log.ActsLogUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.azeckoski.reflectutils.ClassFields;
import org.azeckoski.reflectutils.ReflectUtils;
import org.azeckoski.reflectutils.ClassData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.alipay.test.acts.db.enums.CSVColEnum;
import com.alipay.test.acts.log.ActsLogUtil;
import com.alipay.test.acts.object.manager.ObjectTypeManager;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;
import com.google.common.reflect.TypeToken;

/**
 * external method for acts ide
 *
 * @author mahongliang
 * @version $Id: CSVApisUtil.java, v 0.1 2015年11月20日 下午12:48:57  Exp $
 */
public class CSVApisUtil {
    private static final Log LOG = LogFactory.getLog(CSVApisUtil.class);

    /**
     * External method. Parse Class
     *
     * @param clsName  class
     * @param classLoade  clsLoad
     * @param genRootPath csv root path
     * @throws ClassNotFoundException
     */
    public static Set<String> paraClassToCscFile(String clsName, ClassLoader classLoade,
                                                 String genRootPath) throws ClassNotFoundException {
        Set<String> sbfWarnError = new HashSet<String>();
        Class<?> specifyClass = classLoade.loadClass(clsName);
        if (specifyClass.isInterface()) {
            processInterface(specifyClass, genRootPath, sbfWarnError);
        } else {
            doProcess(specifyClass, null, new HashSet<String>(),
                mkCsvFolderForCls(specifyClass, null, genRootPath), sbfWarnError);
        }

        return sbfWarnError;
    }

    /**
     * External method. Parse the input and return parameters of the specified method
     *
     * @param clsName  class
     * @param classLoade  clsLoad
     * @param genRootPath csv root path
     * @throws ClassNotFoundException
     */
    public static Set<String> paraClassSpeciMethodToCscFile(String clsName, ClassLoader classLoade,
                                                            String genRootPath, String methodName,
                                                            boolean isResultOnly)
                                                                                 throws ClassNotFoundException {
        Set<String> sbfWarnError = new HashSet<String>();
        Class<?> specifyClass = classLoade.loadClass(clsName);
        processInterfaceMethod(specifyClass, genRootPath, sbfWarnError, methodName, isResultOnly);

        return sbfWarnError;

    }

    /**
     * create a CSV file based on the class
     *
     * @param clsMain
     * @param genRootPath
     * @return created csv file
     */
    public static String mkCsvFolderForCls(Class<?> clsMain, Type clsType, String genRootPath) {
        if (isWrapClass(clsMain)) {
            LOG.warn("do nothing when simple type");
            return null;
        }
        if (clsMain.isArray() && isWrapClass(clsMain.getComponentType())) {
            LOG.warn("do nothing  when the element'class in array is simple type");
            return null;
        }

        Class<?> clsAdd = clsMain.isArray() ? clsMain.getComponentType() : clsMain;

        String csvRootPath;
        String csvFolder;
        File objModel = FileUtil.getTestResourceFileByRootPath(genRootPath);
        if (!objModel.exists()) {
            objModel.mkdir();
        }
        if (clsType == null) {
            csvFolder = genRootPath + clsAdd.getSimpleName();
            csvRootPath = csvFolder + "/" + clsAdd.getSimpleName() + ".csv";
        } else {
            if (clsType instanceof ParameterizedType) {
                Class<?> clsSub = getParameRawCls(clsType);
                Type typeInner = HandMutiParameType(clsType, 0);
                Class<?> typeSub = getClass(typeInner, 0);
                //csv name with a generic name
                csvFolder = genRootPath + clsAdd.getSimpleName() + "_" + clsSub.getSimpleName()
                            + "_" + typeSub.getSimpleName();
                csvRootPath = csvFolder + "/" + clsAdd.getSimpleName() + "_"
                              + clsSub.getSimpleName() + "_" + typeSub.getSimpleName() + ".csv";
            } else {
                Class<?> clsSub = getClass(clsType, 0);
                //csv name with a generic name
                csvFolder = genRootPath + clsAdd.getSimpleName() + "_" + clsSub.getSimpleName();
                csvRootPath = csvFolder + "/" + clsAdd.getSimpleName() + "_"
                              + clsSub.getSimpleName() + ".csv";
            }

        }
        File file = FileUtil.getTestResourceFileByRootPath(csvFolder);
        if (!file.exists()) {
            file.mkdir();
        }
        return csvRootPath;
    }

    /**
     * get .csv file path based on the class
     *
     * @param objClass
     * @param csvPath
     * @return
     */
    private static String getCsvFileName(Class<?> objClass, String csvPath) {

        if (isWrapClass(objClass)) {
            LOG.warn("do nothing when simple type");
            return null;
        }
        String[] paths = csvPath.split("/");
        ArrayUtils.reverse(paths);

        String className = objClass.getSimpleName() + ".csv";

        if (!StringUtils.equals(className, paths[0])) {
            csvPath = StringUtils.replace(csvPath, paths[0], className);
        }

        return csvPath;
    }

    public static String getGenericCsvFileName(Class<?> sueperClass, Class<?> subClass,
                                               String csvPath) {

        if (isWrapClass(sueperClass)) {
            LOG.warn("do nothing when simple type");
            return null;
        }

        if (null == subClass) {
            return getCsvFileName(sueperClass, csvPath);
        }
        String[] paths = csvPath.split("/");
        ArrayUtils.reverse(paths);

        String className = sueperClass.getSimpleName() + "_" + subClass.getSimpleName() + ".csv";

        if (!StringUtils.equals(className, paths[0])) {
            csvPath = StringUtils.replace(csvPath, paths[0], className);
        }

        return csvPath;
    }

    /**
     *
     * @param
     * @param
     */
    private static void processInterface(Class<?> specifyClass, String genRootPath,
                                         Set<String> sbfWarnError) {
        ReflectUtils refUtil = ReflectUtils.getInstance();
        ClassFields<?> clsFiled = refUtil.analyzeClass(specifyClass);
        ClassData<?> getPro = clsFiled.getClassData();
        List<Method> listMetchod = getPro.getMethods();
        for (Method method : listMetchod) {
            Invokable invoke = Invokable.from(method);
            ImmutableList<Parameter> mParameters = invoke.getParameters();
            //input parameters
            for (Parameter parameter : mParameters) {

                try {
                    Class<?> paraCls = parameter.getType().getRawType();
                    TypeToken<?> preToken = parameter.getType();
                    TypeToken<?> genericTypeToken = preToken.resolveType(preToken.getType());
                    String csvFile = null;
                    if (!(genericTypeToken.getType() instanceof ParameterizedType)) {
                        doProcess(paraCls, null, new HashSet<String>(),
                            mkCsvFolderForCls(paraCls, null, genRootPath), sbfWarnError);
                    } else {
                        if (Map.class.isAssignableFrom(paraCls)) {
                            //MAP
                            csvFile = mkCsvFolderForCls(paraCls,
                                HandMutiParameType(genericTypeToken.getType(), 1), genRootPath);
                        } else {
                            csvFile = mkCsvFolderForCls(paraCls,
                                HandMutiParameType(genericTypeToken.getType(), 0), genRootPath);
                        }
                        ContainerUtils.handContainer(paraCls,
                            HandMutiParameType(genericTypeToken.getType(), 0),
                            HandMutiParameType(genericTypeToken.getType(), 1), csvFile,
                            new HashSet<String>(), sbfWarnError, true);
                    }

                } catch (Throwable e) {
                    //catch ex, then parsing other parameters
                    String genModelMsg = "translate class [" + parameter.getClass().getName()
                                         + "] to CSV failed:" + e.getMessage()
                                         + ", recommended to generate templates using class!"
                                         + "\n";
                    sbfWarnError.add(genModelMsg);
                }
            }
            try {
                //return parameters
                retClsToMkCsv(genRootPath, method, sbfWarnError);
            } catch (Throwable e) {
                //catch ex, then parsing other parameters
                String genModelMsg = "translate class" + method.getGenericReturnType().toString()
                                     + "] to CSV failed:" + e.getMessage()
                                     + ", recommended to generate templates using class!" + "\n";
                sbfWarnError.add(genModelMsg);
            }
        }
    }

    /**
     * generate the input and return parameters of the specified method in the interface
     *
     * @param specifyClass
     * @param genRootPath
     * @param sbfWarnError
     * @param methodName
     * @param isResultOnly
     */
    private static void processInterfaceMethod(Class<?> specifyClass, String genRootPath,
                                               Set<String> sbfWarnError, String methodName,
                                               boolean isResultOnly) {
        ReflectUtils refUtil = ReflectUtils.getInstance();
        ClassFields<?> clsFiled = refUtil.analyzeClass(specifyClass);
        ClassData<?> getPro = clsFiled.getClassData();
        List<Method> listMetchod = getPro.getMethods();
        for (Method method : listMetchod) {
            if (StringUtils.equals(method.getName(), methodName)) {
                Invokable invoke = Invokable.from(method);
                ImmutableList<Parameter> mParameters = invoke.getParameters();
                //input parameter
                if (!isResultOnly) {
                    for (Parameter parameter : mParameters) {

                        try {
                            Class<?> paraCls = parameter.getType().getRawType();
                            TypeToken<?> preToken = parameter.getType();
                            TypeToken<?> genericTypeToken = preToken
                                .resolveType(preToken.getType());
                            String csvFile = null;
                            if (!(genericTypeToken.getType() instanceof ParameterizedType)) {
                                doProcess(paraCls, null, new HashSet<String>(),
                                    mkCsvFolderForCls(paraCls, null, genRootPath), sbfWarnError);
                            } else {
                                if (Map.class.isAssignableFrom(paraCls)) {
                                    //MAP
                                    csvFile = mkCsvFolderForCls(paraCls,
                                        HandMutiParameType(genericTypeToken.getType(), 1),
                                        genRootPath);
                                } else {
                                    csvFile = mkCsvFolderForCls(paraCls,
                                        HandMutiParameType(genericTypeToken.getType(), 0),
                                        genRootPath);
                                }
                                ContainerUtils.handContainer(paraCls,
                                    HandMutiParameType(genericTypeToken.getType(), 0),
                                    HandMutiParameType(genericTypeToken.getType(), 1), csvFile,
                                    new HashSet<String>(), sbfWarnError, true);
                            }

                        } catch (Throwable e) {
                            //catch ex, then parsing other parameters
                            String genModelMsg = "class ["
                                                 + parameter.getClass().getName()
                                                 + "] to CSV failed:"
                                                 + e.getMessage()
                                                 + ", recommended to generate templates using class!"
                                                 + "\n";
                            sbfWarnError.add(genModelMsg);
                        }
                    }
                }
                try {
                    //return parameter
                    retClsToMkCsv(genRootPath, method, sbfWarnError);
                } catch (Throwable e) {
                    //catch ex, then parsing other parameters
                    String genModelMsg = "class [" + method.getGenericReturnType().toString()
                                         + "] to CSV failed:" + e.getMessage()
                                         + ", recommended to generate templates using class!"
                                         + "\n";
                    sbfWarnError.add(genModelMsg);
                }
            }

        }
    }

    /**
     * parse return parameter
     *
     * @param genRootPath
     * @param method
     */
    private static void retClsToMkCsv(String genRootPath, Method method, Set<String> sbfWarnError) {
        TypeToken<?> preToken = TypeToken.of(method.getGenericReturnType());
        TypeToken<?> genericTypeToken = preToken.resolveType(method.getGenericReturnType());
        Class<?> rawClss = genericTypeToken.getRawType();

        if (!(method.getGenericReturnType() instanceof ParameterizedType)) {
            doProcess(genericTypeToken.getRawType(), null, new HashSet<String>(),
                mkCsvFolderForCls(rawClss, null, genRootPath), sbfWarnError);
        } else {
            if (Map.class.isAssignableFrom(rawClss)) {
                //MAP
                String csvFile = mkCsvFolderForCls(rawClss,
                    HandMutiParameType(genericTypeToken.getType(), 1), genRootPath);
                ContainerUtils.handContainer(rawClss,
                    HandMutiParameType(genericTypeToken.getType(), 0),
                    HandMutiParameType(genericTypeToken.getType(), 1), csvFile,
                    new HashSet<String>(), sbfWarnError, true);
            } else {
                //other
                String csvFile = mkCsvFolderForCls(rawClss,
                    HandMutiParameType(genericTypeToken.getType(), 0), genRootPath);
                ContainerUtils.handContainer(rawClss,
                    HandMutiParameType(genericTypeToken.getType(), 0), null, csvFile,
                    new HashSet<String>(), sbfWarnError, true);
            }
        }
    }

    private static final ObjectTypeManager objectTypeManager             = new ObjectTypeManager();

    /** multiple elements:"1;2;3" */
    private static final String            LIST_CONTENT_TEMPLATE         = "1";
    /** multiple elements:"FILE@1;2" */
    private static final String            COMPLEX_LIST_CONTENT_TEMPLATE = "FILE@1";
    private static final String            COMPLEX_TYPE_CONTENT_TEMPLATE = "FILE@1";
    private static final String            FILE_WORDS                    = "FILE";

    /**
     *
     * @param
     * @param
     */
    public static void doProcess(Class<?> classTopara, Class<?> subCls, Set<String> setCalue,
                                 String csvRoot, Set<String> sbfWarn) {
        if (StringUtils.isBlank(csvRoot)) {
            ActsLogUtil.warn(LOG, "The path is empty and the CSV file cannot be generated.");
            return;
        }
        if (null == classTopara) {
            ActsLogUtil.warn(LOG, "The class name is empty and cannot generate a CSV file");
            return;
        }

        if (classTopara.isArray()) {
            classTopara = classTopara.getComponentType();
        }

        if (isWrapClass(classTopara) || setCalue.contains(classTopara.getName())) {
            return;
        }

        File file = FileUtil.getTestResourceFileByRootPath(csvRoot);
        if (file.exists()) {
            ActsLogUtil.warn(LOG, "file [" + csvRoot + "] already exists,skip directly");
            return;
        }

        Map<String, Field> getPro = findTargetClsFields(classTopara);

        //prevent loops
        avoidDeedLoop(setCalue, classTopara, subCls);

        List<String[]> outputValues = new ArrayList<String[]>();
        //assemble the first line of the CSV file:the header line
        List<String> header = new ArrayList<String>();
        header.add(CSVColEnum.CLASS.getCode());
        header.add(CSVColEnum.PROPERTY.getCode());
        header.add(CSVColEnum.TYPE.getCode());
        header.add(CSVColEnum.RULE.getCode());
        header.add(CSVColEnum.FLAG.getCode());
        header.add("value");
        outputValues.add(header.toArray(new String[header.size()]));

        int i = 1;

        for (Entry<String, Field> proValue : getPro.entrySet()) {
            if (null == proValue.getValue()) {
                continue;
            }

            List<String> value = new ArrayList<String>();
            if (1 == i) {
                //If it is the first generated, the content needs to contain the class name.
                value.add(classTopara.getName());
            } else {
                value.add("");
            }
            //attribute's name
            value.add(proValue.getKey());
            //attribute's type name
            value.add(proValue.getValue().getType().getName());

            //data rule
            value.add("");

            //class type of this attribute
            Class<?> filedCls = proValue.getValue().getType();
            Boolean isHandle = false;
            if (setCalue.contains(filedCls.getName())) {
                //prevent infinite loops
                value.add("N");
                value.add("null");
                isHandle = true;
            } else if (isWrapClass(filedCls)) {

                addSimpleValue(proValue.getKey(), filedCls, value);
                isHandle = true;
            }

            if (null != proValue.getValue()) {
                if (!isHandle && Map.class.isAssignableFrom(filedCls)) {

                    try {
                        value.add("M");
                        Class<?> rawClass = getParameRawCls(proValue.getValue().getGenericType());

                        Type mapKeycls = HandMutiParameType(proValue.getValue().getGenericType(), 0);
                        Type mapValueCls = HandMutiParameType(proValue.getValue().getGenericType(),
                            1);

                        String mapPath;
                        if (mapValueCls instanceof ParameterizedType) {
                            mapPath = getGenericCsvFileName(rawClass,
                                (Class) ((ParameterizedType) mapValueCls).getRawType(), csvRoot);
                        } else {
                            mapPath = getGenericCsvFileName(rawClass, getClass(mapValueCls, 0),
                                csvRoot);
                        }

                        ContainerUtils.handContainer(rawClass, mapKeycls, mapValueCls, mapPath,
                            setCalue, sbfWarn, false);
                        value.add(cutCsvName(mapPath) + "@1");
                    } catch (Throwable e) {
                        //default value when failed
                        value.set(4, "N");
                        value.add("null");
                        LOG.warn("", e);
                    }
                } else if (!isHandle) {

                    Class<?> norMalCls = getClass(proValue.getValue().getGenericType(), 0);
                    //Array
                    if (filedCls.isArray()) {
                        norMalCls = filedCls.getComponentType();
                    }

                    value.add("Y");
                    if (objectTypeManager.isCollectionType(filedCls)) {
                        if (objectTypeManager.isSimpleType(norMalCls)) {
                            value.add(LIST_CONTENT_TEMPLATE);
                        } else if (Map.class.isAssignableFrom(norMalCls)) {
                            //List<Map<>>
                            TypeToken<?> preToken = TypeToken.of(proValue.getValue()
                                .getGenericType());
                            TypeToken<?> genericTypeToken = preToken.resolveType(proValue
                                .getValue().getGenericType());

                            Type mapType = HandMutiParameType(genericTypeToken.getType(), 0);
                            Class<?> rawClass = CSVApisUtil.getParameRawCls(mapType);

                            Type mapKeycls = CSVApisUtil.HandMutiParameType(mapType, 0);
                            Type mapValueCls = CSVApisUtil.HandMutiParameType(mapType, 1);

                            String mapPath;
                            if (mapValueCls instanceof ParameterizedType) {
                                mapPath = getGenericCsvFileName(rawClass,
                                    (Class) ((ParameterizedType) mapValueCls).getRawType(), csvRoot);
                            } else {
                                mapPath = getGenericCsvFileName(rawClass, getClass(mapValueCls, 0),
                                    csvRoot);
                            }

                            ContainerUtils.handContainer(rawClass, mapKeycls, mapValueCls, mapPath,
                                setCalue, sbfWarn, false);
                            value.add(cutCsvName(mapPath) + "@1");

                        } else {

                            //Set<Object> is not supported
                            if (Set.class.isAssignableFrom(filedCls)) {
                                value.set(4, "N");
                                value.add("null");
                            } else {
                                //List<Object>:element is a complex object

                                //prevent nested list elements
                                if (setCalue.contains(norMalCls.getName())) {
                                    value.set(4, "N");
                                    value.add("null");
                                } else {
                                    value.add(COMPLEX_LIST_CONTENT_TEMPLATE.replace(FILE_WORDS,
                                        norMalCls.getSimpleName() + ".csv"));
                                }

                                try {
                                    doProcess(norMalCls, null, setCalue,
                                        getCsvFileName(norMalCls, csvRoot), sbfWarn);
                                } catch (Throwable e) {
                                    //default value when failed
                                    value.set(4, "N");
                                    value.set(5, "null");
                                    LOG.warn("process complex classes in list failed", e);
                                }
                            }
                        }
                    } else if (proValue.getValue().getGenericType() instanceof ParameterizedType) {
                        value.add(COMPLEX_LIST_CONTENT_TEMPLATE.replace(FILE_WORDS,
                            filedCls.getSimpleName() + "_" + norMalCls.getSimpleName() + ".csv"));
                        try {
                            doProcess(filedCls, norMalCls, setCalue,
                                getGenericCsvFileName(filedCls, norMalCls, csvRoot), sbfWarn);
                        } catch (Throwable e) {
                            //default value when failed
                            value.set(4, "N");
                            value.set(5, "null");
                            LOG.warn("process generic classes failed", e);
                        }
                    } else if (filedCls.isInterface()
                               || Modifier.isAbstract(filedCls.getModifiers())) {
                        //stop generating templates when interface or abstraction
                        value.set(4, "N");
                        value.add("null");
                    } else {
                        value.add(COMPLEX_TYPE_CONTENT_TEMPLATE.replace(FILE_WORDS,
                            filedCls.getSimpleName() + ".csv"));
                    }
                    if (!isWrapClass(norMalCls) && !setCalue.contains(norMalCls.getName())
                        && !Map.class.isAssignableFrom(norMalCls)) {
                        //deal with internal complex classes
                        try {
                            doProcess(norMalCls, null, setCalue,
                                getCsvFileName(norMalCls, csvRoot), sbfWarn);
                        } catch (Throwable e) {
                            //default value when failed
                            value.set(4, "N");
                            value.set(5, "null");
                            LOG.warn("process internal complex classes failed", e);
                        }
                    }
                }
            }
            outputValues.add(value.toArray(new String[value.size()]));
            i++;
        }

        //no field of complicated objects
        if (getPro.size() == 0) {
            sbfWarn.add(cutCsvName(csvRoot));
            List<String> value = new ArrayList<String>();
            value.add(classTopara.getName());
            value.add("");
            value.add("");
            value.add("");
            value.add("");
            value.add("");
            outputValues.add(value.toArray(new String[value.size()]));
        }
        writeToCsv(file, outputValues);
        setCalue.remove(classTopara.getName());
    }

    public static String cutCsvName(final String csvRoot) {
        return StringUtils.substringAfterLast(csvRoot, "/");
    }

    public static Map<String, Field> findTargetClsFields(Class<?> cls) {
        if (isWrapClass(cls)) {
            return null;
        }
        ReflectUtils refUtil = ReflectUtils.getInstance();
        ClassFields<?> clsFiled = refUtil.analyzeClass(cls);
        List<Field> getPro = clsFiled.getClassData().getFields();
        Map<String, Field> reClsProp = new HashMap<String, Field>();
        for (Field proValue : getPro) {
            if (Modifier.isFinal(proValue.getModifiers())
                || Modifier.isStatic(proValue.getModifiers())) {
                continue;
            }
            String propName = proValue.getName();
            reClsProp.put(propName, proValue);
        }

        return reClsProp;
    }

    public static void writeToCsv(File file, List<String[]> outputValues) {

        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (Exception e) {
            ActsLogUtil.warn(LOG, "file [" + file.getName() + "] not fund." + e);
            throw new RuntimeException(e);
        }
        //write the generated content to a CSV file
        try {
            OutputStreamWriter osw = null;
            osw = new OutputStreamWriter(outputStream);
            CSVWriter csvWriter = new CSVWriter(osw);
            csvWriter.writeAll(outputValues);
            csvWriter.close();
            ActsLogUtil.warn(LOG, file.getName() + "generated successfully");
        } catch (Exception e) {
            ActsLogUtil.warn(LOG, "write to CSV file failed:" + file.getName() + e);
            throw new RuntimeException(e);
        }
    }

    public static void addSimpleValue(String fieldName, Class<?> fieldType, List<String> value) {
        if (StringUtils.equals(java.util.Date.class.getName(), fieldType.getName())) {
            value.add("D");
            value.add("today");
        } else if (StringUtils.equals(java.util.Currency.class.getName(), fieldType.getName())) {

            value.add("Y");
            value.add("CNY");

        } else if (StringUtils.equals(fieldName, "currencyValue")) {
            //default value
            value.add("Y");
            value.add("156");

        } else if (StringUtils.equals(fieldType.getName(), "int")
                   || StringUtils.equals(fieldType.getName(), "java.lang.Integer")
                   || StringUtils.equals(fieldType.getName(), "long")
                   || StringUtils.equals(fieldType.getName(), "java.lang.Long")
                   || StringUtils.equals(fieldType.getName(), "short")) {
            value.add("Y");
            value.add("0");
        } else if (StringUtils.equals(fieldType.getName(), "float")
                   || StringUtils.equals(fieldType.getName(), "double")) {
            value.add("Y");
            value.add("0.0");
        } else if (StringUtils.equals(fieldType.getName(), "boolean")) {
            value.add("Y");
            value.add("false");
        } else if (StringUtils.equals(fieldType.getName(), "char")) {
            value.add("Y");
            value.add("A");
        } else if (StringUtils.equals(fieldType.getName(), "java.math.BigDecimal")) {
            value.add("Y");
            value.add("0.001");
        } else if (StringUtils.equals(fieldType.getName(), "java.lang.Object")) {
            value.set(2, "java.lang.Object");
            value.add("N");
            value.add("");
        } else if (StringUtils.equals(fieldType.getName(), "java.lang.Void")) {
            value.set(2, "java.lang.Void");
            value.add("N");
            value.add("null");
        } else {
            value.add("Y");
            value.add("null");
        }
    }

    /**
     * get the type of generic
     * @param type
     * @param i
     * @return
     */
    public static Class<?> getClass(Type type, int i) {
        if (type instanceof ParameterizedType) {

            return getGenericClass((ParameterizedType) type, i);
        } else if (type instanceof GenericArrayType) {
            return (Class) ((GenericArrayType) type).getGenericComponentType();
        } else if (type instanceof TypeVariable) {
            return getClass(((TypeVariable) type).getBounds()[0], 0);
        } else if (type instanceof WildcardType) {
            WildcardType wuleType = (WildcardType) type;
            if (null != wuleType.getUpperBounds()[0]) {
                return getClass(wuleType.getUpperBounds()[0], 0);
            } else {
                return getClass(wuleType.getLowerBounds()[0], 0);
            }
        } else if (type instanceof Class) {
            return (Class<?>) type;
        } else {
            return (Class<?>) type;
        }
    }

    /**
     *
     * @param type
     * @param i
     * @return
     */
    public static Type HandMutiParameType(Type type, int i) {
        if (type instanceof ParameterizedType) {
            try {
                ParameterizedType outType = (ParameterizedType) type;
                return outType.getActualTypeArguments()[i];
            } catch (Exception e) {
                return null;
            }
        } else if (type instanceof Class) {
            if (0 == i) {
                return type;
            }
            return null;
        } else {
            if (0 == i) {
                getClass(type, 0);
            } else {
                return null;
            }
        }
        return null;

    }

    /**
     *
     * @param type
     *
     * @return
     */
    public static Class<?> getParameRawCls(Type type) {
        if (type instanceof ParameterizedType) {
            try {
                ParameterizedType outType = (ParameterizedType) type;
                return (Class) outType.getRawType();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     *
     * @param type
     *
     * @return
     */
    public static int getTypeCount(Type type) {
        if (type instanceof ParameterizedType) {
            try {
                ParameterizedType outType = (ParameterizedType) type;
                return outType.getActualTypeArguments().length;
            } catch (Exception e) {
                return 0;
            }
        } else if (type instanceof Class) {
            return 0;
        }
        return 1;
    }

    private static Class<?> getGenericClass(ParameterizedType parameterizedType, int i) {
        Type genericClass = null;
        try {
            genericClass = parameterizedType.getActualTypeArguments()[i];
        } catch (Exception e) {
            return null;
        }

        if (genericClass instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) genericClass).getRawType();
        } else if (genericClass instanceof GenericArrayType) {
            return (Class<?>) ((GenericArrayType) genericClass).getGenericComponentType();
        } else if (genericClass instanceof TypeVariable) {
            return getClass(((TypeVariable) genericClass).getBounds()[0], 0);
        } else if (genericClass instanceof WildcardType) {
            WildcardType wuleType = (WildcardType) genericClass;
            if (null != wuleType.getUpperBounds()[0]) {
                return getClass(wuleType.getUpperBounds()[0], 0);
            } else {
                return getClass(wuleType.getLowerBounds()[0], 0);
            }

        } else {
            return (Class<?>) genericClass;
        }
    }

    private static void avoidDeedLoop(Set<String> mapLoop, Class<?> clsFullName, Class<?> subClass) {

        String clsName = clsFullName.getName();
        //filter out simple type
        if (isWrapClass(clsFullName)) {
            return;
        }

        if (null != subClass) {
            mapLoop.remove(clsFullName);
            clsName += "_" + subClass.getSimpleName();

        }
        if (!mapLoop.contains(clsName)) {
            mapLoop.add(clsName);
        }
    }

    private final static Set<String> simpleClassType = new HashSet<String>();

    static {
        simpleClassType.add("boolean");
        simpleClassType.add("java.lang.Integer");
        simpleClassType.add("java.lang.Float");
        simpleClassType.add("java.lang.Double");
        simpleClassType.add("java.lang.Long");
        simpleClassType.add("java.lang.Short");
        simpleClassType.add("java.lang.Byte");
        simpleClassType.add("java.lang.Boolean");
        simpleClassType.add("java.lang.Character");
        simpleClassType.add("java.util.Properties");
        simpleClassType.add("java.lang.String");
        simpleClassType.add("java.util.Date");

        simpleClassType.add("java.util.Currency");
        simpleClassType.add("java.math.BigDecimal");
        simpleClassType.add("java.io.Serializable");
        simpleClassType.add("java.lang.Object");
        simpleClassType.add("java.lang.Void");

    }

    /**
     *
     * @param clsToJude
     * @return
     */
    public static boolean isWrapClass(Class<?> clsToJude) {

        if (clsToJude.isPrimitive() || clsToJude.isEnum()
            || clsToJude.getName().toLowerCase().contains("enum")
            || simpleClassType.contains(clsToJude.getName())) {
            return true;
        }

        return false;

    }
}
