package com.baomidou.mybatisplus.core.toolkit;

import java.util.Collection;
import java.util.Map;

/**
 * 断言类
 *
 * @author miemie
 * @since 2018-07-24
 */
public final class Assert {

    /**
     * 断言这个 boolean 为 true
     *
     * @param expression boolean 值
     * @param message    消息
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw ExceptionUtils.mpe(message);
        }
    }

    /**
     * 断言这个 boolean 为 false
     *
     * @param expression boolean 值
     * @param message    消息
     */
    public static void isFalse(boolean expression, String message) {
        isTrue(!expression, message);
    }

    /**
     * 断言这个 object 为 null
     *
     * @param object  对象
     * @param message 消息
     */
    public static void isNull(Object object, String message) {
        isTrue(object == null, message);
    }

    /**
     * 断言这个 object 不为 null
     *
     * @param object  对象
     * @param message 消息
     */
    public static void notNull(Object object, String message) {
        isTrue(object != null, message);
    }

    /**
     * 断言这个 collection 不为空
     *
     * @param collection 集合
     * @param message    消息
     */
    public static void notEmpty(Collection<?> collection, String message) {
        isTrue(CollectionUtils.isNotEmpty(collection), message);
    }

    /**
     * 断言这个 map 不为空
     *
     * @param map     集合
     * @param message 消息
     */
    public static void notEmpty(Map<?, ?> map, String message) {
        isTrue(CollectionUtils.isNotEmpty(map), message);
    }
}
