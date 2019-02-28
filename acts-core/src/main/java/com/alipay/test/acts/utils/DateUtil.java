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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Commonly used date processing tools, focus on date strings,return value is basically<code>String</code></br>
 * As a tool used in the Velocity template, the responsibility is simple and clear</br>
 * ALipay DateUtil is Powerful in Java Code
 * 
 * @author peng.lanqp
 * @author 松雪
 * @version $Id: DateUtil.java, v 0.1 2010-11-19 上午09:19:56 peng.lanqp Exp $
 */
public class DateUtil {

    /** yyyyMMdd */
    public final static String SHORT_FORMAT           = "yyyyMMdd";

    /** yyyyMMddHHmmss */
    public final static String LONG_FORMAT            = "yyyyMMddHHmmss";

    /** yyyy-MM-dd */
    public final static String WEB_FORMAT             = "yyyy-MM-dd";

    /** HHmmss */
    public final static String TIME_FORMAT            = "HHmmss";

    /** yyyyMM */
    public final static String MONTH_FORMAT           = "yyyyMM";

    /** yyyy年MM月dd日 */
    public final static String CHINA_FORMAT           = "yyyy年MM月dd日";

    /** yyyy-MM-dd HH:mm:ss */
    public final static String LONG_WEB_FORMAT        = "yyyy-MM-dd HH:mm:ss";

    /** yyyy-MM-dd HH:mm */
    public final static String LONG_WEB_FORMAT_NO_SEC = "yyyy-MM-dd HH:mm";

    /**
     * Date parsed into a string
     * 
     * @param date
     *            Date to be formatted
     * @param format
     *            The format of the output
     * @return Formatted string
     */
    public static String format(Date date, String format) {
        if (date == null || StringUtils.isBlank(format)) {
            return StringUtils.EMPTY;
        }

        return new SimpleDateFormat(format, Locale.SIMPLIFIED_CHINESE).format(date);
    }

    /**
     * Format the current time
     * 
     * @param format
     *            The format of the output
     * @return
     */
    public static String formatCurrent(String format) {
        if (StringUtils.isBlank(format)) {
            return StringUtils.EMPTY;
        }

        return format(new Date(), format);
    }

    /**
     * string parsed into date
     * 
     * @param dateStr
     *            data string
     * @param format
     *            The format of the input
     * @return date object
     * @throws ParseException
     */
    public static Date parse(String dateStr, String format) throws ParseException {
        if (StringUtils.isBlank(format)) {
            throw new ParseException("format can not be null.", 0);
        }

        if (dateStr == null || dateStr.length() < format.length()) {
            throw new ParseException("date string's length is too small.", 0);
        }

        return new SimpleDateFormat(format, Locale.SIMPLIFIED_CHINESE).parse(dateStr);
    }

    /**
     * Date string formatting
     * 
     * @param dateStr
     *            Date string to be formatted
     * @param formatIn
     *            The format of the input
     * @param formatOut
     *            The format of the output
     * @return Formatted string
     * @throws ParseException
     */
    public static String format(String dateStr, String formatIn, String formatOut)
                                                                                  throws ParseException {

        Date date = parse(dateStr, formatIn);
        return format(date, formatOut);
    }

    /**
     * Parse the date object into a string in <code>yyyyMMdd</code> format
     *
     * @param date
     *            date to be formatted
     * @return
     */
    public static String formatShort(Date date) {
        return format(date, SHORT_FORMAT);
    }

    /**
     * Parse the date string into a string in <code>yyyyMMdd</code> format
     * 
     * @param dateStr
     *            Date string to be formatted
     * @param formatIn
     *            The format of the input
     * @return Formatted string
     */
    public static String formatShort(String dateStr, String formatIn) throws ParseException {
        return format(dateStr, formatIn, SHORT_FORMAT);
    }

    /**
     * Parse the date object into a string in <code>yyyy-MM-dd</code> format
     * 
     * @param date
     *            date to be formatted
     * @return Formatted string
     */
    public static String formatWeb(Date date) {
        return format(date, WEB_FORMAT);
    }

    /**
     * Parse the date string into a string in <code>yyyy-MM-dd</code> format
     * 
     * @param dateStr
     *            Date string to be formatted
     * @param formatIn
     *            The format of the input
     * @return Formatted string
     * @throws ParseException
     */
    public static String formatWeb(String dateStr, String formatIn) throws ParseException {
        return format(dateStr, formatIn, WEB_FORMAT);
    }

    /**
     * Parse the date object into a string in <code>yyyyMM</code> format
     * 
     * @param date
     *            date to be formatted
     * @return Formatted string
     */
    public static String formatMonth(Date date) {

        return format(date, MONTH_FORMAT);
    }

    /**
     * Parse the date object into a string in <code>HHmmss</code> format
     * 
     * @param date
     *            date to be formatted
     * @return Formatted string
     */
    public static String formatTime(Date date) {
        return format(date, TIME_FORMAT);
    }

    /**
     * Get the timestamp in the format of yyyyMMddHHmmss + a random number of length n
     * 
     * @param n
     *            The length of the random number
     * @return
     */
    public static String getTimestamp(int n) {
        return formatCurrent(LONG_FORMAT) + RandomStringUtils.randomNumeric(n);
    }

}
