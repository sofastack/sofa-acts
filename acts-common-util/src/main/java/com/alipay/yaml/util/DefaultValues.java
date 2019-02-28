/**
 * 
 */
package com.alipay.yaml.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zhiyuan.lzy
 *
 */
public final class DefaultValues {
    /**
     * @param clazz the class for which a default value is needed
     * @return A reasonable default value for the given class (the boxed default value for primitives, <code>null</code>
     * otherwise).
     */
    public static <T> T defaultValue(Class<T> type) {
        // Primitives.wrap(type).cast(...) would avoid the warning, but we can't use that from here
        @SuppressWarnings("unchecked")
        // the put method enforces this key-value relationship
        T t = (T) DEFAULTS.get(type);

        //对枚举类型特殊处理,判断enum是否和type类型相等/是type的超类
        if (t == null && Enum.class.isAssignableFrom(type)) {
            t = type.getEnumConstants()[0];
        }

        return t;
    }

    private static final Map<Class<?>, Object> DEFAULTS;

    static {
        // Only add to this map via put(Map, Class<T>, T)
        Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
        put(map, boolean.class, false);
        put(map, char.class, '\0');
        put(map, byte.class, (byte) 0);
        put(map, short.class, (short) 0);
        put(map, int.class, 0);
        put(map, long.class, 0L);
        put(map, float.class, 0f);
        put(map, double.class, 0d);
        put(map, String.class, "");
        DEFAULTS = Collections.unmodifiableMap(map);
    }

    private static <T> void put(Map<Class<?>, Object> map, Class<T> type, T value) {
        map.put(type, value);
    }

    private DefaultValues() {
    }
}
