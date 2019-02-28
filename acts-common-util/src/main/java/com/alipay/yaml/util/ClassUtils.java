/**
 * 
 */
package com.alipay.yaml.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Miscellaneous class utility methods. Mainly for internal use within the
 * framework; consider
 * <a href="http://commons.apache.org/lang/" target="_blank">Apache Commons
 * Lang</a> for a more comprehensive suite of class utilities.
 *
 * @author Juergen Hoeller
 * @author Keith Donald
 * @author Rob Harrop
 * @author Sam Brannen
 * @since 1.1
 */
@SuppressWarnings("unchecked")
public abstract class ClassUtils {

    /** Suffix for array class names: "[]" */
    public static final String                   ARRAY_SUFFIX               = "[]";

    /** Prefix for internal array class names: "[" */
    private static final String                  INTERNAL_ARRAY_PREFIX      = "[";

    /** Prefix for internal non-primitive array class names: "[L" */
    private static final String                  NON_PRIMITIVE_ARRAY_PREFIX = "[L";

    /** The package separator character '.' */
    private static final char                    PACKAGE_SEPARATOR          = '.';

    /** The inner class separator character '$' */
    private static final char                    INNER_CLASS_SEPARATOR      = '$';

    /** The CGLIB class separator character "$$" */
    public static final String                   CGLIB_CLASS_SEPARATOR      = "$$";

    /** The ".class" file suffix */
    public static final String                   CLASS_FILE_SUFFIX          = ".class";

    /**
     * Map with primitive wrapper type as key and corresponding primitive type
     * as value, for example: Integer.class -> int.class.
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap    = new HashMap<Class<?>, Class<?>>(
                                                                                8);

    /**
     * Map with primitive type as key and corresponding wrapper type as value,
     * for example: int.class -> Integer.class.
     */
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap  = new HashMap<Class<?>, Class<?>>(
                                                                                8);

    /**
     * Map with primitive type name as key and corresponding primitive type as
     * value, for example: "int" -> "int.class".
     */
    private static final Map<String, Class<?>>   primitiveTypeNameMap       = new HashMap<String, Class<?>>(
                                                                                32);

    /**
     * Map with common "java.lang" class name as key and corresponding Class as
     * value. Primarily for efficient deserialization of remote invocations.
     */
    private static final Map<String, Class<?>>   commonClassCache           = new HashMap<String, Class<?>>(
                                                                                32);

    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);
        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
            registerCommonClasses(entry.getKey());
        }

        Set<Class<?>> primitiveTypes = new HashSet<Class<?>>(32);
        primitiveTypes.addAll(primitiveWrapperTypeMap.values());
        primitiveTypes.addAll(Arrays.asList(boolean[].class, byte[].class, char[].class,
            double[].class, float[].class, int[].class, long[].class, short[].class));
        primitiveTypes.add(void.class);
        for (Class<?> primitiveType : primitiveTypes) {
            primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
        }

        registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class,
            Float[].class, Integer[].class, Long[].class, Short[].class);
        registerCommonClasses(Number.class, Number[].class, String.class, String[].class,
            Object.class, Object[].class, Class.class, Class[].class);
        registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class,
            Error.class, StackTraceElement.class, StackTraceElement[].class);
    }

    /**
     * Check if the given class represents a primitive wrapper, i.e. Boolean,
     * Byte, Character, Short, Integer, Long, Float, or Double.
     * 
     * @param clazz
     *            the class to check
     * @return whether the given class is a primitive wrapper class
     */
    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        return primitiveWrapperTypeMap.containsKey(clazz);
    }

    /**
     * Check if the given class represents a primitive (i.e. boolean, byte,
     * char, short, int, long, float, or double) or a primitive wrapper (i.e.
     * Boolean, Byte, Character, Short, Integer, Long, Float, or Double).
     * 
     * @param clazz
     *            the class to check
     * @return whether the given class is a primitive or primitive wrapper class
     */
    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
    }

    /**
     * Register the given common classes with the ClassUtils cache.
     */
    private static void registerCommonClasses(Class<?>... commonClasses) {
        for (Class<?> clazz : commonClasses) {
            commonClassCache.put(clazz.getName(), clazz);
        }
    }
}
