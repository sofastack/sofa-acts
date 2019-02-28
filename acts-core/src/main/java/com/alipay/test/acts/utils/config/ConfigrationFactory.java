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

import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alipay.test.acts.constant.ActsConstants;
import com.alipay.test.acts.utils.config.impl.ConfigrationImpl;

/**
 * 
 * @author tianzhu.wtzh
 * @version $Id: ConfigrationFactory.java, v 0.1 2015年10月20日 上午11:39:09 tianzhu.wtzh Exp $
 */
public class ConfigrationFactory {

    protected static final Log  log                       = LogFactory
                                                              .getLog(ConfigrationFactory.class);

    private static Configration configImpl;
    public static final String  ATS_CONFIG_SYSENV_PREFIX  = "ats.config.";
    public static final String  SOFA_CONFIG_SYSENV_PREFIX = "sofatest.";
    public static final String  ATS_EXT_CONFIG_KEY        = "ext_config_file";

    /**
     * get Configration
     *
     * not require synchronization, Configration is generally loaded at system startup, single-threaded loading when started
     * @return
     */
    public static Configration getConfigration() {
        if (configImpl == null) {
            configImpl = new ConfigrationImpl();
            /**
             * Load the configuration from a configuration file or environment variable
             * Environment variables take precedence over configuration files
             * The environment variable is prefixed with ats.config
             */
            loadFromConfig(ActsConstants.ACTS_CONFIG_BASE_DIR + ActsConstants.ACTS_CONFIG_FILE_NAME);
        }
        //load additional configuration files
        String extConfs = configImpl.getPropertyValue(ATS_EXT_CONFIG_KEY);
        if (StringUtils.isNotBlank(extConfs)) {
            String[] files = extConfs.split(",");
            for (String confName : files) {
                loadFromConfig(ActsConstants.ACTS_CONFIG_BASE_DIR + confName);
            }
        }
        return configImpl;
    }

    /**
     * Load the configuration from the configuration file.
     *
     * default the ats-config.properties file is chosen.
     */
    public static void loadFromConfig(String confName) {
        if (log.isInfoEnabled()) {
            log.info("loading configuration [" + confName + "]");
        }

        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

        URL atsConfigUrl = currentClassLoader.getResource(confName);

        if (atsConfigUrl == null) {
            log.error("can not find ats config [" + confName + "]!");
            return;
        }

        Properties atsProperties = new Properties();
        try {
            atsProperties.load(atsConfigUrl.openStream());
        } catch (Exception e) {
            log.error("can not find ats config [" + confName + "] details [" + e.getMessage() + "]");
            return;
        }

        Set<Map.Entry<Object, Object>> entrySet = atsProperties.entrySet();
        for (Map.Entry<Object, Object> entry : entrySet) {
            Object keyObject = entry.getKey();
            Object valueObject = entry.getValue();
            String envConfigValue = System.getProperty(ATS_CONFIG_SYSENV_PREFIX
                                                       + keyObject.toString());
            if (StringUtils.isNotBlank(envConfigValue)) {
                configImpl.setProperty(keyObject.toString(), envConfigValue);
            } else {
                configImpl.setProperty(keyObject.toString(), valueObject.toString());
            }
        }
    }

}
