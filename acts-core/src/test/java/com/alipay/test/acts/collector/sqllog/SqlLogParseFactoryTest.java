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
import java.util.List;

/**
 * 
 * @author zhiyuan.lzy
 * @version $Id: SqlLogParseFactoryTest.java, v 0.1 2016年1月20日 下午5:15:23 zhiyuan.lzy Exp $
 */
public class SqlLogParseFactoryTest {
    /**
     * Test method for {@link SqlLogParseFactory#genVirtualTable(String, List)}.
     */
    @Test
    public void testParseSqlParamValue() {
        List<String> sqlExecLog = new ArrayList<String>();

        fillUpdateList(sqlExecLog);

        List<String> list = SqlLogParseFactory.parseSqlParamValue(sqlExecLog.get(1),
            sqlExecLog.get(2));
        Assert.assertTrue(list.size() == 4);
    }

    private void fillUpdateList(List<String> sqlExecLog) {

        sqlExecLog
            .add("update test_table set params=?, GMT_MODIFIED=now() where ((param2 = ?) AND (param3 = ?) AND (param4 = ?))");

        sqlExecLog.add("[value1={key1=2.51, key2=1.50}, 222222222, test, 111111111111]");

        sqlExecLog.add("[java.lang.String, java.lang.String, java.lang.String, java.lang.String]");
    }
}
