package com.baomidou.mybatisplus.extension.plugins.handler;

public interface TableNameHandlerFactory {
    TableNameHandler createContextTableNameHandler(QueryParameterWrapper queryParameterWrapper);
}
