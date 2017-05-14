/**
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
package com.baomidou.mybatisplus.toolkit;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.entity.TableFieldInfo;
import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.mapper.IKeyGenerator;
import com.baomidou.mybatisplus.mapper.SqlRunner;
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
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 实体类反射表辅助类
 * </p>
 *
 * @author hubin sjy
 * @Date 2016-09-09
 */
public class TableInfoHelper {

    private static final Log logger = LogFactory.getLog(TableInfoHelper.class);
    /**
     * 缓存反射类表信息
     */
    private static final Map<String, TableInfo> tableInfoCache = new ConcurrentHashMap<>();
    /**
     * 默认表主键
     */
    private static final String DEFAULT_ID_NAME = "id";

    /**
     * <p>
     * 获取实体映射表信息
     * <p>
     *
     * @param clazz 反射实体类
     * @return
     */
    public static TableInfo getTableInfo(Class<?> clazz) {
        return tableInfoCache.get(clazz.getName());
    }

    /**
     * <p>
     * 获取所有实体映射表信息
     * <p>
     * @return
     */
    public static List<TableInfo> getTableInfos() {
    	List<TableInfo> tableInfos = new ArrayList<TableInfo>();
    	for (Map.Entry<String, TableInfo> entry : tableInfoCache.entrySet()) {  
    		tableInfos.add(entry.getValue());  
    	} 
        return tableInfos;
    }
    
    /**
     * <p>
     * 实体类反射获取表信息【初始化】
     * <p>
     *
     * @param clazz 反射实体类
     * @return
     */
    public synchronized static TableInfo initTableInfo(MapperBuilderAssistant builderAssistant, Class<?> clazz) {
        TableInfo ti = tableInfoCache.get(clazz.getName());
        if (ti != null) {
            return ti;
        }
        TableInfo tableInfo = new TableInfo();
        GlobalConfiguration globalConfig;
        if (null != builderAssistant) {
            tableInfo.setCurrentNamespace(builderAssistant.getCurrentNamespace());
            tableInfo.setConfigMark(builderAssistant.getConfiguration());
            globalConfig = GlobalConfiguration.getGlobalConfig(builderAssistant.getConfiguration());
        } else {
            // 兼容测试场景
            globalConfig = GlobalConfiguration.DEFAULT;
        }
        /* 表名 */
        TableName table = clazz.getAnnotation(TableName.class);
        String tableName = clazz.getSimpleName();
        if (table != null && StringUtils.isNotEmpty(table.value())) {
            tableName = table.value();
        } else {
            // 开启字段下划线申明
            if (globalConfig.isDbColumnUnderline()) {
                tableName = StringUtils.camelToUnderline(tableName);
            }
            // 大写命名判断
            if (globalConfig.isCapitalMode()) {
                tableName = tableName.toUpperCase();
            } else {
                // 首字母小写
                tableName = StringUtils.firstToLowerCase(tableName);
            }
        }
        tableInfo.setTableName(tableName);

        // 开启了自定义 KEY 生成器
        if (null != globalConfig.getKeyGenerator()) {
            tableInfo.setKeySequence(clazz.getAnnotation(KeySequence.class));
        }

        /* 表结果集映射 */
        if (table != null && StringUtils.isNotEmpty(table.resultMap())) {
            tableInfo.setResultMap(table.resultMap());
        }
        List<TableFieldInfo> fieldList = new ArrayList<>();
        List<Field> list = getAllFields(clazz);
        boolean existTableId = existTableId(list);
        for (Field field : list) {

            /*
             * 主键ID 初始化
             */
            if (existTableId) {
                if (initTableId(globalConfig, tableInfo, field, clazz)) {
                    continue;
                }
            } else if (initFieldId(globalConfig, tableInfo, field, clazz)) {
                continue;
            }

            /*
             * 字段初始化
             */
            if (initTableField(globalConfig, tableInfo, fieldList, field, clazz)) {
                continue;
            }

            /*
             * 字段, 使用 camelToUnderline 转换驼峰写法为下划线分割法, 如果已指定 TableField , 便不会执行这里
             */
            fieldList.add(new TableFieldInfo(globalConfig, tableInfo, field));
        }

		/* 字段列表 */
        tableInfo.setFieldList(globalConfig, fieldList);
        /*
         * 未发现主键注解，提示警告信息
		 */
        if (StringUtils.isEmpty(tableInfo.getKeyColumn())) {
            logger.warn(String.format("Warn: Could not find @TableId in Class: %s.", clazz.getName()));
        }
        /*
         * 注入
		 */
        tableInfoCache.put(clazz.getName(), tableInfo);
        return tableInfo;
    }

