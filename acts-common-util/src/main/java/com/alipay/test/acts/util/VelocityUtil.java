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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.alipay.test.acts.constant.VelocityExtendProperties;
import com.alipay.test.acts.exception.ActsException;

/**
 * velocity tool
 *
 * @author dasong.jds
 * @version $Id: VelocityUtil.java, v 0.1 2015年6月7日 下午5:41:22 dasong.jds Exp $
 */
public class VelocityUtil {

    /** default charset:UTF-8 */
    private static String charset = "UTF-8";

    /**
     * Render text
     * 
     * @param context
     * @param template
     * @return
     */
    public static synchronized String evaluateString(Map<String, Object> context, String template) {
        Writer writer = new StringWriter();
        try {
            VelocityContext velocityContext = new VelocityContext(context);

            addExtendProperties(velocityContext);

            Velocity.evaluate(velocityContext, writer, StringUtils.EMPTY, template);
            return writer.toString();
        } catch (Exception e) {
            throw new ActsException("velocity evaluate error[template=" + template + "]", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    /**
     * Render template file
     * 
     * @param context
     * @param templateFile
     * @return
     */
    public static String evaluateFile(Map<String, Object> context, String templateFile) {
        return evaluateFile(context, new File(templateFile));
    }

    /**
     * Render template file
     * 
     * @param context
     * @param templateFile
     * @return
     */
    public static String evaluateFile(Map<String, Object> context, File templateFile) {
        Writer writer = new StringWriter();
        BufferedReader reader = null;
        try {
            VelocityContext velocityContext = new VelocityContext(context);

            addExtendProperties(velocityContext);

            reader = new BufferedReader(new InputStreamReader(new FileInputStream(templateFile),
                charset));

            Velocity.evaluate(velocityContext, writer, "", reader);
            return writer.toString();
        } catch (Exception e) {
            throw new ActsException("velocity evaluate error[templateFile=" + templateFile + "]", e);
        } finally {
            IOUtils.closeQuietly(writer);
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * Add extended parameters
     * 
     * @param context
     */
    private static void addExtendProperties(VelocityContext context) {
        //default params
        context.put(VelocityExtendProperties.CUR_TIME_MILLIS_STR, System.currentTimeMillis());
        context.put(VelocityExtendProperties.CUR_DATE, DateUtil.getWebTodayString());
        context.put(VelocityExtendProperties.CUR_DATE_TIME, DateUtil.simpleFormat(new Date()));
    }

}
