/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

/**
 * <p>
 * 实体类反射表辅助类
 * </p>
 *
 * @author hubin sjy
 * @since 2016-09-09
 */
public class TableInfoHelper {

    private static final Log logger = LogFactory.getLog(TableInfoHelper.class);

    /**
     * 储存反射类表信息
     */
    private static final Map<Class<?>, TableInfo> TABLE_INFO_CACHE = new ConcurrentHashMap<>();

    /**
     * 默认表主键名称
     */
    private static final String DEFAULT_ID_NAME = "id";

    /**
     * <p>
     * 获取实体映射表信息
     * <p>
     *
     * @param clazz 反射实体类
     * @return 数据库表反射信息
     */
    public static TableInfo getTableInfo(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        TableInfo tableInfo = TABLE_INFO_CACHE.get(ClassUtils.getUserClass(clazz));
        if (null != tableInfo) {
            return tableInfo;
        }
        //尝试获取父类缓存
        Class currentClass = clazz;
        while (null == tableInfo && Object.class != currentClass) {
            currentClass = currentClass.getSuperclass();
            tableInfo = TABLE_INFO_CACHE.get(ClassUtils.getUserClass(currentClass));
        }
        if (tableInfo != null) {
            TABLE_INFO_CACHE.put(ClassUtils.getUserClass(clazz), tableInfo);
        }
        return tableInfo;
    }

    /**
     * <p>
     * 获取所有实体映射表信息
     * <p>
     *
     * @return 数据库表反射信息集合
     */
    public static List<TableInfo> getTableInfos() {
        return new ArrayList<>(TABLE_INFO_CACHE.values());
    }

    /**
     * <p>
     * 实体类反射获取表信息【初始化】
     * <p>
     *
     * @param clazz 反射实体类
     * @return 数据库表反射信息
     */
    public synchronized static TableInfo initTableInfo(MapperBuilderAssistant builderAssistant, Class<?> clazz) {
        TableInfo tableInfo = TABLE_INFO_CACHE.get(clazz);
        if (tableInfo != null) {
            if (tableInfo.getConfigMark() == null && builderAssistant != null) {
                tableInfo.setConfigMark(builderAssistant.getConfiguration());
            }
            return tableInfo;
        }

        /* 没有获取到缓存信息,则初始化 */
        tableInfo = new TableInfo();
        GlobalConfig globalConfig;
        if (null != builderAssistant) {
            tableInfo.setCurrentNamespace(builderAssistant.getCurrentNamespace());
            tableInfo.setConfigMark(builderAssistant.getConfiguration());
            tableInfo.setUnderCamel(builderAssistant.getConfiguration().isMapUnderscoreToCamelCase());
            globalConfig = GlobalConfigUtils.getGlobalConfig(builderAssistant.getConfiguration());
        } else {
            // 兼容测试场景
            globalConfig = GlobalConfigUtils.defaults();
        }

        /* 初始化表名相关 */
        initTableName(clazz, globalConfig, tableInfo);

        /* 初始化字段相关 */
        initTableFields(clazz, globalConfig, tableInfo);

        /* 放入缓存 */
        TABLE_INFO_CACHE.put(clazz, tableInfo);

        /* 缓存 Lambda 映射关系 */
        LambdaUtils.createCache(clazz, tableInfo);
        return tableInfo;
    }

    /**
     * <p>
     * 初始化 表数据库类型,表名,resultMap
     * </p>
     *
     * @param clazz        实体类
     * @param globalConfig 全局配置
     * @param tableInfo    数据库表反射信息
     */
    public static void initTableName(Class<?> clazz, GlobalConfig globalConfig, TableInfo tableInfo) {
        /* 数据库全局配置 */
        GlobalConfig.DbConfig dbConfig = globalConfig.getDbConfig();
        /* 设置数据库类型 */
        tableInfo.setDbType(dbConfig.getDbType());

        /* 设置表名 */
        TableName table = clazz.getAnnotation(TableName.class);
        String tableName = clazz.getSimpleName();
        if (table != null && StringUtils.isNotEmpty(table.value())) {
            tableName = table.value();
        } else {
            // 开启表名下划线申明
            if (dbConfig.isTableUnderline()) {
                tableName = StringUtils.camelToUnderline(tableName);
            }
            // 大写命名判断
            if (dbConfig.isCapitalMode()) {
                tableName = tableName.toUpperCase();
            } else {
                // 首字母小写
                tableName = StringUtils.firstToLowerCase(tableName);
            }
            // 存在表名前缀
            if (null != dbConfig.getTablePrefix()) {
                tableName = dbConfig.getTablePrefix() + tableName;
            }
        }
        tableInfo.setTableName(tableName);

        /* 表结果集映射 */
        if (table != null && StringUtils.isNotEmpty(table.resultMap())) {
            tableInfo.setResultMap(table.resultMap());
        }

        /* 开启了自定义 KEY 生成器 */
        if (null != dbConfig.getKeyGenerator()) {
            tableInfo.setKeySequence(clazz.getAnnotation(KeySequence.class));
        }
    }

