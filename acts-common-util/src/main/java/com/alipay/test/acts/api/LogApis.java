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
package com.alipay.test.acts.api;

import com.alipay.test.acts.log.ActsLogUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author baishuo.lp
 * @version $Id: LogApis.java, v 0.1 2015年3月7日 下午12:41:26 baishuo.lp Exp $
 */
public class LogApis {
    private static final Log logger = LogFactory.getLog(LogApis.class);

    public static void debug(String message) {
        ActsLogUtil.debug(logger, message);
    }

    public static void info(String message) {
        ActsLogUtil.info(logger, message);
    }

    public static void warn(String message) {
        ActsLogUtil.warn(logger, message);
    }

    public static void error(String message) {
        ActsLogUtil.error(logger, message);
    }

    public static void error(String message, Throwable e) {
        ActsLogUtil.error(logger, message, e);
    }

    public static void fail(String message) {
        ActsLogUtil.fail(logger, message);
    }

    public static void fail(String message, Throwable e) {
        ActsLogUtil.fail(logger, message, e);
    }

    public static void addTestLog(Object message) {
        ActsLogUtil.addProcessLog(message);
    }
}
