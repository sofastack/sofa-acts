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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author tinghe
 * @version $Id: DateUtil.java, v 0.1 2013-6-25 下午1:57:40 tinghe Exp $
 */
public class DateUtil {

    public final static long     ONE_DAY_SECONDS      = 86400;
    public final static long     ONE_DAY_MILL_SECONDS = 86400000;

    public static final TimeZone UTC_TIME_ZONE        = TimeZone.getTimeZone("GMT");
    public static final long     MILLIS_PER_SECOND    = 1000;
    public static final long     MILLIS_PER_MINUTE    = 60 * MILLIS_PER_SECOND;
    public static final long     MILLIS_PER_HOUR      = 60 * MILLIS_PER_MINUTE;
    public static final long     MILLIS_PER_DAY       = 24 * MILLIS_PER_HOUR;

    public final static String   monthFormat          = "yyyyMM";
    public final static String   chineseDtFormat      = "yyyy年MM月dd日";
    public final static String   noSecondFormat       = "yyyy-MM-dd HH:mm";

    /**  yyyy-MM-dd HH:mm:ss */
    public static final String   simple               = "yyyy-MM-dd HH:mm:ss";

    /**  yyyy-MM-dd */
    public static final String   dbSimple             = "yyyy-MM-dd";

    /**  yyyy年MM月dd日 */
    public static final String   dtSimpleChinese      = "yyyy年MM月dd日";

    public static final String   week                 = "EEEE";

    /**  yyyyMMdd */
    public static final String   dtShort              = "yyyyMMdd";

    /**  yyyyMMddHHmmss */
    public static final String   dtLong               = "yyyyMMddHHmmss";

    /**  HH:mm:ss */
    public static final String   hmsFormat            = "HH:mm:ss";

    /** yyyy-MM-dd HH:mm */
    public static final String   simpleFormat         = "yyyy-MM-dd HH:mm";

    public static final String   dtLongMill           = "yyyyMMddHHmmssS";

    public final static String   timeFormat           = "HHmmss";

    /**
     *
     * @param date1
     * @param days
     *
     * @return 新的日期
     */
    public static Date addDays(Date date1, long days) {
        return addSeconds(date1, days * ONE_DAY_SECONDS);
    }

    /**
     *
     * @param date
     * @param hours
     *
     * @return
     */
    public static Date addHours(Date date, long hours) {
        return addMinutes(date, hours * 60);
    }

    /**
     *
     * @param date
     * @param minutes
     *
     * @return
     */
    public static Date addMinutes(Date date, long minutes) {
        return addSeconds(date, minutes * 60);
    }

    /**
     * @param date1
     * @param secs
     *
     * @return
     */

    public static Date addSeconds(Date date1, long secs) {
        return new Date(date1.getTime() + (secs * 1000));
    }

    /**
     * calculate the date difference
     *
     * @param beforDate
     * @param afterDate
     * @return days
     */
    public static final int calculateDecreaseDate(String beforDate, String afterDate)
                                                                                     throws ParseException {
        Date date1 = getFormat(dbSimple).parse(beforDate);
        Date date2 = getFormat(dbSimple).parse(afterDate);
        long decrease = getDateBetween(date1, date2) / 1000 / 3600 / 24;
        int dateDiff = (int) decrease;
        return dateDiff;
    }

