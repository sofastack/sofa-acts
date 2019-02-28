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
package com.alipay.test.acts.yaml.cpUnit.property;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.test.acts.cache.ActsCacheData;
import com.alipay.test.acts.context.ActsCaseContextHolder;
import com.alipay.test.acts.db.enums.CSVColEnum;
import com.alipay.test.acts.exception.ActsException;
import com.alipay.test.acts.exception.ModleFileException;
import com.alipay.test.acts.helper.CSVHelper;
import com.alipay.test.acts.log.ActsLogUtil;
import com.alipay.test.acts.model.VirtualList;
import com.alipay.test.acts.model.VirtualMap;
import com.alipay.test.acts.object.ActsObjectUtil;
import com.alipay.test.acts.object.enums.UnitFlagEnum;
import com.alipay.test.acts.object.generator.CustomGenerator;
import com.alipay.test.acts.object.manager.ObjectTypeManager;
import com.alipay.test.acts.object.processor.ObjHandUtil;
import com.alipay.test.acts.object.processor.ObjectProcessor;
import com.alipay.test.acts.util.FileUtil;
import com.alipay.test.acts.yaml.cpUnit.ObjectCPUnit;
import ognl.OgnlException;
import org.apache.commons.lang.ClassUtils;
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
 * @version $Id: BaseUnitProperty.java, v 0.1 2015年8月12日 下午4:40:26 baishuo.lp Exp $
 */
public class ObjectUnitProperty extends BaseUnitProperty {
    private static final Log              logger            = LogFactory
                                                                .getLog(ObjectUnitProperty.class);

    private String                        targetCSVPath;

    private Class<?>                      classType;

    private String                        description;

    private Map<String, BaseUnitProperty> attributeMap      = new LinkedHashMap<String, BaseUnitProperty>();

    private String                        className;

    private ClassLoader                   classLoader;
    private final ObjectTypeManager       objectTypeManager = new ObjectTypeManager();

    @SuppressWarnings("unchecked")
    public ObjectUnitProperty(String keyName, String keyPath, String parentCSVPath,
                              Map<String, Object> attribute) {
        super(keyName, keyPath, null);
        this.description = ("" + attribute.get("__desc")).trim();
        if (this.description.contains("@")) {
            String[] valueParts = this.description.split("@");
            this.keyName = valueParts[0].trim();
            this.description = valueParts[1].trim();
        }
        this.targetCSVPath = generateTargetCSVPath(this.keyName, parentCSVPath);
        attribute.remove("__desc");
        for (Entry<String, Object> entry : attribute.entrySet()) {
            String childKeyName = entry.getKey();
            Object value = entry.getValue();
            String flagCode = null;
            if (childKeyName.endsWith("]")) {
                flagCode = childKeyName.substring(childKeyName.indexOf('[') + 1,
                    childKeyName.length() - 1);
                childKeyName = childKeyName.substring(0, childKeyName.indexOf('['));
            }
            String childKeyPath = this.keyPath + "." + childKeyName;
            BaseUnitProperty property = null;
            if (value instanceof Map) {
                Map<String, Object> mapValue = (Map<String, Object>) value;
                if (mapValue.get("__desc") != null) {

                    property = new ObjectUnitProperty(childKeyName, childKeyPath,
                        this.targetCSVPath, mapValue);
                } else {

                    property = new BaseUnitProperty(childKeyName, childKeyPath, value);
                }
            } else if (value instanceof List) {
                List<Object> listValue = (List<Object>) value;
                property = new ListObjectUnitProperty(childKeyName, childKeyPath, parentCSVPath,
                    listValue);
            } else if (value instanceof VirtualList) {
                List<Object> listValue = (List<Object>) value;
                property = new ListObjectUnitProperty(childKeyName, childKeyPath, parentCSVPath,
                    listValue);
            } else if (value instanceof VirtualMap) {

                Map<String, Object> mapValue = (Map<String, Object>) value;
                if (mapValue.get("__desc") != null) {

                    property = new ObjectUnitProperty(childKeyName, childKeyPath,
                        this.targetCSVPath, mapValue);
                }
            } else {
                property = new BaseUnitProperty(childKeyName, childKeyPath, value);
            }

            if (flagCode != null) {
                property.setFlagCode(flagCode);
            }
            this.attributeMap.put(childKeyName, property);
        }

        this.loadCSVFile();

    }

