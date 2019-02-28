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
package com.alipay.test.acts.object.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.test.acts.cache.ActsCacheData;
import com.alipay.test.acts.db.enums.CSVColEnum;
import com.alipay.test.acts.exception.ActsException;
import com.alipay.test.acts.helper.CSVHelper;
import com.alipay.test.acts.log.ActsLogUtil;
import com.alipay.test.acts.model.VirtualList;
import com.alipay.test.acts.model.VirtualMap;
import com.alipay.test.acts.object.ActsObjectUtil;
import com.alipay.test.acts.object.enums.UnitFlagEnum;
import com.alipay.test.acts.object.manager.ObjectTypeManager;
import com.alipay.test.acts.util.BaseDataUtil;
import com.alipay.test.acts.util.FileUtil;
import com.alipay.test.acts.yaml.cpUnit.property.BaseUnitProperty;
import com.alipay.test.acts.yaml.cpUnit.property.ListObjectUnitProperty;
import com.alipay.test.acts.yaml.cpUnit.property.MapObjectUnitProperty;
import com.alipay.test.acts.yaml.cpUnit.property.ObjectUnitProperty;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author baishuo.lp
 * @version $Id: ObjectProcessor.java, v 0.1 2015年8月19日 下午3:26:52 baishuo.lp Exp $
 */
public class ObjectProcessor {

    private static final Log                    logger            = LogFactory
                                                                      .getLog(ObjectProcessor.class);

    private final ObjectTypeManager             objectTypeManager = new ObjectTypeManager();

    private final String                        csvPath;

    private final String                        description;

    private final Map<String, BaseUnitProperty> attributeMap;

    private String                              keyPath;

    private String                              className;

    private Class<?>                            classType;

    private ClassLoader                         classLoader;

    public Map<String, Map<String, String>>     flags             = new LinkedHashMap<String, Map<String, String>>();

    public ObjectProcessor(ClassLoader classLoader, String csvPath, String description) {
        this.csvPath = csvPath;
        this.description = description;
        this.attributeMap = new HashMap<String, BaseUnitProperty>();
        this.classLoader = classLoader;
        loadCSVFile();
        this.keyPath = this.className;
    }

    public ObjectProcessor(ClassLoader classLoader, String csvPath, String description,
                           String encoding) {
        this.csvPath = csvPath;
        this.description = description;
        this.attributeMap = new HashMap<String, BaseUnitProperty>();
        this.classLoader = classLoader;
        loadCSVFile(encoding);
        this.keyPath = this.className;
    }

    public ObjectProcessor(String csvPath, String description) {
        this.csvPath = csvPath;
        this.description = description;
        this.attributeMap = new HashMap<String, BaseUnitProperty>();
        this.classLoader = ObjectProcessor.class.getClassLoader();

        loadCSVFile();
        this.keyPath = this.className;
    }

