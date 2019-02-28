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
package com.alipay.test.acts.cache;

import com.alipay.test.acts.constant.ActsPathConstants;
import com.alipay.test.acts.constant.ActsYamlConstants;
import com.alipay.test.acts.object.comparer.UnitComparer;
import com.alipay.test.acts.object.generator.CustomGenerator;
import com.alipay.test.acts.util.FileUtil;
import com.alipay.test.acts.yaml.enums.CPUnitTypeEnum;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Acts cache
 * 
 * @author baishuo.lp
 * @version $Id: ActsCacheData.java, v 0.1 2015年8月12日 下午5:09:43 baishuo.lp Exp $
 */
public class ActsCacheData {

    private static Map<String, CustomGenerator> customGenerators = new HashMap<String, CustomGenerator>();

    private static Map<String, UnitComparer>    customComparers  = new HashMap<String, UnitComparer>();

    private final static Set<String>            dbDataSet        = new HashSet<String>();

    private final static Set<String>            objectDataSet    = new HashSet<String>();

    static {
        loadDataSet();
    }

    @SuppressWarnings("unchecked")
    public static void loadDataSet() {

        collectDbTableName();

        File folder = FileUtil.getTestResourceFile(ActsPathConstants.OBJECT_DATA_PATH);
        if (folder.isDirectory()) {
            String[] files = folder.list();
            for (String fileName : files) {
                objectDataSet.add(fileName);
            }
        }

        File msgFile = FileUtil.getTestResourceFile(ActsPathConstants.MSGCONFIG_PATH);
        LinkedHashMap<?, ?> msgConfigData = FileUtil.readYaml(msgFile);
        if (CollectionUtils.isEmpty(msgConfigData)) {
            return;
        }

    }

    private static void collectDbTableName() {
        File folder = FileUtil.getTestResourceFile(ActsPathConstants.DB_DATA_PATH);
        if (folder.isDirectory()) {
            String[] files = folder.list();
            for (String fileName : files) {
                String tableName = fileName.split(".csv")[0];
                dbDataSet.add(tableName);
            }
        }
    }

    public static Set<String> getDbTableNameSet() {

        if (0 == dbDataSet.size()) {
            collectDbTableName();
        }
        return dbDataSet;
    }

    public static CPUnitTypeEnum getCPUnitType(String unitName) {
        if (dbDataSet.contains(unitName)) {
            return CPUnitTypeEnum.DATABASE;
        } else if (objectDataSet.contains(unitName)) {
            return CPUnitTypeEnum.OBJECT;
        } /*else if (messageDataMap.containsKey(unitName)) {
            return CPUnitTypeEnum.MESSAGE;
          }*/else if (unitName.startsWith(ActsYamlConstants.GROUPKEY)) {
            return CPUnitTypeEnum.GROUP;
        }
        return null;
    }

    public static void addCustomGenerator(String customCode, CustomGenerator generator) {
        customGenerators.put(customCode, generator);
    }

    public static CustomGenerator getCustomGenerator(String customCode) {
        return customGenerators.get(customCode);
    }

    public static void addCustomComparer(String customCode, UnitComparer comparer) {
        customComparers.put(customCode, comparer);
    }

    public static UnitComparer getCustomComparer(String customCode) {
        return customComparers.get(customCode);
    }
}
