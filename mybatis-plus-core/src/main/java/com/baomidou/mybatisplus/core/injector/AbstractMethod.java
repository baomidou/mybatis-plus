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
package com.baomidou.mybatisplus.core.injector;

import com.baomidou.mybatisplus.core.metadata.OrderFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * 抽象的注入方法类
 *
 * @author hubin
 * @since 2018-04-06
 */
public abstract class AbstractMethod implements Constants {

    protected final Log logger = LogFactory.getLog(getClass());

    protected Configuration configuration;

    protected LanguageDriver languageDriver;

    protected MapperBuilderAssistant builderAssistant;

    /**
     * 方法名称
     *
     * @since 3.5.0
     */
    protected final String methodName;

    /**
     * @param methodName 方法名
     * @since 3.5.0
     */
    protected AbstractMethod(String methodName) {
        Assert.notNull(methodName, "方法名不能为空");
        this.methodName = methodName;
    }

    /**
     * 注入自定义方法
     */
    public void inject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        this.configuration = builderAssistant.getConfiguration();
        this.builderAssistant = builderAssistant;
        this.languageDriver = configuration.getDefaultScriptingLanguageInstance();
        /* 注入自定义方法 */
        injectMappedStatement(mapperClass, modelClass, tableInfo);
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
     * SQL 更新 set 语句
     *
     * @param table 表信息
     * @return sql set 片段
     */
    protected String sqlLogicSet(TableInfo table) {
        return "SET " + table.getLogicDeleteSql(false, false);
    }

    /**
     * SQL 更新 set 语句
     *
     * @param logic  是否逻辑删除注入器
     * @param ew     是否存在 UpdateWrapper 条件
     * @param table  表信息
     * @param alias  别名
     * @param prefix 前缀
     * @return sql
     */
    protected String sqlSet(boolean logic, boolean ew, TableInfo table, boolean judgeAliasNull, final String alias,
                            final String prefix) {
        String sqlScript = table.getAllSqlSet(logic, prefix);
        if (judgeAliasNull) {
            sqlScript = SqlScriptUtils.convertIf(sqlScript, String.format("%s != null", alias), true);
        }
        if (ew) {
            sqlScript = sqlScript + NEWLINE + convertIfEwParam(U_WRAPPER_SQL_SET, false);
        }
        return SqlScriptUtils.convertSet(sqlScript);
    }

    /**
     * SQL 注释
     *
     * @return sql
     */
    protected String sqlComment() {
        return NEWLINE + convertIfEwParam(Q_WRAPPER_SQL_COMMENT, true);
    }

    /**
     * SQL 注释
     *
     * @return sql
     */
    protected String sqlFirst() {
        return convertIfEwParam(Q_WRAPPER_SQL_FIRST, true);
    }

    protected String convertIfEwParam(final String param, final boolean newLine) {
        return SqlScriptUtils.convertIf(SqlScriptUtils.unSafeParam(param),
            String.format("%s != null and %s != null", WRAPPER, param), newLine);
    }

    /**
     * SQL 查询所有表字段
     *
     * @param table        表信息
     * @param queryWrapper 是否为使用 queryWrapper 查询
     * @return sql 脚本
     */
    protected String sqlSelectColumns(TableInfo table, boolean queryWrapper) {
        /* 假设存在用户自定义的 resultMap 映射返回 */
        String selectColumns = ASTERISK;
        if (table.getResultMap() == null || table.isAutoInitResultMap()) {
            /* 未设置 resultMap 或者 resultMap 是自动构建的,视为属于mp的规则范围内 */
            selectColumns = table.getAllSqlSelect();
        }
        if (!queryWrapper) {
            return selectColumns;
        }
        return convertChooseEwSelect(selectColumns);
    }

    /**
     * SQL 查询记录行数
     *
     * @return count sql 脚本
     */
    protected String sqlCount() {
        return convertChooseEwSelect(ASTERISK);
    }

    /**
     * SQL 设置selectObj sql select
     *
     * @param table 表信息
     */
    protected String sqlSelectObjsColumns(TableInfo table) {
        return convertChooseEwSelect(table.getAllSqlSelect());
    }

    protected String convertChooseEwSelect(final String otherwise) {
        return SqlScriptUtils.convertChoose(String.format("%s != null and %s != null", WRAPPER, Q_WRAPPER_SQL_SELECT),
            SqlScriptUtils.unSafeParam(Q_WRAPPER_SQL_SELECT), otherwise);
    }

