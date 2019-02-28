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

import com.alipay.test.acts.constant.ActsPathConstants;
import com.alipay.test.acts.db.enums.CSVColEnum;
import com.alipay.test.acts.exception.ActsException;
import com.alipay.test.acts.helper.CSVHelper;
import com.alipay.test.acts.model.PrepareData;
import com.alipay.test.acts.model.VirtualEventObject;
import com.alipay.test.acts.model.VirtualObject;
import com.alipay.test.acts.model.VirtualTable;
import com.alipay.test.acts.object.processor.ObjectProcessor;
import com.alipay.test.acts.object.result.Result;
import com.alipay.yaml.Yaml;
import com.alipay.yaml.constructor.Constructor;
import com.alipay.yaml.constructor.CustomClassLoaderConstructor;
import com.alipay.yaml.introspector.Property;
import com.alipay.yaml.nodes.Node;
import com.alipay.yaml.nodes.NodeId;
import com.alipay.yaml.nodes.NodeTuple;
import com.alipay.yaml.nodes.ScalarNode;
import com.alipay.yaml.nodes.Tag;
import com.alipay.yaml.representer.Representer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/***
 * yaml util
 *
 */
public class BaseDataUtil {

    private final static Log logger = LogFactory.getLog(BaseDataUtil.class);

    /**
     * split the original yaml file into multiple yaml files by case and save these yamls
     * notice: please manually delete the original yaml file, after confirming that the newly yaml file is correct
     *
     * @param testScriptSimpleName The name of the corresponding test script, example:"GetArgInfoActsTest"
     * @param origYamlFileAbsolutePath
     * @param encoding example:"UTF-8" "GBK"
     */
    public static void saveYamlDataToCaseByCase(String testScriptSimpleName,
                                                String origYamlFileAbsolutePath, String encoding) {

        File yamlFile = new File(origYamlFileAbsolutePath);

        if (!yamlFile.exists()) {
            throw new RuntimeException("Specified yaml file does not exist!");
        }

        Map<String, PrepareData> prepareDatas = loadFromYaml(yamlFile, Thread.currentThread()
            .getContextClassLoader());

        String yamlFolderPath = StringUtils.substringBeforeLast(origYamlFileAbsolutePath, "/")
                                + "/" + testScriptSimpleName;
        File yamlFolder = new File(yamlFolderPath);

        storeToYamlByCase(prepareDatas, yamlFolder, encoding);

    }

    /**
     * specify encoding, save one yaml file per case.
     *
     * @param prepareDatas
     * @param folder
     * @param encoding
     */
    public static void storeToYamlByCase(Map<String, PrepareData> prepareDatas, File folder,
                                         String encoding) {
        TreeMap<String, PrepareData> treeMap = new TreeMap<String, PrepareData>(
            new Comparator<String>() {
                @Override
                public int compare(String str1, String str2) {
                    return str1.compareTo(str2);
                }
            });
        treeMap.putAll(prepareDatas);

        try {
            if (folder.exists()) {
                FileUtils.cleanDirectory(folder);
            }
        } catch (Exception e) {
            throw new ActsException("Throw an exception when saving yaml", e);
        }

        for (String caseId : treeMap.keySet()) {

            TreeMap<String, PrepareData> tempMap = new TreeMap<String, PrepareData>();

            tempMap.put(caseId, treeMap.get(caseId));

            Yaml yaml = new Yaml(new BaseDataUtil.myRepresenter());
            String str = yaml.dump(tempMap);
            try {

                File caseFile = new File(folder.getAbsolutePath() + "/" + caseId + ".yaml");
                FileUtils.writeStringToFile(caseFile, str, encoding);

            } catch (IOException e) {
                throw new ActsException("Throw an exception when saving yaml", e);
            }
        }
    }

