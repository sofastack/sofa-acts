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

import com.alipay.test.acts.model.PrepareData;
import com.alipay.test.acts.model.VirtualTable;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author qingqin
 * @version $Id: MethodUtilsTest.java, v 0.1 2019年01月12日 下午3:13 qingqin Exp $
 */
public class CaseResultCollectUtilTest {

    /**
     * Test method for {@link CaseResultCollectUtil#mergeAllSameTables(List)}.
     */
    @Test
    public void testMergeAllSameTables() {
        List<VirtualTable> caseVirtualTables = new ArrayList<VirtualTable>();

        VirtualTable vt1 = new VirtualTable();
        vt1.setTableName("tb1");
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("c1", "c1");
        row.put("c2", "c2");
        vt1.addRow(row);

        VirtualTable vt11 = new VirtualTable();
        vt11.setTableName("tb1");
        Map<String, Object> row11 = new HashMap<String, Object>();
        row11.put("c1", "c11");
        row11.put("c3", "c3");
        vt11.addRow(row11);

        VirtualTable vt2 = new VirtualTable();
        vt2.setTableName("tb2");
        Map<String, Object> row2 = new HashMap<String, Object>();
        row2.put("c3", "c3");
        row2.put("c4", "c4");
        vt2.addRow(row2);

        caseVirtualTables.add(vt1);
        caseVirtualTables.add(vt2);
        caseVirtualTables.add(vt11);
        List<VirtualTable> result = CaseResultCollectUtil.mergeAllSameTables(caseVirtualTables);

        Assert.assertEquals(result.size(), 2);
    }

    /**
     * Test method for {@link CaseResultCollectUtil#collectSqlLog(String, PrepareData)}
     */
    @Test
    public void testCollectSqlLog() {
        prepareSqlLog();

        PrepareData prepareData = new PrepareData();
        CaseResultCollectUtil.collectSqlLog("xyz_zyx_caseID_001", prepareData);

        Assert.assertEquals(prepareData.getExpectDataSet().getVirtualTables().size(), 1);

        CaseResultCollectUtil.collectSqlLog("xyz_zyx_caseID_002", prepareData);
        Assert.assertEquals(prepareData.getExpectDataSet().getVirtualTables().size(), 1);
    }

