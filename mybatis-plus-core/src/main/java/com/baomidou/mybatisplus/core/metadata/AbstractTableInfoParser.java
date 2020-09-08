package com.baomidou.mybatisplus.core.metadata;

import java.util.List;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

/**
 * TableInfo 解析器
 *
 * @author jeff
 * @since 2020/9/8
 */
public abstract class AbstractTableInfoParser<T extends TableInfo> implements TableInfoParser<T> {

    /**
     * 创建TableInfo实例
     * @param clazz TableInfo对应的类
     * @return TableInfo本身或者子类
     */
    protected abstract T createTableInfo(Class<?> clazz);

    /**
     * 扫描 实体类, 补充 TableInfo
     * @param clazz 反射实体类
     * @param tableInfo 待完善的TableInfo
     */
    private void scanTable(MapperBuilderAssistant builderAssistant, Class<?> clazz, T tableInfo){
        String currentNamespace = builderAssistant.getCurrentNamespace();
        Configuration configuration = builderAssistant.getConfiguration();

        TableInfoHelper.initTableInfo(configuration, currentNamespace, clazz, tableInfo);

        doScanTable(clazz, configuration, tableInfo);

        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        if(CollectionUtils.isNotEmpty(fieldList)){
            for (TableFieldInfo fieldInfo : fieldList){
                scanField(fieldInfo, configuration, tableInfo);
            }
        }
    }

    /**
     * 扩展扫描实体类,来自定义构建TableInfo
     * @param clazz 反射实体类
     * @param configuration mybatis配置
     * @param tableInfo 待完善的TableInfo
     */
    protected abstract void doScanTable(Class<?> clazz, Configuration configuration, T tableInfo);

    /**
     * 扫描字段
     * @param fieldInfo 字段信息
     * @param configuration mybatis配置
     * @param tableInfo 待完善的TableInfo
     */
    private void scanField(TableFieldInfo fieldInfo, Configuration configuration, T tableInfo){
        doScanField(fieldInfo, configuration, tableInfo);
    }

    /**
     * 自定义扫描字段,以扩展字段信息
     * @param fieldInfo 字段信息
     * @param configuration mybatis配置
     * @param tableInfo 待完善的TableInfo
     */
    protected abstract void doScanField(TableFieldInfo fieldInfo, Configuration configuration, T tableInfo);

    @Override
    public T build(MapperBuilderAssistant builderAssistant, Class<?> clazz){
        T tableInfo = createTableInfo(clazz);
        scanTable(builderAssistant, clazz, tableInfo);
        return tableInfo;
    }

}