    public ObjectUnitProperty(ObjectCPUnit unit) {
        super(unit.getUnitName(), unit.getUnitName(), null);
        this.description = unit.getDescription();
        this.targetCSVPath = unit.getTargetCSVPath();
        this.attributeMap = unit.getAttributeMap();
        this.loadCSVFile();
    }

    /**
     *
     * @param classLoader
     * @return
     */
    public Object genObject(ClassLoader classLoader) {
        //3. class name
        Class<?> objClass = null;
        String realClassName = this.className;
        this.classLoader = classLoader;
        if (this.classType == null || realClassName.contains(".")) {
            //3.1 if qualifiedName
            try {
                objClass = classLoader.loadClass(realClassName);
            } catch (ClassNotFoundException e) {
                ActsLogUtil.error(logger, "Failed to load class , class qualified name:"
                                          + realClassName, e);
            }
        } else if (!this.classType.getSimpleName().equals(realClassName)) {
            //3.2 find subclass
            String packageName = ClassUtils.getPackageName(className);
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

        //4.construct obj
        Object objValue = null;
        try {
            objValue = ActsObjectUtil.genInstance(objClass);
        } catch (Exception e) {
            ActsLogUtil.error(
                logger,
                "keyPath:" + this.keyPath + ", Failed to create obj based on class ["
                        + objClass.getSimpleName()
                        + "], please check qualified name of class or impl class in .csv file", e);
            return objValue;
        }

        //5. set field
        for (Entry<String, BaseUnitProperty> entry : this.attributeMap.entrySet()) {
            String fieldName = entry.getKey();
            BaseUnitProperty property = entry.getValue();
            String flagCode = property.getFlagCode();
            Object referedValue = property.getExpectValue();

            Object oldReferedValue = referedValue;

            //5.2 get field
            Field field = ActsObjectUtil.getField(objClass, fieldName);
            Class<?> propertyClass = ActsObjectUtil.getClass(objClass, fieldName);
            Class<?> fieldType = field.getType();

            if (field == null || propertyClass == null) {
                ActsLogUtil.error(logger, "Failed to find field [" + fieldName + "] in class ["
                                          + objClass.getSimpleName() + "] or super class");
                return null;
            }

            //5.3 set field
            Object fieldValue = null;

            flagCode = (property != null && property.getFlagCode() != null) ? property
                .getFlagCode() : flagCode;

            //5.4 if flagCodeis not N
            if (UnitFlagEnum.getByCode(flagCode) != UnitFlagEnum.N
                && !referedValue.toString().equals("null")) {
                if (UnitFlagEnum.getByCode(flagCode) == UnitFlagEnum.CUSTOM) {

                    fieldValue = ActsCacheData.getCustomGenerator(flagCode).generater(
                        this.keyPath + "." + fieldName, property.getExpectValue(),
                        fieldType.getSimpleName());
                } else if (objectTypeManager.isSimpleType(fieldType)) {

                    fieldValue = generateSimpleProperty(fieldName, fieldType, flagCode,
                        referedValue);
                } else {
                    if (objectTypeManager.isCollectionType(fieldType)) {

                        Class<?> argumentClass = objectTypeManager.getCollectionItemClass(
                            field.getGenericType(), fieldType);

                        //Array
                        if (fieldType.isArray()) {
                            argumentClass = fieldType.getComponentType();
                        }

                        String csvDir = StringUtils.substringBeforeLast(targetCSVPath, "/");
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
                            ObjectProcessor processor = new ObjectProcessor(classLoader,
                                convertCsv, desc);
                            try {
                                fieldValue = processor.genObject();
                            } catch (Exception e) {
                                logger.error("processor.genObject occured error:", e);
                            }
                        } else if (Collection.class.isAssignableFrom(fieldType)
                                   && ObjHandUtil.isSubListConvert(convertCsv,
                                       VirtualList.class.getName())) {
                            ObjectProcessor processor = new ObjectProcessor(classLoader,
                                convertCsv, desc);
                            try {
                                fieldValue = processor.genObject();
                            } catch (Exception e) {
                                logger.error("processor.genObject occured error:", e);
                            }
                        } else if (objectTypeManager.isSimpleType(argumentClass)) {

                            fieldValue = generateSimpleCollection(property, argumentClass,
                                fieldType, fieldName, referedValue);
                        } else {

                            fieldValue = generateComplexCollection(property, argumentClass,
                                fieldType, fieldName, referedValue);
                        }
                    } else {

                        if (property instanceof ObjectUnitProperty) {
                            fieldValue = generateChildObject((ObjectUnitProperty) property,
                                fieldType, fieldName, referedValue);
                        } else {
                            String value = String.valueOf(referedValue);
                            if (!StringUtils.isBlank(value)) {
                                if (referedValue.equals("null")) {
                                    fieldValue = null;
                                } else if (StringUtils.equals(objClass.getSimpleName(),
                                    "VirtualList")
                                           || StringUtils.equals(objClass.getSimpleName(),
                                               "VirtualMap")) {
                                    fieldValue = referedValue;
                                } else {
                                    ActsLogUtil.fail(logger, String.format(
                                        "Failed to set fieldValue, referedValue=%s", referedValue));
                                }

                            }
                        }
                    }
                }
                ActsObjectUtil.setProperty(objValue, fieldName, fieldValue);
                if (property.isUnique()) {
                    property.setExpectValue(oldReferedValue);
                }
            } else if (StringUtils.equals(referedValue == null ? "null" : referedValue.toString(),
                "null")) {
                property.setActualValue(fieldValue);
                ActsObjectUtil.setProperty(objValue, fieldName, fieldValue);
            }
        }

        if (StringUtils.equals(objClass.getName(), "com.alipay.test.acts.model.VirtualMap")) {
            return ObjHandUtil.handMapConvert(objValue);
        } else if (StringUtils.equals(objClass.getName(), "com.alipay.test.acts.model.VirtualList")) {
            return ObjHandUtil.handListConvert(objValue, targetCSVPath);
        }
        return objValue;

    }

