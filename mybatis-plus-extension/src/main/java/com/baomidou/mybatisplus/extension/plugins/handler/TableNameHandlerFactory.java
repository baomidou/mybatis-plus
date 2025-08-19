package com.baomidou.mybatisplus.extension.plugins.handler;

/**
 * 动态表名处理器Factory
 *
 * @author mika
 * @since 3.5.13
 */
public interface TableNameHandlerFactory {
    TableNameHandler createContextTableNameHandler(QueryParameterWrapper queryParameterWrapper);
}