    /**
     * SQL map 查询条件
     */
    @Deprecated
    protected String sqlWhereByMap(TableInfo table) {
        if (table.isWithLogicDelete()) {
            // 逻辑删除
            String sqlScript = SqlScriptUtils.convertChoose("v == null", " ${k} IS NULL ",
                " ${k} = #{v} ");
            sqlScript = SqlScriptUtils.convertForeach(sqlScript, COLUMN_MAP, "k", "v", "AND");
            sqlScript = SqlScriptUtils.convertIf(sqlScript, String.format("%s != null and !%s.isEmpty", COLUMN_MAP, COLUMN_MAP), true);
            sqlScript += (NEWLINE + table.getLogicDeleteSql(true, true));
            return SqlScriptUtils.convertWhere(sqlScript);
        } else {
            String sqlScript = SqlScriptUtils.convertChoose("v == null", " ${k} IS NULL ",
                " ${k} = #{v} ");
            sqlScript = SqlScriptUtils.convertForeach(sqlScript, COLUMN_MAP, "k", "v", "AND");
            sqlScript = SqlScriptUtils.convertWhere(sqlScript);
            return SqlScriptUtils.convertIf(sqlScript, String.format("%s != null and !%s", COLUMN_MAP,
                COLUMN_MAP_IS_EMPTY), true);
        }
    }

    private static final String BIND_SQL_SEGMENT = "<bind name=\"_sgEs_\" value=\"ew.sqlSegment != null and ew.sqlSegment != ''\"/>";

    private static final String AND_SQL_SEGMENT = SqlScriptUtils.convertIf(" AND ${" + WRAPPER_SQLSEGMENT + "}", "_sgEs_ and " + WRAPPER_NONEMPTYOFNORMAL, true);

    private static final String LAST_SQL_SEGMENT = SqlScriptUtils.convertIf(" ${" + WRAPPER_SQLSEGMENT + "}", "_sgEs_ and " + WRAPPER_EMPTYOFNORMAL, true);

    /**
     * EntityWrapper方式获取select where
     *
     * @param newLine 是否提到下一行
     * @param table   表信息
     * @return String
     */
    protected String sqlWhereEntityWrapper(boolean newLine, TableInfo table) {
        /*
         * 存在逻辑删除 SQL 注入
         */
        if (table.isWithLogicDelete()) {
            String sqlScript = table.getAllSqlWhere(true, true, true, WRAPPER_ENTITY_DOT);
            sqlScript = SqlScriptUtils.convertIf(sqlScript, WRAPPER_ENTITY + " != null", true);
            sqlScript = SqlScriptUtils.convertIf(BIND_SQL_SEGMENT + NEWLINE + sqlScript + NEWLINE + AND_SQL_SEGMENT + NEWLINE + LAST_SQL_SEGMENT,
                WRAPPER + " != null", true);
            sqlScript = SqlScriptUtils.convertWhere(table.getLogicDeleteSql(false, true) + NEWLINE + sqlScript);
            return newLine ? NEWLINE + sqlScript : sqlScript;
        }
        /*
         * 普通 SQL 注入
         */
        String sqlScript = table.getAllSqlWhere(false, false, true, WRAPPER_ENTITY_DOT);
        sqlScript = SqlScriptUtils.convertIf(sqlScript, WRAPPER_ENTITY + " != null", true);
        sqlScript = SqlScriptUtils.convertWhere(sqlScript + NEWLINE + AND_SQL_SEGMENT) + NEWLINE + LAST_SQL_SEGMENT;
        sqlScript = SqlScriptUtils.convertIf(BIND_SQL_SEGMENT + NEWLINE + sqlScript, WRAPPER + " != null", true);
        return newLine ? NEWLINE + sqlScript : sqlScript;
    }

    protected String sqlOrderBy(TableInfo tableInfo) {
        /* 不存在排序字段，直接返回空 */
        List<OrderFieldInfo> orderByFields = tableInfo.getOrderByFields();
        if (CollectionUtils.isEmpty(orderByFields)) {
            return StringPool.EMPTY;
        }
        orderByFields.sort(Comparator.comparingInt(OrderFieldInfo::getSort));
        StringBuilder sql = new StringBuilder();
        sql.append(NEWLINE).append(" ORDER BY ");
        sql.append(orderByFields.stream().map(orderFieldInfo -> String.format("%s %s", orderFieldInfo.getColumn(),
            orderFieldInfo.getType())).collect(joining(",")));
        /* 当wrapper中传递了orderBy属性，@orderBy注解失效 */
        return SqlScriptUtils.convertIf(sql.toString(), String.format("%s == null or %s", WRAPPER,
            WRAPPER_EXPRESSION_ORDER), true);
    }

    /**
     * 过滤 TableFieldInfo 集合, join 成字符串
     */
    protected String filterTableFieldInfo(List<TableFieldInfo> fieldList, Predicate<TableFieldInfo> predicate,
                                          Function<TableFieldInfo, String> function, String joiningVal) {
        Stream<TableFieldInfo> infoStream = fieldList.stream();
        if (predicate != null) {
            return infoStream.filter(predicate).map(function).collect(joining(joiningVal));
        }
        return infoStream.map(function).collect(joining(joiningVal));
    }

    /**
     * 获取乐观锁相关
     *
     * @param tableInfo 表信息
     * @return String
     */
    protected String optlockVersion(TableInfo tableInfo) {
        if (tableInfo.isWithVersion()) {
            return tableInfo.getVersionFieldInfo().getVersionOli(ENTITY, ENTITY_DOT);
        }
        return EMPTY;
    }

