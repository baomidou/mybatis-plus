package com.baomidou.mybatisplus.extension.plugins.handler;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;

import java.util.Map;

/**
 * 暴露MPBoundSql/BoundSql中的查询参数
 *
 * @author mika
 * @since 3.5.13
 */
public class QueryParameterWrapper {
    private Map<String, Object> queryParameterMap;
    private QueryParameterWrapper(Map<String, Object> queryParameterMap) {
        this.queryParameterMap = queryParameterMap;
    }

    public static QueryParameterWrapper createWrapper(PluginUtils.MPBoundSql mpBs) {
        Object parameterObjs = mpBs.parameterObject();
        if (parameterObjs instanceof Map) {
            return new QueryParameterWrapper((Map<String, Object>)parameterObjs);
        }
        return null;
    }

    public Object getParameterValue(String key) {
        if (queryParameterMap.containsKey(key)){
            return queryParameterMap.get(key);
        }
        return null;
    }
}