    public ObjectProcessor(String csvPath, String description, String encoding) {
        this.csvPath = csvPath;
        this.description = description;
        this.attributeMap = new HashMap<String, BaseUnitProperty>();
        this.classLoader = ObjectProcessor.class.getClassLoader();

        loadCSVFile(encoding);
        this.keyPath = this.className;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public Object genObject() throws Exception {

        //3. class name
        Class<?> objClass = null;
        String realClassName = this.className;

        Map<String, String> flagMap = new HashMap<String, String>();
        if (this.classType == null || realClassName.contains(".")) {
            //3.1 if qualified name
            try {
                objClass = classLoader.loadClass(realClassName);
            } catch (ClassNotFoundException e) {
                ActsLogUtil.error(logger, "Failed to load class based on qualified name:"
                                          + realClassName, e);
                throw new Exception("Failed to load class based on qualified name:" + realClassName
                                    + ", please mvn clea install");
            }
        } else if (!this.classType.getSimpleName().equals(realClassName)) {
            //3.2 find subclass
            String packageName = this.classType.getPackage().getName();
            String prefix = packageName.substring(0, packageName.lastIndexOf('.'));
            Reflections reflections = new Reflections(ClasspathHelper.forPackage(prefix),
                new SubTypesScanner(), new FilterBuilder().includePackage(prefix));
            for (Class<?> subClass : reflections.getSubTypesOf(objClass)) {
                if (StringUtils.equals(subClass.getSimpleName(), realClassName.trim())) {
                    objClass = subClass;
                    break;
                }
            }
        } else {
            objClass = this.classType;
        }

        //4. construct obj
        Object objValue = ActsObjectUtil.genInstance(objClass);

        //5. set attribute
        for (Entry<String, BaseUnitProperty> entry : this.attributeMap.entrySet()) {
            String fieldName = entry.getKey();
            if (StringUtils.isBlank(fieldName)) {

                continue;
            }
            BaseUnitProperty property = entry.getValue();
            String flagCode = property.getFlagCode();
            Object referedValue = property.getExpectValue();

            //5.2 get field
            Field field = ActsObjectUtil.getField(objClass, fieldName);
            Class<?> propertyClass = ActsObjectUtil.getClass(objClass, fieldName);
            flagMap.put(fieldName, flagCode);

            if (field == null || propertyClass == null) {
                ActsLogUtil.error(logger, "Failed to find field [" + fieldName + "] in class ["
                                          + objClass.getSimpleName() + "] or super class");
                throw new Exception("please check class template, field :" + fieldName
                                    + ", class :" + objClass.getSimpleName());
            }

            Class<?> fieldType = field.getType();

            //5.3 set field
            Object fieldValue = null;

            flagCode = (property != null && property.getFlagCode() != null) ? property
                .getFlagCode() : flagCode;

            //5.4 if flagCode is not N
            if (UnitFlagEnum.getByCode(flagCode) != UnitFlagEnum.N
                && !"null".equals(referedValue.toString())) {
                if (UnitFlagEnum.getByCode(flagCode) == UnitFlagEnum.CUSTOM) {

                    fieldValue = ActsCacheData.getCustomGenerator(flagCode).generater(
                        this.keyPath + "." + fieldName, property.getExpectValue(),
                        fieldType.getSimpleName());
                } else if (objectTypeManager.isSimpleType(fieldType)) {

                    fieldValue = generateSimpleProperty(fieldName, fieldType, flagCode,
                        referedValue);
                } else {
                    if (objectTypeManager.isCollectionType(fieldType)) {

                        String csvDir = StringUtils.substringBeforeLast(csvPath, "/");
                        String exceptValue = (String) property.getExpectValue();
                        String desc = StringUtils.substringAfterLast(exceptValue, "@");
                        String convertCsv = null;
                        if (StringUtils.substringBefore(exceptValue, "@").contains(":")) {
                            convertCsv = csvDir
                                         + "/"
                                         + StringUtils.substringAfter(
                                             (StringUtils.substringBefore(exceptValue, "@")), ":");
                        } else {
                            convertCsv = csvDir + "/"
                                         + StringUtils.substringBefore(exceptValue, "@");
                        }

                        if (Map.class.isAssignableFrom(fieldType)
                            && ObjHandUtil.isSubListConvert(convertCsv, VirtualMap.class.getName())) {

                            //VirtualMap
                            Map<Object, Object> map = new HashMap<Object, Object>();
                            List<String> descList = BaseDataUtil.loadDesc(convertCsv);

                            for (String descTemp : descList) {
                                ObjectProcessor processorTemp = new ObjectProcessor(classLoader,
                                    convertCsv, descTemp);
                                Map<Object, Object> descMap = (Map<Object, Object>) processorTemp
                                    .genObject();
                                map.putAll(descMap);
                            }

                            fieldValue = map;

                        } else if (Collection.class.isAssignableFrom(fieldType)
                                   && ObjHandUtil.isSubListConvert(convertCsv,
                                       VirtualList.class.getName())) {
                            ObjectProcessor processor = new ObjectProcessor(classLoader,
                                convertCsv, desc);
                            fieldValue = processor.genObject();
                        } else {
                            Class<?> argumentClass = objectTypeManager.getCollectionItemClass(
                                field.getGenericType(), fieldType);

                            //Array
                            if (fieldType.isArray()) {
                                argumentClass = fieldType.getComponentType();
                            }

                            if (objectTypeManager.isSimpleType(argumentClass)) {

                                fieldValue = generateSimpleCollection(property, argumentClass,
                                    fieldType, fieldName, referedValue);
                            } else {

                                fieldValue = generateComplexCollection(property, argumentClass,
                                    fieldType, fieldName, referedValue);
                            }
                        }
                    } else {

                        fieldValue = generateChildObject(property, fieldType, fieldName,
                            referedValue);
                    }
                }
                property.setActualValue(fieldValue);
                ActsObjectUtil.setProperty(objValue, fieldName, fieldValue);
            } else if (StringUtils.equals(referedValue == null ? "null" : referedValue.toString(),
                "null")) {
                property.setActualValue(fieldValue);
                ActsObjectUtil.setProperty(objValue, fieldName, fieldValue);
            }
        }

        //save flag map
        flags.put(realClassName, flagMap);

        if (StringUtils.equals(objClass.getName(), "com.alipay.test.acts.model.VirtualMap")) {
            return ObjHandUtil.handMapConvert(objValue);
        } else if (StringUtils.equals(objClass.getName(), "com.alipay.test.acts.model.VirtualList")) {
            return ObjHandUtil.handListConvert(objValue, csvPath);
        }
        return objValue;
    }

    /**
     *
     *
     * @param property
     * @param fieldType
     * @param fieldName
     * @param referedValue
     * @return
     */
    protected Object generateChildObject(BaseUnitProperty property, Class<?> fieldType,
                                         String fieldName, Object referedValue) {

        if (StringUtils.isBlank(String.valueOf(referedValue))) {
            return null;
        } else {
            if (property instanceof ObjectUnitProperty) {
                return ((ObjectUnitProperty) property).genObject(classLoader);
            } else {
                return referedValue;
            }
        }

    }

    /**
     *
     * @param property
     * @param argumentClass
     * @param fieldClass
     * @param fieldName
     * @param referedValue
     * @return
     */
    protected Object generateComplexCollection(BaseUnitProperty property, Class<?> argumentClass,
                                               Class<?> fieldClass, String fieldName,
                                               Object referedValue) {
        Object fieldValue = null;
        if (property.getExpectValue() == null) {
            return null;
        }

        if (property instanceof ListObjectUnitProperty) {
            ListObjectUnitProperty listProperty = (ListObjectUnitProperty) property;
            listProperty.setTargetCSVPath(FileUtil.getRelativePath(argumentClass.getSimpleName()
                                                                   + ".csv", this.csvPath));
            listProperty.setClassType(argumentClass);
            return listProperty.genObject(classLoader);
        } else if (property instanceof MapObjectUnitProperty) {
            MapObjectUnitProperty mapProperty = (MapObjectUnitProperty) property;
            if (String.valueOf(mapProperty.getBaseValue()).contains(".csv@")) {
                mapProperty.setTargetCSVPath(FileUtil.getRelativePath(argumentClass.getSimpleName()
                                                                      + ".csv", this.csvPath));
            }
            mapProperty.setClassType(argumentClass);
            return mapProperty.genObject(classLoader);

        } else if (!(property.getExpectValue() instanceof String)) {
            ActsLogUtil.fail(logger, "in yaml, the type of element of collection must be string");
        }

        String value = String.valueOf(referedValue);
        if (StringUtils.isBlank(value)) {
            return null;
        } else if (StringUtils.equals("@element_empty@", value)) {
            return objectTypeManager.getCollectionObject(fieldClass);
        } else {
            String[] valueParts = value.split("@");
            Assert
                .assertTrue("desc of complex obj must contain only one @", valueParts.length == 2);
            String[] values = valueParts[1].trim().split(";");

            if (fieldClass.isArray()) {
                fieldValue = Array.newInstance(fieldClass.getComponentType(), valueParts.length);
            } else {
                fieldValue = objectTypeManager.getCollectionObject(fieldClass);
            }
            for (int i = 0; i < values.length; i++) {
                Object valuePart = generateChildObject(property, argumentClass, fieldName,
                    valueParts[0].trim() + "@" + values[i].trim());
                objectTypeManager.setCollectionObjectValue(fieldValue, valuePart, values[i], i,
                    fieldClass);
            }
        }

        return fieldValue;
    }

    /**
     * simple
     *
     * @param property
     * @param argumentClass
     * @param fieldClass
     * @param fieldName
     * @param referedValue
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Object generateSimpleCollection(BaseUnitProperty property, Class<?> argumentClass,
                                              Class<?> fieldClass, String fieldName,
                                              Object referedValue) {
        Object fieldValue = null;
        if (property.getExpectValue() == null) {
            return null;
        }

        if (property instanceof ListObjectUnitProperty) {
            ListObjectUnitProperty listProperty = (ListObjectUnitProperty) property;
            List<BaseUnitProperty> childPropertyList = listProperty.getObjectList();
            fieldValue = new ArrayList();
            for (BaseUnitProperty childProperty : childPropertyList) {
                ((List) fieldValue).add(childProperty.getExpectValue());
            }
            return fieldValue;
        } else if (!(property.getExpectValue() instanceof String)) {
            ActsLogUtil.fail(logger, "in yaml, the type of element of collection must be string");
        }

        String value = String.valueOf(referedValue);
        if (StringUtils.isBlank(value)) {
            return null;
        } else if (StringUtils.equals("@element_empty@", value)) {
            return objectTypeManager.getCollectionObject(fieldClass);
        } else if (value.startsWith("{") || value.startsWith("[")) {
            return JSON.parseObject(value, new TypeReference<Map<String, String>>() {
            });
        } else {
            String[] valueParts = value.split(";");
            if (fieldClass.isArray()) {
                fieldValue = Array.newInstance(fieldClass.getComponentType(), valueParts.length);
            } else {
                fieldValue = objectTypeManager.getCollectionObject(fieldClass);
            }
            for (int i = 0; i < valueParts.length; i++) {
                Object valuePart = objectTypeManager.getSimpleObject(argumentClass, valueParts[i],
                    fieldName, argumentClass.getName());
                objectTypeManager.setCollectionObjectValue(fieldValue, valuePart, valueParts[i], i,
                    fieldClass);
            }
        }

        return fieldValue;
    }

    /**
     * simple
     *
     * @return
     */
    protected Object generateSimpleProperty(String fieldName, Class<?> fieldType, String flagCode,
                                            Object referedValue) {

        Object fieldValue = null;
        if (referedValue == null || StringUtils.isBlank(String.valueOf(referedValue))) {

            return objectTypeManager.getSimpleObject(fieldType, String.valueOf(referedValue),
                fieldName, fieldType.getName());
        }

        switch (UnitFlagEnum.getByCode(flagCode)) {
            case F:
                return FileUtil.readFile(FileUtil.getRelativePath(String.valueOf(referedValue),
                    this.csvPath));
            case D:
            case C:
            case Y:
                return objectTypeManager.getSimpleObject(fieldType, String.valueOf(referedValue),
                    fieldName, fieldType.getName());
            default:
                Assert.fail(this.keyPath + "." + fieldName + " cant't create object, current flag:"
                            + flagCode + "");
                break;
        }
        return fieldValue;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void loadCSVFile() throws ActsException {
        List tableList = CSVHelper.readFromCsv(this.csvPath);
        if (tableList == null || tableList.size() == 0) {
            ActsLogUtil.fail(logger, this.csvPath + " is empty or doesn't exist !");
        }
        if (tableList.size() < 2) {
            throw new ActsException("the file content is empty or illegal! file path:" + csvPath);
        }
        //2. number of column
        String[] labels = (String[]) tableList.get(0);
        int baseIndex = 0, classNameCol = 0, colNameCol = 0, flagCol = 0, indexCol = -1;
        for (int i = 0; i < labels.length; i++) {
            String label = labels[i].toLowerCase().trim();

            if (StringUtils.equals(label, this.description)) {
                indexCol = i;
            } else {
                CSVColEnum labelEnum = CSVColEnum.getByCode(label);
                if (labelEnum != null) {
                    switch (labelEnum) {
                        case CLASS:
                            classNameCol = i;
                            baseIndex++;
                            break;
                        case PROPERTY:
                            colNameCol = i;
                            baseIndex++;
                            break;
                        case TYPE:
                            baseIndex++;
                            break;
                        case RULE:
                            baseIndex++;
                            break;
                        case FLAG:
                            flagCol = i;
                            baseIndex++;
                            break;
                        default:
                            Assert.fail("format of the .csv file is illegal");
                    }
                }
            }
        }
        boolean noValue = false;
        if (indexCol == -1) {
            if (StringUtils.isNumeric(this.description)) {
                indexCol = baseIndex + Integer.valueOf(this.description) - 1;
            } else {
                noValue = true;
            }
        }

        this.className = ((String[]) tableList.get(1))[classNameCol];
        for (int i = 1; i < tableList.size(); i++) {
            String[] row = (String[]) tableList.get(i);
            String fieldName = row[colNameCol];
            String flagCode = row[flagCol];
            String referedValue = noValue ? null : row[indexCol];

            //5.1 "
            referedValue = StringUtils.replace(referedValue, "\"\"", "\"");
            BaseUnitProperty property = null;
            if (!this.attributeMap.containsKey(fieldName)) {
                if (!noValue && referedValue.contains(".csv@")) {
                    if (!referedValue.contains(":")) {
                        String[] valueParts = referedValue.split(".csv@");

                        //list<Complex Type>, get desc-number of complex obj
                        String[] descParts = referedValue.split(";");
                        for (int index = 0; index < descParts.length; index++) {
                            String temp = StringUtils.substringAfter(descParts[index], "@");
                            descParts[index] = temp;
                        }

                        if (descParts.length == 1) {
                            Map<String, Object> attribute = new HashMap<String, Object>();
                            attribute.put("__desc", valueParts[0] + "@" + valueParts[1]);
                            property = new ObjectUnitProperty(fieldName, this.keyPath + "."
                                                                         + fieldName, this.csvPath,
                                attribute);
                        } else {

                            //list<Object> ,but Set<Object> is not supported.
                            property = new ListObjectUnitProperty(fieldName, this.keyPath + "."
                                                                             + fieldName,
                                this.csvPath, new ArrayList());
                            ListObjectUnitProperty listProperty = (ListObjectUnitProperty) property;
                            for (int j = 0; j < descParts.length; j++) {
                                Map<String, Object> attribute = new HashMap<String, Object>();
                                attribute.put("__desc", valueParts[0] + "@" + descParts[j]);
                                ObjectUnitProperty childProperty = new ObjectUnitProperty(
                                    fieldName, this.keyPath + "." + fieldName + "[" + j + "]",
                                    this.csvPath, attribute);
                                listProperty.getObjectList().add(childProperty);
                            }
                        }
                    } else {

                        String[] parts = referedValue.split(";");
                        Map<String, BaseUnitProperty> tmpMap = new LinkedHashMap<String, BaseUnitProperty>();
                        int index = 0;
                        for (String part : parts) {

                            String[] mapParts = part.split("\\:");
                            String[] baseInfo = mapParts[1].split(".csv@");
                            String key = mapParts[0];
                            String baseName = baseInfo[0];
                            String desc = baseInfo[1];
                            Map<String, Object> attribute = new HashMap<String, Object>();
                            attribute.put("__desc", baseName + "@" + desc);
                            BaseUnitProperty mapProperty = new ObjectUnitProperty(fieldName,
                                this.keyPath + "." + fieldName + "[" + index + "]", this.csvPath,
                                attribute);
                            tmpMap.put(key, mapProperty);
                            index++;

                        }
                        property = new MapObjectUnitProperty(fieldName, this.keyPath + "."
                                                                        + fieldName, this.csvPath,
                            tmpMap);
                    }
                } else {
                    if (!referedValue.contains(":")) {
                        property = new BaseUnitProperty(fieldName, this.keyPath + "." + fieldName,
                            referedValue);
                    } else {
                        String[] parts = referedValue.split(";");
                        Map<String, BaseUnitProperty> tmpMap = new LinkedHashMap<String, BaseUnitProperty>();
                        int index = 0;
                        for (String part : parts) {
                            String[] mapParts = part.split("\\:");
                            String key = mapParts[0];
                            String value = mapParts[1];
                            BaseUnitProperty mapProperty = new BaseUnitProperty(fieldName,
                                this.keyPath + "." + fieldName + "[" + index + "]", value);
                            tmpMap.put(key, mapProperty);
                            property = new MapObjectUnitProperty(fieldName, this.keyPath + "."
                                                                            + fieldName,
                                this.csvPath, tmpMap);
                        }
                    }
                }

                property.setExpectValue(referedValue);
                property.setFlagCode(flagCode);

            } else {
                property = this.attributeMap.get(fieldName);
                if (property.getFlagCode() == null) {
                    property.setFlagCode(flagCode);
                }
            }
            property.setBaseValue(referedValue);
            property.setBaseFlagCode(flagCode);
            this.attributeMap.put(fieldName, property);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void loadCSVFile(String encoding) throws ActsException {
        //1. load
        List tableList = CSVHelper.readFromCsv(this.csvPath, encoding);
        if (tableList == null || tableList.size() == 0) {
            ActsLogUtil.fail(logger, this.csvPath + "is empty or doesn't exist");
        }
        if (tableList.size() < 2) {
            throw new ActsException("the file content is empty or illegal ! file path:" + csvPath);
        }
        //2. get serial number of column
        String[] labels = (String[]) tableList.get(0);
        int baseIndex = 0, classNameCol = 0, colNameCol = 0, flagCol = 0, indexCol = -1;
        for (int i = 0; i < labels.length; i++) {
            String label = labels[i].toLowerCase().trim();

            if (StringUtils.equalsIgnoreCase(label, this.description)) {
                indexCol = i;
            } else {
                CSVColEnum labelEnum = CSVColEnum.getByCode(label);
                if (labelEnum != null) {
                    switch (labelEnum) {
                        case CLASS:
                            classNameCol = i;
                            baseIndex++;
                            break;
                        case PROPERTY:
                            colNameCol = i;
                            baseIndex++;
                            break;
                        case TYPE:
                            baseIndex++;
                            break;
                        case RULE:
                            baseIndex++;
                            break;
                        case FLAG:
                            flagCol = i;
                            baseIndex++;
                            break;
                        default:
                            Assert.fail("the format of the .csv file is illegal");
                    }
                }
            }
        }
        boolean noValue = false;
        if (indexCol == -1) {
            if (StringUtils.isNumeric(this.description)) {
                indexCol = baseIndex + Integer.valueOf(this.description) - 1;
            } else {
                noValue = true;
            }
        }

        this.className = ((String[]) tableList.get(1))[classNameCol];
        for (int i = 1; i < tableList.size(); i++) {
            String[] row = (String[]) tableList.get(i);
            String fieldName = row[colNameCol];
            String flagCode = row[flagCol];
            String referedValue = noValue ? null : row[indexCol];

            //5.1
            referedValue = StringUtils.replace(referedValue, "\"\"", "\"");
            BaseUnitProperty property = null;
            if (!this.attributeMap.containsKey(fieldName)) {
                if (!noValue && referedValue.contains(".csv@")) {
                    if (!referedValue.contains(":")) {
                        String[] valueParts = referedValue.split(".csv@");

                        //list<complex type>, get desc-number of complex obj
                        String[] descParts = referedValue.split(";");
                        for (int index = 0; index < descParts.length; index++) {
                            String temp = StringUtils.substringAfter(descParts[index], "@");
                            descParts[index] = temp;
                        }

                        if (descParts.length == 1) {
                            Map<String, Object> attribute = new HashMap<String, Object>();
                            attribute.put("__desc", valueParts[0] + "@" + valueParts[1]);
                            property = new ObjectUnitProperty(fieldName, this.keyPath + "."
                                                                         + fieldName, this.csvPath,
                                attribute);
                        } else {

                            //list<Object>, but Set<Object> is not supported
                            property = new ListObjectUnitProperty(fieldName, this.keyPath + "."
                                                                             + fieldName,
                                this.csvPath, new ArrayList());
                            ListObjectUnitProperty listProperty = (ListObjectUnitProperty) property;
                            for (int j = 0; j < descParts.length; j++) {
                                Map<String, Object> attribute = new HashMap<String, Object>();
                                attribute.put("__desc", valueParts[0] + "@" + descParts[j]);
                                ObjectUnitProperty childProperty = new ObjectUnitProperty(
                                    fieldName, this.keyPath + "." + fieldName + "[" + j + "]",
                                    this.csvPath, attribute);
                                listProperty.getObjectList().add(childProperty);
                            }
                        }
                    } else {

                        String[] parts = referedValue.split(";");
                        Map<String, BaseUnitProperty> tmpMap = new LinkedHashMap<String, BaseUnitProperty>();
                        int index = 0;
                        for (String part : parts) {

                            String[] mapParts = part.split("\\:");
                            String[] baseInfo = mapParts[1].split(".csv@");
                            String key = mapParts[0];
                            String baseName = baseInfo[0];
                            String desc = baseInfo[1];
                            Map<String, Object> attribute = new HashMap<String, Object>();
                            attribute.put("__desc", baseName + "@" + desc);
                            BaseUnitProperty mapProperty = new ObjectUnitProperty(fieldName,
                                this.keyPath + "." + fieldName + "[" + index + "]", this.csvPath,
                                attribute);
                            tmpMap.put(key, mapProperty);
                            index++;

                        }
                        property = new MapObjectUnitProperty(fieldName, this.keyPath + "."
                                                                        + fieldName, this.csvPath,
                            tmpMap);
                    }
                } else {
                    if (!referedValue.contains(":")) {
                        property = new BaseUnitProperty(fieldName, this.keyPath + "." + fieldName,
                            referedValue);
                    } else {
                        String[] parts = referedValue.split(";");
                        Map<String, BaseUnitProperty> tmpMap = new LinkedHashMap<String, BaseUnitProperty>();
                        int index = 0;
                        for (String part : parts) {
                            String[] mapParts = part.split("\\:");
                            String key = mapParts[0];
                            String value = mapParts[1];
                            BaseUnitProperty mapProperty = new BaseUnitProperty(fieldName,
                                this.keyPath + "." + fieldName + "[" + index + "]", value);
                            tmpMap.put(key, mapProperty);
                            property = new MapObjectUnitProperty(fieldName, this.keyPath + "."
                                                                            + fieldName,
                                this.csvPath, tmpMap);
                        }
                    }
                }

                property.setExpectValue(referedValue);
                property.setFlagCode(flagCode);

            } else {
                property = this.attributeMap.get(fieldName);
                if (property.getFlagCode() == null) {
                    property.setFlagCode(flagCode);
                }
            }
            property.setBaseValue(referedValue);
            property.setBaseFlagCode(flagCode);
            this.attributeMap.put(fieldName, property);
        }
    }

    /**
     * Getter method for property <tt>keyPath</tt>.
     *
     * @return property value of keyPath
     */
    public String getKeyPath() {
        return keyPath;
    }

    /**
     * Getter method for property <tt>objectTypeManager</tt>.
     *
     * @return property value of objectTypeManager
     */
    public ObjectTypeManager getObjectTypeManager() {
        return objectTypeManager;
    }

    /**
     * Getter method for property <tt>description</tt>.
     *
     * @return property value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter method for property <tt>csvPath</tt>.
     *
     * @return property value of csvPath
     */
    public String getCsvPath() {
        return csvPath;
    }

    /**
     * Getter method for property <tt>className</tt>.
     *
     * @return property value of className
     */
    public String getClassName() {
        return className;
    }

    /**
     * Getter method for property <tt>classType</tt>.
     *
     * @return property value of classType
     */
    public Class<?> getClassType() {
        return classType;
    }

    /**
     * Getter method for property <tt>attributeMap</tt>.
     *
     * @return property value of attributeMap
     */
    public Map<String, BaseUnitProperty> getAttributeMap() {
        return attributeMap;
    }

    /**
     * Setter method for property <tt>keyPath</tt>.
     *
     * @param keyPath value to be assigned to property keyPath
     */
    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }

    /**
     * Setter method for property <tt>className</tt>.
     *
     * @param className value to be assigned to property className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Setter method for property <tt>classType</tt>.
     *
     * @param classType value to be assigned to property classType
     */
    public void setClassType(Class<?> classType) {
        this.classType = classType;
    }

    /**
     * Getter method for property <tt>flags</tt>.
     * 
     * @return property value of flags
     */
    public Map<String, Map<String, String>> getFlags() {
        return flags;
    }

    /**
     * Setter method for property <tt>flags</tt>.
     * 
     * @param flags value to be assigned to property flags
     */
    public void setFlags(Map<String, Map<String, String>> flags) {
        this.flags = flags;
    }

}