    /**
     * <p>
     * 初始化 表主键,表字段
     * </p>
     *
     * @param clazz        实体类
     * @param globalConfig 全局配置
     * @param tableInfo    数据库表反射信息
     */
    public static void initTableFields(Class<?> clazz, GlobalConfig globalConfig, TableInfo tableInfo) {
        /* 数据库全局配置 */
        GlobalConfig.DbConfig dbConfig = globalConfig.getDbConfig();
        List<Field> list = getAllFields(clazz);
        // 标记是否读取到主键
        boolean isReadPK = false;
        // 是否存在 @TableId 注解
        boolean existTableId = isExistTableId(list);

        List<TableFieldInfo> fieldList = new ArrayList<>();
        for (Field field : list) {
            /*
             * 主键ID 初始化
             */
            if (!isReadPK) {
                if (existTableId) {
                    isReadPK = initTableIdWithAnnotation(dbConfig, tableInfo, field, clazz);
                } else {
                    isReadPK = initTableIdWithoutAnnotation(dbConfig, tableInfo, field, clazz);
                }
                if (isReadPK) {
                    continue;
                }
            }
            /* 有 @TableField 注解的字段初始化 */
            if (initTableFieldWithAnnotation(dbConfig, tableInfo, fieldList, field, clazz)) {
                continue;
            }

            /* 无 @TableField 注解的字段初始化 */
            fieldList.add(new TableFieldInfo(dbConfig, tableInfo, field));
        }

        /* 检查逻辑删除字段只能有最多一个 */
        Assert.isTrue(fieldList.parallelStream().filter(TableFieldInfo::isLogicDelete).count() < 2L,
            String.format("annotation of @TableLogic can't more than one in class : %s.", clazz.getName()));

        /* 字段列表 */
        tableInfo.setFieldList(fieldList);

        /* 未发现主键注解，提示警告信息 */
        if (StringUtils.isEmpty(tableInfo.getKeyColumn())) {
            logger.warn(String.format("Warn: Could not find @TableId in Class: %s.", clazz.getName()));
        }
    }

