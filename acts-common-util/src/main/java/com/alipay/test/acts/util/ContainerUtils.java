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

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.alipay.test.acts.db.enums.CSVColEnum;
import com.alipay.test.acts.model.VirtualList;
import com.alipay.test.acts.model.VirtualMap;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mahongliang
 * @version $Id: ContainerUtils.java, v 0.1 2015年11月20日 下午9:08:16  Exp $
 */
public class ContainerUtils {

    /**
     * list or map
     *
     * @param rawType
     * @param keyType
     * @param ValueType
     * @param csvRoot
     * @param setCalue
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void handContainer(Class<?> rawType, Type keyType, Type ValueType,
                                     final String csvRoot, Set<String> setCalue,
                                     Set<String> sbfWarn, Boolean isInterface) {
        if (keyType == null || null == rawType || CSVApisUtil.isWrapClass(rawType)) {
            return;
        }
        //If it already exists, do nothing and return
        File file = new File(csvRoot);
        if (file.exists()) {
            return;
        }

        if (Collection.class.isAssignableFrom(rawType)) {
            //merge at the outermost layer
            handContainerList(rawType, keyType, setCalue, csvRoot, sbfWarn, isInterface);
        } else if (Map.class.isAssignableFrom(rawType)) {
            handContainerMap(rawType, keyType, ValueType, csvRoot, setCalue, sbfWarn, isInterface);
        } else {
            if (rawType.isArray()) {
                rawType = rawType.getComponentType();
            }
            handContainerNoraml(rawType, keyType, csvRoot, setCalue, sbfWarn, isInterface);
        }

        return;
    }

    /**
     *
     * @param rawType
     * @param keyType
     * @param setCalue
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    private static void handContainerNoraml(Class<?> rawType, Type keyType, String csvRoot,
                                            Set<String> setCalue, Set<String> sbfWarn,
                                            Boolean isInterface) {
        Class<?> keyRaw = CSVApisUtil.getParameRawCls(keyType);
        if (keyType instanceof ParameterizedType) {
            if (Map.class.isAssignableFrom(keyRaw) || Collection.class.isAssignableFrom(keyRaw)) {
                Type innerFirst = CSVApisUtil.HandMutiParameType(keyType, 0);
                Type innerSecond = CSVApisUtil.HandMutiParameType(keyType, 1);
                String cvsChild = null;
                if (Map.class.isAssignableFrom(keyRaw)) {
                    cvsChild = CSVApisUtil.getGenericCsvFileName(keyRaw,
                        CSVApisUtil.getClass(innerSecond, 1), csvRoot);
                } else {
                    cvsChild = CSVApisUtil.getGenericCsvFileName(keyRaw,
                        CSVApisUtil.getClass(innerFirst, 0), csvRoot);
                }
                handContainer(CSVApisUtil.getParameRawCls(keyType), innerFirst, innerSecond,
                    cvsChild, setCalue, sbfWarn, isInterface);

                //external
                CSVApisUtil.doProcess(rawType, keyRaw, setCalue, csvRoot, sbfWarn);
                csvReplaceObj(keyRaw, cvsChild, csvRoot, sbfWarn);
            } else {
                Type innerFirst = CSVApisUtil.HandMutiParameType(keyType, 0);
                String cvsChild = CSVApisUtil.getGenericCsvFileName(keyRaw,
                    CSVApisUtil.getClass(innerFirst, 0), csvRoot);
                handContainerNoraml(keyRaw, innerFirst, cvsChild, setCalue, sbfWarn, isInterface);
                //external
                CSVApisUtil.doProcess(rawType, keyRaw, setCalue, csvRoot, sbfWarn);
                csvReplaceObj(keyRaw, cvsChild, csvRoot, sbfWarn);
            }
        } else {
            //raw type
            if (null != keyRaw && keyRaw.isArray()) {
                keyRaw = keyRaw.getComponentType();
            }
            try {
                witeClassListToCvs(rawType, CSVApisUtil.getClass(keyType, 0), setCalue, sbfWarn,
                    csvRoot);
            } catch (Exception e) {
                sbfWarn.add(rawType.toString());
            }
        }

    }

    /**
     *
     * @param rawType
     * @param keyType
     * @param valueType
     * @param setCalue
     * @throws IOException
     * @throws FileNotFoundException
     */
    private static void handContainerMap(Class<?> rawType, Type keyType, Type valueType,
                                         String csvRoot, Set<String> setCalue, Set<String> sbfWarn,
                                         Boolean isInterface) {
        if (!Map.class.isAssignableFrom(rawType)) {
            return;
        }
        //Key
        Class<?> keyCls = CSVApisUtil.getClass(keyType, 0);
        String keyPath = CSVApisUtil.getGenericCsvFileName(keyCls, null, csvRoot);
        //external
        CSVApisUtil.doProcess(keyCls, null, setCalue, keyPath, sbfWarn);

        //Value
        Class<?> valueRaw = CSVApisUtil.getParameRawCls(valueType);
        String valuePath = null;
        if (valueType instanceof ParameterizedType) {
            if (Map.class.isAssignableFrom(valueRaw)) {

                Class<?> clsMap = CSVApisUtil.getClass(
                    CSVApisUtil.HandMutiParameType(valueType, 1), 0);
                valuePath = CSVApisUtil.getGenericCsvFileName(valueRaw, clsMap, csvRoot);
            } else {
                Class<?> clsMap = CSVApisUtil.getClass(
                    CSVApisUtil.HandMutiParameType(valueType, 0), 0);
                valuePath = CSVApisUtil.getGenericCsvFileName(valueRaw, clsMap, csvRoot);
            }

            handContainer(valueRaw, CSVApisUtil.HandMutiParameType(valueType, 0),
                CSVApisUtil.HandMutiParameType(valueType, 1), valuePath, setCalue, sbfWarn,
                isInterface);
        } else {
            valuePath = CSVApisUtil.getGenericCsvFileName(CSVApisUtil.getClass(valueType, 0), null,
                csvRoot);
            CSVApisUtil.doProcess(CSVApisUtil.getClass(valueType, 0), null, setCalue, valuePath,
                sbfWarn);
        }

        //handled separately if list
        if (valueType instanceof ParameterizedType) {
            handleMapAndList(rawType, keyCls, (Class) ((ParameterizedType) valueType).getRawType(),
                isInterface, csvRoot, keyPath, valuePath, setCalue);
        } else {
            handleMapAndList(rawType, keyCls, CSVApisUtil.getClass(valueType, 0), isInterface,
                csvRoot, keyPath, valuePath, setCalue);
        }
    }

