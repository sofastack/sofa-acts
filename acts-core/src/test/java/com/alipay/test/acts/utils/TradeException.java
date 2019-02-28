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
package com.alipay.test.acts.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author feixun.dxf
 * @version $Id: TradeException.java, v 0.1 2016年1月21日 下午10:46:50 feixun.dxf Exp $
 */
public class TradeException extends RuntimeException implements Serializable {

    /** serial UID */
    private static final long          serialVersionUID = -7374337528802243672L;

    private String                     code;

    private BigDecimal                 bigDecimal;

    private Double[]                   doubles;

    private List<Map<String, String>>  list;

    private Map<String, List<Integer>> map;

    private Set<Date>                  set;

    private Msg                        msg;

    public static class Msg {
        String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    /**
     * 
     */
    public TradeException() {
        super();
    }

    public TradeException(String code) {
        super();
        this.code = code;
    }

    public TradeException(String code, BigDecimal bigDecimal) {
        super();
        this.code = code;
        this.bigDecimal = bigDecimal;
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
     * Setter method for property <tt>code</tt>.
     * 
     * @param code value to be assigned to property code
     */
    public void setCode(String code) {
        this.code = code;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    public Double[] getDoubles() {
        return doubles;
    }

    public void setDoubles(Double[] doubles) {
        this.doubles = doubles;
    }

    public List<Map<String, String>> getList() {
        return list;
    }

    public void setList(List<Map<String, String>> list) {
        this.list = list;
    }

    public Map<String, List<Integer>> getMap() {
        return map;
    }

    public void setMap(Map<String, List<Integer>> map) {
        this.map = map;
    }

    public Set<Date> getSet() {
        return set;
    }

    public void setSet(Set<Date> set) {
        this.set = set;
    }

    public Msg getMsg() {
        return msg;
    }

    public void setMsg(Msg msg) {
        this.msg = msg;
    }
}
