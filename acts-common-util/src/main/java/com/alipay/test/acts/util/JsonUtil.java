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

import com.alibaba.fastjson.JSON;
import com.alipay.test.acts.log.ActsLogUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

/**
 * Json
 * 
 * @author baishuo.lp
 * @version $Id: JsonUtil.java, v 0.1 2015年2月8日 上午1:46:48 baishuo.lp Exp $
 */
public class JsonUtil {
    private static final Log LOG = LogFactory.getLog(JsonUtil.class);

    /**
     *
     * @param object
     * @return
     */
    public static String toPrettyString(Object object) {
        try {
            return JSON.toJSONString(object, true);
        } catch (Exception e) {
            ActsLogUtil.warn(LOG, "Object cannot be converted to json");
            ActsLogUtil.debug(LOG, LogUtil.getErrorMessage(e));
            return "{}";
        }
    }

    /**
     * 
     * @param file
     * @param object
     */
    public static void writeObjectToFile(File file, Object object) {
        String jsString = JSON.toJSONString(object, true);
        FileUtil.writeFile(file, jsString, -1);
    }

    /**
     * 
     * @param jsonRelativePath
     * @param clazz
     * @return
     */
    public static <T> T genObjectFromJsonFile(String jsonRelativePath, Class<T> clazz) {
        String jsonFullPath = FileUtil.getRelativePath(jsonRelativePath, null);
        String jsonString = FileUtil.readFile(jsonFullPath);
        T testObject = JSON.parseObject(jsonString, clazz);
        return testObject;
    }

    /**
     * 
     * @param jsonString
     * @param clazz
     * @return
     */
    public static <T> T genObjectFromJsonString(String jsonString, Class<T> clazz) {
        return JSON.parseObject(jsonString, clazz);
    }

}
