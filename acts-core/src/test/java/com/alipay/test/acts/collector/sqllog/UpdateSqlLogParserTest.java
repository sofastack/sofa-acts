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
public class UpdateSqlLogParserTest {

    /**
     * Test method for {@link UpdateSqlLogParser#parseTableName(String)}.
     */
    @Test
    public void testParseTableName() {
        String updateSql = "update test_table set params=?, GMT_MODIFIED=now() where ((param2 = ?) AND (param3 = ?) AND (param4 = ?))";
        UpdateSqlLogParser parser = new UpdateSqlLogParser();
        String name = parser.parseTableName(updateSql);
        Assert.assertEquals(name, "test_table");
    }

    /***
     * Test method for {@link UpdateSqlLogParser#genSql(String, List, List)}
     */
    @Test
    public void testGenSql() {
        String updateSql = "update test_table set params=?, GMT_MODIFIED=now() where ((param2 = ?) AND (param3 = ?) AND (param4 = ?))";
        UpdateSqlLogParser parser = new UpdateSqlLogParser();

        List<String> values = new ArrayList<String>(4);
        values.add("q");
        values.add("i");
        values.add("n");
        values.add("g");

        List<String> types = new ArrayList<String>(4);

        String sql = parser.genSql(updateSql, values, types);

        Assert
            .assertEquals(sql,
                "select * from test_table where ((param2 = 'i') AND (param3 = 'n') AND (param4 = 'g'))\r\n");

    }

    /***
     * Test method for {@link UpdateSqlLogParser#parseTableFlags(String, Set)}
     *
     */
    @Test
    public void testParseTableFlags() {
        String updateSql = "update test_table set params=?, GMT_MODIFIED=now() where ((param2 = ?) AND (param3 = ?) AND (param4 = ?))";
        UpdateSqlLogParser parser = new UpdateSqlLogParser();

        Set<String> tableFields = new HashSet<String>(4);
        tableFields.add("param2");
        tableFields.add("param3");
        tableFields.add("param4");

        Map<String, String> flags = parser.parseTableFlags(updateSql, tableFields);
        Assert.assertEquals(flags.get("param"), null);
        Assert.assertEquals(flags.get("param2"), "C");
        Assert.assertEquals(flags.get("param3"), "C");
        Assert.assertEquals(flags.get("param4"), "C");
    }
}