    /**
     * 查询
     */
    protected MappedStatement addSelectMappedStatementForTable(Class<?> mapperClass, String id, SqlSource sqlSource,
                                                               TableInfo table) {
        String resultMap = table.getResultMap();
        if (null != resultMap) {
            /* 返回 resultMap 映射结果集 */
            return addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.SELECT, null,
                resultMap, null, NoKeyGenerator.INSTANCE, null, null);
        } else {
            /* 普通查询 */
            return addSelectMappedStatementForOther(mapperClass, id, sqlSource, table.getEntityType());
        }
    }

    /**
     * 查询
     *
     * @since 3.5.0
     */
    protected MappedStatement addSelectMappedStatementForTable(Class<?> mapperClass, SqlSource sqlSource, TableInfo table) {
        return addSelectMappedStatementForTable(mapperClass, this.methodName, sqlSource, table);
    }

    /**
     * 查询
     */
    protected MappedStatement addSelectMappedStatementForOther(Class<?> mapperClass, String id, SqlSource sqlSource,
                                                               Class<?> resultType) {
        return addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.SELECT, null,
            null, resultType, NoKeyGenerator.INSTANCE, null, null);
    }

    /**
     * 查询
     *
     * @since 3.5.0
     */
    protected MappedStatement addSelectMappedStatementForOther(Class<?> mapperClass, SqlSource sqlSource, Class<?> resultType) {
        return addSelectMappedStatementForOther(mapperClass, this.methodName, sqlSource, resultType);
    }

    /**
     * 插入
     */
    protected MappedStatement addInsertMappedStatement(Class<?> mapperClass, Class<?> parameterType, String id,
                                                       SqlSource sqlSource, KeyGenerator keyGenerator,
                                                       String keyProperty, String keyColumn) {
        return addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.INSERT, parameterType, null,
            Integer.class, keyGenerator, keyProperty, keyColumn);
    }

    /**
     * 插入
     *
     * @since 3.5.0
     */
    protected MappedStatement addInsertMappedStatement(Class<?> mapperClass, Class<?> parameterType,
                                                       SqlSource sqlSource, KeyGenerator keyGenerator,
                                                       String keyProperty, String keyColumn) {
        return addInsertMappedStatement(mapperClass, parameterType, this.methodName, sqlSource, keyGenerator, keyProperty, keyColumn);
    }


    /**
     * 删除
     */
    protected MappedStatement addDeleteMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource) {
        return addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.DELETE, null,
            null, Integer.class, NoKeyGenerator.INSTANCE, null, null);
    }

    /**
     * @since 3.5.0
     */
    protected MappedStatement addDeleteMappedStatement(Class<?> mapperClass, SqlSource sqlSource) {
        return addDeleteMappedStatement(mapperClass, this.methodName, sqlSource);
    }

    /**
     * 更新
     */
    protected MappedStatement addUpdateMappedStatement(Class<?> mapperClass, Class<?> parameterType, String id,
                                                       SqlSource sqlSource) {
        return addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.UPDATE, parameterType, null,
            Integer.class, NoKeyGenerator.INSTANCE, null, null);
    }

    /**
     * 更新
     *
     * @since 3.5.0
     */
    protected MappedStatement addUpdateMappedStatement(Class<?> mapperClass, Class<?> parameterType,
                                                       SqlSource sqlSource) {
        return addUpdateMappedStatement(mapperClass, parameterType, this.methodName, sqlSource);
    }

    /**
     * 添加 MappedStatement 到 Mybatis 容器
     */
    protected MappedStatement addMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource,
                                                 SqlCommandType sqlCommandType, Class<?> parameterType,
                                                 String resultMap, Class<?> resultType, KeyGenerator keyGenerator,
                                                 String keyProperty, String keyColumn) {
        String statementName = mapperClass.getName() + DOT + id;
        if (hasMappedStatement(statementName)) {
            logger.warn(LEFT_SQ_BRACKET + statementName + "] Has been loaded by XML or SqlProvider or Mybatis's Annotation, so ignoring this injection for [" + getClass() + RIGHT_SQ_BRACKET);
            return null;
        }
        /* 缓存逻辑处理 */
        boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
        return builderAssistant.addMappedStatement(id, sqlSource, StatementType.PREPARED, sqlCommandType,
            null, null, null, parameterType, resultMap, resultType,
            null, !isSelect, isSelect, false, keyGenerator, keyProperty, keyColumn,
            configuration.getDatabaseId(), languageDriver, null);
    }

    /**
     * @since 3.5.0
     */
    protected MappedStatement addMappedStatement(Class<?> mapperClass, SqlSource sqlSource,
                                                 SqlCommandType sqlCommandType, Class<?> parameterType,
                                                 String resultMap, Class<?> resultType, KeyGenerator keyGenerator,
                                                 String keyProperty, String keyColumn) {
        return addMappedStatement(mapperClass, this.methodName, sqlSource, sqlCommandType, parameterType, resultMap, resultType, keyGenerator, keyProperty, keyColumn);
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


    /**
     * @param configuration 配置对象
     * @param script        (统一去除空白行)
     * @param parameterType 参数类型
     * @return SqlSource
     * @since 3.5.3.2
     */
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        return languageDriver.createSqlSource(configuration, script, parameterType);
    }

}
