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

import java.io.File;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * get the system's table structure
 * @author xiaoleicxl
 * @version $Id: FullTableAnalysis.java, v 0.1 2015年10月15日 下午1:09:45 xiaoleicxl Exp $
 */
public class FullTableAnalysis {
    private static final Log LOG = LogFactory.getLog(FullTableAnalysis.class);

    /**
     * Parse the system's table structure through dal-config.xml
     * @param dalConfigXml
     * @return
     */
    public static HashSet<String> getFullTableByDalConfig(File dalConfigXml) {

        HashSet<String> tableSet = new HashSet<String>();

        DalConfigAnalysisHandler handler = new DalConfigAnalysisHandler();
        try {
            XMLParserUtil.parseXML(dalConfigXml, handler);
            return handler.getFullTables();
        } catch (Exception e) {
            LOG.error("parse dal-config.Xml error!", e);
        }
        return tableSet;

    }

}
