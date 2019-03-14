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
package com.alipay.test.acts.object.comparer.impl;

import java.util.Date;

import com.alipay.test.acts.util.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alipay.test.acts.log.ActsLogUtil;
import com.alipay.test.acts.object.comparer.UnitComparer;

/**
 * "D" flag
 *
 * @author muxiao.jl
 * @version $Id: DateComparer.java, v 0.1 2014年11月5日 下午1:08:53 muxiao.jl Exp $
 */
public class DateComparer implements UnitComparer {
    private static final Log LOG    = LogFactory.getLog(DateComparer.class);

    long                     errSec = 0;

    public DateComparer() {
        super();
    }

    public DateComparer(long errSec) {
        super();
        this.errSec = errSec;
    }

    /**
     *
     */
    @Override
    public boolean compare(Object expect, Object actual, String comparerFlagCode) {

        String exp = String.valueOf(expect);
        if (StringUtils.isBlank(exp) && actual == null) {
            return true;
        }

        long errSec = this.errSec;

        if (exp.toLowerCase().equals("now")) {
            exp = DateUtil.getLongDateString(new Date());
        } else if (exp.toLowerCase().startsWith("today")) {
            if (exp.equalsIgnoreCase("today")) {
                exp = DateUtil.getDateString(new Date());
            } else {
                String diffString;
                if (exp.contains("+")) {
                    diffString = exp.substring(exp.lastIndexOf("+") + 1).trim();
                } else {
                    diffString = exp.substring(exp.lastIndexOf("today") + 5).trim();
                }
                int diff = 0;
                try {
                    diff = Integer.valueOf(diffString);
                } catch (NumberFormatException e) {
                    ActsLogUtil.fail(LOG, "parsing error:" + diffString, e);
                }
                exp = DateUtil.getDateString(DateUtil.addDays(new Date(), diff));
            }
        }

        Date actDate = null;
        try {
            actDate = (Date) actual;
        } catch (Exception e1) {
            ActsLogUtil.fail(LOG, "parsing error:" + actual, e1);
        }
        String actString = null;

        if (actDate != null) {

            switch (exp.length()) {
                case 6:
                    actString = DateUtil.getTimeString(actDate);
                    break;
                case 8:
                    actString = DateUtil.getDateString(actDate);
                    break;
                default:
                    actString = DateUtil.getLongDateString(actDate);
                    break;
            }
        }
        if (exp.contains(".") && StringUtils.isNotBlank(actString)) {

            char[] actChars = actString.toCharArray();
            for (int i = 0; i < exp.length() && i < actChars.length; i++) {
                if (exp.charAt(i) == '.') {
                    actChars[i] = '.';
                }
            }
            actString = new String(actChars);
        }

        if (null == actDate || null == actString) {
            actString = "null";
        }
        if (StringUtils.isNumeric(exp) && StringUtils.isNumeric(actString) && exp.length() == 14
            && actString.length() == 14 && errSec > 0) {

            long expTime = DateUtil.parseDateLongFormat(exp).getTime() / 1000;
            long actTime = DateUtil.parseDateLongFormat(actString).getTime() / 1000;
            if (Math.abs(expTime - actTime) > errSec) {
                return false;
            }

        } else if (!exp.equals(actString)) {
            return false;
        }

        return true;
    }
}
