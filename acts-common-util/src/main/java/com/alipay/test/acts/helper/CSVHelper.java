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
package com.alipay.test.acts.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;
import com.alipay.test.acts.db.enums.CSVColEnum;
import com.alipay.test.acts.log.ActsLogUtil;
import com.alipay.test.acts.object.ActsObjectUtil;
import com.alipay.test.acts.object.manager.ObjectTypeManager;
import com.alipay.test.acts.util.FileUtil;
import ognl.OgnlException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.alipay.test.acts.exception.ActsException;

/**
 * CSV Auxiliary tools
 * 
 * @author baishuo.lp
 * @version $Id: CSVHelper.java, v 0.1 2015年8月13日 下午9:02:09 baishuo.lp Exp $
 */
public class CSVHelper {

    private static final Log               LOG               = LogFactory.getLog(CSVHelper.class);

    private static final int               STATIC            = 0x00000008;
    private static final int               FINAL             = 0x00000010;

    private static final ObjectTypeManager objectTypeManager = new ObjectTypeManager();

    /**
     * 
     * @param objClass
     * @param field
     * @param value
     */
    private static void addSimpleValue(Class<?> objClass, Field field, List<String> value) {
        if (StringUtils.equals(java.util.Date.class.getName(), field.getType().getName())) {
            value.add("D");
            value.add("today");
        } else if (StringUtils
            .equals(java.util.Currency.class.getName(), field.getType().getName())) {

            value.add("Y");
            value.add("CNY");

        } else if (StringUtils.equals(objClass.getSimpleName(), "MultiCurrencyMoney")
                   && StringUtils.equals(field.getName(), "currencyValue")) {

            value.add("Y");
            value.add("156");

        } else if (StringUtils.equals(field.getType().getName(), "int")
                   || StringUtils.equals(field.getType().getName(), "java.lang.Integer")
                   || StringUtils.equals(field.getType().getName(), "long")
                   || StringUtils.equals(field.getType().getName(), "java.lang.Long")
                   || StringUtils.equals(field.getType().getName(), "short")) {
            value.add("Y");
            value.add("0");
        } else if (StringUtils.equals(field.getType().getName(), "float")
                   || StringUtils.equals(field.getType().getName(), "double")) {
            value.add("Y");
            value.add("0.0");
        } else if (StringUtils.equals(field.getType().getName(), "boolean")) {
            value.add("Y");
            value.add("false");
        } else if (StringUtils.equals(field.getType().getName(), "char")) {
            value.add("Y");
            value.add("A");
        } else {
            value.add("Y");
            value.add("");
        }
    }

    @SuppressWarnings("unchecked")
    public static int insertObjDataAndReturnIndex(Object actual, String csvPath) {

        List<String[]> outputValues = new ArrayList<String[]>();

        File file = FileUtil.getTestResourceFileByRootPath(csvPath);
        if (file.exists()) {
            outputValues = readFromCsv(file);
        } else {
            List<String> header = new ArrayList<String>();
            header.add(CSVColEnum.CLASS.getCode());
            header.add(CSVColEnum.PROPERTY.getCode());
            header.add(CSVColEnum.TYPE.getCode());
            header.add(CSVColEnum.RULE.getCode());
            header.add(CSVColEnum.FLAG.getCode());
            outputValues.add(header.toArray(new String[header.size()]));

            //supper class supported
            List<Field> fields = ActsObjectUtil.getAllFields(actual.getClass());

            int i = 1;

            //not value
            for (Field field : fields) {

                //if static or final, do nothing
                if ((field.getModifiers() & STATIC) > 0 || (field.getModifiers() & FINAL) > 0) {
                    continue;
                }
                List<String> value = new ArrayList<String>();
                if (1 == i) {

                    value.add(actual.getClass().getName());
                } else {
                    value.add("");
                }
                value.add(field.getName());
                value.add(field.getType().getName());
                value.add("");
                value.add("Y");
                outputValues.add(value.toArray(new String[value.size()]));
                i++;
            }
        }

        //value
        int index = outputValues.get(0).length - 4;
        int row = outputValues.size();

        for (int i = 0; i < row; i++) {

            List<String> value = new ArrayList<String>();
            for (int j = 0; j < outputValues.get(i).length; j++) {
                value.add(outputValues.get(i)[j]);
            }
            if (0 == i) {
                value.add("value" + String.valueOf(index));
            } else {
                Object actualField = null;
                try {
                    actualField = ActsObjectUtil.getProperty(actual, value.get(1));
                } catch (OgnlException e) {

                    ActsLogUtil.warn(LOG,
                        "failed to obtain obj:" + actual + ", attribute:" + value.get(1)
                                + ". error stack:" + e);
                    throw new RuntimeException(e);
                }
                if (actualField == null) {
                    value.add("null");
                } else {
                    //supper class supported
                    Field field = ActsObjectUtil.getField(actual.getClass(), value.get(1));
                    Class<?> clz = field.getType();

                    if (objectTypeManager.isSimpleType(clz)) {

                        //simple
                        String simpleValue = objectTypeManager.getSimpleObjValue(clz, actualField,
                            value.get(1));
                        value.add(simpleValue);
                    } else if (objectTypeManager.isCollectionType(clz)) {

                        Class<?> argumentClass;

                        if (clz.isArray()) {
                            //Array
                            argumentClass = clz.getComponentType();
                        } else {
                            //collection
                            argumentClass = objectTypeManager.getCollectionItemClass(
                                field.getGenericType(), clz);
                        }
                        if (objectTypeManager.isSimpleType(argumentClass)) {
                            String collectionStr = objectTypeManager.getCollectionObjectString(clz,
                                actualField, true, null);
                            value.add(collectionStr);
                        } else {

                            String subCsvPath = getCsvFileName(argumentClass, csvPath);
                            String collectionStr = objectTypeManager.getCollectionObjectString(clz,
                                actualField, false, subCsvPath);
                            value.add(collectionStr);
                        }
                    } else {

                        //comples type
                        String subCsvPath = getCsvFileName(clz, csvPath);
                        int subIndex = insertObjDataAndReturnIndex(actualField, subCsvPath);
                        value.add(clz.getSimpleName() + ".csv@" + String.valueOf(subIndex));
                    }
                }
            }
            outputValues.set(i, value.toArray(new String[value.size()]));
        }
        writeToCsv(file, outputValues);
        return index;
    }

