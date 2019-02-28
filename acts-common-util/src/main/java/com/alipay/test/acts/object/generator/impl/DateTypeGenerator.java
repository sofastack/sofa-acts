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
package com.alipay.test.acts.object.generator.impl;

import com.alipay.test.acts.log.ActsLogUtil;
import com.alipay.test.acts.object.generator.ObjectGenerator;
import com.alipay.test.acts.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTypeGenerator implements ObjectGenerator {

    private static String    dateFormat = "yyyy-MM-dd hh:mm:ss";
    private static String    today      = "today";

    private static final Log LOG        = LogFactory.getLog(DateTypeGenerator.class);

    @Override
    public boolean isSimpleType() {
        return true;
    }

    @Override
    public Object generateFieldObject(Class<?> clz, String fieldName, String referedCSVValue) {
        Assert.assertEquals("Date is is required!", clz, Date.class);
        Date retDate = null;
        if (!StringUtils.isBlank(referedCSVValue) && !referedCSVValue.equals("null")) {
            if (referedCSVValue.equalsIgnoreCase("today")) {
                retDate = new Date();
            } else if (referedCSVValue.startsWith("today")) {
                String diffString;
                if (referedCSVValue.contains("+")) {
                    diffString = referedCSVValue.substring(referedCSVValue.lastIndexOf("+") + 1)
                        .trim();
                } else {
                    diffString = referedCSVValue
                        .substring(referedCSVValue.lastIndexOf("today") + 5).trim();
                }
                int diff = 0;
                try {
                    diff = Integer.valueOf(diffString);
                } catch (NumberFormatException e) {
                    ActsLogUtil.fail(LOG, "Failed to parse [" + referedCSVValue + "]");
                }
                retDate = DateUtil.addDays(new Date(), diff);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                try {
                    retDate = sdf.parse(referedCSVValue.trim());
                } catch (Exception e) {
                    ActsLogUtil.fail(LOG, "Failed to parse filed [" + fieldName + "], value ["
                                          + referedCSVValue + "] !", e);
                }
            }
        }
        return retDate;
    }

    @Override
    public String generateObjectValue(Object obj, String csvPath, boolean isSimple) {
        String str = "null";
        if (obj != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            try {
                str = sdf.format(obj);
            } catch (Exception e) {
                ActsLogUtil.error(LOG, "Failed to parse [" + obj + "] !", e);
            }
        }

        if (StringUtils.equalsIgnoreCase(str, DateUtil.formatDateString(new Date(), dateFormat))) {
            return today;
        } else {
            return str;
        }
    }

    @Override
    public Class<?> getItemClass(Type collectionItemType, Class<?> clz) {
        return null;
    }

    @Override
    public void setObjectValue(Object collectionObject, Object value, String originalValue,
                               int index) {
    }

}
