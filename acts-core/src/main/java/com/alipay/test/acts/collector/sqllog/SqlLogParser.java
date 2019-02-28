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

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * sql log Parser
 * 
 * @author hongling.xiang
 * @version $Id: SqlLogParser.java, v 0.1 2015年10月26日 上午11:04:25 hongling.xiang Exp $
 */
public interface SqlLogParser {

    /**
     * Parse sql to get the table name
     * 
     * @param sql
     * @return
     */
    public String parseTableName(String sql);

    /**
     * Parse sql execution log to generate table data
     * 
     * @param sql
     * @param paramValue
     * @param paramType
     * @return
     * @throws ClassNotFoundException 
     */
    public List<Map<String, Object>> parseGenTableDatas(String sql, List<String> paramValue,
                                                        List<String> paramType);

    /**
     * Parse sql field tags
     * 
     * @param sql
     * @param tableFields
     * @return
     */
    public Map<String, String> parseTableFlags(String sql, Set<String> tableFields);
}
