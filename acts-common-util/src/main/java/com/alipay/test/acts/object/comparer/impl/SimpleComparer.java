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
package com.alipay.test.acts.object.comparer.impl;

import com.alipay.test.acts.driver.ActsConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import com.alipay.test.acts.constant.ActsConfigConstants;
import com.alipay.test.acts.constant.ActsYamlConstants;
import com.alipay.test.acts.object.comparer.UnitComparer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * "Y" flag
 *
 * @author midang
 *
 * @version $Id: StringComparer.java,v 0.1 2010-8-12 上午11:26:37 midang Exp $
 */
public class SimpleComparer implements UnitComparer {

    private final static String lineSeparator = SystemUtils.LINE_SEPARATOR;

    public static List<String>  ignoreList    = null;

    /**
     * 
     * <pre>
     * compare("null",null)     = true
     * compare("null","null")   = true
     * compare("abc","abc")     = true
     * </pre>
     * 
     * @param expect
     * @param actual
     * @return
     */
    @Override
    public boolean compare(Object expect, Object actual, String comparerFlagCode) {
        if (ignoreList == null) {
            ignoreList = new ArrayList<String>();
            String ignoreStr = ActsConfiguration
                .getActsProperty(ActsConfigConstants.IGNORE_STRING_LIST_KEY);
            if (StringUtils.isNotBlank(ignoreStr)) {
                for (String ignoreString : ignoreStr.split(",")) {
                    ignoreList.add(ignoreString);
                }
            }
        }

        String exp = String.valueOf(expect);
        if (actual == null && (StringUtils.isBlank(exp) || "null".equals(exp))) {
            return true;
        }
        if (exp != null) {
            exp = exp.replace("\"\"", "\"");
            exp = exp.replace(ActsYamlConstants.LINESEPARATOR, lineSeparator);
        }

        if (ignoreList.size() > 0 && exp != null && actual != null) {
            for (String ignoreString : ignoreList) {
                exp = exp.replaceAll(ignoreString, "");
                actual = String.valueOf(actual).replaceAll(ignoreString, "");
            }
        }

        return StringUtils.equals(exp, String.valueOf(actual));
    }

}
