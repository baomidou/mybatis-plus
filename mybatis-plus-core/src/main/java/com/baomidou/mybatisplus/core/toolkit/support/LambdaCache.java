package com.baomidou.mybatisplus.core.toolkit.support;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ming
 * @Date 2018/5/12
 */
public class LambdaCache {

    private static final Map<String, Map<String, String>> LAMBDA_CACHE = new ConcurrentHashMap<>();

    public static void put(Class clazz, TableInfo tableInfo) {
        LAMBDA_CACHE.put(clazz.getName(), createLambdaMap(tableInfo));
    }

    private static Map<String, String> createLambdaMap(TableInfo tableInfo) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isNotEmpty(tableInfo.getKeyProperty())) {
            map.put(tableInfo.getKeyProperty(), tableInfo.getKeyColumn());
        }
        tableInfo.getFieldList().forEach(i -> map.put(i.getProperty(), i.getColumn()));
        return map;
    }

    public static Map<String, String> getColumnMap(Class clazz) {
        return LAMBDA_CACHE.get(clazz.getName());
    }
}
