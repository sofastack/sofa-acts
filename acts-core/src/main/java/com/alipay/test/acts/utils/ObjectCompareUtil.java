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

import com.alipay.test.acts.util.VelocityUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.google.common.collect.Lists;
import org.testng.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectCompareUtil {
    private final static Log                       log             = LogFactory
                                                                       .getLog(ObjectCompareUtil.class);

    private static StringBuffer                    reportStr       = new StringBuffer();
    // Replacement of expected result variable
    public static Map<String, Object>              varParaMap      = new HashMap<String, Object>();
    // Y:Ordinary verification,N:not check，R:Regular check <className,<fieldName,flag>>
    public static Map<String, Map<String, String>> varFlagMap      = new HashMap<String, Map<String, String>>();
    private static final String[]                  comparableTypes = { "int", "float", "double",
            "long", "short", "byte", "boolean", "char", "java.lang.Integer", "java.lang.Float",
            "java.lang.Double", "java.lang.Long", "java.lang.Short", "java.lang.Byte",
            "java.lang.Boolean", "java.lang.Character", "java.lang.String", "java.math.BigDecimal",
            "java.util.Date"                                      };

    public static void compare(Object target, Object expect,
                               Map<String, Map<String, String>> flags, Map<String, Object> paramMap) {

        varParaMap = paramMap;
        varFlagMap = flags;
        reportStr = new StringBuffer();
        DetailCollectUtils.appendAndLog("\nexpect:" + ObjectUtil.toJson(expect) + "\nactual:"
                                        + ObjectUtil.toJson(target), log);
        Assert.assertTrue(compareByFields(target, expect), reportStr.toString());
    }

    public static boolean matchObj(Object target, Object expect,
                                   Map<String, Map<String, String>> flags,
                                   Map<String, Object> paramMap) {

        varParaMap = paramMap;
        varFlagMap = flags;
        return compareByFields(target, expect);
    }

    public static boolean compareByFields(Object target, Object expect) {

        if (target == null) {
            return expect == null;
        }
        if (expect == null) {
            return target == null;
        }

        if (target.getClass().equals(Throwable.class)
            || target.getClass().equals(StackTraceElement[].class)) {
            reportStr.append("\n" + "Matching, skip the current type check"
                             + target.getClass().getName());
            return true;
        }

        if (expect instanceof String) {
            // Use velocity for variable substitution
            String expectStr = (String) expect;
            if (expectStr.indexOf("$") != -1) { // there are variables that need to be replaced
                String parsedValue = VelocityUtil.evaluateString(varParaMap, expectStr);
                expect = parsedValue;
            }

        }
        boolean isSame = true;

        String objName = target.getClass().getName();
        Class<?> objType = target.getClass();

        if (isComparable(objType)) {
            return compare(target, expect);
        } else if (objType.isArray()) {
            Object[] targetArray = (Object[]) target;
            Object[] expectArray = (Object[]) expect;
            if (targetArray.length != expectArray.length) {
                reportStr.append("\n" + "The length of array is different:" + objName);
                return false;
            }
            for (int i = 0; i < targetArray.length; i++) {
                if (!compareByFields(targetArray[i], expectArray[i])) {
                    reportStr.append("\n" + "element in array check failed,index=" + i);
                    return false;
                }
            }
            return true;
        } else if (target instanceof Map) {

            Map<Object, Object> targetMap = (Map) target;
            Map<Object, Object> expectMap = (Map) expect;

            if (targetMap.size() != expectMap.size()) {
                reportStr.append("\n" + "The size of hashMap is different:" + objName);
                return false;
            }

            //Two-way check, support out of order
            //Validate the actual value based on the expected Map
            for (Entry<Object, Object> entry : expectMap.entrySet()) {
                Object expectVal = entry.getValue();
                Object targetVal = targetMap.get(entry.getKey());
                if (!(compareByFields(targetVal, expectVal))) {
                    reportStr.append("\n" + "element of hashmap check failed, key="
                                     + entry.getKey());
                    return false;
                }
            }

            //Verification of the expected value based on the actual map
            for (Entry<Object, Object> entry : targetMap.entrySet()) {
                Object targetVal = entry.getValue();
                Object expectVal = expectMap.get(entry.getKey());
                if (!(compareByFields(targetVal, expectVal))) {
                    reportStr.append("\n" + "element in hashmap check failed, key="
                                     + entry.getKey());
                    return false;
                }
            }

        } else if (target instanceof List) {
            List targetList = (List) target;
            List expectListCpy = Lists.newArrayList((List) expect);
            if (targetList.size() != expectListCpy.size()) {
                reportStr.append("\n" + "The length of the list is different: " + objName);
                return false;
            }
            for (Object objTarget : targetList) {
                boolean isFound = false;
                for (Object objExpect : expectListCpy) {
                    if (compareByFields(objTarget, objExpect)) {
                        isFound = true;
                        //expectListCpy.remove(objExpect);
                        break;
                    }
                }
                if (!isFound) {
                    reportStr.append("\n" + "Target element not found in expected list:"
                                     + ObjectUtil.toJson(objTarget));
                    return false;
                }
            }
        } else {
            Map objFlag = null;
            if (!(varFlagMap == null || varFlagMap.isEmpty())) {
                objFlag = varFlagMap.get(objName);
            }
            List<Field> fields = new ArrayList<Field>();

            for (Class<?> c = objType; c != null; c = c.getSuperclass()) {
                for (Field field : c.getDeclaredFields()) {
                    int modifiers = field.getModifiers();
                    if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)
                        && !fields.contains(field)) {
                        fields.add(field);
                    }
                }
            }
            for (Field field : fields) {
                String fieldName = field.getName();

                //if class type is Throwable/StackTraceElement[],pass
                if (StringUtils.equals(fieldName, "suppressedExceptions")
                    || field.getType().equals(Throwable.class)) {
                    reportStr.append("\n" + "Matching, skip the current type check"
                                     + target.getClass().getName());
                    continue;
                }

                if (StringUtils.equals(fieldName, "stackTrace")
                    && field.getType().equals(StackTraceElement[].class)) {
                    reportStr.append("\n"
                                     + "Matching, skip the current type check, type of object:"
                                     + target.getClass().getName() + ",field:" + field.getName());
                    continue;
                }

                field.getType().toString();
                try {
                    field.setAccessible(true);
                    Object objTarget = field.get(target);
                    Object objExpect = field.get(expect);
                    if (objFlag != null && objFlag.get(fieldName) != null) {
                        if (objFlag.get(fieldName).equals("N")) {
                            continue;
                        } else if (objFlag.get(fieldName).equals("R")) {
                            Pattern pattern = Pattern.compile((String) objExpect);
                            Matcher matcher = pattern.matcher((String) objTarget);
                            boolean matchRes = matcher.matches();
                            if (!matchRes) {
                                isSame = false;
                            }
                            continue;
                        }

                        else if (((String) objFlag.get(fieldName)).startsWith("D")) {

                            if (null == objTarget) {
                                boolean isEqual = (null == objExpect);
                                if (!isEqual) {
                                    reportStr.append("\n" + "time check failed,actual value:"
                                                     + target + "\nExpected value:" + expect);
                                }
                                continue;
                            }

                            boolean isDate = objTarget.getClass().getName()
                                .equalsIgnoreCase("java.util.Date");
                            if (!isDate) {
                                continue;
                            }
                            String currentFlag = (String) objFlag.get(fieldName);

                            Date real = (Date) objTarget;
                            Date expect1 = (Date) objExpect;
                            if (objExpect == null) {
                                expect1 = new Date();
                            }
                            /*
                             * There are two cases, one is directly D, and the other is D200.
                             * The D is directly compared with the time stored in yaml,D200 is compared with the current time.
                             */
                            if (currentFlag.equals("D")) {
                                boolean isEqual = compare(target, expect);
                                if (!isEqual) {
                                    reportStr.append("\n" + "Time check failed,actual value:"
                                                     + target + "\nExpected value:" + expect);
                                }
                                continue;
                            } else {
                                long timeFlow = Long.valueOf(currentFlag.replace("D", ""));
                                Long realTime = real.getTime();
                                Long expectTime = expect1.getTime();
                                if (Math.abs((realTime - expectTime) / 1000) > timeFlow) {
                                    isSame = false;
                                    reportStr.append("\n" + "Time check failed,actual value:"
                                                     + target + "\nExpected value:" + expect);
                                }
                                continue;
                            }

                        }

                    }

                    if (field.get(target) instanceof Map) {
                        Map<Object, Object> tarMap = (Map) field.get(target);
                        Map<Object, Object> expMap = (Map) field.get(expect);

                        if (objFlag != null && objFlag.get(fieldName) != null
                            && objFlag.get(fieldName).equals("ME")) {

                            for (Entry<Object, Object> entry : expMap.entrySet()) {
                                Object expectVal = entry.getValue();
                                Object targetVal = tarMap.get(entry.getKey());
                                if (!(compareByFields(targetVal, expectVal))) {
                                    isSame = false;
                                    reportStr.append("\n" + "element in hashmap check failed，key="
                                                     + entry.getKey());
                                }
                            }
                            continue;
                        }
                    }

                    if (!compareByFields(objTarget, objExpect)) {
                        reportStr.append("\n" + fieldName + "object check failed,expected value"
                                         + ObjectUtil.toJson(objExpect) + "actual value"
                                         + ObjectUtil.toJson(objTarget));

                        isSame = false;
                    }

                } catch (IllegalArgumentException e) {
                    return false;
                } catch (IllegalAccessException e) {
                    return false;
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return isSame;

    }

    public static boolean isComparable(Class<?> objType) {
        for (String comparableType : comparableTypes) {
            if (comparableType.equals(objType.getName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean compare(Object target, Object expect) {
        if (target.getClass().isPrimitive()) {
            if (!(target == expect)) {
                return false;
            }

        } else if (StringUtils.equals(target.getClass().getName(), "java.math.BigDecimal")) {
            BigDecimal bitTarge = (BigDecimal) target;
            BigDecimal bitExpect = (BigDecimal) expect;
            if (0 != bitTarge.compareTo(bitExpect)) {
                return false;
            }
        } else if (target.getClass() == String.class) {
            if (!target.equals(expect)) {
                reportStr.append("\n" + "check failed, actual value:" + target
                                 + "\nExpected value:" + expect);
                return false;
            }
        } else {
            if (!target.equals(expect)) {
                reportStr.append("\n" + "check failed, actual value:" + target
                                 + "\nExpected value:" + expect);
                return false;
            }
        }
        return true;
    }

    public static void addParam(String key, String value) {
        varParaMap.put(key, value);
    }

    /**
     * Getter method for property <tt>reportStr</tt>.
     * 
     * @return property value of reportStr
     */
    public static StringBuffer getReportStr() {
        return reportStr;
    }

    public static void main(String[] args) {

    }

}