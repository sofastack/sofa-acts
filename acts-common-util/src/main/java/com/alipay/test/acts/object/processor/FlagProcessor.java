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
package com.alipay.test.acts.object.processor;

import com.alipay.test.acts.exception.ActsException;
import com.alipay.test.acts.helper.CSVHelper;
import com.alipay.test.acts.log.ActsLogUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/***
 * flag
 * 
 * @author zhiyuan.lzy
 * @version $Id: FlagProcessor.java, v 0.1 2016年2月1日 下午4:44:14 zhiyuan.lzy Exp $
 */
public class FlagProcessor {

    private static final Log logger          = LogFactory.getLog(FlagProcessor.class);

    private final int        CLASSNAMECOL    = 0;
    private final int        PROPERTYNAMECOL = 1;
    private final int        FLAGVALUECOL    = 4;

    public FlagProcessor() {

    }

    /***
     * external method. get flag
     * 
     * @param csvPath
     * @param encoding
     * @return
     */
    public Map<String, Map<String, String>> genFlag(String csvPath, String encoding) {

        Map<String, Map<String, String>> map = processCsvFolder(csvPath, encoding);
        return map;
    }

    @SuppressWarnings({ "rawtypes" })
    private Map<String, Map<String, String>> processCsvFolder(String csvPath, String encoding)
                                                                                              throws ActsException {
        Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
        File csvFile = new File(csvPath);

        File folder = csvFile.getParentFile();

        Collection allFiles = FileUtils.listFiles(folder, new String[] { "csv" }, true);
        Iterator iterator = allFiles.iterator();

        while (iterator.hasNext()) {
            File currentFile = (File) iterator.next();
            if (isGeneric(currentFile)) {
                continue;
            }
            Map<String, Map<String, String>> oneMap = processCsvFile(currentFile.getAbsolutePath(),
                encoding);
            result.putAll(oneMap);
        }

        return result;

    }

    /***
     * generic type
     * 
     * @param file
     * @return
     */
    private boolean isGeneric(File file) {

        String fileName = file.getName();
        if (StringUtils.contains(fileName, "_")) {
            return true;
        }
        return false;

    }

    @SuppressWarnings({ "rawtypes" })
    private Map<String, Map<String, String>> processCsvFile(String csvPath, String encoding) {

        Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
        //1. load data in csv
        List tableList = CSVHelper.readFromCsv(csvPath, encoding);
        if (tableList == null || tableList.size() == 0) {
            ActsLogUtil.fail(logger, csvPath + " is empty or doesn't exist !");
        }
        if (tableList.size() < 2) {
            throw new ActsException("the file content is empty or illegal! file path:" + csvPath);
        }

        String className = ((String[]) tableList.get(1))[CLASSNAMECOL];
        Map<String, String> value = new HashMap<String, String>();
        result.put(className, value);
        for (int i = 1; i < tableList.size(); i++) {
            String[] row = (String[]) tableList.get(i);
            String fieldName = row[PROPERTYNAMECOL];
            String flagCode = row[FLAGVALUECOL];
            result.get(className).put(fieldName, flagCode);

        }
        return result;

    }

}