    /**
     * get csv name
     *
     * @param objClass
     * @param csvPath
     * @return
     */
    public static String getCsvFileName(Class<?> objClass, String csvPath) {

        String[] paths = csvPath.split("/");
        ArrayUtils.reverse(paths);

        String className = objClass.getSimpleName() + ".csv";

        if (!StringUtils.equals(className, paths[0])) {
            csvPath = StringUtils.replace(csvPath, paths[0], className);
        }

        return csvPath;
    }

    /**
     * write csv
     *
     * @param file
     * @param outputValues
     */
    public static void writeToCsv(File file, List<String[]> outputValues) {

        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (Exception e) {
            ActsLogUtil.warn(LOG, "File not found :" + file.getName() + "!" + e);
            throw new RuntimeException(e);
        }

        try {
            OutputStreamWriter osw = null;
            osw = new OutputStreamWriter(outputStream);
            CSVWriter csvWriter = new CSVWriter(osw);
            csvWriter.writeAll(outputValues);
            csvWriter.close();
            ActsLogUtil.warn(LOG, file.getName() + " save sucessfully");
        } catch (Exception e) {
            ActsLogUtil.warn(LOG, "Failed to save file:" + file.getName() + e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Read csv file based on path
     * 
     * @param csvPath
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static List readFromCsv(String csvPath) {
        File file = new File(csvPath);
        return readFromCsv(file);
    }

    /**
     * Read csv file based on path
     * 
     * @param csvPath
     * @param encoding
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static List readFromCsv(String csvPath, String encoding) {
        File file = new File(csvPath);
        return readFromCsv(file, encoding);
    }

    /**
     * Read csv file based on path
     * 
     * @param file
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static List readFromCsv(File file) {
        if (null == file) {
            throw new ActsException("file is null");
        }
        if (!file.exists()) {
            throw new ActsException(file.getAbsolutePath() + "file does not exist");
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (Exception e) {
            LOG.error("Failed to read file [" + file.getName() + "], ", e);
        }

        List tableList = null;
        try {
            InputStreamReader isr = new InputStreamReader(inputStream);
            CSVReader csvReader = new CSVReader(isr);
            tableList = csvReader.readAll();
            csvReader.close();
            isr.close();
            inputStream.close();
        } catch (Exception e) {
            LOG.error("Failed to read data through CSV file stream.", e);
        }
        return tableList;
    }

    /**
     * Read csv file based on path, specify encoding
     * 
     * @param file
     * @param encode example:UTF-8
     * 
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static List readFromCsv(File file, String encode) {
        if (null == file) {
            throw new ActsException("file is null");
        }
        if (!file.exists()) {
            throw new ActsException(file.getAbsolutePath() + "file does not exist");
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (Exception e) {
            LOG.error("Failed to read file [" + file.getName() + "]", e);
        }
        List tableList = null;
        try {
            InputStreamReader isr = new InputStreamReader(inputStream, Charset.forName(encode));
            CSVReader csvReader = new CSVReader(isr);
            tableList = csvReader.readAll();
            csvReader.close();
            isr.close();
            inputStream.close();
        } catch (Exception e) {
            LOG.error("Failed to read data through CSV file stream.", e);
        }
        return tableList;
    }

}