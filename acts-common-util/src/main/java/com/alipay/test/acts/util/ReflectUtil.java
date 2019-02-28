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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.alipay.test.acts.api.CSVApis;
import com.alipay.test.acts.exception.ActsException;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author ming.cm
 * @author batuo.zxm
 * @version $Id: ReflectUtil.java, v 0.1 2015-5-14 下午9:05:01 ming.cm Exp $
 * @version $Id: ReflectUtil.java, v 0.1 2015年9月30日 下午4:21:15 batuo.zxm Exp $
 */
public class ReflectUtil {

    public static boolean isAbstractClass(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    public static boolean canInstantiate(Class<?> clazz) {
        return isAbstractClass(clazz);
    }

    public static Object instantiateClass(Class<?> clazz) throws InstantiationException,
                                                         IllegalAccessException,
                                                         ClassNotFoundException {
        String className = clazz.getName();
        Object object = clazz.getClassLoader().loadClass(className).newInstance();
        return object;

    }

    /**
     * Get defined fields
     * 
     * @param clazz
     * @param fieldName
     * @return
     * @throws NoSuchFieldException
     */
    public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getField(superClass, fieldName);
            }
        }
    }

    /**
     * Get defined fields
     * 
     * @param object
     * @param fieldName
     * @return
     */
    public static Field getField(Object object, String fieldName) {
        Class<?> clazz = object.getClass();
        Field field;
        try {
            field = getField(clazz, fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        field.setAccessible(true);
        return field;
    }

    /**
     * Assign a value to the field
     * 
     * @param object
     * @param fieldName
     * @param newValue
     */
    public static void setFieldValue(Object object, String fieldName, Object newValue) {
        Class<?> clazz = object.getClass();
        try {
            Field field = getField(clazz, fieldName);
            field.setAccessible(true);
            setFieldValue(object, field, newValue);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    @SuppressWarnings("deprecation")
    public static Object valueByCorrectType(Object object, Class<?> clazz, Object value) {
        Object newValue = null;
        if (value == null) {
            return null;
        }
        if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
            try {
                newValue = Integer.valueOf(value.toString());
            } catch (Exception ex) {
                newValue = 0;
            }
        } else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
            try {
                newValue = Double.valueOf(value.toString());
            } catch (Exception ex) {
                newValue = 0.0;
            }
        } else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
            try {
                newValue = Long.valueOf(value.toString());
            } catch (Exception ex) {
                newValue = 0L;
            }
        } else if (clazz.equals(char.class) || clazz.equals(Character.class)) {
            try {
                newValue = value.toString().charAt(0);
            } catch (Exception ex) {
                newValue = 'a';
            }
        } else if (clazz.equals(byte.class) || clazz.equals(Byte.class)) {
            try {
                newValue = Byte.valueOf(value.toString());
            } catch (Exception ex) {
                newValue = 0;
            }
        } else if (clazz.equals(short.class) || clazz.equals(Short.class)) {
            try {
                newValue = Short.valueOf(value.toString());
            } catch (Exception ex) {
                newValue = 0;
            }
        } else if (clazz.equals(float.class) || clazz.equals(Float.class)) {
            try {
                newValue = Float.valueOf(value.toString());
            } catch (Exception ex) {
                newValue = 0f;
            }
        } else if (clazz.equals(BigDecimal.class)) {
            try {
                newValue = new BigDecimal(value.toString());

            } catch (Exception ex) {
                newValue = new BigDecimal(0L);
            }
        } else if (clazz.equals(boolean.class) || clazz.equals(Boolean.class)) {
            try {
                newValue = Boolean.valueOf(value.toString());
            } catch (Exception ex) {
                newValue = false;
            }
        } else if (clazz.equals(Currency.class)) {
            try {
                newValue = Currency.getInstance(value.toString());
            } catch (Exception ex) {
                newValue = Currency.getInstance(Locale.getDefault());
            }
        } else if (clazz.equals(Date.class)) {
            if (value != null) {
                try {
                    if (value instanceof Date) {
                        newValue = value;
                    } else if (value instanceof String) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        newValue = sdf.parse(value.toString());
                    }

                } catch (Exception ex) {
                    Date date = new Date();
                    newValue = date;
                }
            }

        } else {
            newValue = value;
        }
        return newValue;
    }

    public static Object valueByCorrectType(Object object, Field field, Object value) {
        Class<?> clazz = field.getType();

        return valueByCorrectType(object, clazz, value);
    }

    public static void setFieldValue(Object object, Field field, Object value)
                                                                              throws IllegalArgumentException,
                                                                              IllegalAccessException {
        Object newValue = valueByCorrectType(object, field, value);
        if (newValue == null) {
            return;
        } else {
            field.set(object, newValue);
        }

    }

    /**
     *
     * @param object
     * @param fieldName
     * @return
     */
    public static Object getFieldValue(Object object, String fieldName) {
        Field[] fields = object.getClass().getDeclaredFields();
        Object result = null;
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                try {
                    field.setAccessible(true);
                    result = field.get(object);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);

                }
                break;
            }
        }
        return result;

    }

    /**
     * 
     * @param clazz
     * @param methodName
     * @return
     */
    public static Method findMethod(Class<?> clazz, String methodName) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                method.setAccessible(true);
                return method;
            }
        }

        methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                method.setAccessible(true);
                return method;
            }
        }

        throw new RuntimeException("method not find" + clazz + "|" + methodName);

    }

    /**
     * 
     * @param clazz
     * @param methodName
     * @param parameterTypes 
     * @return
     */
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
        Method[] desclaredMethods = clazz.getDeclaredMethods();
        Method[] publicMethods = clazz.getMethods();

        List<Method> methods = new ArrayList<Method>();
        if (desclaredMethods != null) {
            methods.addAll(Arrays.asList(desclaredMethods));
        }
        if (publicMethods != null) {
            methods.addAll(Arrays.asList(publicMethods));
        }

        List<Class<?>> list = Arrays.asList(parameterTypes);
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length == parameterTypes.length
                    && list.containsAll(Arrays.asList(params))) {
                    method.setAccessible(true);
                    return method;
                }

            }
        }

        throw new RuntimeException("method not find" + clazz + "|" + methodName);

    }

    /**
     * 
     * @param object
     * @param methodName
     * @param args
     * @return
     */
    public static Object invokeMethod(Object object, String methodName, Object[] args) {
        try {
            return findMethod(object.getClass(), methodName).invoke(object, args);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    /**
     * 
     * @param clazz 
     * @param methodName
     * @param args
     * @return
     */
    public static Object invokeMethod(Class<?> clazz, String methodName, Object... args) {
        try {
            Method method = findMethod(clazz, methodName);
            method.setAccessible(true);
            return method.invoke(clazz.newInstance(), args);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    /**
     * 
     * @param clazz 
     * @param methodName
     * @param parameterTypes 
     * @param args
     * @return
     */
    public static Object invokeMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes,
                                      Object[] args) {
        try {
            Method method = findMethod(clazz, methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(clazz.newInstance(), args);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    /**
     * 
     * @param object 
     * @param methodName
     * @param parameterTypes 
     * @param args
     * @return
     */
    public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes,
                                      Object[] args) {
        try {
            Method method = findMethod(object.getClass(), methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(object, args);
        } catch (Exception e) {
            throw new RuntimeException(methodName + "|" + Arrays.toString(parameterTypes), e);
        }

    }

    /**
     * 
     * @param className
     * @return
     */
    public static Class<?> getClassForName(String className) {
        try {
            return ClassUtils.getClass(className);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    public static String genModelForSpeciMethod(String csvModelRootPath, ClassLoader classLoader,
                                                String className, String methodName,
                                                boolean isResultOnly) {
        try {
            Set<String> setWarnMsg = CSVApis.genCsvFromSpeciMethodByRootPath(csvModelRootPath,
                classLoader, className, methodName, isResultOnly);

            return StringUtils.join(setWarnMsg, ";");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found:" + className, e);
        } catch (Exception exp) {
            throw new ActsException("Failed to generate template:" + exp.getMessage());
        }

    }

    /**
     * generate template based on classes
     *
     * @param classLoader
     * @param className
     * @return
     */
    public static String genModelForCls(String csvModelRootPath, ClassLoader classLoader,
                                        String className) {

        try {
            Set<String> setWarnMsg = CSVApis.genCsvFromObjClassByRootPath(csvModelRootPath,
                classLoader, className);

            return StringUtils.join(setWarnMsg, ";");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found:" + className, e);
        } catch (Exception exp) {
            throw new ActsException("Failed to generate template:" + exp.getMessage());
        }

    }

    /**
     * generate template based on classes
     * @param className
     * @return
     */
    public static String genModelForCls(String csvModelRootPath, String className) {

        try {

            Set<String> setWarnMsg = CSVApis.genCsvFromObjClassByRootPath(csvModelRootPath,
                getClassForName(className).getClass().getClassLoader(), className);
            return setWarnMsg.toString();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found:" + className, e);
        } catch (Exception exp) {
            throw new ActsException("Failed to generate template:" + exp.getMessage());
        }

    }

}