    /**
     * <p>
     * 判断主键注解是否存在
     * </p>
     *
     * @param list 字段列表
     * @return true 为存在 @TableId 注解;
     */
    public static boolean isExistTableId(List<Field> list) {
        for (Field field : list) {
            TableId tableId = field.getAnnotation(TableId.class);
            if (tableId != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>
     * 主键属性初始化
     * </p>
     *
     * @param dbConfig  全局配置信息
     * @param tableInfo 表信息
     * @param field     字段
     * @param clazz     实体类
     * @return true 继续下一个属性判断，返回 continue;
     */
    private static boolean initTableIdWithAnnotation(GlobalConfig.DbConfig dbConfig, TableInfo tableInfo,
                                                     Field field, Class<?> clazz) {
        TableId tableId = field.getAnnotation(TableId.class);
        boolean underCamel = tableInfo.isUnderCamel();
        if (tableId != null) {
            if (StringUtils.isEmpty(tableInfo.getKeyColumn())) {
                /* 主键策略（ 注解 > 全局 ） */
                // 设置 Sequence 其他策略无效
                if (IdType.NONE == tableId.type()) {
                    tableInfo.setIdType(dbConfig.getIdType());
                } else {
                    tableInfo.setIdType(tableId.type());
                }

                /* 字段 */
                String column = field.getName();
                if (StringUtils.isNotEmpty(tableId.value())) {
                    column = tableId.value();
                } else {
                    // 开启字段下划线申明
                    if (underCamel) {
                        column = StringUtils.camelToUnderline(column);
                    }
                    // 全局大写命名
                    if (dbConfig.isCapitalMode()) {
                        column = column.toUpperCase();
                    }
                }
                tableInfo.setKeyRelated(checkRelated(underCamel, field.getName(), column))
                    .setClazz(field.getDeclaringClass())
                    .setKeyColumn(column)
                    .setKeyProperty(field.getName());
                return true;
            } else {
                throwExceptionId(clazz);
            }
        }
        return false;
    }

    /**
     * <p>
     * 主键属性初始化
     * </p>
     *
     * @param tableInfo 表信息
     * @param field     字段
     * @param clazz     实体类
     * @return true 继续下一个属性判断，返回 continue;
     */
    private static boolean initTableIdWithoutAnnotation(GlobalConfig.DbConfig dbConfig, TableInfo tableInfo,
                                                        Field field, Class<?> clazz) {
        String column = field.getName();
        if (dbConfig.isCapitalMode()) {
            column = column.toUpperCase();
        }
        if (DEFAULT_ID_NAME.equalsIgnoreCase(column)) {
            if (StringUtils.isEmpty(tableInfo.getKeyColumn())) {
                tableInfo.setKeyRelated(checkRelated(tableInfo.isUnderCamel(), field.getName(), column))
                    .setIdType(dbConfig.getIdType())
                    .setKeyColumn(column)
                    .setKeyProperty(field.getName())
                    .setClazz(field.getDeclaringClass());
                return true;
            } else {
                throwExceptionId(clazz);
            }
        }
        return false;
    }

    /**
     * <p>
     * 字段属性初始化
     * </p>
     *
     * @param dbConfig  数据库全局配置
     * @param tableInfo 表信息
     * @param fieldList 字段列表
     * @param clazz     当前表对象类
     * @return true 继续下一个属性判断，返回 continue;
     */
    private static boolean initTableFieldWithAnnotation(GlobalConfig.DbConfig dbConfig, TableInfo tableInfo,
                                                        List<TableFieldInfo> fieldList, Field field, Class<?> clazz) {
        /* 获取注解属性，自定义字段 */
        TableField tableField = field.getAnnotation(TableField.class);
        if (null == tableField) {
            return false;
        }
        String columnName = field.getName();
        if (StringUtils.isNotEmpty(tableField.value())) {
            columnName = tableField.value();
        }
        /*
         * el 语法支持，可以传入多个参数以逗号分开
         */
        String el = field.getName();
        if (StringUtils.isNotEmpty(tableField.el())) {
            el = tableField.el();
        }
        String[] columns = columnName.split(StringPool.SEMICOLON);
        String[] els = el.split(StringPool.SEMICOLON);
        if (columns.length == els.length) {
            for (int i = 0; i < columns.length; i++) {
                fieldList.add(new TableFieldInfo(dbConfig, tableInfo, field, columns[i], els[i], tableField));
            }
            return true;
        }
        throw ExceptionUtils.mpe(String.format("Class: %s, Field: %s, 'value' 'el' Length must be consistent.",
            clazz.getName(), field.getName()));
    }

    /**
     * <p>
     * 判定 related 的值
     * </p>
     *
     * @param underCamel 驼峰命名
     * @param property   属性名
     * @param column     字段名
     * @return related
     */
    public static boolean checkRelated(boolean underCamel, String property, String column) {
        if (StringUtils.isNotColumnName(column)) {
            // 首尾有转义符,手动在注解里设置了转义符,去除掉转义符
            column = column.substring(1, column.length() - 1);
        }
        if (underCamel && column.contains(StringPool.UNDERSCORE)) {
            // 开启了驼峰并且 column 包含下划线
            return !property.toUpperCase(Locale.ENGLISH).equals(
                column.replace(StringPool.UNDERSCORE, StringPool.EMPTY).toUpperCase(Locale.ENGLISH));
        } else {
            // 未开启驼峰,直接判断 property 是否与 column 相同(全大写)
            return !property.toUpperCase(Locale.ENGLISH).equals(column.toUpperCase(Locale.ENGLISH));
        }
    }

    /**
     * <p>
     * 发现设置多个主键注解抛出异常
     * </p>
     */
    private static void throwExceptionId(Class<?> clazz) {
        throw ExceptionUtils.mpe("There must be only one, Discover multiple @TableId annotation in " +
            clazz.getName());
    }

    /**
     * 获取该类的所有属性列表
     *
     * @param clazz 反射类
     * @return 属性集合
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fieldList = ReflectionKit.getFieldList(ClassUtils.getUserClass(clazz));
        if (CollectionUtils.isNotEmpty(fieldList)) {
            return fieldList.stream()
                .filter(i -> {
                    /* 过滤注解非表字段属性 */
                    TableField tableField = i.getAnnotation(TableField.class);
                    return (tableField == null || tableField.exist());
                }).collect(toList());
        }
        return fieldList;
    }

    /**
     * <p>
     * 自定义 KEY 生成器
     * </p>
     */
    public static KeyGenerator genKeyGenerator(TableInfo tableInfo, MapperBuilderAssistant builderAssistant,
                                               String baseStatementId, LanguageDriver languageDriver) {
        IKeyGenerator keyGenerator = GlobalConfigUtils.getKeyGenerator(builderAssistant.getConfiguration());
        if (null == keyGenerator) {
            throw new IllegalArgumentException("not configure IKeyGenerator implementation class.");
        }
        String id = baseStatementId + SelectKeyGenerator.SELECT_KEY_SUFFIX;
        Class<?> resultTypeClass = tableInfo.getKeySequence().clazz();
        StatementType statementType = StatementType.PREPARED;
        String keyProperty = tableInfo.getKeyProperty();
        String keyColumn = tableInfo.getKeyColumn();
        SqlSource sqlSource = languageDriver.createSqlSource(builderAssistant.getConfiguration(),
            keyGenerator.executeSql(tableInfo.getKeySequence().value()), null);
        builderAssistant.addMappedStatement(id, sqlSource, statementType, SqlCommandType.SELECT, null, null, null,
            null, null, resultTypeClass, null, false, false, false,
            new NoKeyGenerator(), keyProperty, keyColumn, null, languageDriver, null);
        id = builderAssistant.applyCurrentNamespace(id, false);
        MappedStatement keyStatement = builderAssistant.getConfiguration().getMappedStatement(id, false);
        SelectKeyGenerator selectKeyGenerator = new SelectKeyGenerator(keyStatement, true);
        builderAssistant.getConfiguration().addKeyGenerator(id, selectKeyGenerator);
        return selectKeyGenerator;
    }
}
