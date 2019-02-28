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
package com.alipay.test.acts.constant;

/**
 * 
 * @author mokong
 * @version $Id: ActsConstants.java, v 0.1 2015-10-8 下午2:38:27 root Exp $
 */
public class ActsConstants {

    public static final String ACTS_CONFIG_BASE_DIR         = "config/";

    public static final String ACTS_CONFIG_FILE_NAME        = "acts-config.properties";

    public static final String ACTS_CONFIG_SYSENV_PREFIX    = "acts.config.";

    public static final String SOFA_CONFIG_SYSENV_PREFIX    = "sofatest.";

    public static final String DB_CONF_KEY                  = "dbconf_file";

    public static final String DB_CONF_DIR                  = ACTS_CONFIG_BASE_DIR + "dbConf/";

    public static final String TEST_DATA_SOURCE_DIR         = ACTS_CONFIG_BASE_DIR
                                                              + "testDataSource/";

    public static final String TR_MODE                      = "tr_mode";

    public static final String SMOKE_TEST_FLAG              = "smoke_test";

    public static final String DATA_SOURCE_BEAN_NAME        = "data_source_bean_name";

    public static final String DATASOURCE_KEY               = "ds";

    public static final String DATA_SOURCE_XML_FILE         = "data_source_xml_file";

    public static final String SOFA_TEST_CONFIG_DIR         = ACTS_CONFIG_BASE_DIR
                                                              + "sofaTestConfig/";

    public static final String OPEN_MINI_BUNDLE             = "open_mini_bundle";

    public static final String MINI_BUNDLE                  = "mini_bundle";

    public static final String MINI_REPLACE_XML             = "mini_replace_xml";

    public static final String TEST_XMODE                   = "test_xmode";

    public static final String SWITCH_ENV                   = "switch_env";

    public static final String SPLIT_YAML_BY_CASE           = "spilt_yaml_by_case";

    public static final String IS_DB_NULL_BLANK_DIFF        = "is_db_null_blank_diff";

    public static final String THREADLOCAL_CHECK_INFO       = "threadlocal_check_info";

    public static final String THREADLOCAL_NOT_CHECK_SCRIPT = "threadlocal_not_check_script";

}