    /**
     * specify the encoding of yaml.
     *
     * @param prepareDatas
     * @param file
     * @param encoding
     */
    public static void storeToYaml(Map<String, PrepareData> prepareDatas, File file, String encoding) {
        TreeMap<String, PrepareData> treeMap = new TreeMap<String, PrepareData>(
            new Comparator<String>() {
                @Override
                public int compare(String str1, String str2) {
                    return str1.compareTo(str2);
                }
            });
        treeMap.putAll(prepareDatas);

        Yaml yaml = new Yaml(new BaseDataUtil.myRepresenter());
        String str = yaml.dump(treeMap);
        try {
            FileUtils.writeStringToFile(file, str, encoding);
        } catch (IOException e) {
            throw new ActsException("Throw an exception when saving yaml", e);
        }
    }

    /**
     * save yaml
     * @param prepareDatas
     * @param file
     */
    public static void storeToYaml(Map<String, PrepareData> prepareDatas, File file) {
        TreeMap<String, PrepareData> treeMap = new TreeMap<String, PrepareData>(
            new Comparator<String>() {
                @Override
                public int compare(String str1, String str2) {
                    return str1.compareTo(str2);
                }
            });
        treeMap.putAll(prepareDatas);
        Yaml yaml = new Yaml(new BaseDataUtil.myRepresenter());
        String str = yaml.dump(treeMap);
        try {
            FileUtils.writeStringToFile(file, str);
        } catch (IOException e) {
            throw new ActsException("Throw an exception when saving yaml", e);
        }
    }

    /**
     * read test case from yaml file.
     *
     * @param folder he package where yaml is
     * @param classLoader
     * @param encoding
     * @return
     */
    public static Map<String, PrepareData> loadFromYamlByCase(File folder, ClassLoader classLoader,
                                                              String encoding) {

        Map<String, PrepareData> caseMap = new HashMap<String, PrepareData>();
        if (folder.exists()) {
            try {
                File[] files = folder.listFiles();

                for (File file : files) {
                    if (file.getAbsolutePath().endsWith("yaml")) {
                        String str = FileUtils.readFileToString(file, encoding);

                        Yaml yaml = new Yaml(new BaseDataUtil.SelectiveConstructor(classLoader),
                            new BaseDataUtil.myRepresenter());
                        caseMap.putAll((Map<String, PrepareData>) yaml.load(str));
                    }
                }

            } catch (Exception e) {
                String sberr = e.getMessage();
                if (StringUtils.contains(sberr, " Cannot create property=")) {
                    String filedName = StringUtils.substringBetween(sberr,
                        "Unable to find property '", "' on class");
                    if (StringUtils.isBlank(filedName)) {
                        if (StringUtils.contains(sberr, "Class not found")) {
                            String clsName = StringUtils.substringBetween(sberr,
                                "Class not found:", ";");
                            throw new ActsException(
                                "class "
                                        + clsName
                                        + "failed to load, it is recommended to import the project after re-mvn!");
                        }
                    } else {
                        throw new ActsException("Please remove this field in csv or yaml!"
                                                + StringUtils.trim(filedName));
                    }

                }
                throw new ActsException("Throw an exception when reading yaml", e);
            }
        }

        return caseMap;

    }

    /**
     * read test case from yaml file.
     * @param file
     * @param classLoader
     * @param encoding
     * @return
     */
    public static Map<String, PrepareData> loadFromYaml(File file, ClassLoader classLoader,
                                                        String encoding) {
        if (file.exists()) {
            try {
                String str = FileUtils.readFileToString(file, encoding);

                Yaml yaml = new Yaml(new BaseDataUtil.SelectiveConstructor(classLoader),
                    new BaseDataUtil.myRepresenter());
                return (Map<String, PrepareData>) yaml.load(str);

            } catch (Exception e) {
                String sberr = e.getMessage();
                if (StringUtils.contains(sberr, " Cannot create property=")) {
                    String filedName = StringUtils.substringBetween(sberr,
                        "Unable to find property '", "' on class");
                    if (StringUtils.isBlank(filedName)) {
                        if (StringUtils.contains(sberr, "Class not found")) {
                            String clsName = StringUtils.substringBetween(sberr,
                                "Class not found:", ";");
                            throw new ActsException(
                                "class "
                                        + clsName
                                        + "failed to load, it is recommended to import the project after re-mvn!");
                        }
                    } else {
                        throw new ActsException("Please remove this field in csv or yaml!"
                                                + StringUtils.trim(filedName));
                    }

                }
                throw new ActsException("Throw an exception when reading yaml", e);
            }
        }
        return null;
    }

