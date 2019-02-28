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
package com.alipay.test.acts.object;

import com.alipay.test.acts.log.ActsLogUtil;
import com.alipay.test.acts.util.JsonUtil;
import ognl.DefaultMemberAccess;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author baishuo.lp
 * @version $Id: ObjectUtil.java, v 0.1 2015年8月19日 下午12:08:51 baishuo.lp Exp $
 */
public class ActsObjectUtil {

    private static final Log logger = LogFactory.getLog(ActsObjectUtil.class);

    public static boolean easyCompare(Object exp, Object act) {
        String expString = JsonUtil.toPrettyString(exp);
        String actString = JsonUtil.toPrettyString(act);
        return StringUtils.equals(expString, actString);
    }

    public static void setProperty(Object object, String ognlExpression, Object value) {
        try {
            OgnlContext ognlContext = new OgnlContext();
            ognlContext.setMemberAccess(new DefaultMemberAccess(true));
            Object ognlExprObj = Ognl.parseExpression(ognlExpression);
            Ognl.setValue(ognlExprObj, ognlContext, object, value);
        } catch (OgnlException e) {
            ActsLogUtil.fail(logger, "Ongl error, failed to set property based on "
                                     + ognlExpression, e);
        }
    }

    public static Object getProperty(Object object, String ognlExpression) throws OgnlException {
        OgnlContext ognlContext = new OgnlContext();
        ognlContext.setMemberAccess(new DefaultMemberAccess(true));
        Object ognlExprObj = Ognl.parseExpression(ognlExpression);
        return Ognl.getValue(ognlExprObj, ognlContext, object);

    }

    public static List<Field> getAllFields(Class<?> objClass) {

        List<Field> lis = new ArrayList<Field>();

        Class<?> cls = objClass;

        while (null != cls && !cls.equals(Object.class)) {

            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                lis.add(field);
            }
            cls = cls.getSuperclass();
        }

        return lis;
    }

    /**
     * get field from class or super class by field name.
     *
     * @param objClass    -targeted class
     * @param fieldName   -targeted field
     *
     * @return
     */
    public static Field getField(Class<?> objClass, String fieldName) {

        Class<?> cls = objClass;

        while (!cls.equals(Object.class)) {

            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {

                if (StringUtils.equals(field.getName(), fieldName)) {

                    return field;
                }
            }
            cls = cls.getSuperclass();
        }

        return null;
    }

    /**
     * get real class which declares target field by field name.
     * maybe class or super class.
     * 
     * @param objClass
     * @param fieldName
     * 
     * @return
     */
    public static Class<?> getClass(Class<?> objClass, String fieldName) {

        Class<?> cls = objClass;

        while (!cls.equals(Object.class)) {

            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {

                if (StringUtils.equals(field.getName(), fieldName)) {

                    return cls;
                }
            }
            cls = cls.getSuperclass();
        }

        return null;
    }

    /**
     * initiate an instance of the objClass
     * int/char/double/float/byte/long/short = 0，boolean = false
     * object/enum = null
     */
    @SuppressWarnings("unchecked")
    public static Object genInstance(Class<?> objClass) throws Exception {
        Object objValue = null;

        Constructor constructors[] = objClass.getConstructors();
        for (Constructor constructor : constructors) {
            Class[] types = constructor.getParameterTypes();
            Object[] params = new Object[types.length];
            for (int i = 0; i < types.length; i++) {
                if (types[i].isPrimitive()) {
                    if (types[i] == boolean.class) {
                        params[i] = false;
                    } else {
                        params[i] = 0;
                    }
                } else if (types[i].isAssignableFrom(List.class)) {
                    params[i] = new ArrayList<Object>();
                } else if (types[i].isAssignableFrom(Map.class)) {
                    params[i] = new HashMap<Object, Object>();
                }
            }
            try {
                objValue = constructor.newInstance(params);
                if (objValue != null) {
                    break;
                }
            } catch (InstantiationException e) {
                logger.info("InstantiationException", e);
            } catch (IllegalAccessException e) {
                logger.info("IllegalAccessException", e);
            } catch (IllegalArgumentException e) {
                logger.info("IllegalArgumentException", e);
            } catch (InvocationTargetException e) {
                logger.info("InvocationTargetException", e);
            }
        }
        if (objValue == null) {
            ActsLogUtil
                .error(
                    logger,
                    "Failed to create instance by class["
                            + objClass.getSimpleName()
                            + "], and please check default constructor avaliable in class or class qualified name correct in CSV.");
            throw new Exception(
                "Failed to create object, and please check default constructor avaliable in class or class qualified name correct in CSV.");
        }

        return objValue;
    }
}
