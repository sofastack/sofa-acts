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
package com.alipay.test.acts.object.processor;

import com.alipay.test.acts.helper.CSVHelper;
import com.alipay.test.acts.log.ActsLogUtil;
import com.alipay.test.acts.model.VirtualList;
import com.alipay.test.acts.model.VirtualMap;
import com.alipay.test.acts.util.CSVApisUtil;
import com.alipay.test.acts.util.ReflectUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mahongliang
 * @version $Id: ObjHandUtil.java, v 0.1 2015年11月29日 下午11:58:50  Exp $
 */
public final class ObjHandUtil {

    private static final Log logger = LogFactory.getLog(ObjHandUtil.class);

    /**
    *
    * @param convertCsv
    * @param clsName
    * @return
    */
    public static boolean isSubListConvert(String convertCsv, String clsName) {
        if (StringUtils.isBlank(convertCsv) || !StringUtils.contains(convertCsv, ".csv")
            || StringUtils.isBlank(clsName)) {
            return false;
        }
        List<?> tableList = CSVHelper.readFromCsv(convertCsv);
        String className = StringUtils.trim(((String[]) tableList.get(1))[0]);
        if (StringUtils.equals(className, clsName)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param objValue
     * @return
     */
    public static Collection<Object> handListConvert(Object objValue, String csvPath) {
        try {
            VirtualList obj = (VirtualList) objValue;
            List<?> tableList = CSVHelper.readFromCsv(csvPath);
            String typeName = StringUtils.trim(((String[]) tableList.get(1))[2]);

            Class<?> listRet = Class.forName(typeName);
            if (List.class.isAssignableFrom(listRet)) {
                List<Object> retList = new ArrayList<Object>();
                Class<?> typeCls = obj.getVirtualList().getClass();
                if (CSVApisUtil.isWrapClass(typeCls) && null == obj.getVirtualList()) {
                    retList.add(ReflectUtil.valueByCorrectType(null, typeCls, "1"));
                }
                if (CSVApisUtil.isWrapClass(typeCls)
                    && typeCls.getName().equals("java.lang.String")
                    && StringUtils.isBlank((String) obj.getVirtualList())) {
                    retList.add("demo");
                } else {
                    retList.add(obj.getVirtualList());
                }
                return retList;
            } else {
                Set<Object> retSet = new HashSet<Object>();
                Class<?> typeCls = obj.getVirtualList().getClass();
                if (CSVApisUtil.isWrapClass(typeCls) && null == obj.getVirtualList()) {
                    retSet.add(ReflectUtil.valueByCorrectType(null, typeCls, "1"));
                }
                if (CSVApisUtil.isWrapClass(typeCls)
                    && typeCls.getName().equals("java.lang.String")
                    && StringUtils.isBlank((String) obj.getVirtualList())) {
                    retSet.add("demo");
                } else {
                    retSet.add(obj.getVirtualList());
                }
                return retSet;
            }
        } catch (Exception e) {
            logger.error("handListConvert error", e);
        }

        return null;
    }

    /**
     *
     * @param objValue
     * @return
     */
    public static Map<Object, Object> handMapConvert(Object objValue) {
        VirtualMap obj = (VirtualMap) objValue;
        Map<Object, Object> retMap = new HashMap<Object, Object>();
        try {
            retMap.put(obj.getMapKey(), obj.getMapValue());
        } catch (Exception e) {
            ActsLogUtil.error(logger, "handMapConvert error,", e);
            return null;
        }
        return retMap;
    }

}
