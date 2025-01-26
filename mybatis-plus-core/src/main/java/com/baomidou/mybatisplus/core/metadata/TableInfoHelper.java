/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.metadata;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.AnnotationHandler;
import com.baomidou.mybatisplus.core.handlers.PostInitTableInfoHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.toolkit.*;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.parsing.PropertyParser;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.SimpleTypeRegistry;

import java.lang.reflect.Field;
import java.util.*;
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
     * 储存表名对应的反射类表信息
     */
    private static final Map<String, TableInfo> TABLE_NAME_INFO_CACHE = new ConcurrentHashMap<>();

    /**
     * 默认表主键名称
     */
    private static final String DEFAULT_ID_NAME = "id";

    /**
     * <p>
     * 获取实体映射表信息
     * </p>
     *
     * @param clazz 反射实体类
     * @return 数据库表反射信息
     */
    public static TableInfo getTableInfo(Class<?> clazz) {
        if (clazz == null || clazz.isPrimitive() || SimpleTypeRegistry.isSimpleType(clazz) || clazz.isInterface()) {
            return null;
        }
        // https://github.com/baomidou/mybatis-plus/issues/299
        Class<?> targetClass = ClassUtils.getUserClass(clazz);
        TableInfo tableInfo = TABLE_INFO_CACHE.get(targetClass);
        if (null != tableInfo) {
            return tableInfo;
        }
        //尝试获取父类缓存
        Class<?> currentClass = clazz;
        while (null == tableInfo && Object.class != currentClass) {
            currentClass = currentClass.getSuperclass();
            tableInfo = TABLE_INFO_CACHE.get(ClassUtils.getUserClass(currentClass));
        }
        //把父类的移到子类中来
        if (tableInfo != null) {
            TABLE_INFO_CACHE.put(targetClass, tableInfo);
        }
        return tableInfo;
    }

    /**
     * <p>
     * 根据表名获取实体映射表信息
     * </p>
     *
     * @param tableName 表名
     * @return 数据库表反射信息
     */
    public static TableInfo getTableInfo(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            return null;
        }
        return TABLE_NAME_INFO_CACHE.get(tableName);
    }

    /**
     * <p>
     * 获取所有实体映射表信息
     * </p>
     *
     * @return 数据库表反射信息集合
     */
    public static List<TableInfo> getTableInfos() {
        return Collections.unmodifiableList(new ArrayList<>(TABLE_INFO_CACHE.values()));
    }

    /**
     * 清空实体表映射缓存信息
     *
     * @param entityClass 实体 Class
     */
    public static void remove(Class<?> entityClass) {
        TABLE_INFO_CACHE.remove(entityClass);
    }

    /**
     * <p>
     * 实体类反射获取表信息【初始化】
     * </p>
     *
     * @param clazz 反射实体类
     * @return 数据库表反射信息
     */
    public static synchronized TableInfo initTableInfo(MapperBuilderAssistant builderAssistant, Class<?> clazz) {
        TableInfo targetTableInfo = TABLE_INFO_CACHE.get(clazz);
        final Configuration configuration = builderAssistant.getConfiguration();
        if (targetTableInfo != null) {
            Configuration oldConfiguration = targetTableInfo.getConfiguration();
            if (!oldConfiguration.equals(configuration)) {
                // 不是同一个 Configuration,进行重新初始化
                targetTableInfo = initTableInfo(configuration, builderAssistant.getCurrentNamespace(), clazz);
            }
            return targetTableInfo;
        }
        return initTableInfo(configuration, builderAssistant.getCurrentNamespace(), clazz);
    }

    /**
     * <p>
     * 实体类反射获取表信息【初始化】
     * </p>
     *
     * @param clazz 反射实体类
     * @return 数据库表反射信息
     */
    private static synchronized TableInfo initTableInfo(Configuration configuration, String currentNamespace, Class<?> clazz) {
        GlobalConfig globalConfig = GlobalConfigUtils.getGlobalConfig(configuration);
        PostInitTableInfoHandler postInitTableInfoHandler = globalConfig.getPostInitTableInfoHandler();
        /* 没有获取到缓存信息,则初始化 */
        TableInfo tableInfo = postInitTableInfoHandler.creteTableInfo(configuration, clazz);
        tableInfo.setCurrentNamespace(currentNamespace);

        /* 初始化表名相关 */
        PropertySelector propertySelector = initTableName(clazz, globalConfig, tableInfo);

        /* 初始化字段相关 */
        initTableFields(configuration, clazz, globalConfig, tableInfo, propertySelector);

        /* 自动构建 resultMap */
        tableInfo.initResultMapIfNeed();
        postInitTableInfoHandler.postTableInfo(tableInfo, configuration);
        TABLE_INFO_CACHE.put(clazz, tableInfo);
        TABLE_NAME_INFO_CACHE.put(tableInfo.getTableName(), tableInfo);

        /* 缓存 lambda */
        LambdaUtils.installCache(tableInfo);
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
     * @return 需要排除的字段名
     */
    private static PropertySelector initTableName(Class<?> clazz, GlobalConfig globalConfig, TableInfo tableInfo) {
        /* 数据库全局配置 */
        GlobalConfig.DbConfig dbConfig = globalConfig.getDbConfig();
        AnnotationHandler annotationHandler = globalConfig.getAnnotationHandler();
        TableName table = annotationHandler.getAnnotation(clazz, TableName.class);
        Configuration configuration = tableInfo.getConfiguration();
        String tableName = clazz.getSimpleName();
        String tablePrefix = dbConfig.getTablePrefix();
        String schema = dbConfig.getSchema();
        boolean tablePrefixEffect = true;
        PropertySelector propertySelector = i -> true;
        if (table != null) {
            if (StringUtils.isNotBlank(table.value())) {
                tableName = PropertyParser.parse(table.value(), configuration.getVariables());
                if (StringUtils.isNotBlank(tablePrefix) && !table.keepGlobalPrefix()) {
                    tablePrefixEffect = false;
                }
            } else {
                tableName = initTableNameWithDbConfig(tableName, dbConfig);
            }
            if (StringUtils.isNotBlank(table.schema())) {
                schema = PropertyParser.parse(table.schema(), configuration.getVariables());
            }
            /* 表结果集映射 */
            if (StringUtils.isNotBlank(table.resultMap())) {
                tableInfo.setResultMap(table.resultMap());
            }
            tableInfo.setAutoInitResultMap(table.autoResultMap());
            String[] ep = table.excludeProperty();
            String[] ip = table.properties();
            if (ArrayUtils.isNotEmpty(ip)) {
                List<String> list = Arrays.asList(ip);
                propertySelector = list::contains;
            } else if (ArrayUtils.isNotEmpty(ep)) {
                List<String> list = Arrays.asList(ep);
                propertySelector = i -> !list.contains(i);
            }
        } else {
            tableName = initTableNameWithDbConfig(tableName, dbConfig);
        }

        // 表追加前缀
        String targetTableName = tableName;
        if (StringUtils.isNotBlank(tablePrefix) && tablePrefixEffect) {
            targetTableName = tablePrefix + targetTableName;
        }

        // 表格式化
        String tableFormat = dbConfig.getTableFormat();
        if (StringUtils.isNotBlank(tableFormat)) {
            targetTableName = String.format(tableFormat, targetTableName);
        }

        // 表追加 schema 信息
        if (StringUtils.isNotBlank(schema)) {
            targetTableName = schema + StringPool.DOT + targetTableName;
        }

        tableInfo.setTableName(targetTableName);

        /* 开启了自定义 KEY 生成器 */
        if (CollectionUtils.isNotEmpty(dbConfig.getKeyGenerators())) {
            tableInfo.setKeySequence(annotationHandler.getAnnotation(clazz, KeySequence.class));
        }
        return propertySelector;
    }

    /**
     * 根据 DbConfig 初始化 表名
     *
     * @param className 类名
     * @param dbConfig  DbConfig
     * @return 表名
     */
    private static String initTableNameWithDbConfig(String className, GlobalConfig.DbConfig dbConfig) {
        String tableName = className;
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
        return tableName;
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
    private static void initTableFields(Configuration configuration, Class<?> clazz, GlobalConfig globalConfig,
                                        TableInfo tableInfo, PropertySelector propertySelector) {
        AnnotationHandler annotationHandler = globalConfig.getAnnotationHandler();
        PostInitTableInfoHandler postInitTableInfoHandler = globalConfig.getPostInitTableInfoHandler();
        Reflector reflector = tableInfo.getReflector();
        List<Field> list = getAllFields(clazz, annotationHandler);
        // 标记是否读取到主键
        boolean isReadPK = false;
        // 是否存在 @TableId 注解
        boolean existTableId = isExistTableId(list, annotationHandler);
        // 是否存在 @TableLogic 注解
        boolean existTableLogic = isExistTableLogic(list, annotationHandler);

        List<TableFieldInfo> fieldList = new ArrayList<>(list.size());
        for (Field field : list) {
            if (!propertySelector.selection(field.getName())) {
                continue;
            }

            boolean isPK = false;
            OrderBy orderBy = annotationHandler.getAnnotation(field, OrderBy.class);
            boolean isOrderBy = orderBy != null;
            /* 主键ID 初始化 */
            if (existTableId) {
                TableId tableId = annotationHandler.getAnnotation(field, TableId.class);
                if (tableId != null) {
                    if (isReadPK) {
                        throw ExceptionUtils.mpe("@TableId can't more than one in Class: \"%s\".", clazz.getName());
                    }

                    initTableIdWithAnnotation(globalConfig, tableInfo, field, tableId);
                    isPK = isReadPK = true;
                }
            } else if (!isReadPK) {
                isPK = isReadPK = initTableIdWithoutAnnotation(globalConfig, tableInfo, field);
            }

            if (isPK) {
                if (orderBy != null) {
                    tableInfo.getOrderByFields().add(new OrderFieldInfo(tableInfo.getKeyColumn(), orderBy.asc(), orderBy.sort()));
                }
                continue;
            }
            final TableField tableField = annotationHandler.getAnnotation(field, TableField.class);

            /* 有 @TableField 注解的字段初始化 */
            if (tableField != null) {
                TableFieldInfo tableFieldInfo = new TableFieldInfo(globalConfig, tableInfo, field, tableField, reflector, existTableLogic, isOrderBy);
                fieldList.add(tableFieldInfo);
                postInitTableInfoHandler.postFieldInfo(tableFieldInfo, configuration);
                continue;
            }

            /* 无 @TableField  注解的字段初始化 */
            TableFieldInfo tableFieldInfo = new TableFieldInfo(globalConfig, tableInfo, field, reflector, existTableLogic, isOrderBy);
            fieldList.add(tableFieldInfo);
            postInitTableInfoHandler.postFieldInfo(tableFieldInfo, configuration);
        }

        /* 字段列表 */
        tableInfo.setFieldList(fieldList);

        /* 未发现主键注解，提示警告信息 */
        if (!isReadPK) {
            logger.warn(String.format("Can not find table primary key in Class: \"%s\".", clazz.getName()));
        }
    }

    /**
     * <p>
     * 判断主键注解是否存在
     * </p>
     *
     * @param clazz 实体类
     * @param list  字段列表
     * @return true 为存在 {@link TableId} 注解;
     */
    public static boolean isExistTableId(Class<?> clazz, List<Field> list) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        AnnotationHandler annotationHandler = GlobalConfigUtils.getGlobalConfig(tableInfo.getConfiguration()).getAnnotationHandler();
        return isExistTableId(list, annotationHandler);
    }

    /**
     * <p>
     * 判断主键注解是否存在
     * </p>
     *
     * @param list 字段列表
     * @return true 为存在 {@link TableId} 注解;
     */
    public static boolean isExistTableId(List<Field> list, AnnotationHandler annotationHandler) {
        return list.stream().anyMatch(field -> annotationHandler.isAnnotationPresent(field, TableId.class));
    }

    /**
     * <p>
     * 判断逻辑删除注解是否存在
     * </p>
     *
     * @param clazz 实体类
     * @param list  字段列表
     * @return true 为存在 {@link TableLogic} 注解;
     */
    public static boolean isExistTableLogic(Class<?> clazz, List<Field> list) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        AnnotationHandler annotationHandler = GlobalConfigUtils.getGlobalConfig(tableInfo.getConfiguration()).getAnnotationHandler();
        return isExistTableLogic(list, annotationHandler);
    }

    /**
     * <p>
     * 判断逻辑删除注解是否存在
     * </p>
     *
     * @param list 字段列表
     * @return true 为存在 {@link TableLogic} 注解;
     */
    public static boolean isExistTableLogic(List<Field> list, AnnotationHandler annotationHandler) {
        return list.stream().anyMatch(field -> annotationHandler.isAnnotationPresent(field, TableLogic.class));
    }

    /**
     * <p>
     * 判断排序注解是否存在
     * </p>
     *
     * @param clazz 实体类
     * @param list  字段列表
     * @return true 为存在 {@link OrderBy} 注解;
     */
    public static boolean isExistOrderBy(Class<?> clazz, List<Field> list) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        AnnotationHandler annotationHandler = GlobalConfigUtils.getGlobalConfig(tableInfo.getConfiguration()).getAnnotationHandler();
        return isExistOrderBy(list, annotationHandler);
    }

    /**
     * <p>
     * 判断排序注解是否存在
     * </p>
     *
     * @param list              字段列表
     * @param annotationHandler 注解处理类
     * @return true 为存在 {@link OrderBy} 注解;
     */
    public static boolean isExistOrderBy(List<Field> list, AnnotationHandler annotationHandler) {
        return list.stream().anyMatch(field -> annotationHandler.isAnnotationPresent(field, OrderBy.class));
    }

    /**
     * <p>
     * 主键属性初始化
     * </p>
     *
     * @param globalConfig 全局配置信息
     * @param tableInfo    表信息
     * @param field        字段
     * @param tableId      注解
     */
    private static void initTableIdWithAnnotation(GlobalConfig globalConfig, TableInfo tableInfo, Field field, TableId tableId) {
        GlobalConfig.DbConfig dbConfig = globalConfig.getDbConfig();
        boolean underCamel = tableInfo.isUnderCamel();
        final String property = field.getName();
        if (globalConfig.getAnnotationHandler().isAnnotationPresent(field, TableField.class)) {
            logger.warn(String.format("This \"%s\" is the table primary key by @TableId annotation in Class: \"%s\",So @TableField annotation will not work!",
                property, tableInfo.getEntityType().getName()));
        }
        /* 主键策略（ 注解 > 全局 ） */
        // 设置 Sequence 其他策略无效
        if (IdType.NONE == tableId.type()) {
            tableInfo.setIdType(dbConfig.getIdType());
        } else {
            tableInfo.setIdType(tableId.type());
        }

        /* 字段 */
        String column = property;
        if (StringUtils.isNotBlank(tableId.value())) {
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
        final Class<?> keyType = tableInfo.getReflector().getGetterType(property);
        if (keyType.isPrimitive()) {
            logger.warn(String.format("This primary key of \"%s\" is primitive !不建议如此请使用包装类 in Class: \"%s\"",
                property, tableInfo.getEntityType().getName()));
        }
        if (StringUtils.isEmpty(tableId.value())) {
            String columnFormat = dbConfig.getColumnFormat();
            if (StringUtils.isNotBlank(columnFormat)) {
                column = String.format(columnFormat, column);
            }
        }
        tableInfo.setKeyRelated(checkRelated(underCamel, property, column))
            .setKeyColumn(column)
            .setKeyProperty(property)
            .setKeyType(keyType);
    }

    /**
     * <p>
     * 主键属性初始化
     * </p>
     *
     * @param globalConfig 全局配置
     * @param tableInfo    表信息
     * @param field        字段
     * @return true 继续下一个属性判断，返回 continue;
     */
    private static boolean initTableIdWithoutAnnotation(GlobalConfig globalConfig, TableInfo tableInfo, Field field) {
        GlobalConfig.DbConfig dbConfig = globalConfig.getDbConfig();
        final String property = field.getName();
        if (DEFAULT_ID_NAME.equalsIgnoreCase(property)) {
            if (globalConfig.getAnnotationHandler().isAnnotationPresent(field, TableField.class)) {
                logger.warn(String.format("This \"%s\" is the table primary key by default name for `id` in Class: \"%s\",So @TableField will not work!",
                    property, tableInfo.getEntityType().getName()));
            }
            String column = property;
            if (dbConfig.isCapitalMode()) {
                column = column.toUpperCase();
            }
            final Class<?> keyType = tableInfo.getReflector().getGetterType(property);
            if (keyType.isPrimitive()) {
                logger.warn(String.format("This primary key of \"%s\" is primitive !不建议如此请使用包装类 in Class: \"%s\"",
                    property, tableInfo.getEntityType().getName()));
            }
            String columnFormat = dbConfig.getColumnFormat();
            if (StringUtils.isNotBlank(columnFormat)) {
                column = String.format(columnFormat, column);
            }
            tableInfo.setKeyRelated(checkRelated(tableInfo.isUnderCamel(), property, column))
                .setIdType(dbConfig.getIdType())
                .setKeyColumn(column)
                .setKeyProperty(property)
                .setKeyType(keyType);
            return true;
        }
        return false;
    }

    /**
     * 判定 related 的值
     * <p>
     * 为 true 表示不符合规则
     *
     * @param underCamel 驼峰命名
     * @param property   属性名
     * @param column     字段名
     * @return related
     */
    public static boolean checkRelated(boolean underCamel, String property, String column) {
        column = StringUtils.getTargetColumn(column);
        String propertyUpper = property.toUpperCase(Locale.ENGLISH);
        String columnUpper = column.toUpperCase(Locale.ENGLISH);
        if (underCamel) {
            // 开启了驼峰并且 column 包含下划线
            return !(propertyUpper.equals(columnUpper) ||
                propertyUpper.equals(columnUpper.replace(StringPool.UNDERSCORE, StringPool.EMPTY)));
        } else {
            // 未开启驼峰,直接判断 property 是否与 column 相同(全大写)
            return !propertyUpper.equals(columnUpper);
        }
    }

    /**
     * <p>
     * 获取该类的所有属性列表
     * </p>
     *
     * @param clazz 反射类
     * @return 属性集合
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        AnnotationHandler annotationHandler = GlobalConfigUtils.getGlobalConfig(tableInfo.getConfiguration()).getAnnotationHandler();
        return getAllFields(clazz, annotationHandler);
    }

    /**
     * <p>
     * 获取该类的所有属性列表
     * </p>
     *
     * @param clazz             反射类
     * @param annotationHandler 注解处理类
     * @return 属性集合
     */
    public static List<Field> getAllFields(Class<?> clazz, AnnotationHandler annotationHandler) {
        List<Field> fieldList = ReflectionKit.getFieldList(ClassUtils.getUserClass(clazz));
        return fieldList.stream()
            .filter(field -> {
                /* 过滤注解非表字段属性 */
                TableField tableField = annotationHandler.getAnnotation(field, TableField.class);
                return (tableField == null || tableField.exist());
            }).collect(toList());
    }

    public static KeyGenerator genKeyGenerator(String baseStatementId, TableInfo tableInfo, MapperBuilderAssistant builderAssistant) {
        List<IKeyGenerator> keyGenerators = GlobalConfigUtils.getKeyGenerators(builderAssistant.getConfiguration());
        if (CollectionUtils.isEmpty(keyGenerators)) {
            throw new IllegalArgumentException("not configure IKeyGenerator implementation class.");
        }
        IKeyGenerator keyGenerator = null;
        if (keyGenerators.size() > 1) {
            // 多个主键生成器
            KeySequence keySequence = tableInfo.getKeySequence();
            if (null != keySequence && DbType.OTHER != keySequence.dbType()) {
                keyGenerator = keyGenerators.stream().filter(k -> k.dbType() == keySequence.dbType()).findFirst().orElse(null);
            }
        }
        // 无法找到注解指定生成器，默认使用第一个生成器
        if (null == keyGenerator) {
            keyGenerator = keyGenerators.get(0);
        }
        Configuration configuration = builderAssistant.getConfiguration();
        String id = builderAssistant.getCurrentNamespace() + StringPool.DOT + baseStatementId + SelectKeyGenerator.SELECT_KEY_SUFFIX;
        ResultMap resultMap = new ResultMap.Builder(builderAssistant.getConfiguration(), id, tableInfo.getKeyType(), new ArrayList<>()).build();
        MappedStatement mappedStatement = new MappedStatement.Builder(builderAssistant.getConfiguration(), id,
            new StaticSqlSource(configuration, keyGenerator.executeSql(tableInfo.getKeySequence().value())), SqlCommandType.SELECT)
            .keyProperty(tableInfo.getKeyProperty())
            .resultMaps(Collections.singletonList(resultMap))
            .build();
        configuration.addMappedStatement(mappedStatement);
        return new SelectKeyGenerator(mappedStatement, true);
    }

    @FunctionalInterface
    private interface PropertySelector {
        boolean selection(String property);
    }
}