    @Override
    public void compare(Object object) {
        for (Entry<String, BaseUnitProperty> entry : this.attributeMap.entrySet()) {
            String columnName = entry.getKey();
            BaseUnitProperty property = this.attributeMap.get(columnName);
            String flagCode = property.getFlagCode();
            Object expectValue = property.getExpectValue();

            expectValue = replaceUniqueValue(property, expectValue, columnName);

            Object actualField = null;

            try {
                actualField = ActsObjectUtil.getProperty(object, columnName);
            } catch (OgnlException e) {
                ActsLogUtil.error(logger, String.format(
                    "ongl error, failed to find [%s] obj's attribute: [%s], csv path :%s", object,
                    columnName, this.targetCSVPath), e);
                continue;
            }

            if (actualField == null) {
                if (!StringUtils.isBlank(String.valueOf(expectValue))) {
                    ActsLogUtil.error(
                        logger,
                        this.keyPath + "." + columnName + "is different with file ["
                                + this.targetCSVPath + "], col [" + this.description
                                + "], expect value:" + expectValue + ", actual value:"
                                + String.valueOf(actualField) + ", check flag:" + flagCode);
                    property.setActualValue(actualField);
                    property.setCompareSuccess(false);
                }
            } else {

                property.compare(actualField);
                if (!property.isCompareSuccess()) {
                    if (property.getClass() == BaseUnitProperty.class) {
                        ActsLogUtil.error(logger, this.keyPath + "." + columnName
                                                  + "is different with file [" + this.targetCSVPath
                                                  + "], col [" + this.description
                                                  + "], expect value:" + expectValue
                                                  + ", actual value:" + String.valueOf(actualField)
                                                  + ", check flag:" + flagCode);

                        if (!property.isUnique()) {
                            if (UnitFlagEnum.getByCode(flagCode) == UnitFlagEnum.CUSTOM) {
                                CustomGenerator generator = ActsCacheData
                                    .getCustomGenerator(this.flagCode);
                                property.setActualValue(generator.generater(this.targetCSVPath,
                                    actualField, actualField.getClass().getSimpleName()));
                            } else {
                                property.setActualValue(generateSimpleProperty(flagCode,
                                    actualField.getClass(), flagCode, actualField));
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object dump(String fieldName) {
        Map dumpMap = new LinkedHashMap();
        Map objectMap = new LinkedHashMap();
        objectMap.put("__desc", this.keyName + "@" + this.description);
        for (Entry<String, BaseUnitProperty> entry : this.attributeMap.entrySet()) {
            BaseUnitProperty property = entry.getValue();
            if (property instanceof ListObjectUnitProperty) {
                ListObjectUnitProperty listProperty = (ListObjectUnitProperty) property;
                objectMap.putAll((Map) listProperty.dump(entry.getKey()));
            } else if (property instanceof ObjectUnitProperty) {
                objectMap.putAll((Map) ((ObjectUnitProperty) property).dump(entry.getKey()));
            } else {
                Map childMap = (Map) property.dump();
                objectMap.putAll(childMap);
            }
        }
        if (objectMap.size() > 1) {
            dumpMap.put(fieldName, objectMap);
        }
        return dumpMap;
    }

    /**
     *
     * @param property
     * @param fieldType
     * @param fieldName
     * @param referedValue
     * @return
     */
    protected Object generateChildObject(ObjectUnitProperty property, Class<?> fieldType,
                                         String fieldName, Object referedValue) {
        if (property instanceof ObjectUnitProperty) {
            property.setClassType(fieldType);
            return property.genObject(classLoader);
        } else {
            if (StringUtils.isBlank(String.valueOf(referedValue))) {
                return null;
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
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Object generateComplexCollection(BaseUnitProperty property, Class<?> argumentClass,
                                               Class<?> fieldClass, String fieldName,
                                               Object referedValue) {
        Object fieldValue = null;
        if (property.getExpectValue() == null) {
            return null;
        }

        if (property instanceof ListObjectUnitProperty) {
            ListObjectUnitProperty listProperty = (ListObjectUnitProperty) property;
            listProperty.setClassType(argumentClass);
            return listProperty.genObject(classLoader);
        } else if (property instanceof MapObjectUnitProperty) {
            MapObjectUnitProperty mapProperty = (MapObjectUnitProperty) property;
            mapProperty.setClassType(argumentClass);
            return mapProperty.genObject(classLoader);

        } else if (property instanceof ObjectUnitProperty) {
            //notice:
            ((ObjectUnitProperty) property).setClassType(argumentClass);
            List list = new ArrayList();
            list.add(((ObjectUnitProperty) property).genObject(classLoader));
            return list;
        } else if (!(property.getExpectValue() instanceof String)) {
            ActsLogUtil.fail(logger, "in yaml, the type of elements in collection must be string");
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
                Object valuePart = generateChildObject(null, argumentClass, fieldName,
                    valueParts[0].trim() + "@" + values[i].trim());
                objectTypeManager.setCollectionObjectValue(fieldValue, valuePart, values[i], i,
                    fieldClass);
            }
        }

        return fieldValue;
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
            ActsLogUtil.fail(logger, "in yaml, the type of elements in collection must be string");
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
     *
     * @param fieldName
     * @param fieldType
     * @param flagCode
     * @param referedValue
     * @return
     */
    protected Object generateSimpleProperty(String fieldName, Class<?> fieldType, String flagCode,
                                            Object referedValue) {
        Object fieldValue = null;
        if (referedValue == null || StringUtils.isBlank(String.valueOf(referedValue))) {
            return fieldValue;
        }

        if (StringUtils.contains(String.valueOf(referedValue), "BigDecimal.csv")) {
            referedValue = "0.01";
        }

        switch (UnitFlagEnum.getByCode(flagCode)) {
            case F:
                return FileUtil.readFile(FileUtil.getRelativePath(String.valueOf(referedValue),
                    this.targetCSVPath));
            case D:
            case C:
            case Y:
                return objectTypeManager.getSimpleObject(fieldType, String.valueOf(referedValue),
                    fieldName, fieldType.getName());
            default:
                Assert.fail(this.keyPath + "." + fieldName + "cant't create object, current flag:"
                            + flagCode);
                break;
        }
        return fieldValue;
    }

    private Object replaceUniqueValue(BaseUnitProperty property, Object originValue,
                                      String columnName) {

        Map<String, Object> uniqueMap = ActsCaseContextHolder.get().getUniqueMap();
        property.setOldExpectValue(originValue);
        if (uniqueMap.containsKey(this.keyPath + "." + columnName)) {
            property.setUnique(true);
            originValue = uniqueMap.get(this.keyPath + "." + columnName);
        } else if (uniqueMap.containsKey(columnName)) {
            property.setUnique(true);
            originValue = uniqueMap.get(columnName);
        } else {
            property.setUnique(false);
        }
        property.setExpectValue(originValue);
        return originValue;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void loadCSVFile() {

        try {
            //1. load
            List tableList = CSVHelper.readFromCsv(this.targetCSVPath);
            if (tableList == null || tableList.size() == 0) {
                ActsLogUtil.fail(logger, this.targetCSVPath + "is empty or doesn't exist");
            }
            if (tableList.size() < 2) {
                throw new ActsException("the file content is empty or illegal ! file path:"
                                        + targetCSVPath);
            }
            //2. get serial number of column
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
                                Assert.fail("the format of the .csv file is illegal");
                        }
                    }
                }
            }
            if (indexCol == -1) {
                Assert.assertTrue(this.targetCSVPath
                                  + "is wrong, and desc must be number string, current:"
                                  + this.description, StringUtils.isNumeric(this.description));
                indexCol = baseIndex + Integer.valueOf(this.description) - 1;
            }

            this.className = ((String[]) tableList.get(1))[classNameCol];
            for (int i = 1; i < tableList.size(); i++) {
                String[] row = (String[]) tableList.get(i);
                String fieldName = row[colNameCol];
                String flagCode = row[flagCol];
                String referedValue = row[indexCol];

                referedValue = StringUtils.replace(referedValue, "\"\"", "\"");
                BaseUnitProperty property = null;
                if (!this.attributeMap.containsKey(fieldName)) {
                    if (referedValue.contains(".csv@")) {
                        if (!referedValue.contains(":")) {

                            String[] valueParts = referedValue.split(".csv@");

                            //list<Complex Type>
                            String[] descParts = referedValue.split(";");
                            for (int index = 0; index < descParts.length; index++) {
                                String temp = StringUtils.substringAfter(descParts[index], "@");
                                descParts[index] = temp;
                            }

                            if (descParts.length == 1) {
                                Map<String, Object> attribute = new HashMap<String, Object>();
                                attribute.put("__desc", valueParts[0] + "@" + valueParts[1]);
                                property = new ObjectUnitProperty(fieldName, this.keyPath + "."
                                                                             + fieldName,
                                    this.targetCSVPath, attribute);
                            } else {
                                property = new ListObjectUnitProperty(fieldName, this.keyPath + "."
                                                                                 + fieldName,
                                    this.targetCSVPath, new ArrayList());
                                ListObjectUnitProperty listProperty = (ListObjectUnitProperty) property;
                                for (int j = 0; j < descParts.length; j++) {
                                    Map<String, Object> attribute = new HashMap<String, Object>();
                                    attribute.put("__desc", valueParts[0] + "@" + descParts[j]);
                                    ObjectUnitProperty childProperty = new ObjectUnitProperty(
                                        fieldName, this.keyPath + "." + fieldName + "[" + j + "]",
                                        this.targetCSVPath, attribute);
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
                                    this.keyPath + "." + fieldName + "[" + index + "]",
                                    this.targetCSVPath, attribute);
                                tmpMap.put(key, mapProperty);
                                index++;
                            }
                            property = new MapObjectUnitProperty(fieldName, this.keyPath + "."
                                                                            + fieldName,
                                this.targetCSVPath, tmpMap);
                        }
                    } else {
                        if (!referedValue.contains(":")) {
                            property = new BaseUnitProperty(fieldName, this.keyPath + "."
                                                                       + fieldName, referedValue);
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
                                index++;
                            }
                            property = new MapObjectUnitProperty(fieldName, this.keyPath + "."
                                                                            + fieldName,
                                this.targetCSVPath, tmpMap);
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

        } catch (RuntimeException e) {
            throw new ModleFileException(this.targetCSVPath, e);
        }

    }

    private static String generateTargetCSVPath(String propertyName, String parentCSVPath) {
        return FileUtil.getRelativePath(propertyName + ".csv", parentCSVPath);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ObjectUnitProperty [targetCSVPath=" + targetCSVPath + ", classType=" + classType
               + ", description=" + description + ", attributeMap=" + attributeMap + ", keyName="
               + keyName + ", flagCode=" + flagCode + ", keyPath=" + keyPath + "]";
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
     * Setter method for property <tt>description</tt>.
     *
     * @param description value to be assigned to property description
     */
    public void setDescription(String description) {
        this.description = description;
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
     * Getter method for property <tt>targetCSVPath</tt>.
     *
     * @return property value of targetCSVPath
     */
    public String getTargetCSVPath() {
        return targetCSVPath;
    }

    /**
     * Setter method for property <tt>targetCSVPath</tt>.
     *
     * @param targetCSVPath value to be assigned to property targetCSVPath
     */
    public void setTargetCSVPath(String targetCSVPath) {
        this.targetCSVPath = targetCSVPath;
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
     * Setter method for property <tt>classType</tt>.
     *
     * @param classType value to be assigned to property classType
     */
    public void setClassType(Class<?> classType) {
        this.classType = classType;
    }
}
