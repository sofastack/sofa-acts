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
package com.alipay.sample.servicetest.base;

import java.util.Map;
import com.alipay.test.acts.runtime.ActsRuntimeContext;
import com.alipay.test.acts.component.db.ActsDBUtils;
import java.util.List;

public class SampleActsBaseUtils {

    /**
     * get result
     *
     * @param actsRuntimeContext
     * @return
     */
    public static Object getCaseResult(ActsRuntimeContext actsRuntimeContext) {

        return actsRuntimeContext.resultObj;
    }

    /**
     * put a key to ActsRuntimeContext which is used $param
     *
     *
     * @param actsRuntimeContext
     * @param paramKey
     * @param paramValue
     */
    public static void putExpSpecialParamForCheck(ActsRuntimeContext actsRuntimeContext,
                                                  String paramKey, Object paramValue) {
        actsRuntimeContext.paramMap.put(paramKey, paramValue);
    }

    /**
     * put the checkResult to ActsRuntimeContext
     *
     * @param actsRuntimeContext
     * @param paramKey
     * @param paramValue
     */
    public static void putListExpSpecialParamForCheck(ActsRuntimeContext actsRuntimeContext,
                                                      Map<String, Object> paramMap) {
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            actsRuntimeContext.paramMap.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * get user difined param from ActsRuntimeContext
     *
     * @param paramKey
     * @param actsRuntimeContext
     * @return
     */
    public static Object getUserDifinedParamByKey(String paramKey,
                                                  ActsRuntimeContext actsRuntimeContext) {
        return actsRuntimeContext.prepareData.getVirtualParams().getParams().get(paramKey)
                .getObject();
    }

    /**
     *
     * get test Result
     * @param actsRuntimeContext
     * @return
     */
    public static Object getCaseRunResult(ActsRuntimeContext actsRuntimeContext) {
        return actsRuntimeContext.getResultObj();
    }

    /**
     * add param to actsRuntimeContext
     *
     * @param obj
     * @param actsRuntimeContext
     */
    public static void putArgToPrePareDater(Object obj, ActsRuntimeContext actsRuntimeContext) {
        actsRuntimeContext.prepareData.getArgs().addArg(obj);
    }

    /**
     * DB update util
     *
     * @param sql
     * @param tableName
     * @return
     */
    public static int getUpdateResult(String sql, String tableName) {
        return ActsDBUtils.getUpdateResultMap(sql, tableName);
    }

    /**
     * DB update util whit dbconfigkey
     *
     * @param sql
     * @param tableName
     * @param dbConfigKey datasource bean name
     * @return
     */
    public static int getUpdateResult(String sql, String tableName, String dbConfigKey) {
        return ActsDBUtils.getUpdateResultMap(sql, tableName, dbConfigKey);
    }

    /**
     * DB query util no dbconfigkey
     *
     * @param sql
     * @param tableName
     * @return
     */
    public static List<Map<String, Object>> getQueryResultMap(String sql, String tableName) {
        return ActsDBUtils.getQueryResultMap(sql, tableName);
    }

    /**
     *  DB query util with dbconfigKey
     *
     * @param sql
     * @param tableName
     * @param dbConfigKey
     * @return
     */
    public static List<Map<String, Object>> getQueryResultMap(String sql, String tableName,
                                                              String dbConfigKey) {
        return ActsDBUtils.getQueryResultMap(sql, tableName, dbConfigKey);
    }


}

 
