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
package com.alipay.test.acts.db.enums;

/**
 * Enumeration of column values for csv
 * 
 * @author baishuo.lp
 * @version $Id: CSVColEnum.java, v 0.1 2015年8月17日 下午9:55:06 baishuo.lp Exp $
 */
public enum CSVColEnum {

    COLUMN("column", "column in CSV file"),

    COMMENT("comment", "comment in CSV file"),

    TYPE("type", "type of column in CSV file"),

    CLASS("class", "class name of CSV file"),

    PROPERTY("property", "property name of CSV file"),

    FLAG("flag", "Generation/comparison flag"),

    RULE("rule", "Atomic data rules");

    private String code;

    private String description;

    CSVColEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static CSVColEnum getByCode(String code) {
        for (CSVColEnum col : CSVColEnum.values()) {
            if (col.getCode().equalsIgnoreCase(code)) {
                return col;
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

    /**
     * Getter method for property <tt>description</tt>.
     * 
     * @return property value of description
     */
    public String getDescription() {
        return description;
    }

}
