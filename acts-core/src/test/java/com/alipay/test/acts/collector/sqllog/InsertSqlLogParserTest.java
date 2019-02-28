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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author qingqin
 * @version $Id: UpdateSqlLogParserTest.java, v 0.1 2019年01月12日 下午8:23 qingqin Exp $
 */
public class InsertSqlLogParserTest {

    /***
     * Test method for {@link InsertSqlLogParser#parseGenTableDatas(String, List, List)}
     */
    @Test
    public void testParseGenTableDatas() {
        String insertSql = "insert into ast_main_transaction_c (tx_id,status,gmt_create) values (?, ?, systimestamp)";

        List<String> paramValue = new ArrayList<String>();
        paramValue.add("6666");
        paramValue.add("F");
        List<String> paramType = new ArrayList<String>();
        paramType.add("java.lang.String");
        paramType.add("java.lang.String");

        InsertSqlLogParser parser = new InsertSqlLogParser();
        List<Map<String, Object>> list = parser
            .parseGenTableDatas(insertSql, paramValue, paramType);
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0).get("tx_id"), "6666");
        Assert.assertEquals(list.get(0).get("status"), "F");
        Assert.assertEquals(list.get(0).get("gmt_create"), null);
    }

    /**
     * Test method for {@link InsertSqlLogParser#parseTableName(String)}.
     */
    @Test
    public void testParseTableName() {
        String insertSql = "insert into ast_main_transaction_c (tx_id,status,gmt_create) values (?, ?, systimestamp)";
        InsertSqlLogParser parser = new InsertSqlLogParser();
        String name = parser.parseTableName(insertSql);
        Assert.assertEquals(name, "ast_main_transaction_c");
    }

    /***
     * Test method for {@link InsertSqlLogParser#parseTableFlags(String, Set)}
     *
     */
    @Test
    public void testParseTableFlags() {
        String updateSql = "insert into ast_main_transaction_c (tx_id,status,gmt_create) values (?, ?, systimestamp)";
        InsertSqlLogParser parser = new InsertSqlLogParser();

        Set<String> tableFields = new HashSet<String>(4);
        tableFields.add("tx_id");
        tableFields.add("status");
        tableFields.add("gmt_create");

        Map<String, String> flags = parser.parseTableFlags(updateSql, tableFields);
        Assert.assertEquals(flags.get("gmt_create"), "Y");
        Assert.assertEquals(flags.get("status"), "Y");
        Assert.assertEquals(flags.get("tx_id"), "Y");

    }
}