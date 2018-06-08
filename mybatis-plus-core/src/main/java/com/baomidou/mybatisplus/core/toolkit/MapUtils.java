package com.baomidou.mybatisplus.core.toolkit;

import java.util.Map;

/**
 * MapUtils
 *
 * @author Caratacus
 * @since 2016/11/16
 */
public class MapUtils {

    /**
     * 判断Map是否为空
     *
     * @param map
     * @return
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    /**
     * 判断Map是否不为空
     *
     * @param map
     * @return
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

}