    /**
     *
     * @param rawType
     * @param keyType
     * @param setCalue
     * @throws IOException
     * @throws FileNotFoundException
     */
    private static void handContainerList(Class<?> rawType, Type keyType, Set<String> setCalue,
                                          String csvRoot, Set<String> sbfWarn, Boolean isInterface) {
        if (!Collection.class.isAssignableFrom(rawType)) {
            return;
        }

        Class<?> keyRaw = CSVApisUtil.getParameRawCls(keyType);
        Class<?> keyRawCls = null;
        String cvsChild = null;

        //all <>
        if (keyType instanceof ParameterizedType) {

            //Map or Collection
            if (Map.class.isAssignableFrom(keyRaw) || Collection.class.isAssignableFrom(keyRaw)) {

                Type innerFirst = CSVApisUtil.HandMutiParameType(keyType, 0);
                Type innerSecond = CSVApisUtil.HandMutiParameType(keyType, 1);
                if (Map.class.isAssignableFrom(keyRaw)) {
                    //Map
                    cvsChild = CSVApisUtil.getGenericCsvFileName(keyRaw,
                        CSVApisUtil.getClass(innerFirst, 1), csvRoot);
                } else {
                    //other Collection
                    cvsChild = CSVApisUtil.getGenericCsvFileName(keyRaw,
                        CSVApisUtil.getClass(innerSecond, 0), csvRoot);
                }
                handContainer(CSVApisUtil.getParameRawCls(keyType), innerFirst, innerSecond,
                    cvsChild, setCalue, sbfWarn, isInterface);
                keyRawCls = keyRaw;

            } else {
                //not Collection and Map
                Type innerFirst = CSVApisUtil.HandMutiParameType(keyType, 0);
                cvsChild = CSVApisUtil.getGenericCsvFileName(keyRaw,
                    CSVApisUtil.getClass(innerFirst, 0), csvRoot);
                handContainerNoraml(keyRaw, innerFirst, cvsChild, setCalue, sbfWarn, isInterface);
            }
        } else {
            keyRawCls = CSVApisUtil.getClass(keyType, 0);
            cvsChild = CSVApisUtil.getGenericCsvFileName(keyRawCls, null, csvRoot);
            CSVApisUtil.doProcess(keyRawCls, null, setCalue, cvsChild, sbfWarn);
        }
        //handled separately if list
        handleMapAndList(rawType, keyRawCls, null, isInterface, csvRoot, cvsChild, null, setCalue);
    }