    /**
     * <p>
     * 判断主键注解是否存在
     * </p>
     *
     * @param list 字段列表
     * @return
     */
    public static boolean existTableId(List<Field> list) {
        boolean exist = false;
        for (Field field : list) {
            TableId tableId = field.getAnnotation(TableId.class);
            if (tableId != null) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    /**
     * <p>
     * 主键属性初始化
     * </p>
     *
     * @param tableInfo
     * @param field
     * @param clazz
     * @return true 继续下一个属性判断，返回 continue;
     */
    private static boolean initTableId(GlobalConfiguration globalConfig, TableInfo tableInfo, Field field, Class<?> clazz) {
        TableId tableId = field.getAnnotation(TableId.class);
        if (tableId != null) {
            if (StringUtils.isEmpty(tableInfo.getKeyColumn())) {
                /*
                 * 主键策略（ 注解 > 全局 > 默认 ）
				 */
                // 设置 Sequence 其他策略无效
                if (IdType.NONE != tableId.type()) {
                    tableInfo.setIdType(tableId.type());
                } else {
                    tableInfo.setIdType(globalConfig.getIdType());
                }
                
                /* 字段 */
                String column = field.getName();
                if (StringUtils.isNotEmpty(tableId.value())) {
                    column = tableId.value();
                    tableInfo.setKeyRelated(true);
                } else {
                    // 开启字段下划线申明
                    if (globalConfig.isDbColumnUnderline()) {
                        column = StringUtils.camelToUnderline(column);
                        tableInfo.setKeyRelated(true);
                    }
                    // 全局大写命名
                    if (globalConfig.isCapitalMode()) {
                        column = column.toUpperCase();
                    }
                }
                tableInfo.setKeyColumn(column);
                tableInfo.setKeyProperty(field.getName());
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
     * @param tableInfo
     * @param field
     * @param clazz
     * @return true 继续下一个属性判断，返回 continue;
     */
    private static boolean initFieldId(GlobalConfiguration globalConfig, TableInfo tableInfo, Field field, Class<?> clazz) {
        String column = field.getName();
        if (globalConfig.isCapitalMode()) {
            column = column.toUpperCase();
        }
        if (DEFAULT_ID_NAME.equalsIgnoreCase(column)) {
            if (StringUtils.isEmpty(tableInfo.getKeyColumn())) {
                tableInfo.setIdType(globalConfig.getIdType());
                tableInfo.setKeyColumn(column);
                tableInfo.setKeyProperty(field.getName());
                return true;
            } else {
                throwExceptionId(clazz);
            }
        }
        return false;
    }

    /**
     * <p>
     * 发现设置多个主键注解抛出异常
     * </p>
     */
    private static void throwExceptionId(Class<?> clazz) {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("There must be only one, Discover multiple @TableId annotation in ");
        errorMsg.append(clazz.getName());
        throw new MybatisPlusException(errorMsg.toString());
    }

    /**
     * <p>
     * 字段属性初始化
     * </p>
     *
     * @param globalConfig 全局配置
     * @param tableInfo    表信息
     * @param fieldList    字段列表
     * @param clazz        当前表对象类
     * @return true 继续下一个属性判断，返回 continue;
     */
    private static boolean initTableField(GlobalConfiguration globalConfig, TableInfo tableInfo, List<TableFieldInfo> fieldList,
                                          Field field, Class<?> clazz) {
        /* 获取注解属性，自定义字段 */
        TableField tableField = field.getAnnotation(TableField.class);
        if (tableField != null) {
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
            String[] columns = columnName.split(";");
            String[] els = el.split(";");
            if (columns.length == els.length) {
                for (int i = 0; i < columns.length; i++) {
                    fieldList.add(new TableFieldInfo(globalConfig, tableInfo, columns[i], els[i], field, tableField));
                }
            } else {
                String errorMsg = "Class: %s, Field: %s, 'value' 'el' Length must be consistent.";
                throw new MybatisPlusException(String.format(errorMsg, clazz.getName(), field.getName()));
            }
            return true;
        }
        return false;
    }

    /**
     * 获取该类的所有属性列表
     *
     * @param clazz 反射类
     * @return
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fieldList = ReflectionKit.getFieldList(clazz);
        if (CollectionUtils.isNotEmpty(fieldList)) {
            Iterator<Field> iterator = fieldList.iterator();
            while (iterator.hasNext()) {
                Field field = iterator.next();
                /* 过滤注解非表字段属性 */
                TableField tableField = field.getAnnotation(TableField.class);
                if (tableField != null && !tableField.exist()) {
                    iterator.remove();
                }
            }
        }
        return fieldList;
    }

    /**
     * 初始化SqlSessionFactory (供Mybatis原生调用)
     *
     * @param sqlSessionFactory
     * @return
     */
    public static void initSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        Configuration configuration = sqlSessionFactory.getConfiguration();
        GlobalConfiguration globalConfig = GlobalConfiguration.getGlobalConfig(configuration);
        // SqlRunner
        SqlRunner.FACTORY = sqlSessionFactory;
        if (globalConfig == null) {
            GlobalConfiguration defaultCache = GlobalConfiguration.defaults();
            defaultCache.setSqlSessionFactory(sqlSessionFactory);
            GlobalConfiguration.setGlobalConfig(configuration, defaultCache);
        } else {
            globalConfig.setSqlSessionFactory(sqlSessionFactory);
        }
    }

    /**
     * <p>
     * 自定义 KEY 生成器
     * </p>
     */
    public static KeyGenerator genKeyGenerator(TableInfo tableInfo, MapperBuilderAssistant builderAssistant,
                                               String baseStatementId, LanguageDriver languageDriver) {
        IKeyGenerator keyGenerator = GlobalConfiguration.getKeyGenerator(builderAssistant.getConfiguration());
        if (null == keyGenerator) {
            throw new IllegalArgumentException("not configure IKeyGenerator implementation class.");
        }
        String id = baseStatementId + SelectKeyGenerator.SELECT_KEY_SUFFIX;
        Class<?> resultTypeClass = tableInfo.getKeySequence().idClazz();
        StatementType statementType = StatementType.PREPARED;
        String keyProperty = tableInfo.getKeyProperty();
        String keyColumn = tableInfo.getKeyColumn();
        SqlSource sqlSource = languageDriver.createSqlSource(builderAssistant.getConfiguration(),
                keyGenerator.executeSql(tableInfo), null);
        builderAssistant.addMappedStatement(id, sqlSource, statementType, SqlCommandType.SELECT, null, null, null,
                null, null, resultTypeClass, null, false, false, false,
                new NoKeyGenerator(), keyProperty, keyColumn, null, languageDriver, null);
        id = builderAssistant.applyCurrentNamespace(id, false);
        MappedStatement keyStatement = builderAssistant.getConfiguration().getMappedStatement(id, false);
        SelectKeyGenerator answer = new SelectKeyGenerator(keyStatement, true);
        builderAssistant.getConfiguration().addKeyGenerator(id, answer);
        return answer;
    }

}
