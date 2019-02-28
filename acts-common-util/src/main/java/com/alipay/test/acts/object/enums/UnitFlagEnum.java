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
package com.alipay.test.acts.object.enums;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author baishuo.lp
 * @version $Id: UnitComparerFlagEnum.java, v 0.1 2015年8月12日 下午2:28:41 baishuo.lp Exp $
 */
public enum UnitFlagEnum {

    Y("Y", "常规比较器"),

    N("N", "不校验"),

    D("D", "日期比较器"),

    C("C", "条件校验器"),

    R("R", "正则比较器"),

    A("A", "非空比较器"),

    M("M", "Map比较器"),

    F("F", "文件比较器"),

    CUSTOM("CUSTOM", "自定义比较器");

    private String code;

    private String description;

    private UnitFlagEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * get UnitFlagEnum based on code
     * 
     * @param code
     * @return UnitComparerFlagEnum
     */
    public static UnitFlagEnum getByCode(String code) {
        for (UnitFlagEnum unitComparerFlagEnum : UnitFlagEnum.values()) {
            if (StringUtils.equals(unitComparerFlagEnum.getCode(), code)) {
                return unitComparerFlagEnum;
            }
        }
        return UnitFlagEnum.CUSTOM;
    }

    /**
     * Getter method for property <tt>code</tt>.
     * 
     * @return property value of code
     */
    public String getCode() {
        return code;
    }

    /**
     * Getter method for property <tt>description</tt>.
     * 
     * @return property value of description
     */
    public String getDescription() {
        return description;
    }

}
