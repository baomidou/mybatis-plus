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
package com.baomidou.mybatisplus.core.injector;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;

/**
 * <p>
 * 抽象的注入方法类
 * </p>
 *
 * @author hubin
 * @since 2018-04-06
 */
public abstract class AbstractMethod {

    protected Configuration configuration;
    protected LanguageDriver languageDriver;
    protected MapperBuilderAssistant builderAssistant;

    /**
     * 注入自定义方法
     */
    public void inject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
        this.configuration = builderAssistant.getConfiguration();
        this.builderAssistant = builderAssistant;
        this.languageDriver = configuration.getDefaultScriptingLanguageInstance();
        Class<?> modelClass = extractModelClass(mapperClass);
        if (null != modelClass) {
            /**
             * 注入自定义方法
             */
            TableInfo tableInfo = TableInfoHelper.initTableInfo(builderAssistant, modelClass);
            injectMappedStatement(mapperClass, modelClass, tableInfo);
        }
    }

    /**
     * 提取泛型模型,多泛型的时候请将泛型T放在第一位
     *
     * @param mapperClass mapper 接口
     * @return mapper 泛型
     */
    protected Class<?> extractModelClass(Class<?> mapperClass) {
        Type[] types = mapperClass.getGenericInterfaces();
        ParameterizedType target = null;
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                Type[] typeArray = ((ParameterizedType) type).getActualTypeArguments();
                if (ArrayUtils.isNotEmpty(typeArray)) {
                    for (Type t : typeArray) {
                        if (t instanceof TypeVariable || t instanceof WildcardType) {
                            break;
                        } else {
                            target = (ParameterizedType) type;
                            break;
                        }
                    }
                }
                break;
            }
        }
        return target == null ? null : (Class<?>) target.getActualTypeArguments()[0];
    }

    /**
     * 是否已经存在MappedStatement
     *
     * @param mappedStatement MappedStatement
     * @return true or false
     */
    private boolean hasMappedStatement(String mappedStatement) {
        return configuration.hasStatement(mappedStatement, false);
    }

    /**
     * <p>
     * SQL 更新 set 语句
     * </p>
     *
     * @param logic  是否逻辑删除注入器
     * @param ew     是否存在 UpdateWrapper 条件
     * @param table  表信息
     * @param prefix 前缀
     * @return sql
     */
    protected String sqlSet(boolean logic, boolean ew, TableInfo table, String prefix) {
        String sqlScript = table.getAllSqlSet(logic, prefix);
        if (ew) {
            sqlScript += StringPool.NEWLINE;
            sqlScript += SqlScriptUtils.convertIf(SqlScriptUtils.unSafeParam(Constants.U_WRAPPER_SQL_SET),
                String.format("%s != null and %s != null", Constants.WRAPPER, Constants.U_WRAPPER_SQL_SET), false);
        }
        sqlScript = SqlScriptUtils.convertTrim(sqlScript, "SET", null, null, ",");
        return sqlScript;
    }

    /**
     * <p>
     * SQL 查询所有表字段
     * </p>
     *
     * @param table        表信息
     * @param queryWrapper 是否为使用 queryWrapper 查询
     * @return sql 脚本
     */
    protected String sqlSelectColumns(TableInfo table, boolean queryWrapper) {
        /* 假设存在 resultMap 映射返回 */
        String selectColumns = StringPool.ASTERISK;
        if (table.getResultMap() == null) {
            /* 普通查询 */
            selectColumns = table.getAllSqlSelect();
        }
        if (!queryWrapper) {
            return selectColumns;
        }
        return SqlScriptUtils.convertChoose(String.format("%s != null and %s != null",
            Constants.WRAPPER, Constants.Q_WRAPPER_SQL_SELECT),
            SqlScriptUtils.unSafeParam(Constants.Q_WRAPPER_SQL_SELECT), selectColumns);
    }

    /**
     * <p>
     * SQL 设置selectObj sql select
     * </p>
     *
     * @param table 表信息
     */
    protected String sqlSelectObjsColumns(TableInfo table) {
        return SqlScriptUtils.convertChoose(String.format("%s != null and %s != null",
            Constants.WRAPPER, Constants.Q_WRAPPER_SQL_SELECT),
            SqlScriptUtils.unSafeParam(Constants.Q_WRAPPER_SQL_SELECT), table.getAllSqlSelect());
    }

    /**
     * <p>
     * SQL map 查询条件
     * </p>
     */
    protected String sqlWhereByMap(TableInfo table) {
        String sqlScript = SqlScriptUtils.convertChoose("v == null", " ${k} IS NULL ",
            " ${k} = #{v} ");
        sqlScript = SqlScriptUtils.convertForeach(sqlScript, Constants.COLUMN_MAP, "k", "v", "AND");
        sqlScript = SqlScriptUtils.convertWhere(sqlScript);
        sqlScript = SqlScriptUtils.convertIf(sqlScript,
            String.format("%s != null and !%s", Constants.COLUMN_MAP, Constants.COLUMN_MAP_IS_EMPTY), true);
        return sqlScript;
    }

    /**
     * <p>
     * EntityWrapper方式获取select where
     * </p>
     *
     * @param table 表信息
     * @return String
     */
    protected String sqlWhereEntityWrapper(TableInfo table) {
        String sqlScript = table.getAllSqlWhere(false, true, Constants.WRAPPER_ENTITY_SPOT);
        sqlScript = SqlScriptUtils.convertIf(sqlScript, String.format("%s != null", Constants.WRAPPER_ENTITY), true);
        sqlScript += StringPool.NEWLINE;
        sqlScript += SqlScriptUtils.convertIf(String.format(" ${%s}", Constants.WRAPPER_SQLSEGMENT),
            String.format("%s != null and %s != '' and ew.notEmptyOfWhere", Constants.WRAPPER_SQLSEGMENT, Constants.WRAPPER_SQLSEGMENT),
            true);
        sqlScript = SqlScriptUtils.convertTrim(sqlScript, "WHERE", null, "AND|OR", null);
        sqlScript += StringPool.NEWLINE;
        sqlScript += SqlScriptUtils.convertIf(String.format(" ${%s}", Constants.WRAPPER_SQLSEGMENT),
            String.format("%s != null and %s != '' and ew.emptyOfWhere", Constants.WRAPPER_SQLSEGMENT, Constants.WRAPPER_SQLSEGMENT),
            true);
        sqlScript = SqlScriptUtils.convertIf(sqlScript, "ew != null", true);
        return sqlScript;
    }

    /**
     * 查询
     */
    protected MappedStatement addSelectMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource, Class<?> resultType,
                                                       TableInfo table) {
        if (null != table) {
            String resultMap = table.getResultMap();
            if (null != resultMap) {
                /* 返回 resultMap 映射结果集 */
                return addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.SELECT, null, resultMap, null,
                    new NoKeyGenerator(), null, null);
            }
        }

        /* 普通查询 */
        return addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.SELECT, null, null, resultType,
            new NoKeyGenerator(), null, null);
    }

    /**
     * 插入
     */
    protected MappedStatement addInsertMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id, SqlSource sqlSource,
                                                       KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
        return addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.INSERT, modelClass, null, Integer.class,
            keyGenerator, keyProperty, keyColumn);
    }

    /**
     * 删除
     */
    protected MappedStatement addDeleteMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource) {
        return addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.DELETE, null, null, Integer.class,
            new NoKeyGenerator(), null, null);
    }

    /**
     * 更新
     */
    protected MappedStatement addUpdateMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id, SqlSource sqlSource) {
        return addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.UPDATE, modelClass, null, Integer.class,
            new NoKeyGenerator(), null, null);
    }

    /**
     * 添加 MappedStatement 到 Mybatis 容器
     */
    protected MappedStatement addMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource,
                                                 SqlCommandType sqlCommandType, Class<?> parameterClass,
                                                 String resultMap, Class<?> resultType, KeyGenerator keyGenerator,
                                                 String keyProperty, String keyColumn) {
        String statementName = mapperClass.getName() + StringPool.DOT + id;
        if (hasMappedStatement(statementName)) {
            System.err.println(StringPool.LEFT_BRACE + statementName + "} Has been loaded by XML or SqlProvider, ignoring the injection of the SQL.");
            return null;
        }
        /* 缓存逻辑处理 */
        boolean isSelect = false;
        if (sqlCommandType == SqlCommandType.SELECT) {
            isSelect = true;
        }
        return builderAssistant.addMappedStatement(id, sqlSource, StatementType.PREPARED, sqlCommandType, null, null, null,
            parameterClass, resultMap, resultType, null, !isSelect, isSelect, false, keyGenerator, keyProperty, keyColumn,
            configuration.getDatabaseId(), languageDriver, null);
    }

    /**
     * 注入自定义 MappedStatement
     *
     * @param mapperClass mapper 接口
     * @param modelClass  mapper 泛型
     * @param tableInfo   数据库表反射信息
     * @return MappedStatement
     */
    public abstract MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo);
}
