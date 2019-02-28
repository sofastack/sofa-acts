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
package com.alipay.test.acts.utils.config;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alipay.test.acts.constant.ActsConstants;

/**
 * Read the system configuration cache
 * 
 * @author tianzhu.wtzh
 * @version $Id: PropertyConfig.java, v 0.1 2015年10月20上午10:27:33 tianzhu.wtzh
 *          Exp $
 */
public class PropertyConfig {

    protected static Log       log         = LogFactory.getLog(PropertyConfig.class);

    public static Configration testConfigs = null;

    public static String       DB_MODE     = "devdb";

    private static String      dbconfFile;

    /**
     * Load configuration file parameters,
     * load different test data connections according to configuration items
     */
    public static synchronized void initConfigs() {

        // configuration cache, loading only once
        if (null == testConfigs) {
            testConfigs = ConfigrationFactory.getConfigration();
        }

        if (null != testConfigs) {

            dbconfFile = testConfigs.getPropertyValue(ActsConstants.DB_CONF_KEY);

            if (StringUtils.isNotBlank(dbconfFile)) {

                DB_MODE = getDbMode(dbconfFile);

                boolean switchEnv = openSwitchEnv();
                if (switchEnv) {
                    dbconfFile = ActsConstants.DB_CONF_DIR + dbconfFile.trim();
                    ConfigrationFactory.loadFromConfig(dbconfFile);
                } else {

                    dbconfFile = ActsConstants.ACTS_CONFIG_BASE_DIR + dbconfFile.trim();
                    ConfigrationFactory.loadFromConfig(dbconfFile);
                }
            } else {
                dbconfFile = ActsConstants.ACTS_CONFIG_BASE_DIR
                             + ActsConstants.ACTS_CONFIG_FILE_NAME;
                log.info("ACTS is not contain [dbconf_file]!");
            }

            testConfigs.setProperty("filePath", "");
        }

    }

    /**
     * Positioning the sofa-test-config.properties file according to DB_MODE
     * 
     * @return
     */
    public static String[] getTestConfigPattern() {
        if (null == testConfigs) {
            initConfigs();
        }
        String configPattern = DB_MODE + "-sofa-test-config.properties";
        String sofaTestMode = System.getProperty("sofatest.ats.db.mode", "");
        if (!StringUtils.isEmpty(sofaTestMode)) {
            configPattern = sofaTestMode + "-sofa-test-config.properties";
        }
        log.info("ACTS config init :sofaTestConfigFile = " + ActsConstants.SOFA_TEST_CONFIG_DIR
                 + configPattern);
        return new String[] { ActsConstants.SOFA_TEST_CONFIG_DIR, configPattern };
    }

    /**
     * Get DB_MODE according to the name of the data source file
     * 
     * @param dbconfFile
     * @return
     * @author mokong
     */
    private static String getDbMode(String dbconfFile) {
        String[] strs = dbconfFile.split(".conf");
        String dbMode = strs[0].trim();
        // System variables can override default variables, global variables can be modified by "mvn -D"
        String sofaTestMode = System.getProperty("sofatest.db_mode", "");
        if (!StringUtils.isEmpty(sofaTestMode)) {
            dbMode = sofaTestMode;
        }
        log.info("ATS configuration initialization:DB_MODE = " + dbMode);
        return dbMode;
    }

    /***
     * @return true:tr false:ws */
    public static boolean isTRModel() {
        if (null == testConfigs) {
            initConfigs();
        }
        if (null != testConfigs && testConfigs.getPropertyValue(ActsConstants.TR_MODE) != null
            && "true".equals(testConfigs.getPropertyValue(ActsConstants.TR_MODE).trim())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Whether to support automatic switching environment
     * 
     * @return true(default):support
     */
    public static boolean openSwitchEnv() {
        if (null == testConfigs) {
            initConfigs();
        }
        if (null != testConfigs && testConfigs.getPropertyValue(ActsConstants.SWITCH_ENV) != null
            && "false".equals(testConfigs.getPropertyValue(ActsConstants.SWITCH_ENV).trim())) {
            return false;
        }
        return true;
    }

    /**
     * Get data configuration file
     */
    public static String getDBConfigFile() {
        if (null == testConfigs) {
            initConfigs();
        }
        String dbConfFile = testConfigs.getPropertyValue(ActsConstants.DB_CONF_KEY);
        if (StringUtils.isBlank(dbConfFile)) {
            return null;
        }
        return dbConfFile;
    }

    /**
     * read Config
     * 
     * @param key
     *            need to read Config
     * @return
     */
    public static String getConfig(String key) {
        if (null == testConfigs) {
            initConfigs();
        }
        String value = testConfigs.getPropertyValue(key);
        return value;
    }

    /**
     * Set or modify a set of values
     * 
     * @param key
     * @param value
     */
    public static void setConfig(String key, String value) {
        if (null == testConfigs) {
            initConfigs();
        }
        testConfigs.setProperty(key, value);
    }

    /**
     * Get the database configuration file
     * 
     * @return
     */
    public static String getDbconfFile() {
        if (null == testConfigs) {
            initConfigs();
        }
        return dbconfFile;
    }

}
