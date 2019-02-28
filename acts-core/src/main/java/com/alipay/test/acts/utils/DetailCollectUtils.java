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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alipay.test.acts.util.LogUtil;

public class DetailCollectUtils {
    /* log* */
    private static final Log    log = LogFactory.getLog(DetailCollectUtils.class);

    private static StringBuffer sb  = new StringBuffer();

    /**
     * Append detail.
     *
     * @param content the content
     */
    public static void appendDetail(String content) {
        sb.append(content);
        sb.append("\r\n");
    }

    /**
     * Append and log.
     *
     * @param content the content
     * @param log the log
     */
    public static void appendAndLog(String content, Log log) {
        log.info(content);
        sb.append(content);
        sb.append("\r\n");
    }

    /**
     * Append and log colored info.
     *
     * @param content the content
     * @param log the log
     */
    public static void appendAndLogColoredInfo(String content, Log log) {
        LogUtil.printColoredInfo(log, content);
        sb.append(content);
        sb.append("\r\n");
    }

    /**
     * Append and log colored error.
     *
     * @param content the content
     * @param log the log
     */
    public static void appendAndLogColoredError(String content, Log log) {
        LogUtil.printColoredError(log, content);
        sb.append(content);
        sb.append("\r\n");
    }

    /**
     * Buffer to bytes byte [ ].
     *
     * @return the byte [ ]
     */
    public static byte[] bufferToBytes() {
        return sb.toString().getBytes();
    }

    /**
     * Clear buffer.
     */
    public static void clearBuffer() {
        sb = new StringBuffer();
    }

    /**
     * Save buffer.
     *
     * @param logPath the log path
     */
    public static void saveBuffer(String logPath) {
        try {
            if (StringUtils.isEmpty(logPath)) {

                log.debug("logpath is empty, skip");
                return;
            }

            logPath = StringUtils.replace(logPath, "yaml", "log");
            File caseDetail = new File(logPath);
            FileOutputStream fop = new FileOutputStream(caseDetail);
            byte[] contentInBytes = DetailCollectUtils.bufferToBytes();
            DetailCollectUtils.clearBuffer();
            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }

}
