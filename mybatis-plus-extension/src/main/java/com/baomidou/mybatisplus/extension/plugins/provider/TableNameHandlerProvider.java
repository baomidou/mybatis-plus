package com.baomidou.mybatisplus.extension.plugins.provider;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;

/**
 * @author Langel
 * @since 2021-08-04
 **/
public interface TableNameHandlerProvider {

    /**
     * get {@link TableNameHandler} by table name
     * @param value tableName
     * @return {@link TableNameHandler}
     */
    TableNameHandler get(String value);

}
