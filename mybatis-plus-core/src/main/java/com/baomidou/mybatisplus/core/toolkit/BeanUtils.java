package com.baomidou.mybatisplus.core.toolkit;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.beans.BeanMap;

/**
 * <p>
 * Bean 转换工具类<br/>
 * 使用请依赖 cglib 包
 * </p>
 *
 * @author hubin
 * @since 2018-06-12
 */
public class BeanUtils {

    /**
     * <p>
     * 将对象装换为map
     * </p>
     *
     * @param bean 转换对象
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        if (bean != null) {
            Map<String, Object> map = new HashMap<>(16);
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(String.valueOf(key), beanMap.get(key));
            }
            return map;
        }
        return null;
    }

    /**
     * <p>
     * map 装换为 java bean 对象
     * </p>
     *
     * @param map   转换 MAP
     * @param clazz 对象 Class
     * @return
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz) {
        T bean = null;
        try {
            bean = clazz.newInstance();
            BeanMap.create(bean).putAll(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * <p>
     * List<T> 转换为 List<Map<String, Object>>
     * </p>
     *
     * @param beanList 转换对象集合
     * @return
     */
    public static <T> List<Map<String, Object>> beansToMaps(List<T> beanList) {
        if (CollectionUtils.isEmpty(beanList)) {
            return null;
        }
        return beanList.stream().map(BeanUtils::beanToMap).collect(toList());
    }

    /**
     * <p>
     * List<Map<String,Object>> 转换为 List<T>
     * </p>
     *
     * @param maps  转换 MAP 集合
     * @param clazz 对象 Class
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> List<T> mapsToBeans(List<Map<String, Object>> maps, Class<T> clazz) {
        if (CollectionUtils.isNotEmpty(maps)) {
            return null;
        }
        return maps.stream().map(e -> mapToBean(e, clazz)).collect(toList());
    }
}