    private static void handleMapAndList(Class<?> rawType, Class<?> childClsOne,
                                         Class<?> childClsNext, Boolean isInterface,
                                         String csvRoot, String csvChildPath, String csvSecondPath,
                                         Set<String> setCalue) {
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

        if (Collection.class.isAssignableFrom(rawType)) {
            //       if (isInterface) {
            List<String> value = new ArrayList<String>();
            value.add(VirtualList.class.getName());
            //attribute name
            value.add("virtualList");
            //attribute type
            if (CSVApisUtil.isWrapClass(childClsOne)) {
                value.add(rawType.getName());
                //data rule
                value.add("");
                if (StringUtils.equals(childClsOne.getName(), "java.lang.String")) {
                    value.add("Y");
                    value.add("1");
                } else {
                    CSVApisUtil.addSimpleValue("virtualList", childClsOne, value);
                }
            } else {
                value.add(rawType.getName());
                //data rule
                value.add("");
                //prevent infinite loops
                if (setCalue.contains(childClsOne.getName())) {
                    value.add("N");
                    value.add("null");
                } else {
                    value.add("Y");
                    value.add(CSVApisUtil.cutCsvName(csvChildPath) + "@1");
                }
            }
            outputValues.add(value.toArray(new String[value.size()]));

        } else if (Map.class.isAssignableFrom(rawType)) {
            for (int i = 0; i < 2; i++) {
                List<String> value = new ArrayList<String>();
                if (i == 0) {
                    value.add(VirtualMap.class.getName());
                    //attribute name
                    value.add("mapKey");
                    //attribute type
                    if (CSVApisUtil.isWrapClass(childClsOne)) {
                        value.add(childClsOne.getName());
                        //data type
                        value.add("");
                        if (StringUtils.equals(childClsOne.getName(), "java.lang.String")) {
                            value.add("Y");
                            value.add("1");
                        } else {
                            CSVApisUtil.addSimpleValue("mapKey", childClsOne, value);
                        }
                    } else {
                        value.add(childClsOne.getName());
                        //data type
                        value.add("");
                        //prevent infinite loops
                        if (setCalue.contains(childClsOne.getName())) {
                            value.add("N");
                            value.add("null");
                        } else {
                            value.add("Y");
                            value.add(CSVApisUtil.cutCsvName(csvChildPath) + "@1");
                        }
                    }
                } else {
                    value.add(null);
                    //attribute name
                    value.add("mapValue");
                    if (CSVApisUtil.isWrapClass(childClsNext)) {
                        value.add(childClsNext.getName());
                        //data rule
                        value.add("");
                        if (StringUtils.equals(childClsNext.getName(), "java.lang.String")) {
                            value.add("Y");
                            value.add("1");
                        } else {
                            CSVApisUtil.addSimpleValue("mapValue", childClsNext, value);
                        }
                    } else {
                        //attribute type
                        value.add(childClsNext.getName());

                        //data rule
                        value.add("");
                        //prevent infinite loops
                        if (setCalue.contains(childClsNext.getName())) {
                            value.add("N");
                            value.add("null");
                        } else {
                            value.add("Y");
                            value.add(CSVApisUtil.cutCsvName(csvSecondPath) + "@1");
                        }
                    }
                }
                outputValues.add(value.toArray(new String[value.size()]));
            }
        }
        CSVApisUtil.writeToCsv(FileUtil.getTestResourceFileByRootPath(csvRoot), outputValues);
    }

    private static String witeClassListToCvs(Class<?> clsParent, Class<?> clsChild,
                                             Set<String> setCalue, Set<String> sbfWarn,
                                             final String csvRoot) throws IOException {
        String cvsChild = null;
        //internal
        if (null != clsChild) {
            cvsChild = CSVApisUtil.getGenericCsvFileName(clsChild, null, csvRoot);
            CSVApisUtil.doProcess(clsChild, null, setCalue, cvsChild, sbfWarn);
        }

        //external
        CSVApisUtil.doProcess(clsParent, null, setCalue, csvRoot, sbfWarn);

        csvReplaceObj(clsChild, cvsChild, csvRoot, sbfWarn);

        return csvRoot;
    }

    /**
     *
     * @param clsChild
     * @param cvsChild
     * @param csvParent
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void csvReplaceObj(Class<?> clsChild, String cvsChild, String csvParent,
                                      Set<String> sbfWarn) {
        try {
            if (null != clsChild) {
                FileReader fReader = new FileReader(csvParent);
                CSVReader csvReader = new CSVReader(fReader);
                List<String[]> readLine = csvReader.readAll();
                List<String> addLine = new ArrayList<String>(6);
                int i = 0;
                for (String[] readDetail : readLine) {
                    if (StringUtils.equals(readDetail[2], "java.lang.Object")) {
                        addLine.add(readDetail[0]);
                        addLine.add(readDetail[1]);
                        addLine.add(clsChild.getName());
                        addLine.add(readDetail[3]);

                        if (CSVApisUtil.isWrapClass(clsChild)) {
                            if (StringUtils.equals(clsChild.getName(), "java.lang.String")) {
                                addLine.add("Y");
                                addLine.add("1");
                            } else {
                                CSVApisUtil.addSimpleValue("", clsChild, addLine);
                            }
                        } else if (StringUtils.isNotBlank(cvsChild)) {
                            File openFile = new File(cvsChild);
                            if (openFile.exists()) {
                                addLine.add("Y");
                                addLine.add(CSVApisUtil.cutCsvName(cvsChild) + "@1");
                            } else {
                                addLine.add("N");
                                addLine.add("");
                            }
                        } else {
                            addLine.add("N");
                            addLine.add("");
                        }
                        break;
                    }
                    i++;
                }

                readLine.set(i, addLine.toArray(new String[addLine.size()]));

                csvReader.close();
                fReader.close();

                FileWriter fWrite = new FileWriter(csvParent);
                CSVWriter csvWriter = new CSVWriter(fWrite);
                csvWriter.writeAll(readLine);
                csvWriter.close();
                fWrite.close();
            }
        } catch (Exception e) {
            sbfWarn.add("failed to add sub-file [" + CSVApisUtil.cutCsvName(cvsChild)
                        + "] to file [" + CSVApisUtil.cutCsvName(csvParent) + "]");
        }
    }
}
