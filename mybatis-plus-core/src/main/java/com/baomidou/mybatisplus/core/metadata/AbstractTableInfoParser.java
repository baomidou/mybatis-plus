package com.baomidou.mybatisplus.core.metadata;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.session.Configuration;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * TableInfo 解析器
 *
 * @author jeff
 * @since 2020/9/8
 */
@Slf4j
public abstract class AbstractTableInfoParser<T extends TableInfo> implements TableInfoParser<T> {

    /**
     * 创建TableInfo实例
     * @param clazz TableInfo对应的类
     * @return TableInfo本身或者子类
     */
    protected abstract T createTableInfo(Class<?> clazz);

    /**
     * 创建字段
     * @param globalConfig 全局配置
     * @param tableInfo 表信息
     * @param field 反射字段
     * @param reflector mybatis内置反射缓存对象
     * @param existTableLogic 是否有逻辑删除注解
     * @return 字段信息
     */
    protected TableFieldInfo createTableFieldInfo(GlobalConfig globalConfig, TableInfo tableInfo, Field field,
                                              Reflector reflector, boolean existTableLogic){
        GlobalConfig.DbConfig dbConfig = globalConfig.getDbConfig();
        TableField tableField = field.getAnnotation(TableField.class);
        if(tableField != null){
            return new TableFieldInfo(dbConfig, tableInfo, field, tableField, reflector, existTableLogic);
        }
        else{
            return new TableFieldInfo(dbConfig, tableInfo, field, reflector, existTableLogic);
        }
    }

    /**
     * 扫描 实体类, 补充 TableInfo
     * @param clazz 反射实体类
     * @param tableInfo 待完善的TableInfo
     */
    private void scanTable(MapperBuilderAssistant builderAssistant, Class<?> clazz, T tableInfo){
        String currentNamespace = builderAssistant.getCurrentNamespace();
        Configuration configuration = builderAssistant.getConfiguration();

        tableInfo.setCurrentNamespace(currentNamespace);
        tableInfo.setConfiguration(configuration);
        GlobalConfig globalConfig = GlobalConfigUtils.getGlobalConfig(configuration);

        /* 初始化表名相关 */
        final String[] excludeProperty = TableInfoHelper.initTableName(clazz, globalConfig, tableInfo);
        List<String> excludePropertyList = excludeProperty != null && excludeProperty.length > 0 ? Arrays.asList(excludeProperty) : Collections.emptyList();
        List<Field> fields = TableInfoHelper.getAllFields(clazz);

        doBeforeScanTable(clazz, globalConfig, tableInfo, fields);

        List<TableFieldInfo> tableFieldInfos;
        if(CollectionUtils.isNotEmpty(fields)){
            tableFieldInfos = processFields(globalConfig, clazz, configuration, excludePropertyList, tableInfo, fields);
        }
        else{
            tableFieldInfos = new ArrayList<>();
        }

        doAfterScanTable(clazz, globalConfig, tableInfo, tableFieldInfos);

        /* 自动构建 resultMap */
        tableInfo.initResultMapIfNeed();

        /* 缓存 lambda */
        LambdaUtils.installCache(tableInfo);
    }

    private List<TableFieldInfo> processFields(GlobalConfig globalConfig, Class<?> clazz, Configuration configuration, List<String> excludePropertyList, T tableInfo, List<Field> fields){
        GlobalConfig.DbConfig dbConfig = globalConfig.getDbConfig();
        ReflectorFactory reflectorFactory = configuration.getReflectorFactory();
        //TODO @咩咩 有空一起来撸完这反射模块.
        Reflector reflector = reflectorFactory.findForClass(clazz);

        // 标记是否读取到主键
        boolean isReadPk = false;
        // 是否存在 @TableId 注解
        boolean existTableId = TableInfoHelper.isExistTableId(fields);
        // 是否存在 @TableLogic 注解
        boolean existTableLogic = TableInfoHelper.isExistTableLogic(fields);

        List<TableFieldInfo> fieldList = new ArrayList<>(fields.size());
        for (Field field : fields){
            doBeforeScanField(field, tableInfo);
            if (excludePropertyList.contains(field.getName())) {
                continue;
            }

            /* 主键ID 初始化 */
            if (existTableId) {
                TableId tableId = field.getAnnotation(TableId.class);
                if (tableId != null) {
                    if (isReadPk) {
                        throw ExceptionUtils.mpe("@TableId can't more than one in Class: \"%s\".", clazz.getName());
                    } else {
                        TableInfoHelper.initTableIdWithAnnotation(dbConfig, tableInfo, field, tableId, reflector);
                        isReadPk = true;
                        continue;
                    }
                }
            } else if (!isReadPk) {
                isReadPk = TableInfoHelper.initTableIdWithoutAnnotation(dbConfig, tableInfo, field, reflector);
                if (isReadPk) {
                    continue;
                }
            }

            TableFieldInfo tableFieldInfo = scanField(globalConfig, tableInfo, field, reflector, existTableLogic);
            fieldList.add(tableFieldInfo);
        }

        /* 字段列表 */
        tableInfo.setFieldList(fieldList);

        /* 未发现主键注解，提示警告信息 */
        if (!isReadPk) {
            log.warn(String.format("Can not find table primary key in Class: \"%s\".", clazz.getName()));
        }
        return fieldList;
    }

    /**
     * 扩展扫描实体类,来自定义构建TableInfo
     * @param clazz 反射实体类
     * @param globalConfig 全局配置
     * @param tableInfo 待完善的TableInfo
     * @param fields 实体类的表反射字段集合
     */
    protected abstract void doBeforeScanTable(Class<?> clazz, GlobalConfig globalConfig, T tableInfo, List<Field> fields);



    /**
     * 扫描字段
     * @param field 字段
     * @param globalConfig mybatis配置
     * @param tableInfo 待完善的TableInfo
     */
    private TableFieldInfo scanField(GlobalConfig globalConfig, T tableInfo, Field field,
                        Reflector reflector, boolean existTableLogic){
        TableFieldInfo tableInfoField = createTableFieldInfo(globalConfig, tableInfo, field, reflector, existTableLogic);

        doAfterScanField(tableInfoField, tableInfo);
        return tableInfoField;
    }

    /**
     * 字段处理前执行
     * @param field 字段
     * @param tableInfo 待完善的TableInfo
     */
    protected abstract void doBeforeScanField(Field field, T tableInfo);

    /**
     * 字段处理后执行
     * @param fieldInfo 字段信息
     * @param tableInfo 待完善的TableInfo
     */
    protected abstract void doAfterScanField(TableFieldInfo fieldInfo, T tableInfo);

    /**
     * 表扫描后执行
     * @param clazz 类
     * @param globalConfig 全局配置
     * @param tableInfo 表信息
     * @param tableFieldInfoList 表字段信息集合
     */
    protected abstract void doAfterScanTable(Class<?> clazz, GlobalConfig globalConfig, T tableInfo, List<TableFieldInfo> tableFieldInfoList);

    @Override
    public T build(MapperBuilderAssistant builderAssistant, Class<?> clazz){
        T tableInfo = createTableInfo(clazz);
        scanTable(builderAssistant, clazz, tableInfo);
        return tableInfo;
    }

}