    /**
     * read test case from yaml file.
     * @param file
     * @param classLoader
     * @return
     */
    public static Map<String, PrepareData> loadFromYaml(File file, ClassLoader classLoader) {
        if (file.exists()) {
            try {
                String str = FileUtils.readFileToString(file);

                Yaml yaml = new Yaml(new BaseDataUtil.SelectiveConstructor(classLoader),
                    new BaseDataUtil.myRepresenter());
                return (Map<String, PrepareData>) yaml.load(str);
            } catch (Exception e) {
                String sberr = e.getMessage();
                if (StringUtils.contains(sberr, " Cannot create property=")) {
                    String filedName = StringUtils.substringBetween(sberr,
                        "Unable to find property '", "' on class");
                    if (StringUtils.isBlank(filedName)) {
                        if (StringUtils.contains(sberr, "Class not found")) {
                            String clsName = StringUtils.substringBetween(sberr,
                                "Class not found:", ";");
                            throw new ActsException(
                                "class "
                                        + clsName
                                        + " failed to load, it is recommended to import the project after re-mvn!");
                        }
                    } else {
                        throw new ActsException("Please remove this field in csv or yaml!"
                                                + StringUtils.trim(filedName));
                    }

                }
                throw new ActsException("Throw an exception when reading yaml", e);
            }
        }
        return null;
    }

    /**
     * read test case from yaml file.
     * @param str
     * @param classLoader
     * @return
     */
    public static Map<String, PrepareData> loadFromYaml(String str, ClassLoader classLoader) {

        try {
            Yaml yaml = new Yaml(new BaseDataUtil.SelectiveConstructor(classLoader),
                new BaseDataUtil.myRepresenter());
            return (Map<String, PrepareData>) yaml.load(str);

        } catch (ActsException e) {
            String sberr = e.getMessage();
            if (StringUtils.contains(sberr, " Cannot create property=")) {
                String filedName = StringUtils.substringBetween(sberr, "Unable to find property '",
                    "' on class");
                if (StringUtils.isBlank(filedName)) {
                    if (StringUtils.contains(sberr, "Class not found")) {
                        String clsName = StringUtils.substringBetween(sberr, "Class not found:",
                            ";");
                        throw new ActsException(
                            "class "
                                    + clsName
                                    + " failed to load, it is recommended to import the project after re-mvn!");
                    }
                } else {
                    throw new ActsException("Please remove this field in csv or yaml!"
                                            + StringUtils.trim(filedName));
                }

            }
            throw new ActsException("Throw an exception when reading yaml", e);
        }

    }

    /**
     * Fill case results
     * 
     * @param caseFilePath
     * @param originalCases
     * @param resultCases
     */
    public static void fillCaseResult(String caseFilePath, String encoding,
                                      Map<String, PrepareData> originalCases,
                                      Map<String, PrepareData> resultCases) {

        for (String caseId : originalCases.keySet()) {
            if (resultCases.containsKey(caseId)) {
                originalCases.put(caseId, resultCases.get(caseId));
            }
        }
    }

