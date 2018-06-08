package com.baomidou.mybatisplus.core.toolkit;

/**
 * <p>
 * ArrayUtils工具类
 * </p>
 *
 * @author Caratacus
 * @since 2017-03-09
 */
public class ArrayUtils {

    public static boolean isEmpty(final Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(final Object[] array) {
        return !isEmpty(array);
    }

}