    private void prepareSqlLog() {
        String log = "11:47:42,563 [main] DEBUG JakartaCommonsLoggingImpl : {conn-100000} Connection\n"
                     + "11:48:47,416 [main] INFO  LoggerWrapper : Start acts_caseId=xyz_zyx_caseID_001\n"
                     + "11:48:48,009 [main] DEBUG JakartaCommonsLoggingImpl : {conn-103314} Connection\n"
                     + "11:48:48,024 [main] DEBUG JakartaCommonsLoggingImpl : {conn-103314} Preparing Statement:          select tx_id, "
                     + "status, gmt_create     from ast_main_transaction       where tx_id = ? for update wait 1\n"
                     + "11:48:48,024 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103315} Executing Statement:          select tx_id, "
                     + "status, gmt_create     from ast_main_transaction       where tx_id = ? for update wait 1\n"
                     + "11:48:48,024 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103315} Parameters: [1234567890]\n"
                     + "11:48:48,024 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103315} Types: [java.lang.String]\n"
                     + "11:48:48,040 [main] DEBUG JakartaCommonsLoggingImpl : {rset-103316} ResultSet\n"
                     + "11:48:48,055 [main] DEBUG JakartaCommonsLoggingImpl : {conn-103317} Connection\n"
                     + "11:48:48,055 [main] DEBUG JakartaCommonsLoggingImpl : {conn-103317} Preparing Statement:               insert into "
                     + "ast_main_transaction(tx_id,status,gmt_create) values (?, ?, systimestamp)\n"
                     + "11:48:48,055 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103318} Executing Statement:               insert into "
                     + "ast_main_transaction(tx_id,status,gmt_create) values (?, ?, systimestamp)\n"
                     + "11:48:48,055 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103318} Parameters: [1234567890, P]\n"
                     + "11:48:48,055 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103318} Types: [java.lang.String, java.lang.String]\n"
                     + "11:48:53,887 [main] DEBUG JakartaCommonsLoggingImpl : {conn-103418} Connection\n"
                     + "11:48:53,887 [main] DEBUG JakartaCommonsLoggingImpl : {conn-103418} Preparing Statement:               delete from "
                     + "ast_trans_info where (tx_id = ?)\n"
                     + "11:48:53,887 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103419} Executing Statement:               delete from "
                     + "ast_trans_info where (tx_id = ?)\n"
                     + "11:48:53,887 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103419} Parameters: [1234567890]\n"
                     + "11:48:53,887 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103419} Types: [java.lang.String]\n"
                     + "11:48:53,949 [main] INFO  LoggerWrapper : Finish acts_caseId=xyz_zyx_caseID_001\n"
                     + "11:47:42,563 [main] DEBUG JakartaCommonsLoggingImpl : {conn-100000} Connection\n"
                     + "11:48:48,055 [main] DEBUG JakartaCommonsLoggingImpl : {conn-103317} Preparing Statement:               insert into "
                     + "ast_main_transaction_a(tx_id,status,gmt_create) values (?, ?, systimestamp)\n"
                     + "11:48:48,055 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103318} Executing Statement:               insert into "
                     + "ast_main_transaction_a(tx_id,status,gmt_create) values (?, ?, systimestamp)\n"
                     + "11:48:48,055 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103318} Parameters: [1234567890, P]\n"
                     + "11:48:48,055 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103318} Types: [java.lang.String, java.lang.String]\n"
                     + "11:48:47,416 [main] INFO  LoggerWrapper : Start acts_caseId=xyz_zyx_caseID_002\n"
                     + "11:48:48,009 [main] DEBUG JakartaCommonsLoggingImpl : {conn-103314} Connection\n"
                     + "11:48:48,024 [main] DEBUG JakartaCommonsLoggingImpl : {conn-103314} Preparing Statement:          select tx_id, "
                     + "status, gmt_create     from ast_main_transaction_c       where tx_id = ? for update wait 1\n"
                     + "11:48:48,024 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103315} Executing Statement:          select tx_id, "
                     + "status, gmt_create     from ast_main_transaction_c       where tx_id = ? for update wait 1\n"
                     + "11:48:48,024 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103315} Parameters: [1234567890]\n"
                     + "11:48:48,024 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103315} Types: [java.lang.String]\n"
                     + "11:48:48,040 [main] DEBUG JakartaCommonsLoggingImpl : {rset-103316} ResultSet\n"
                     + "11:48:48,055 [main] DEBUG JakartaCommonsLoggingImpl : {conn-103317} Connection\n"
                     + "11:48:48,055 [main] DEBUG JakartaCommonsLoggingImpl : {conn-103317} Preparing Statement:               insert into "
                     + "ast_main_transaction_c(tx_id,status,gmt_create) values (?, ?, systimestamp)\n"
                     + "11:48:48,055 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103318} Executing Statement:               insert into "
                     + "ast_main_transaction_c(tx_id,status,gmt_create) values (?, ?, systimestamp)\n"
                     + "11:48:48,055 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103318} Parameters: [1234567890, P]\n"
                     + "11:48:48,055 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103318} Types: [java.lang.String, java.lang.String]\n"
                     + "11:48:53,887 [main] DEBUG JakartaCommonsLoggingImpl : {conn-103418} Connection\n"
                     + "11:48:53,887 [main] DEBUG JakartaCommonsLoggingImpl : {conn-103418} Preparing Statement:               delete from "
                     + "ast_trans_info where (tx_id = ?)\n"
                     + "11:48:53,887 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103419} Executing Statement:               delete from "
                     + "ast_trans_info where (tx_id = ?)\n"
                     + "11:48:53,887 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103419} Parameters: [1234567890]\n"
                     + "11:48:53,887 [main] DEBUG JakartaCommonsLoggingImpl : {pstm-103419} Types: [java.lang.String]\n"
                     + "11:48:53,949 [main] INFO  LoggerWrapper : Finish acts_caseId=xyz_zyx_caseID_002";

        BufferedWriter logWriter = null;
        try {
            File file = new File("./logs");
            if (!file.exists()) {
                file.mkdir();
            }
            logWriter = new BufferedWriter(new FileWriter("./logs/acts-sql.log", false));
            logWriter.write(log);
        } catch (IOException e) {
            Assert.fail("can't create log", e);
        } finally {
            if (null != logWriter) {
                try {
                    logWriter.close();
                } catch (IOException ex) {

                }
            }
        }
    }

}
