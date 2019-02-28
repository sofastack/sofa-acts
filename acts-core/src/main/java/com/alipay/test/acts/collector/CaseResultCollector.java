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
package com.alipay.test.acts.collector;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.util.CollectionUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alipay.test.acts.model.PrepareData;
import com.alipay.test.acts.util.BaseDataUtil;

/**
 * Collecting execution results of cases
 * 
 * <p>
 * The function of this class is to Collecting execution results of cases, including
 * <li>the actual execution result of the service</li>
 * <li>DTO object</li>
 * <li>sql log(only including insert, update)</li>
 * <li>store tair objects</li>
 * after finishing, Assembling a new PrepareData object
 * </p>
 * 
 * @author hongling.xiang
 * @version $Id: CaseResultCollector.java, v 0.1 2015年10月27日 上午10:49:15
 *          hongling.xiang Exp $
 */
public class CaseResultCollector {

    private static final Log    log               = LogFactory.getLog(CaseResultCollector.class);

    private static final String SQL_LOG_PATH_NAME = "./logs/acts-sql.log";

    /**
     * Collect use case results
     * 
     * <pre>
     *  Note: in caseData, the PrepareData object has set the event object and the actual result object
     *
     *
     * </pre>
     * 
     * @param caseDatas
     *            data collection
     */
    public static void saveCaseResult(String caseFileFullName, Map<String, PrepareData> caseDatas) {

        try {
            if (CollectionUtils.isEmpty(caseDatas) || StringUtils.isBlank(caseFileFullName)) {
                return;
            }

            doSaveCaseResult(caseFileFullName, caseDatas);

        } catch (Throwable e) {
            log.warn("unknow exception while saving case result", e);
        } finally {
            clearTemporaryData();
        }

    }

    /**
     * Clear temporary data
     */
    private static void clearTemporaryData() {

        // delete acts-sql.log
        File sqlLogFile = new File(SQL_LOG_PATH_NAME);
        if (sqlLogFile.exists()) {
            boolean r = sqlLogFile.delete();

            if (!r) {
                //Try to delete again
                try {
                    FileUtils.forceDeleteOnExit(sqlLogFile);
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
    }

    /**
     * Save the case execution results to a temporary file
     * @throws IOException 
     */
    private static void doSaveCaseResult(String caseFileFullName, Map<String, PrepareData> caseDatas)
                                                                                                     throws IOException {

        // File name is suffixed with _res
        File caseResFile = new File(StringUtils.replace(caseFileFullName, ".yaml", "_res.yaml"));

        if (!caseResFile.exists()) {
            caseResFile.createNewFile();
        }

        // Save the case execution results to a temporary file(Automatically overwrite the original content)
        BaseDataUtil.storeToYaml(caseDatas, caseResFile);

    }
}
