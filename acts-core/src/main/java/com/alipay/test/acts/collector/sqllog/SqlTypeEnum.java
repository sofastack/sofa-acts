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
package com.alipay.test.acts.collector.sqllog;

/**
 * SQL type enum
 * 
 * @author hongling.xiang
 * @version $Id: SqlTypeEnum.java, v 0.1 2015年10月26日 上午10:52:45 hongling.xiang Exp $
 */
public enum SqlTypeEnum {

    INSERT_SQL("insert", "insert sql"),

    UPDATE_SQL("update", "update sql");

    /** code */
    private final String code;

    /** description */
    private final String desc;

    /**
     * constructor
     * 
     * @param code
     * @param desc
     */
    SqlTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
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
     * Getter method for property <tt>desc</tt>.
     * 
     * @return property value of desc
     */
    public String getDesc() {
        return desc;
    }

}
