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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.test.acts.object.comparer.UnitComparer;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

public class MapComparer implements UnitComparer {

    @SuppressWarnings("unchecked")
    @Override
    public boolean compare(Object expect, Object actual, String comparerFlagCode) {
        String exp = null;
        if (expect instanceof Map) {
            exp = map2String((Map<String, String>) expect);
        } else {
            exp = String.valueOf(expect);
        }

        if ((actual == null || actual.toString().equals("{}"))
            && (StringUtils.isBlank(exp) || exp.equalsIgnoreCase("null"))) {
            return true;
        } else if (actual == null && !StringUtils.isBlank(exp)) {
            return false;
        } else if ("{}".equals(actual) && !"{}".equals(exp)) {
            return false;
        } else if (StringUtils.isBlank(exp)) {
            return false;
        }

        HashMap<String, String> actualMap = new HashMap<String, String>();
        try {
            actualMap = (HashMap<String, String>) actual;
        } catch (Exception e) {
            actualMap = string2Map(actual.toString());
        }
        HashMap<String, String> expectMap = string2Map(exp);
        if (expectMap == null || actualMap == null) {
            return false;
        }
        for (Iterator<?> iterator = expectMap.entrySet().iterator(); iterator.hasNext();) {
            Entry<String, String> entry = (Entry<String, String>) iterator.next();
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            if (value != null && value.toString().equals("N")) {
                Object actValue = actualMap.get(key);
                if (actValue != null) {
                    actualMap.remove(key);
                    actualMap.put(key, "N");
                }
            }
        }
        String actualStr = map2String(actualMap);
        String expectStr = map2String(expectMap);
        boolean b = StringUtils.equalsIgnoreCase(actualStr, expectStr);
        return b;
    }

    private HashMap<String, String> string2Map(String str) {
        HashMap<String, String> map = new HashMap<String, String>();

        if (StringUtils.isEmpty(str)) {
            return null;
        }
        if (str.startsWith("{") && str.endsWith("}")) {
            str = str.replace("\"\"", "\"");
            map = JSON.parseObject(str, new TypeReference<HashMap<String, String>>() {
            });
            return map;
        }

        StringTokenizer items;
        for (StringTokenizer entrys = new StringTokenizer(str, ";"); entrys.hasMoreTokens(); map
            .put(items.nextToken(), (items.hasMoreTokens() ? ((items.nextToken())) : null))) {
            items = new StringTokenizer(entrys.nextToken(), ":");
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private String map2String(Map<String, String> map) {

        if (map == null) {
            return null;
        }
        Entry<String, String> entry;
        StringBuffer sb = new StringBuffer();
        for (Iterator<?> iterator = map.entrySet().iterator(); iterator.hasNext();) {
            entry = (Entry<String, String>) iterator.next();
            Object value = entry.getValue();
            String valueStr = "";
            if (value != null) {
                valueStr = value.toString();
            }
            sb.append(entry.getKey().toString()).append(":").append(valueStr)
                .append(iterator.hasNext() ? ";" : "");
        }
        return sb.toString();
    }
}