    /**
     * Fill case results.
     * 
     * @param caseFilePath  Original use case file full path
     * @param originalCases Original use case collection
     * @param resultCases   Result set of use cases
     * @param needFillTables Need to fill in the table
     */
    public static void fillCaseResult(String caseFilePath, String encoding,
                                      Map<String, PrepareData> originalCases,
                                      Map<String, PrepareData> resultCases,
                                      Map<String, Set<String>> needFillTables) {

        for (String caseId : originalCases.keySet()) {
            if (resultCases.containsKey(caseId)) {

                List<VirtualTable> virtualTables = originalCases.get(caseId).getExpectDataSet()
                    .getVirtualTables();
                PrepareData resultPrepareData = resultCases.get(caseId);

                List<VirtualTable> resultExceptTable = resultPrepareData.getExpectDataSet()
                    .getVirtualTables();

                if (resultExceptTable != null && !resultExceptTable.isEmpty()) {
                    Iterator<VirtualTable> iter = resultExceptTable.iterator();
                    while (iter.hasNext()) {
                        if (!needFillTables.get(caseId).contains(iter.next().getTableName())) {
                            iter.remove();
                        }
                    }
                }

                originalCases.put(caseId, resultPrepareData);

                originalCases.get(caseId).getExpectDataSet().addTables(virtualTables);
            }
        }

    }

    /**
     * Serialize special types
     * @author tantian.wc
     * @version $Id: BaseDataUtil.java, v 0.1 2015年10月14日 下午1:25:19 tantian.wc Exp $
     */
    public static class myRepresenter extends Representer {

        @Override
        protected NodeTuple representJavaBeanProperty(Object javaBean, Property property,
                                                      Object propertyValue, Tag customTag) {
            if (property.getType().equals(Currency.class)) {

                Node node = null;
                if (StringUtils.isBlank(String.valueOf(propertyValue))
                    || String.valueOf(propertyValue).equals("null")) {
                    node = representScalar(Tag.STR, "null");
                } else {
                    node = representScalar(Tag.STR, ((Currency) propertyValue).getCurrencyCode());
                }
                return new NodeTuple(representScalar(Tag.STR, property.getName()), node);
            } else if (property.getType().equals(StackTraceElement[].class)) {
                //Return null to skip theproperty
                return null;
            } else {
                super.getPropertyUtils().setSkipMissingProperties(true);
                return super
                    .representJavaBeanProperty(javaBean, property, propertyValue, customTag);
            }
        }
    }

    /**
     * Deserialize special types
     * @author tantian.wc
     * @version $Id: BaseDataUtil.java, v 0.1 2015年10月14日 下午1:24:05 tantian.wc Exp $
     */
    public static class SelectiveConstructor extends Constructor {
        private ClassLoader loader = CustomClassLoaderConstructor.class.getClassLoader();

        public SelectiveConstructor() {
            // define a custom way to create a mapping node
            yamlClassConstructors.put(NodeId.scalar, new MyScalarConstruct());
        }

        public SelectiveConstructor(ClassLoader cLoader) {
            this(Object.class, cLoader);
            yamlClassConstructors.put(NodeId.scalar, new MyScalarConstruct());

        }

        public SelectiveConstructor(Class<? extends Object> theRoot, ClassLoader theLoader) {
            super(theRoot);
            if (theLoader == null) {
                throw new ActsException("Loader must be provided");
            }
            this.loader = theLoader;
        }

        @Override
        protected Class<?> getClassForName(String name) throws ClassNotFoundException {
            return Class.forName(name, false, loader);
        }

        class MyScalarConstruct extends Constructor.ConstructScalar {
            @Override
            public Object construct(Node nnode) {
                Class<?> type = nnode.getType();
                if (type.equals(Currency.class)) {
                    if (StringUtils.isBlank(((ScalarNode) nnode).getValue())) {
                        return null;
                    }
                    return Currency.getInstance(((ScalarNode) nnode).getValue());
                } else if (type.equals(BigDecimal.class)) {
                    if (StringUtils.isBlank(((ScalarNode) nnode).getValue())) {
                        return null;
                    }
                    return new BigDecimal(((ScalarNode) nnode).getValue());
                } else {
                    return super.construct(nnode);
                }
            }
        }

    }

