package com.baomidou.mybatisplus.core.metadata;

import java.lang.reflect.Field;
import java.util.List;

import com.baomidou.mybatisplus.core.config.GlobalConfig;

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
    protected void doBeforeScanTable(Class<?> clazz, GlobalConfig globalConfig, TableInfo tableInfo, List<Field> fields) {

    }

    @Override
    protected void doBeforeScanField(Field field, TableInfo tableInfo) {

    }

    @Override
    protected void doAfterScanField(TableFieldInfo fieldInfo, TableInfo tableInfo) {

    }

    @Override
    protected void doAfterScanTable(Class<?> clazz, GlobalConfig globalConfig, TableInfo tableInfo, List<TableFieldInfo> tableFieldInfoList) {

    }

}
