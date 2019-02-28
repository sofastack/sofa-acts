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
package com.alipay.test.acts.log;

import com.alipay.test.acts.context.ActsCaseContextHolder;
import com.alipay.test.acts.util.JsonUtil;
import com.alipay.test.acts.util.LogUtil;
import org.apache.commons.logging.Log;
import org.junit.Assert;

import java.util.List;

/**
 * ActsLogUtil
 *
 * @author baishuo.lp
 * @version $Id: ActsLogUtil.java, v 0.1 2014年11月25日 上午 11:41:15 baishuo.lp Exp $
 */
public class ActsLogUtil {

    /**
     * add thread log and not to the console
     *
     * @param message
     */
    public static void addProcessLog(Object message) {
        if (ActsCaseContextHolder.exists()) {
            List<String> processData = ActsCaseContextHolder.get().getLogData();
            if (message instanceof String)
                processData.add((String) message);
            else {
                processData.add(JsonUtil.toPrettyString(message));
            }
        }
    }

    /**
     * Print the current error message and add it to the exception log, not to the console
     *
     * @param message
     */
    public static void addProcessLog(String message, Throwable e) {
        if (ActsCaseContextHolder.exists()) {
            List<String> processData = ActsCaseContextHolder.get().getLogData();
            processData.add(message);
            String errMessage = LogUtil.getErrorMessage(e);
            processData.add(errMessage);
            List<String> processLog = ActsCaseContextHolder.get().getProcessErrorLog();
            processLog.add(message);
            processLog.add(errMessage);
        }
    }

    /**
     * debug
     *
     * @param logger
     * @param message
     */
    public static void debug(Log logger, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
        addProcessLog(message);
    }

    /**
     * info
     *
     * @param logger
     * @param message
     */
    public static void info(Log logger, String message) {
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
        addProcessLog(message);
    }

    /**
     * warn
     *
     * @param logger
     * @param message
     */
    public static void warn(Log logger, String message) {
        logger.warn(message);
        addProcessLog(message);
    }

    /**
     * print error without exceptino message
     *
     * @param logger
     * @param message
     */
    public static void error(Log logger, String message) {
        logger.error(message);
        addProcessLog(message);
    }

    /**
     * print error with exceptino message
     *
     * @param logger
     * @param message
     * @param e
     */
    public static void error(Log logger, String message, Throwable e) {
        logger.error(message, e);
        addProcessLog(message, e);
    }

    /**
     * print error and assert false, without exceptino message
     *
     * @param logger
     * @param message
     */
    public static void fail(Log logger, String message) {
        logger.error(message);
        Assert.fail(message);
    }

    /**
     * print error and assert false, with exceptino message
     *
     * @param logger
     * @param message
     * @param e
     */
    public static void fail(Log logger, String message, Throwable e) {
        logger.error(message, e);
        addProcessLog(message, e);
        Assert.fail(message);
    }

}