    /**
     * for acts ide, load all csv files.
     *
     * @param rootPath resource
     * @param subPath objBase or dataBase
     * @return
     */
    public static List<String> loadBase(String rootPath, String subPath) {

        List<String> baseList = new ArrayList<String>();

        String baseDBPath = rootPath + subPath;
        File rootFile = new File(baseDBPath);
        if (!rootFile.isDirectory()) {
            return baseList;
        }
        File[] files = rootFile.listFiles();

        for (File file : files) {
            if (file.isDirectory() && file.listFiles() != null && file.listFiles().length == 0) {
                continue;
            }
            if (file.getName().contains("svn") || file.getName().contains("git")
                || file.getName().contains("DS_Store")) {
                continue;
            }
            baseList.add(file.getName().replace(".csv", ""));
        }
        return baseList;
    }

    public static List<String> loadDesc(String csvPath) {

        List<String> descList = new ArrayList<String>();
        @SuppressWarnings("rawtypes")
        List tableList = CSVHelper.readFromCsv(csvPath);
        if (tableList == null || tableList.size() == 0) {
            return null;
        }
        String[] labels = (String[]) tableList.get(0);
        for (String label : labels) {
            if (CSVColEnum.getByCode(label) == null) {
                descList.add(label);
            }
        }
        return descList;
    }

    /**
     *
     * @param type
     * @param classLoader
     * @param ObjectBaseName
     * @param desc
     * @param rootPath
     * @return
     * @throws Exception
     */
    public static Object getVirtualObjectFromBase(Class<?> type, ClassLoader classLoader,
                                                  String ObjectBaseName, String desc,
                                                  String rootPath) throws Exception {
        if (StringUtils.isBlank(rootPath)) {
            throw new ActsException("csvPath is empty, loading class [" + ObjectBaseName
                                    + "] failed!");
        }

        ObjectProcessor processor;

        if (type.equals(VirtualEventObject.class)) {

            processor = new ObjectProcessor(classLoader, rootPath
                                                         + ActsPathConstants.OBJECT_DATA_PATH
                                                         + ObjectBaseName + "/" + ObjectBaseName
                                                         + ".csv", desc);
            Object obj = processor.genObject();
            VirtualEventObject virtualEventObject = new VirtualEventObject();
            VirtualObject vir = new VirtualObject(obj);
            vir.setFlags(processor.getFlags());
            virtualEventObject.setEventObject(vir);
            return virtualEventObject;
        } else if (type.equals(VirtualObject.class)) {
            processor = new ObjectProcessor(classLoader, rootPath
                                                         + ActsPathConstants.OBJECT_DATA_PATH
                                                         + ObjectBaseName + "/" + ObjectBaseName
                                                         + ".csv", desc);
            Object obj = processor.genObject();

            VirtualObject virtualObject = new VirtualObject(obj, obj.getClass().getCanonicalName());
            virtualObject.setFlags(processor.getFlags());
            return virtualObject;
        } else {
            processor = new ObjectProcessor(classLoader, rootPath
                                                         + ActsPathConstants.OBJECT_DATA_PATH
                                                         + ObjectBaseName + "/" + ObjectBaseName
                                                         + ".csv", desc);
            Object obj = processor.genObject();
            return obj;
        }

    }

