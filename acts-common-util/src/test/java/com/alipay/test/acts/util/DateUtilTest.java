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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by yanzhu on 2019/1/13.
 */
public class DateUtilTest {

    @Test
    public void testString2DateTimeBy23() {
        try {
            Date date = DateUtil.string2DateTimeBy23("2019-01-01");

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String str = formatter.format(date);

            Assert.assertEquals("2019-01-01", str);

            //========
            date = DateUtil.string2DateTimeBy23("2019-01-01 ");

            formatter = new SimpleDateFormat("yyyy-MM-dd");
            str = formatter.format(date);

            Assert.assertEquals("2019-01-01", str);

            //========
            date = DateUtil.string2DateTimeBy23("2019-01-01 23");

            formatter = new SimpleDateFormat("yyyy-MM-dd HH");
            str = formatter.format(date);

            Assert.assertEquals("2019-01-01 23", str);

            //========
            date = DateUtil.string2DateTimeBy23("2019-01-01 23:59");

            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            str = formatter.format(date);

            Assert.assertEquals("2019-01-01 23:59", str);

            //========
            date = DateUtil.string2DateTimeBy23("2019-01-01 23:59:59");

            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            str = formatter.format(date);

            Assert.assertEquals("2019-01-01 23:59:59", str);

            //========
            date = DateUtil.string2DateTimeBy23("2019-01-61");

        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof ParseException);
        }
    }

    @Test
    public void testString2DateTimeByAutoZero() {
        try {
            Date date = DateUtil.string2DateTimeByAutoZero("2019-01-01");

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String str = formatter.format(date);

            Assert.assertEquals("2019-01-01", str);

            //========
            date = DateUtil.string2DateTimeByAutoZero("2019-01-01 ");

            formatter = new SimpleDateFormat("yyyy-MM-dd");
            str = formatter.format(date);

            Assert.assertEquals("2019-01-01", str);

            //========
            date = DateUtil.string2DateTimeByAutoZero("2019-01-01 00");

            formatter = new SimpleDateFormat("yyyy-MM-dd HH");
            str = formatter.format(date);

            Assert.assertEquals("2019-01-01 00", str);

            //========
            date = DateUtil.string2DateTimeByAutoZero("2019-01-01 00:00");

            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            str = formatter.format(date);

            Assert.assertEquals("2019-01-01 00:00", str);

            //========
            date = DateUtil.string2DateTimeByAutoZero("2019-01-01 00:00:00");

            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            str = formatter.format(date);

            Assert.assertEquals("2019-01-01 00:00:00", str);

            //========
            date = DateUtil.string2DateTimeByAutoZero("2019-01-00");

        } catch (Exception e) {
            Assert.assertTrue(e instanceof ParseException);
        }
    }

    @Test
    public void testFormatTimeRange() {
        String range = DateUtil.formatTimeRange(new Date(1546272000000L), new Date(1546358399000L),
            "dd:hh:mm");
        Assert.assertEquals(range, "0:23:59");

        range = DateUtil.formatTimeRange(new Date(1546358399000L), new Date(1546272000000L),
            "dd:hh:mm");
        Assert.assertEquals(range, "0:0:0");
    }

    @Test
    public void testGetLastWeek() {
        try {
            Map<String, String> map = DateUtil.getLastWeek("20190101", 7);

            Assert.assertEquals(map.size(), 2);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ParseException);
        }
    }

    @Test
    public void testFormatDateString() {
        try {
            String str = DateUtil.formatDateString("2007/06/14");
            Assert.assertEquals(str, "20070614");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ParseException);
        }
    }

    @Test
    public void testParseDateNoTime() {
        try {
            Date date = DateUtil.parseDateNoTime("20070614");

            DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            String str = formatter.format(date);

            Assert.assertEquals("20070614", str);

            date = DateUtil.parseDateNoTime("200706");

            date = DateUtil.parseDateNoTime("200706xx");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ParseException);
        }
    }

    @Test
    public void testParseDateNoTime2() {
        try {
            Date date = DateUtil.parseDateNoTime("20070614", "yyyyMMdd");

            DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            String str = formatter.format(date);

            Assert.assertEquals("20070614", str);

            date = DateUtil.parseDateNoTime("20070614", "");

            date = DateUtil.parseDateNoTime("200706", "yyyyMMdd");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ParseException);
        }
    }

    @Test
    public void testParseNoSecondFormat() {
        try {
            Date date = DateUtil.parseNoSecondFormat("2019-01-01 00:00");
            Assert.assertEquals(date.getTime(), 1546272000000L);

            date = DateUtil.parseNoSecondFormat("2019-01-01");

            date = DateUtil.parseNoSecondFormat("2019-01-t1");

        } catch (Exception e) {
            Assert.assertTrue(e instanceof ParseException);
        }
    }

    @Test
    public void testgetBeforeDayString() {
        String date = DateUtil.getBeforeDayString("20190101", 1);

        Assert.assertEquals(date, "20181231");
    }

    @Test
    public void testGetNowDateForPageSelectBehind() {
        String date = DateUtil.getNowDateForPageSelectBehind();

        Assert.assertNotNull(date);
    }

    @Test
    public void testGetNowDateForPageSelectAhead() {
        String date = DateUtil.getNowDateForPageSelectAhead();

        Assert.assertNotNull(date);
    }

    @Test
    public void testIsValidLongDateFormat() {
        boolean res = DateUtil.isValidLongDateFormat("20181212121212");

        Assert.assertTrue(res);

        res = DateUtil.isValidLongDateFormat("20181212121291");

        Assert.assertFalse(res);

        res = DateUtil.isValidLongDateFormat("x0181212121291");

        Assert.assertFalse(res);
    }

    @Test
    public void testIsValidShortDateFormat() {
        boolean res = DateUtil.isValidShortDateFormat("20181212");

        Assert.assertTrue(res);

        res = DateUtil.isValidLongDateFormat("20181213");

        Assert.assertFalse(res);

        res = DateUtil.isValidLongDateFormat("x0181212");

        Assert.assertFalse(res);
    }

    @Test
    public void testGetDiffDays() {
        long res = DateUtil.getDiffDays(new Date(1546271000000L), new Date(1546358399000L));

        Assert.assertEquals(res, -1);

    }

    @Test
    public void testCalculateDecreaseDate() throws ParseException {
        Assert.assertTrue(DateUtil.calculateDecreaseDate("2019-01-10", "2019-01-11") == 1);
    }

    @Test
    public void testParseDateNoTimeWithDelimit() {
        try {
            Date res = DateUtil.parseDateNoTimeWithDelimit("20190110 10:12", " 10:12");

            Assert.assertEquals(res.getDate(), 10);

            res = DateUtil.parseDateNoTimeWithDelimit("201901 10:12", " 10:12");

        } catch (Exception e) {
            Assert.assertTrue(e instanceof ParseException);
        }
    }

    @Test
    public void testGetDiffDate() {
        String res = DateUtil.getDiffDate("20190101", DateUtil.dtShort, 2);
        Assert.assertEquals(res, "20190103");

        res = DateUtil.getDiffDate("201901YY", DateUtil.dtShort, 2);
        Assert.assertEquals(res, "201901YY");
    }

    @Test
    public void testCheckDateValid() {
        Assert.assertTrue(DateUtil.checkDateValid("20100809"));
    }

    @Test
    public void testLongDate() {
        String res = DateUtil.longDate(new Date());
        Assert.assertEquals(res.length(), 14);

        res = DateUtil.longDate(null);
        Assert.assertNull(res);

    }

    @Test
    public void testParseDateLongFormat() {
        Date res = DateUtil.parseDateLongFormat("20181221120101");
        Assert.assertEquals(res.getDate(), 21);

        res = DateUtil.parseDateLongFormat(null);
        Assert.assertNull(res);
    }

    @Test
    public void testParseDateNewFormat() {
        Date res = DateUtil.parseDateNewFormat("2018-04-26 10:10:10");
        Assert.assertEquals(res.getMonth(), 3);

        res = DateUtil.parseDateLongFormat(null);
        Assert.assertNull(res);
    }

    @Test
    public void testGetDiffMinutes() {
        long diff = DateUtil.getDiffMinutes(new Date(System.currentTimeMillis()),
            new Date(System.currentTimeMillis() - 1000 * 60));
        Assert.assertEquals(diff, 1);

    }

    @Test
    public void testGetDiffSeconds() {
        long diff = DateUtil.getDiffSeconds(new Date(System.currentTimeMillis()),
            new Date(System.currentTimeMillis() - 1000 * 60));
        Assert.assertEquals(diff, 60);

    }

    @Test
    public void testGetDiffStringDate() {
        String res = DateUtil.getDiffStringDate(new Date(24 * 60 * 60 * 1000), -1);
        Assert.assertEquals(res, "1970-01-01");

    }

    @Test
    public void testGetEmailDate() throws Throwable {
        Date date = new Date(24 * 60 * 60 * 1000);

        String emailDateStr = DateUtil.getEmailDate(date);

        DateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");

        Date emailDate = formatter.parse(emailDateStr);

        Assert.assertEquals(emailDate, date);

    }

    @Test
    public void testGetShortFirstDayOfMonth() {
        String res = DateUtil.getShortFirstDayOfMonth();

        Assert.assertEquals(res.substring(6, 8), "01");
    }

    @Test
    public void testGetWebFirstDayOfMonth() {
        String res = DateUtil.getWebFirstDayOfMonth();

        Assert.assertEquals(res.substring(8, 10), "01");
    }

    @Test
    public void testIsValidHour() {
        Assert.assertTrue(DateUtil.isValidHour("20"));

        Assert.assertFalse(DateUtil.isValidHour("24"));
    }

    @Test
    public void testIsValidMinuteOrSecond() {
        Assert.assertTrue(DateUtil.isValidMinuteOrSecond("59"));

        Assert.assertFalse(DateUtil.isValidMinuteOrSecond("60"));
    }

    @Test
    public void testIsLeapYear() {
        Assert.assertTrue(DateUtil.isLeapYear(2016));

        Assert.assertFalse(DateUtil.isLeapYear(1700));
    }

    @Test
    public void testIsBeforeNow() {
        Assert.assertTrue(DateUtil.isBeforeNow(new Date(1000)));

        Assert.assertFalse(DateUtil.isBeforeNow(new Date(1000 + System.currentTimeMillis())));
    }

    @Test
    public void testCheckDays() {
        java.util.Date start = new Date();
        Date end = new Date();
        Assert.assertTrue(DateUtil.checkDays(start, end, 0));
    }

    @Test
    public void testCheckTime() {

        Assert.assertTrue(DateUtil.checkTime("12:20:30"));
    }

    @Test
    public void testCountDays() {
        java.util.Date start = new Date();
        Date end = new Date();
        Assert.assertTrue(DateUtil.countDays(start, end) == 0);
    }

    @Test
    public void testCountDays1() {
        Assert.assertTrue(DateUtil.countDays("2010-08-09", "2010-08-10") == 1L);
    }

    @Test
    public void testDateToNumber() throws Exception {
        Date start = new Date();
        Assert.assertTrue((DateUtil.dateToNumber(start)) > 0);

        Assert.assertEquals((DateUtil.dateToNumber(null)), null);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.simple);
        Date data10 = simpleDateFormat.parse("2019-10-28 11:12:12");

        Assert.assertEquals((long) DateUtil.dateToNumber(data10), 20191028L);

        Date data09 = simpleDateFormat.parse("2019-07-01 9:12:12");

        Assert.assertEquals((long) DateUtil.dateToNumber(data09), 20190701L);

    }

    @Test
    public void testDtFromShortToSimpleStr() {
        Assert.assertEquals((DateUtil.dtFromShortToSimpleStr("20100809")), "2010-08-09");
    }

}
