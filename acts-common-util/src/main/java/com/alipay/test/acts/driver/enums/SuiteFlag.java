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
package com.alipay.test.acts.driver.enums;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author baishuo.lp
 * @version $Id: SuiteFlag.java, v 0.1 2015年3月2日 下午6:37:55 baishuo.lp Exp $
 */
public enum SuiteFlag {
    TESTCASE("TC", "TestCase", "正常执行用例", "正常执行用例"),

    TESTSUITE("TS", "TestSuite", "正常执行测试套件", "正常执行测试套件"),

    DELETE("DEL", "SkipTest", "跳过执行", "跳过执行");

    private final String code;

    private final String englishName;

    private final String chineseName;

    private final String description;

    private SuiteFlag(String code, String englishName, String chineseName, String description) {
        this.code = code;
        this.englishName = englishName;
        this.chineseName = chineseName;
        this.description = description;
    }

    public static SuiteFlag getByCode(String code) {
        for (SuiteFlag p : SuiteFlag.values()) {
            if (p.getCode().equalsIgnoreCase(code)) {
                return p;
            }
        }

        return null;
    }

    public static SuiteFlag getByEnglishName(String englishName) {
        for (SuiteFlag value : SuiteFlag.values()) {
            if (StringUtils.equals(englishName, value.getEnglishName())) {
                return value;
            }
        }
        return null;
    }

    public static SuiteFlag getByChineseName(String chineseName) {
        for (SuiteFlag value : SuiteFlag.values()) {
            if (StringUtils.equals(chineseName, value.getChineseName())) {
                return value;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getChineseName() {
        return chineseName;
    }

    public String getDescription() {
        return description;
    }

}