    /**
     *
     * @param classLoader
     * @param ObjectBaseName
     * @param desc
     * @param rootPath
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object getObjectFromBase(ClassLoader classLoader, String ObjectBaseName,
                                           String desc, String rootPath, String encoding) {
        if (StringUtils.isBlank(rootPath)) {
            throw new ActsException("csvPath is empty, loading class [" + ObjectBaseName
                                    + "] failed!");
        }
        Object obj = null;
        try {

            String csvPath = rootPath + ActsPathConstants.OBJECT_DATA_PATH + ObjectBaseName + "/"
                             + ObjectBaseName + ".csv";
            ObjectProcessor processor = new ObjectProcessor(classLoader, csvPath, desc, encoding);

            //VirtualMap, load all key－value
            if (processor.getClassName().equals("com.alipay.test.acts.model.VirtualMap")) {

                Map<Object, Object> map = new HashMap<Object, Object>();
                List<String> descList = loadDesc(csvPath);

                for (String descTemp : descList) {
                    ObjectProcessor processorTemp = new ObjectProcessor(classLoader, csvPath,
                        descTemp, encoding);
                    Map<Object, Object> descMap = (Map<Object, Object>) processorTemp.genObject();
                    map.putAll(descMap);
                }

                obj = map;
            } else {
                obj = processor.genObject();
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    /**
     * load class from file with encoding based on class's simple name
     *
     * @param classLoader classloader that loaded this class
     * @param ObjectBaseName class's simple name
     * @param desc
     * @param rootPath resource directory
     * @return
     */
    public static Map<String, Map<String, String>> getObjBaseFlags(ClassLoader classLoader,
                                                                   String ObjectBaseName,
                                                                   String desc, String rootPath,
                                                                   String encoding) {

        if (StringUtils.isBlank(rootPath)) {
            throw new ActsException("csvPath is empty，load class [" + ObjectBaseName + "] failed!");
        }

        Map<String, Map<String, String>> flagMap = new LinkedHashMap<String, Map<String, String>>();
        try {

            String csvPath = rootPath + ActsPathConstants.OBJECT_DATA_PATH + ObjectBaseName + "/"
                             + ObjectBaseName + ".csv";
            ObjectProcessor processor = new ObjectProcessor(classLoader, csvPath, desc, encoding);

            //get flags
            processor.genObject();

            flagMap = processor.getFlags();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return flagMap;

    }

    /**
     * load class from file without encoding based on class's simple name
     *
     * @param classLoader
     * @param ObjectBaseName
     * @param desc
     * @param rootPath
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object getObjectFromBase(ClassLoader classLoader, String ObjectBaseName,
                                           String desc, String rootPath) {
        if (StringUtils.isBlank(rootPath)) {
            throw new ActsException("csvPath is empty，load class [" + ObjectBaseName + "] failed!");
        }
        Object obj = null;
        try {

            String csvPath = rootPath + ActsPathConstants.OBJECT_DATA_PATH + ObjectBaseName + "/"
                             + ObjectBaseName + ".csv";
            ObjectProcessor processor = new ObjectProcessor(classLoader, csvPath, desc);

            //VirtualMap, load all key－value
            if (processor.getClassName().equals("com.alipay.test.acts.model.VirtualMap")) {

                Map<Object, Object> map = new HashMap<Object, Object>();
                List<String> descList = loadDesc(csvPath);

                for (String descTemp : descList) {
                    ObjectProcessor processorTemp = new ObjectProcessor(classLoader, csvPath,
                        descTemp);
                    Map<Object, Object> descMap = (Map<Object, Object>) processorTemp.genObject();
                    map.putAll(descMap);
                }

                obj = map;
            } else {
                obj = processor.genObject();
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    /**
     * Loading table objects from base
     * @param dataBaseName base file name
     * @param desc
     * @param rootPath 
     * @param encode
     * @return The upper directory of the base file
     */
    public static VirtualTable getVirtualTableFromBase(String dataBaseName, String desc,
                                                       String rootPath, String encode) {
        VirtualTable virtualTable = new VirtualTable(dataBaseName, dataBaseName);
        String targetCSVPath = rootPath + ActsPathConstants.DB_DATA_PATH + dataBaseName + ".csv";

        List<?> tableList = CSVHelper.readFromCsv(new File(targetCSVPath), encode);
        if (tableList == null || tableList.size() == 0) {
            return null;
        }
        String[] labels = (String[]) tableList.get(0);
        int colNameCol = 0, flagCol = 0, indexCol = -1;
        for (int i = 0; i < labels.length; i++) {
            String label = labels[i].toLowerCase().trim();
            if (StringUtils.equalsIgnoreCase(label, desc)) {
                indexCol = i;
            } else {
                CSVColEnum labelEnum = CSVColEnum.getByCode(label);
                if (labelEnum != null) {
                    switch (CSVColEnum.getByCode(label)) {
                        case COLUMN:
                            colNameCol = i;

                            break;
                        case COMMENT:

                            break;
                        case FLAG:
                            flagCol = i;

                            break;
                        case TYPE:

                            break;

                        case RULE:

                            break;
                        default:
                            throw new ActsException("The csv file is in the wrong format");
                    }
                }
            }
        }
        Map<String, String> flags = new LinkedHashMap<String, String>();
        Map<String, Object> tableRowData = new LinkedHashMap<String, Object>();
        for (int i = 1; i < tableList.size(); i++) {
            String[] row = (String[]) tableList.get(i);
            String columnName = row[colNameCol].trim();
            String flagCode = row[flagCol].trim();
            if (indexCol == -1) {
                tableRowData.put(columnName, "");
            } else {
                String value = row[indexCol].trim();
                tableRowData.put(columnName, value);
            }
            flags.put(columnName, flagCode);
        }

        virtualTable.addRow(tableRowData);
        virtualTable.setFlags(flags);
        return virtualTable;

    }

    /**
     * generate csv templates based on input or return parameters of method under common-facade module
     *
     * @param csvModelRootPath resources/model/objModel/ abs path under test module
     * @param commonFacadeRootPath abs path of common－facade module
     * @param apiPackage 如：com.alipay.fc.financecore.common.service.facade.api
     */
    public static Result generateServicesObjModel(String csvModelRootPath,
                                                  String commonFacadeRootPath, String apiPackage) {
        Result result = new Result();

        try {
            // 1.get all .java files from package
            String targetApiPath = commonFacadeRootPath + "/src/main/java/"
                                   + apiPackage.replace(".", "/");
            File pakagefile = new File(targetApiPath);

            File[] files = pakagefile.listFiles();
            String comment = "";
            for (File file : files) {
                if (null == file || !file.exists() || file.isDirectory()) {
                    continue;
                }
                String className = StringUtils.substringBefore(file.getName(), ".");
                if (StringUtils.isBlank(className)) {
                    continue;
                }
                //构造类加载器
                try {

                    String genModelMsg = ReflectUtil.genModelForCls(csvModelRootPath, ReflectUtil
                        .getClassForName(apiPackage + "." + className).getClass().getClassLoader(),
                        apiPackage + "." + className);
                    comment = comment + genModelMsg;
                    logger.info("generate csv templates based on class [" + className
                                + "] successfully!");
                } catch (Exception e) {
                    String errorMsg = "failed to generate csv templates based on class ["
                                      + className + "], error stack:" + e + "\n";
                    logger.info(errorMsg);
                    comment = comment + errorMsg;
                    continue;
                }
            }
            result.setComment(comment);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setComment("An unknown exception occurred in the generated template!" + e);
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * generate csv templates based on input or return parameters of method under common-facade module
     *
     * @param classloader
     * @param csvModelRootPath resources/model/objModel/ directory under test module
     * @param commonFacadeRootPath absolute path of common-facade module
     * @param apiPackage 如：com.alipay.fc.financecore.common.service.facade.api
     */
    public static Result generateServicesObjModel(ClassLoader classloader, String csvModelRootPath,
                                                  String commonFacadeRootPath, String apiPackage) {
        Result result = new Result();

        try {

            // 1.get all .java files from package
            String targetApiPath = commonFacadeRootPath + "/src/main/java/"
                                   + apiPackage.replace(".", "/");
            File pakagefile = new File(targetApiPath);

            File[] files = pakagefile.listFiles();
            String comment = "";
            for (File file : files) {
                if (null == file || !file.exists() || file.isDirectory()) {
                    continue;
                }
                String className = StringUtils.substringBefore(file.getName(), ".");
                if (StringUtils.isBlank(className)) {
                    continue;
                }
                try {
                    String genModelMsg = ReflectUtil.genModelForCls(csvModelRootPath, classloader,
                        apiPackage + "." + className);
                    comment = comment + genModelMsg;
                    logger.info("generate csv templates based on class [" + className
                                + "] successfully!");
                } catch (Throwable e) {
                    String errorMsg = "failed to generate csv templates based on class ["
                                      + className + "], error stack:" + e + "\n";
                    logger.info(errorMsg);
                    comment = comment + errorMsg;
                    continue;
                }
            }
            result.setComment(comment);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setComment("An unknown exception occurred in the generated template!" + e);
            result.setSuccess(false);
        }
        return result;
    }

    public static Result generateServiceObjModel(String csvModelRootPath, ClassLoader classloader,
                                                 String classFullName) {

        Result result = new Result();
        try {

            String genModelMsg = ReflectUtil.genModelForCls(csvModelRootPath, classloader,
                classFullName);
            result.setComment(genModelMsg);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setComment("An unknown exception occurred in the generated template!" + e);
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * Generate csv templates according to class'qualified name,
     * when then class is in current project
     *
     * @param fullClassName
     */
    public static void generateSingleObjModel(String csvModelRootPath, String fullClassName) {
        ReflectUtil.genModelForCls(csvModelRootPath, fullClassName);
    }

    /**
     * Generate csv templates according to the class loader and class'qualified name,
     * when then class is in external jar file
     * @param csvModelRootPath
     * @param clsLoader
     * @param fullClassName
     */
    public static Result generateSingleObjModel(String csvModelRootPath, ClassLoader clsLoader,
                                                String fullClassName) {
        Result result = new Result();
        try {
            result.setSuccess(true);
            String resMsg = ReflectUtil.genModelForCls(csvModelRootPath, clsLoader, fullClassName);
            result.setComment(resMsg);
        } catch (Exception e) {
            result.setComment("An unknown exception occurred in the generated template!" + e);
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * Generate csv templates according to the class loader and class'qualified name,
     * when then class is in external jar file
     *
     * @param csvModelRootPath
     * @param clsLoader
     * @param fullClassName
     */
    public static Result generateSingleObjModel(String csvModelRootPath, ClassLoader clsLoader,
                                                String fullClassName, String methodName,
                                                boolean isResultOnly) {
        Result result = new Result();
        try {
            result.setSuccess(true);
            String resMsg = ReflectUtil.genModelForSpeciMethod(csvModelRootPath, clsLoader,
                fullClassName, methodName, isResultOnly);
            result.setComment(resMsg);
        } catch (Exception e) {
            result.setComment("An unknown exception occurred in the generated template!" + e);
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * generate a single table template
     * @param csvModelRootPath resource directory under testbundle
     * @param conn
     * @param table
     * @param dbType
     * @return
     */
    public static boolean genDBCSVFile(String csvModelRootPath, Connection conn, String table,
                                       String dataSql, String dbType, String encode) {
        try {
            DbTableModelUtil.genDBCSVFile(csvModelRootPath, conn, table, dataSql, dbType, encode);
            logger.info(table + "Generated a data template successfully!");
            return true;
        } catch (Throwable e) {
            logger.info(table + "failed to generate data template!");
            return false;
        }
    }

    /**
     * generate a single table template based on DO class
     * @param csvModelRootPath resource under test bundle
     * @param clsLoader
     * @param fullClassName
     * @param encode
     * @return
     */
    public static boolean genDOCSVFile(String csvModelRootPath, ClassLoader clsLoader,
                                       String fullClassName, String encode) {
        try {
            DbTableModelUtil.genDOCSVFile(csvModelRootPath, clsLoader, fullClassName, encode);
            logger.info(fullClassName + "Generated a data template successfully!");
            return true;
        } catch (Throwable e) {
            logger.info(fullClassName + "failed to generate data template!");
            return false;
        }
    }
}
