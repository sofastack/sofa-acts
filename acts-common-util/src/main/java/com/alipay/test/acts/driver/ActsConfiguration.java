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
package com.alipay.test.acts.driver;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alipay.test.acts.constant.ActsConstants;

/**
 * Acts-config.properties
 * 
 * @author mokong
 * @version $Id: ActsConfiguration.java, v 0.1 2015-10-8 root Exp $
 */
public class ActsConfiguration {

    private static final Log                  LOG           = LogFactory
                                                                .getLog(ActsConfiguration.class);

    private static Map<String, String>        actsConfigMap = new HashMap<String, String>();

    private Properties                        dbProperties  = new Properties();

    private String                            DB_Mode       = "devdb";

    private String                            testDataSourceFile;

    private boolean                           isTRMode      = false;

    /** Key logs need to be colored */
    private static boolean                    coloredLog    = false;

    private static volatile ActsConfiguration instance      = null;

    private ActsConfiguration() {

    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ActsConfiguration getInstance() {
        if (instance == null) {
            synchronized (ActsConfiguration.class) {
                if (instance == null) {
                    instance = new ActsConfiguration();
                    loadActsProperties();
                }

            }
        }

        return instance;
    }

    /**
     * Load acts properties.
     */
    public static void loadActsProperties() {

        if (actsConfigMap.isEmpty()) {

            Properties ActsProperties = getProperties(ActsConstants.ACTS_CONFIG_BASE_DIR
                                                      + ActsConstants.ACTS_CONFIG_FILE_NAME);

            Set<Map.Entry<Object, Object>> entrySet = ActsProperties.entrySet();
            for (Map.Entry<Object, Object> entry : entrySet) {
                Object keyObject = entry.getKey();
                Object valueObject = entry.getValue();
                String envConfigValue = System.getProperty(ActsConstants.ACTS_CONFIG_SYSENV_PREFIX
                                                           + keyObject.toString());
                if (StringUtils.isNotBlank(envConfigValue)) {
                    actsConfigMap.put(keyObject.toString(), envConfigValue);
                } else {
                    actsConfigMap.put(keyObject.toString(), valueObject.toString());
                }
            }
        }

        readJvmArgs();
    }

    /**
     * Gets acts property.
     *
     * @param key the key
     * @return the acts property
     */
    public static String getActsProperty(String key) {
        loadActsProperties();
        return actsConfigMap.get(key);
    }

    /**
     * Is off line boolean.
     *
     * @return the boolean
     */
    public boolean isOffLine() {
        return actsConfigMap.isEmpty();
    }

    /**
     * Load db configuration.
     */
    public void loadDBConfiguration() {
        if (actsConfigMap.isEmpty()) {
            loadActsProperties();
            String dbconfFile = actsConfigMap.get(ActsConstants.DB_CONF_KEY);

            if (StringUtils.isNotBlank(dbconfFile)) {
                DB_Mode = getDbMode(dbconfFile);

                dbconfFile = ActsConstants.DB_CONF_DIR + dbconfFile.trim();
                dbProperties = getProperties(dbconfFile);
            } else {
                dbconfFile = ActsConstants.ACTS_CONFIG_BASE_DIR
                             + ActsConstants.ACTS_CONFIG_FILE_NAME;
                LOG.error("No ACTS configuration [dbconf_file]！");
            }

            testDataSourceFile = ActsConstants.TEST_DATA_SOURCE_DIR + DB_Mode
                                 + "-test-datasource.xml";
            isTRMode = "true".equalsIgnoreCase(StringUtils.trim(actsConfigMap
                .get(ActsConstants.TR_MODE)));
        }

    }

    /**
     * Gets acts db property.
     *
     * @param key the key
     * @return the acts db property
     */
    public String getActsDBProperty(String key) {
        loadDBConfiguration();
        return (String) dbProperties.get(key);
    }

    /**
     * Read parameter variables related to jvm
     */
    private static void readJvmArgs() {
        coloredLog = Boolean.valueOf(System.getProperty("coloredLog", "false"));
    }

    /**
     * Gets sofa config.
     *
     * @param keyName the key name
     * @return the sofa config
     */
    public String getSofaConfig(String keyName) {
        String sofaConfig = "";
        String envSofaValue = System.getProperty(ActsConstants.SOFA_CONFIG_SYSENV_PREFIX
                                                 + keyName.toString());
        if (StringUtils.isNotBlank(envSofaValue)) {
            return envSofaValue;
        } else {
            return sofaConfig;
        }
    }

    /**
     * Get test config pattern.
     *
     * @return the string [ ]
     */
    public String[] getTestConfigPattern() {
        loadActsProperties();
        String configPattern = DB_Mode + "-sofa-test-config.properties";
        String sofaTestMode = System.getProperty("sofatest.acts.db.mode", "");
        if (!StringUtils.isEmpty(sofaTestMode)) {
            configPattern = sofaTestMode + "-sofa-test-config.properties";
        }
        LOG.info("ACTS configuration initialization: sofaTestConfigFile =  "
                 + ActsConstants.SOFA_TEST_CONFIG_DIR + configPattern);
        return new String[] { ActsConstants.SOFA_TEST_CONFIG_DIR, configPattern };
    }

    private static Properties getProperties(String propertiesPath) {
        Properties properties = new Properties();
        try {
            ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
            URL ActsConfigUrl = currentClassLoader.getResource(propertiesPath);
            properties.load(ActsConfigUrl.openStream());
        } catch (Exception e) {
            LOG.error("Error loading configuration file." + propertiesPath, e);
        }
        return properties;
    }

    private String getDbMode(String dbconfFile) {
        String[] strs = dbconfFile.split(".conf");
        String dbMode = strs[0].trim();
        // System variables can override default variables, global variables can be modified by "mvn -D"
        String sofaTestMode = System.getProperty("sofatest.db_mode", "");
        if (!StringUtils.isEmpty(sofaTestMode)) {
            dbMode = sofaTestMode;
        }
        LOG.info("ACTS configuration initialization: DB_MODE = " + dbMode);
        return dbMode;
    }

    /**
     * Gets acts config map.
     *
     * @return the acts config map
     */
    public Map<String, String> getActsConfigMap() {
        return actsConfigMap;
    }

    /**
     * Is colored log boolean.
     *
     * @return the boolean
     */
    public boolean isColoredLog() {
        return coloredLog;
    }

    public String getTestDataSourceFile() {
        return testDataSourceFile;
    }

    public void setTestDataSourceFile(String testDataSourceFile) {
        this.testDataSourceFile = testDataSourceFile;
    }

    public boolean isTRMode() {
        return isTRMode;
    }

    public void setTRMode(boolean TRMode) {
        isTRMode = TRMode;
    }
}
