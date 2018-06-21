package com.baomidou.mybatisplus.core.toolkit;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * 对象工具类
 * <p>
 *
 * @author hubin
 * @since 2018-06-05
 */
public class ObjectUtils {

    /**
     * 判断object是否为空,集合会校验size
     */
    public static boolean isNull(Object... objs) {
        for (Object obj : objs) {
            if (ObjectUtils.isEmpty(obj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断object是否不为空,集合会校验size
     */
    public static boolean isNotNull(Object... obj) {
        return !ObjectUtils.isNull(obj);
    }

    /**
     * 对象非空判断
     */
    public static boolean isNotEmpty(Object obj) {
        return !ObjectUtils.isEmpty(obj);
    }

    /**
     * 对象空判断
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        }
        // else
        return false;
    }
}
