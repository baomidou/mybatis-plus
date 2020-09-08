package com.baomidou.mybatisplus.core.metadata;

import org.apache.ibatis.session.Configuration;

/**
 * TableInfo 解析器的默认实现
 * @author jeff
 * @date 2020/9/8
 */
public class DefaultTableInfoParser extends AbstractTableInfoParser<TableInfo> {

    @Override
    protected TableInfo createTableInfo(Class<?> clazz) {
        return new TableInfo(clazz);
    }

    @Override
    protected void doScanTable(Class<?> clazz, Configuration configuration, TableInfo tableInfo) {

    }

    @Override
    protected void doScanField(TableFieldInfo fieldInfo, Configuration configuration, TableInfo tableInfo) {

    }
}