    /**
     * YYYYMMDD
     *
     * 2010a024      ---->     false 
     * 20100631      ---->     false
     * 20101313      ---->     false
     * 20100809      ---->     true
     * </pre>
     * @param dateStr
     * @return
     */
    public static boolean checkDateValid(String dateStr) {

        Date tmpDate = null;
        try {
            tmpDate = shortstring2Date(dateStr);
        } catch (ParseException e) {
            return false;
        }

        if (tmpDate != null) {
            if (StringUtils.equals(shortDate(tmpDate), dateStr)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param start
     * @param end
     * @param days
     *
     * @return
     */
    public static boolean checkDays(Date start, Date end, int days) {
        int g = countDays(start, end);

        return g <= days;
    }

    /**
     *
     * @param statTime
     *
     * @return alahan add 20050901
     */
    public static boolean checkTime(String statTime) {
        if (statTime.length() > 8) {
            return false;
        }

        String[] timeArray = statTime.split(":");

        if (timeArray.length != 3) {
            return false;
        }

        for (int i = 0; i < timeArray.length; i++) {
            String tmpStr = timeArray[i];

            try {
                Integer tmpInt = new Integer(tmpStr);

                if (i == 0) {
                    if ((tmpInt.intValue() > 23) || (tmpInt.intValue() < 0)) {
                        return false;
                    } else {
                        continue;
                    }
                }

                if ((tmpInt.intValue() > 59) || (tmpInt.intValue() < 0)) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    public static String convert(String dateString, DateFormat formatIn, DateFormat formatOut) {
        try {
            Date date = formatIn.parse(dateString);

            return formatOut.format(date);
        } catch (ParseException e) {

            return "";
        }
    }

    public static String convert2ChineseDtFormat(String dateString) {
        DateFormat df1 = getNewDateFormat(dtShort);
        DateFormat df2 = getNewDateFormat(chineseDtFormat);

        return convert(dateString, df1, df2);
    }

    public static String convert2WebFormat(String dateString) {
        DateFormat df1 = getNewDateFormat(dtShort);
        DateFormat df2 = getNewDateFormat(dbSimple);

        return convert(dateString, df1, df2);
    }

    public static String convertFromWebFormat(String dateString) {
        DateFormat df1 = getNewDateFormat(dtShort);
        DateFormat df2 = getNewDateFormat(dbSimple);

        return convert(dateString, df2, df1);
    }

    /**
     *
     * @param dateStart
     * @param dateEnd
     *
     * @return
     */
    public static int countDays(Date dateStart, Date dateEnd) {
        if ((dateStart == null) || (dateEnd == null)) {
            return -1;
        }

        return (int) (Math.abs(dateEnd.getTime() - dateStart.getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     *
     * @param startDate  2018-08-01
     * @param endDate  2018-08-01
     *
     * @return
     */
    public static long countDays(String startDate, String endDate) {
        Date tempDate1 = null;
        Date tempDate2 = null;
        long days = 0;

        try {
            tempDate1 = string2Date(startDate);

            tempDate2 = string2Date(endDate);
            days = (tempDate2.getTime() - tempDate1.getTime()) / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            return -1L;
        }

        return days;
    }

    /**
     *
     * @param date
     * @param min
     * @return
     */
    public static boolean dateLessThanNowAddMin(Date date, long min) {
        return addMinutes(date, min).before(new Date());

    }

    /**
     * @param date1
     * @param date2
     * @param format
     *
     * @return
     */
    public static boolean dateNotLessThan(String date1, String date2, DateFormat format) {
        try {
            Date d1 = format.parse(date1);
            Date d2 = format.parse(date2);

            if (d1.before(d2)) {
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {

            return false;
        }
    }

    /**
     *
     * @param date
     *
     * @return  YYYYMMDD
     */
    public static final Long dateToNumber(Date date) {
        if (date == null) {
            return null;
        }

        Calendar c = Calendar.getInstance();

        c.setTime(date);

        String month;
        String day;

        if ((c.get(Calendar.MONTH) + 1) >= 10) {
            month = "" + (c.get(Calendar.MONTH) + 1);
        } else {
            month = "0" + (c.get(Calendar.MONTH) + 1);
        }

        if (c.get(Calendar.DATE) >= 10) {
            day = "" + c.get(Calendar.DATE);
        } else {
            day = "0" + c.get(Calendar.DATE);
        }

        String number = c.get(Calendar.YEAR) + "" + month + day;

        return new Long(number);
    }

    /**
     * @param strDate   yyyyMMdd
     * @return    yyyy-MM-dd
     */
    public static final String dtFromShortToSimpleStr(String strDate) {
        if (null != strDate) {
            Date date;
            try {
                date = shortstring2Date(strDate);
            } catch (ParseException e) {
                date = null;
            }
            if (null != date) {
                return dtSimpleFormat(date);
            }
        }
        return "";
    }

    /**
     * yyyyMMddHHmmssS
     *
     * @param date
     *
     * @return
     */
    public static final String dtLongMillFormat(Date date) {
        if (date == null) {
            return "";
        }

        return getFormat(dtLongMill).format(date);
    }

    /**
     * yyyyMM-dd
     *
     * @param date
     * @return
     */
    public static final String dtShortSimpleFormat(Date date) {
        if (date == null) {
            return "";
        }
        return getFormat(dtShort).format(date);
    }

    /**
     * yyyy年MM月dd日
     *
     * @param date
     *
     * @return
     */
    public static final String dtSimpleChineseFormat(Date date) {
        if (date == null) {
            return "";
        }

        return getFormat(dtSimpleChinese).format(date);
    }

    /**
     * yyyy-MM-dd to yyyy年MM月dd日
     *
     * @param date
     *
     * @return
     */
    public static final String dtSimpleChineseFormatStr(String date) throws ParseException {
        if (date == null) {
            return "";
        }

        return getFormat(dtSimpleChinese).format(string2Date(date));
    }

    /**
     * yyyy-MM-dd
     *
     * @param date
     *
     * @return
     */
    public static final String dtSimpleFormat(Date date) {
        if (date == null) {
            return "";
        }

        return getFormat(dbSimple).format(date);
    }

    public static String format(Date date, String format) {
        if (date == null) {
            return null;
        }

        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 2007/06/14 to 20070614
     *
     * @method formatDateString
     * @param date
     * @return
     */
    public static String formatDateString(String date) {
        String result = "";
        if (StringUtils.isBlank(date)) {
            return result;
        }
        if (date.length() == 10) {
            result = date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10);
        }
        return result;
    }

    /**
     *
     * @param date
     * @param format
     * @return
     */
    public static final String formatDateString(Date date, String format) {
        if (date == null || StringUtils.isEmpty(format)) {
            return "";
        }

        return getFormat(format).format(date);
    }

    public static String formatMonth(Date date) {
        if (date == null) {
            return null;
        }

        return new SimpleDateFormat(monthFormat).format(date);
    }

    public static String formatTimeRange(Date startDate, Date endDate, String format) {
        if ((endDate == null) || (startDate == null)) {
            return null;
        }

        String rt = null;
        long range = endDate.getTime() - startDate.getTime();
        long day = range / MILLIS_PER_DAY;
        long hour = (range % MILLIS_PER_DAY) / MILLIS_PER_HOUR;
        long minute = (range % MILLIS_PER_HOUR) / MILLIS_PER_MINUTE;

        if (range < 0) {
            day = 0;
            hour = 0;
            minute = 0;
        }

        rt = format.replaceAll("dd", String.valueOf(day));
        rt = rt.replaceAll("hh", String.valueOf(hour));
        rt = rt.replaceAll("mm", String.valueOf(minute));

        return rt;
    }

    /**
     * systemdate befaore day，return Date
     *
     * @return
     */
    public static Date getBeforeDate() {
        Date date = new Date();

        return new Date(date.getTime() - (ONE_DAY_MILL_SECONDS));
    }

    /**
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getBeforeDay(Date date) throws ParseException {
        Calendar cad = Calendar.getInstance();
        cad.setTime(date);
        cad.add(Calendar.DATE, -1);
        return dtSimpleFormat(cad.getTime());
    }

    /**
     *
     * @param StringDate
     * @return
     * @throws ParseException
     */
    public static String getBeforeDay(String StringDate) throws ParseException {
        Date tempDate = string2Date(StringDate);
        Calendar cad = Calendar.getInstance();

        cad.setTime(tempDate);
        cad.add(Calendar.DATE, -1);
        return dtSimpleFormat(cad.getTime());
    }

    public static String getBeforeDayString(int days) {
        Date date = new Date(System.currentTimeMillis() - (ONE_DAY_MILL_SECONDS * days));
        DateFormat dateFormat = getNewDateFormat(dtShort);

        return getDateString(date, dateFormat);
    }

    public static String getBeforeDayString(String dateString, int days) {
        Date date;
        DateFormat df = getNewDateFormat(dtShort);

        try {
            date = df.parse(dateString);
        } catch (ParseException e) {
            date = new Date();
        }

        date = new Date(date.getTime() - (ONE_DAY_MILL_SECONDS * days));

        return df.format(date);
    }

    /**
     *
     * @param date
     *
     * @return
     */
    public static String getChineseDateString(Date date) {
        DateFormat dateFormat = getNewDateFormat(chineseDtFormat);

        return getDateString(date, dateFormat);
    }

    /**
     *
     * @param dBefor
     * @param dAfter
     *
     * @return Time difference (milliseconds)
     */
    public static final long getDateBetween(Date dBefor, Date dAfter) {
        long lBefor = 0;
        long lAfter = 0;
        long lRtn = 0;

        lBefor = dBefor.getTime();
        lAfter = dAfter.getTime();

        lRtn = lAfter - lBefor;

        return lRtn;
    }

    /**
     *
     * @param dateBefore
     *
     * @return minutes
     */
    public static final int getDateBetweenNow(Date dateBefore) {
        if (dateBefore == null) {
            return 0;
        }
        return (int) (getDateBetween(dateBefore, new Date()) / 1000 / 60);
    }

    /**
     * @return  "yyyyMMdd"
     */
    public static String getDateString(Date date) {
        DateFormat df = getNewDateFormat(dtShort);

        return df.format(date);
    }

    public static String getDateString(Date date, DateFormat dateFormat) {
        if (date == null || dateFormat == null) {
            return null;
        }

        return dateFormat.format(date);
    }

    /**
     *
     * @return yyyyMMdd
     *
     */
    public static String getDateToSimpleString() {
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat(dtShort);
        String nowStr = dateFormat.format(now);
        return nowStr;
    }

    /**
     *
     * @param date
     * @return
     */
    public static Date getDayBegin(Date date) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        df.setLenient(false);

        String dateString = df.format(date);

        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            return date;
        }
    }

    /**
     *
     * @param date
     * @return dayOfWeek
     */
    public static int getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     *
     * @param dt
     * @param idiff
     *
     * @return
     */
    public static final String getDiffDate(Date dt, int idiff) {
        Calendar c = Calendar.getInstance();

        c.setTime(dt);
        c.add(Calendar.DATE, idiff);
        return dtSimpleFormat(c.getTime());
    }

    /**
     * now= 2018-07-19  diff = 1 -> 2018-07-20
     *                  diff = -1 -> 2018-07-18
     *
     * @param diff
     *
     * @return
     */
    public static final String getDiffDate(int diff) {
        Calendar c = Calendar.getInstance();

        c.setTime(new Date());
        c.add(Calendar.DATE, diff);
        return dtSimpleFormat(c.getTime());
    }

    public static final String getDiffDate(String srcDate, String format, int diff) {
        DateFormat f = new SimpleDateFormat(format);

        try {
            Date source = f.parse(srcDate);
            Calendar c = Calendar.getInstance();

            c.setTime(source);
            c.add(Calendar.DATE, diff);
            return f.format(c.getTime());
        } catch (Exception e) {
            return srcDate;
        }
    }

    /**
     *
     * @param dt
     * @param idiff
     *
     * @return
     */
    public static final String getDiffDateDtShort(Date dt, int idiff) {
        Calendar c = Calendar.getInstance();

        c.setTime(dt);
        c.add(Calendar.DATE, idiff);
        return dtShortSimpleFormat(c.getTime());
    }

    /**
     *
     * @param dt
     * @param idiff
     * @return
     */
    public static final String getDiffDateMin(Date dt, int idiff) {
        Calendar c = Calendar.getInstance();

        c.setTime(dt);
        c.add(Calendar.DATE, idiff);
        return simpleFormat(c.getTime());
    }

    public static final Date getDiffDateTime(int diff) {
        Calendar c = Calendar.getInstance();

        c.setTime(new Date());
        c.add(Calendar.DATE, diff);
        return c.getTime();
    }

    /**
     *
     * @param diff
     * @param hours
     *
     * @return
     */
    public static final String getDiffDateTime(int diff, int hours) {
        Calendar c = Calendar.getInstance();

        c.setTime(new Date());
        c.add(Calendar.DATE, diff);
        c.add(Calendar.HOUR, hours);
        return dtSimpleFormat(c.getTime());
    }

    /**
     *
     * @param one
     * @param two
     *
     * @return days
     */
    public static long getDiffDays(Date one, Date two) {
        Calendar sysDate = new GregorianCalendar();

        sysDate.setTime(one);

        Calendar failDate = new GregorianCalendar();

        failDate.setTime(two);
        return (sysDate.getTimeInMillis() - failDate.getTimeInMillis()) / (24 * 60 * 60 * 1000);
    }

    public static long getDiffMinutes(Date one, Date two) {
        Calendar sysDate = new GregorianCalendar();

        sysDate.setTime(one);

        Calendar failDate = new GregorianCalendar();

        failDate.setTime(two);
        return (sysDate.getTimeInMillis() - failDate.getTimeInMillis()) / (60 * 1000);
    }

    /**
     *
     * @param dt
     * @param idiff
     * @return
     */
    public static final String getDiffMon(Date dt, int idiff) {
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.MONTH, idiff);
        return dtSimpleFormat(c.getTime());
    }

    /**
     *
     * @param one
     * @param two
     *
     * @return seconds
     */
    public static long getDiffSeconds(Date one, Date two) {
        Calendar sysDate = new GregorianCalendar();

        sysDate.setTime(one);

        Calendar failDate = new GregorianCalendar();

        failDate.setTime(two);
        return (sysDate.getTimeInMillis() - failDate.getTimeInMillis()) / 1000;
    }

    /**
     *
     * @param dt
     * @param diff
     *
     * @return
     */
    public static String getDiffStringDate(Date dt, int diff) {
        Calendar ca = Calendar.getInstance();

        if (dt == null) {
            ca.setTime(new Date());
        } else {
            ca.setTime(dt);
        }

        ca.add(Calendar.DATE, diff);
        return dtSimpleFormat(ca.getTime());
    }

    public static String getEmailDate(Date today) {
        String todayStr;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");

        todayStr = sdf.format(today);
        return todayStr;
    }

    /**
     *
     * @param format
     * @return
     */
    public static final DateFormat getFormat(String format) {
        return new SimpleDateFormat(format);
    }

    /**
     *
     * @param StringDate
     *
     * @return
     */
    public static Map<String, String> getLastWeek(String StringDate, int interval)
                                                                                  throws ParseException {
        Map<String, String> lastWeek = new HashMap<String, String>();
        Date tempDate = shortstring2Date(StringDate);
        Calendar cad = Calendar.getInstance();

        cad.setTime(tempDate);

        int dayOfMonth = cad.getActualMaximum(Calendar.DAY_OF_MONTH);

        cad.add(Calendar.DATE, (dayOfMonth - 1));
        lastWeek.put("endDate", shortDate(cad.getTime()));
        cad.add(Calendar.DATE, interval);
        lastWeek.put("startDate", shortDate(cad.getTime()));

        return lastWeek;
    }

    public static String getLongDateString() {
        DateFormat df = getNewDateFormat(dtLong);
        return df.format(new Date());
    }

    public static String getLongDateString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(dtLong);

        return getDateString(date, dateFormat);
    }

    public static DateFormat getNewDateFormat(String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);

        df.setLenient(false);
        return df;
    }

    public static String getNewFormatDateString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(simple);
        return getDateString(date, dateFormat);
    }

    /**
     *
     * @param date
     *
     * @return
     *
     * @throws ParseException
     */
    public static String getNextDay(Date date) throws ParseException {
        Calendar cad = Calendar.getInstance();
        cad.setTime(date);
        cad.add(Calendar.DATE, 1);
        return dtSimpleFormat(cad.getTime());
    }

    /**
     *
     * @param StringDate
     *
     * @return
     *
     * @throws ParseException
     */
    public static String getNextDay(String StringDate) throws ParseException {
        Date tempDate = string2Date(StringDate);
        Calendar cad = Calendar.getInstance();

        cad.setTime(tempDate);
        cad.add(Calendar.DATE, 1);
        return dtSimpleFormat(cad.getTime());
    }

    /**
     *
     * @param StringDate
     *            "20061106"
     *
     * @return String "2006-11-07"
     *
     * @throws ParseException
     */
    public static Date getNextDayDtShort(String StringDate) throws ParseException {
        Date tempDate = shortstring2Date(StringDate);
        Calendar cad = Calendar.getInstance();

        cad.setTime(tempDate);
        cad.add(Calendar.DATE, 1);
        return cad.getTime();
    }

    /**
     *
     * @param StringDate
     *            "20061106"
     *
     * @return String "20061107"
     *
     * @throws ParseException
     */
    public static String getNextDayDtShortToShort(String StringDate) throws ParseException {
        Date tempDate = shortstring2Date(StringDate);
        Calendar cad = Calendar.getInstance();

        cad.setTime(tempDate);
        cad.add(Calendar.DATE, 1);
        return dtShortSimpleFormat(cad.getTime());
    }

    /**
     *
     * @param stringDate
     *
     * @return
     */
    public static String getNextMon(String stringDate) throws ParseException {
        Date tempDate = shortstring2Date(stringDate);
        Calendar cad = Calendar.getInstance();

        cad.setTime(tempDate);
        cad.add(Calendar.MONTH, 1);
        return shortDate(cad.getTime());
    }

    /**
     * 2007-02-02 22:23 --> 2007-02-02 22:00
     * 2007-02-02 22:33 --> 2007-02-02 22:30
     * @return
     */
    public static final String getNowDateForPageSelectAhead() {

        Calendar cal = Calendar.getInstance();
        if (cal.get(Calendar.MINUTE) < 30) {
            cal.set(Calendar.MINUTE, 0);
        } else {
            cal.set(Calendar.MINUTE, 30);
        }
        return simpleDate(cal.getTime());
    }

    /**
     * 2007-02-02 22:23 --> 2007-02-02 22:30
     * 2007-02-02 22:33 --> 2007-02-02 23:00
     * @return
     */
    public static final String getNowDateForPageSelectBehind() {
        Calendar cal = Calendar.getInstance();
        if (cal.get(Calendar.MINUTE) < 30) {
            cal.set(Calendar.MINUTE, 30);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
            cal.set(Calendar.MINUTE, 0);
        }
        return simpleDate(cal.getTime());
    }

    public static String getShortDateString(String strDate) {
        return getShortDateString(strDate, "-|/");
    }

    public static String getShortDateString(String strDate, String delimiter) {
        if (StringUtils.isBlank(strDate)) {
            return null;
        }

        String temp = strDate.replaceAll(delimiter, "");

        if (isValidShortDateFormat(temp)) {
            return temp;
        }

        return null;
    }

    public static String getShortFirstDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        Date dt = new Date();

        cal.setTime(dt);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        DateFormat df = getNewDateFormat(dtShort);

        return df.format(cal.getTime());
    }

    public static String getSmsDate(Date today) {
        String todayStr;
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH:mm");

        todayStr = sdf.format(today);
        return todayStr;
    }

    public static String getTimeString(Date date) {
        DateFormat dateFormat = getNewDateFormat(hmsFormat);

        return getDateString(date, dateFormat);
    }

    /**
     *
     * @return
     */
    public static final String getTimeWithSSS() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        return sdFormat.format(date);
    }

    public static String getTodayString() {
        DateFormat dateFormat = getNewDateFormat(dtShort);

        return getDateString(new Date(), dateFormat);
    }

    public static String getTomorrowDateString(String sDate) throws ParseException {
        Date aDate = parseDateNoTime(sDate);

        aDate = addSeconds(aDate, ONE_DAY_SECONDS);

        return getDateString(aDate);
    }

    public static String getWebDateString(Date date) {
        DateFormat dateFormat = getNewDateFormat(dbSimple);

        return getDateString(date, dateFormat);
    }

    public static String getWebFirstDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        Date dt = new Date();

        cal.setTime(dt);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        DateFormat df = getNewDateFormat(dbSimple);

        return df.format(cal.getTime());
    }

    public static String getWebNextDayString() {
        Calendar cad = Calendar.getInstance();
        cad.setTime(new Date());
        cad.add(Calendar.DATE, 1);
        return dtSimpleFormat(cad.getTime());
    }

    public static String getWebTodayString() {
        DateFormat df = getNewDateFormat(dbSimple);

        return df.format(new Date());
    }

    /**
     *
     * @param date
     * @return
     */
    public static final String getWeekDay(Date date) {
        return getFormat(week).format(date);
    }

    public static String getYesterDayDateString(String sDate) throws ParseException {
        Date aDate = parseDateNoTime(sDate);

        aDate = addSeconds(aDate, -ONE_DAY_SECONDS);

        return getDateString(aDate);
    }

    /**
     *
     * @param date
     *
     * @return HH:mm:ss
     */
    public static final String hmsFormat(Date date) {
        if (date == null) {
            return "";
        }

        return getFormat(hmsFormat).format(date);
    }

    /**
     *
     * @param aDate
     * @return days
     */
    public static final Date increaseDate(Date aDate, int days) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(aDate);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    public static boolean isBeforeNow(Date date) {
        if (date == null) {
            return false;
        }
        return date.compareTo(new Date()) < 0;
    }

    /**
     *
     * @param date
     * @return
     */
    public static final boolean isDefaultWorkingDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        return !(week == 7 || week == 1);
    }

    /**
     *
     * @param year
     * @return
     */
    public static final boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);

    }

    /**
     *
     * @param hourStr
     *
     * @return true/false
     */
    public static boolean isValidHour(String hourStr) {
        if (!StringUtils.isEmpty(hourStr) && StringUtils.isNumeric(hourStr)) {
            int hour = new Integer(hourStr).intValue();

            if ((hour >= 0) && (hour <= 23)) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param strDate yyyyMMddHHmmss
     * @return
     */
    public static boolean isValidLongDateFormat(String strDate) {
        if (strDate.length() != dtLong.length()) {
            return false;
        }

        try {
            Long.parseLong(strDate);
        } catch (Exception NumberFormatException) {
            return false;
        }

        DateFormat df = getNewDateFormat(dtLong);

        try {
            df.parse(strDate);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    /**
     *
     * @param strDate yyyyMMddHHmmss
     * @param delimiter
     * @return
     */
    public static boolean isValidLongDateFormat(String strDate, String delimiter) {
        String temp = strDate.replaceAll(delimiter, "");

        return isValidLongDateFormat(temp);
    }

    /**
     *
     * @param str
     *
     * @return true/false
     */
    public static boolean isValidMinuteOrSecond(String str) {
        if (!StringUtils.isEmpty(str) && StringUtils.isNumeric(str)) {
            int hour = new Integer(str).intValue();

            if ((hour >= 0) && (hour <= 59)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isValidShortDateFormat(String strDate) {
        if (strDate.length() != dtShort.length()) {
            return false;
        }

        try {
            Integer.parseInt(strDate);
        } catch (Exception NumberFormatException) {
            return false;
        }

        DateFormat df = getNewDateFormat(dtShort);

        try {
            df.parse(strDate);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    public static boolean isValidShortDateFormat(String strDate, String delimiter) {
        String temp = strDate.replaceAll(delimiter, "");

        return isValidShortDateFormat(temp);
    }

    /**
     *
     * @param date
     *
     * @return yyyyMMddHHmmss
     *
     * @throws ParseException
     */
    public static final String longDate(Date date) {
        if (date == null) {
            return null;
        }

        return getFormat(dtLong).format(date);
    }

    public static Date now() {
        return new Date();
    }

    public static Date parseDateLongFormat(String sDate) {
        DateFormat dateFormat = new SimpleDateFormat(dtLong);
        Date d = null;

        if ((sDate != null) && (sDate.length() == dtLong.length())) {
            try {
                d = dateFormat.parse(sDate);
            } catch (ParseException ex) {
                return null;
            }
        }

        return d;
    }

    public static Date parseDateNewFormat(String sDate) {
        DateFormat dateFormat = new SimpleDateFormat(simple);
        Date d = null;
        if ((sDate != null) && (sDate.length() == simple.length())) {
            try {
                d = dateFormat.parse(sDate);
            } catch (ParseException ex) {
                return null;
            }
        }
        return d;
    }

    public static Date parseDateNoTime(String sDate) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(dtShort);

        if ((sDate == null) || (sDate.length() < dtShort.length())) {
            throw new ParseException("length too little", 0);
        }

        if (!StringUtils.isNumeric(sDate)) {
            throw new ParseException("not all digit", 0);
        }

        return dateFormat.parse(sDate);
    }

    public static Date parseDateNoTime(String sDate, String format) throws ParseException {
        if (StringUtils.isBlank(format)) {
            throw new ParseException("Null format. ", 0);
        }

        DateFormat dateFormat = new SimpleDateFormat(format);

        if ((sDate == null) || (sDate.length() < format.length())) {
            throw new ParseException("length too little", 0);
        }

        return dateFormat.parse(sDate);
    }

    public static Date parseDateNoTimeWithDelimit(String sDate, String delimit)
                                                                               throws ParseException {
        sDate = sDate.replaceAll(delimit, "");

        DateFormat dateFormat = new SimpleDateFormat(dtShort);

        if ((sDate == null) || (sDate.length() != dtShort.length())) {
            throw new ParseException("length not match", 0);
        }

        return dateFormat.parse(sDate);
    }

    public static Date parseNoSecondFormat(String sDate) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(noSecondFormat);

        if ((sDate == null) || (sDate.length() < noSecondFormat.length())) {
            throw new ParseException("length too little", 0);
        }

        if (!StringUtils.isNumeric(sDate)) {
            throw new ParseException("not all digit", 0);
        }

        return dateFormat.parse(sDate);
    }

    /**
     *
     * @param date
     *
     * @return yyyyMMdd
     *
     * @throws ParseException
     */
    public static final String shortDate(Date date) {
        if (date == null) {
            return null;
        }

        return getFormat(dtShort).format(date);
    }

    /**
     *
     * @param stringDate
     *            (yyyyMMdd)
     *
     * @return
     *
     * @throws ParseException
     */
    public static final Date shortstring2Date(String stringDate) throws ParseException {
        if (stringDate == null) {
            return null;
        }

        return getFormat(dtShort).parse(stringDate);
    }

    /**
     *
     * @param shortString yyyymmdd
     * @return yyyy-mm-dd
     * @throws ParseException
     */
    public static final String shortString2SimpleString(String shortString) {
        if (shortString == null) {
            return null;
        }
        try {
            return getFormat(dbSimple).format(shortstring2Date(shortString));
        } catch (Exception e) {
            return null;
        }
    }

    public static final String shortStringToString(String stringDate) throws ParseException {
        if (stringDate == null) {
            return null;
        }
        return shortDate(strToDtSimpleFormat(stringDate));
    }

    /**
     *
     * @param date
     *
     * @return 2005-06-30 15:50
     */
    public static final String simpleDate(Date date) {
        if (date == null) {
            return "";
        }

        return getFormat(simpleFormat).format(date);
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static final String simpleFormat(Date date) {
        if (date == null) {
            return "";
        }
        return getFormat(simple).format(date);
    }

    /**
     *
     * @param dateString 2005-06-30 15:50
     * @return
     * @throws ParseException
     */
    public static final Date simpleFormatDate(String dateString) throws ParseException {
        if (dateString == null) {
            return null;
        }
        return getFormat(simpleFormat).parse(dateString);
    }

    /**
     *
     * @param stringDate yyyy-MM-dd
     *
     * @return
     *
     * @throws ParseException
     */
    public static final Date string2Date(String stringDate) throws ParseException {
        if (stringDate == null) {
            return null;
        }

        return getFormat(dbSimple).parse(stringDate);
    }

    /**
     *
     * @param str
     * @param format
     *
     * @return
     */
    public static Date string2Date(String str, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     *
     * @param stringDate
     *
     * @return
     *
     * @throws ParseException
     */
    public static final Long string2DateLong(String stringDate) throws ParseException {
        Date d = string2Date(stringDate);

        if (d == null) {
            return null;
        }

        return new Long(d.getTime());
    }

    /**
     *
     * @param stringDate
     *
     * @return

     * @throws ParseException
     */
    public static final Date string2DateTime(String stringDate) throws ParseException {
        if (stringDate == null) {
            return null;
        }

        return getFormat(simple).parse(stringDate);
    }

    /**
     *
     * @param stringDate String
     *
     * @return
     *
     * @throws ParseException
     */
    public static final Date string2DateTimeBy23(String stringDate) throws ParseException {
        if (stringDate == null) {
            return null;
        }
        if (stringDate.length() == 11) {
            stringDate = stringDate + "23:59:59";
        } else if (stringDate.length() == 13) {
            stringDate = stringDate + ":59:59";
        } else if (stringDate.length() == 16) {
            stringDate = stringDate + ":59";
        } else if (stringDate.length() == 10) {
            stringDate = stringDate + " 23:59:59";
        }

        return getFormat(simple).parse(stringDate);
    }

    /**
     *
     * @param stringDate
     *
     * @return
     *
     * @throws ParseException
     */
    public static final Date string2DateTimeByAutoZero(String stringDate) throws ParseException {
        if (stringDate == null) {
            return null;
        }
        if (stringDate.length() == 11) {
            stringDate = stringDate + "00:00:00";
        } else if (stringDate.length() == 13) {
            stringDate = stringDate + ":00:00";
        } else if (stringDate.length() == 16) {
            stringDate = stringDate + ":00";
        } else if (stringDate.length() == 10) {
            stringDate = stringDate + " 00:00:00";
        }
        return getFormat(simple).parse(stringDate);
    }

    /**
     *
     * @param strDate
     * @return yyyy-MM-dd HH:mm or yyyy-MM-dd
     */
    public static final Date strToDate(String strDate) {
        if (strToSimpleFormat(strDate) != null) {
            return strToSimpleFormat(strDate);
        } else {
            return strToDtSimpleFormat(strDate);
        }

    }

    /**
     *
     * @param strDate
     * @return yyyy-mm-dd
     */
    public static final Date strToDtSimpleFormat(String strDate) {
        if (strDate == null) {
            return null;
        }

        try {
            return getFormat(dbSimple).parse(strDate);
        } catch (Exception e) {
        }

        return null;
    }

    /**
     *
     * @param strDate
     *
     * @return yyyy-MM-dd HH:mm
     */
    public static final Date strToSimpleFormat(String strDate) {
        if (strDate == null) {
            return null;
        }

        try {
            return getFormat(simpleFormat).parse(strDate);

        } catch (Exception e) {
        }

        return null;
    }

    public static boolean webDateNotLessThan(String date1, String date2) {
        DateFormat df = getNewDateFormat(dbSimple);

        return dateNotLessThan(date1, date2, df);
    }

}