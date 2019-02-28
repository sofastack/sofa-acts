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
package com.alipay.test.acts.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import com.alipay.test.acts.driver.ActsConfiguration;
import com.alipay.test.acts.constant.AnsiColorConstants;

/**
 * 
 * @author baishuo.lp
 * @version $Id: LogUtil.java, v 0.1 2015年2月28日 下午5:36:22 baishuo.lp Exp $
 */
public class LogUtil {

    /**
     * 
     * @param e
     * @return
     */
    public static String getErrorMessage(Throwable e) {
        StringWriter errorStack = new StringWriter();
        e.printStackTrace(new PrintWriter(errorStack));
        return errorStack.toString();
    }

    /**
     * Print critical error message with red
     * @param log
     * @param message
     */
    public static void printColoredError(Log log, String message) {
        if (ActsConfiguration.getInstance().isColoredLog()) {
            String tmp = replaceCrLf(message);
            log.error(colorMultiLine(AnsiColorConstants.ANSI_RED_BEGIN, tmp));
        } else {
            log.error(message);
        }
    }

    /**
     * Print critical info message with blue
     * @param log
     * @param message
     */
    public static void printColoredInfo(Log log, String message) {
        if (ActsConfiguration.getInstance().isColoredLog()) {
            String tmp = replaceCrLf(message);
            log.error(colorMultiLine(AnsiColorConstants.ANSI_BLUE_BEGIN, tmp));
        } else {
            log.error(message);
        }
    }

    /**
     * Correctly colorize text with line breaks
     * @param color
     * @param textWithLf
     * @return
     */
    private static String colorMultiLine(String color, String textWithLf) {
        return color
               + StringUtils.replace(textWithLf, "\n", AnsiColorConstants.ANSI_COLOR_END + "\n"
                                                       + color) + AnsiColorConstants.ANSI_COLOR_END;
    }

    /**
     * Replace \r\n with \n
     * @param input
     * @return
     */
    private static String replaceCrLf(String input) {
        return StringUtils.replace(input, "\r\n", "\n");
    }
}
