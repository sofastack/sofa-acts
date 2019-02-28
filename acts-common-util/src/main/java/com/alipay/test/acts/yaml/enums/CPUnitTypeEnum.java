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
package com.alipay.test.acts.yaml.enums;

import org.apache.commons.lang.StringUtils;

/**
 * CP type enum
 * 
 * @author baishuo.lp
 * @version $Id: CPTypeEnum.java, v 0.1 2015年8月12日 上午11:26:13 baishuo.lp Exp $
 */
public enum CPUnitTypeEnum {

    OBJECT("OBJECT"),

    DATABASE("DATABASE"),

    GROUP("GROUP"),

    MESSAGE("MESSAGE");

    private String code;

    private CPUnitTypeEnum(String code) {
        this.code = code;
    }

    /**
     * get CPTypeEnum based on code
     * 
     * @param code
     * @return CPTypeEnum
     */
    public CPUnitTypeEnum getByCode(String code) {
        for (CPUnitTypeEnum cPTypeEnum : CPUnitTypeEnum.values()) {
            if (StringUtils.equals(cPTypeEnum.getCode(), code)) {
                return cPTypeEnum;
            }
        }
        return null;
    }

    /**
     * Getter method for property <tt>code</tt>.
     * 
     * @return property value of code
     */
    public String getCode() {
        return code;
    }

}
