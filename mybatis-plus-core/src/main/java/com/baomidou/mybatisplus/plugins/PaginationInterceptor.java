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
package com.baomidou.mybatisplus.plugins;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.baomidou.mybatisplus.MybatisDefaultParameterHandler;
import com.baomidou.mybatisplus.enums.DBType;
import com.baomidou.mybatisplus.plugins.pagination.DialectFactory;
import com.baomidou.mybatisplus.plugins.pagination.PageHelper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.baomidou.mybatisplus.plugins.parser.ISqlParser;
import com.baomidou.mybatisplus.plugins.parser.SqlInfo;
import com.baomidou.mybatisplus.toolkit.JdbcUtils;
import com.baomidou.mybatisplus.toolkit.PluginUtils;
import com.baomidou.mybatisplus.toolkit.SqlUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * 分页拦截器
 * </p>
 *
 * @author hubin
 * @Date 2016-01-23
 */
@Intercepts({
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class PaginationInterceptor extends SqlParserHandler implements Interceptor {

    /**
     * 日志
     */
    private static final Log logger = LogFactory.getLog(PaginationInterceptor.class);
    /**
     * COUNT SQL 解析
     */
    private ISqlParser sqlParser;
    /**
     * 溢出总页数，设置第一页
     */
    private boolean overflowCurrent = false;
    /**
     * 方言类型
     */
    private String dialectType;
    /**
     * 方言实现类
     */
    private String dialectClazz;
    /**
     * 是否开启 PageHelper localPage 模式
     */
    private boolean localPage = false;

    /**
     * Physical Pagination Interceptor for all the queries with parameter {@link RowBounds}
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        if (target instanceof StatementHandler) {
            /**
             * SQL 解析链处理
             */
            StatementHandler statementHandler = (StatementHandler) PluginUtils.realTarget(invocation.getTarget());
            MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
            this.sqlParser(metaObject);
            return invocation.proceed();
        } else if (target instanceof Executor) {
            /**
             * PAGE 分页逻辑处理
             */
            // 先判断是不是SELECT操作
            Object[] args = invocation.getArgs();
            MappedStatement mappedStatement = (MappedStatement) args[0];
            if (!SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType())) {
                return invocation.proceed();
            }
            RowBounds rowBounds = (RowBounds) args[2];
            /* 不需要分页的场合 */
            if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
                // 本地线程分页
                if (localPage) {
                    // 采用ThreadLocal变量处理的分页
                    rowBounds = PageHelper.getPagination();
                    if (rowBounds == null) {
                        return invocation.proceed();
                    }
                } else {
                    // 无需分页
                    return invocation.proceed();
                }
            }
            Object parameter = args[1];
            ResultHandler resultHandler = (ResultHandler) args[3];
            Executor executor = (Executor) invocation.getTarget();
            CacheKey cacheKey;
            BoundSql boundSql;
            if (args.length == 4) {
                // 4 个参数时
                boundSql = mappedStatement.getBoundSql(parameter);
                cacheKey = executor.createCacheKey(mappedStatement, parameter, rowBounds, boundSql);
            } else {
                // 6 个参数时
                cacheKey = (CacheKey) args[4];
                boundSql = (BoundSql) args[5];
            }

            // 针对定义了rowBounds，做为mapper接口方法的参数
            String originalSql = boundSql.getSql();
            Connection connection = executor.getTransaction().getConnection();
            DBType dbType = StringUtils.isNotEmpty(dialectType) ? DBType.getDBType(dialectType) : JdbcUtils.getDbType(connection.getMetaData().getURL());
            Configuration configuration = mappedStatement.getConfiguration();
            if (rowBounds instanceof Pagination) {
                Pagination page = (Pagination) rowBounds;
                boolean orderBy = true;
                if (page.isSearchCount()) {
                    SqlInfo sqlInfo = SqlUtils.getOptimizeCountSql(page.isOptimizeCountSql(), sqlParser, originalSql);
                    orderBy = sqlInfo.isOrderBy();
                    // 查询总记录数
                    this.queryTotal(overflowCurrent, mappedStatement, boundSql, connection, parameter, sqlInfo, page);
                }
                originalSql = DialectFactory.buildPaginationSql(page, SqlUtils.concatOrderBy(originalSql, page, orderBy), dbType, dialectClazz);
            } else {
                // support physical Pagination for RowBounds
                originalSql = DialectFactory.buildPaginationSql(rowBounds, originalSql, dbType, dialectClazz);
            }
            // 查询记录数
            BoundSql pageBoundSql = new BoundSql(configuration, originalSql, boundSql.getParameterMappings(), parameter);
            List records = executor.query(mappedStatement, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, pageBoundSql);
            if (rowBounds instanceof Page) {
                Page page = (Page) rowBounds;
                page.setRecords(records);
            }
            return records;
        }
        return invocation.proceed();
    }

    /**
     * <p>
     * 查询总记录条数
     * </p>
     *
     * @param overflowCurrent
     * @param mappedStatement
     * @param boundSql
     * @param connection
     * @param parameter
     * @param sqlInfo
     * @param page
     */
    protected void queryTotal(boolean overflowCurrent, MappedStatement mappedStatement, BoundSql boundSql, Connection connection, Object parameter, SqlInfo sqlInfo, Pagination page) {
        MappedStatement countMappedStatement = this.getCountMappedStatement(mappedStatement, boundSql, sqlInfo, parameter);
        try (PreparedStatement statement = connection.prepareStatement(sqlInfo.getSql())) {
            DefaultParameterHandler parameterHandler = new MybatisDefaultParameterHandler(countMappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            int total = 0;
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    total = resultSet.getInt(1);
                }
            }
            page.setTotal(total);
            /*
             * 溢出总页数，设置第一页
             */
            long pages = page.getPages();
            if (overflowCurrent && (Long.valueOf(page.getCurrent()) > pages)) {
                // 设置为第一条
                page.setCurrent(1);
            }
        } catch (Exception e) {
            logger.error("Error: Method queryTotal execution error !", e);
        }
    }

    /**
     * <p>
     * 获取 Count MappedStatement 如果存在 countStatementId 使用 XML 中的 SQL 如果不存在构建一个
     * </p>
     * <p>
     * 例如： selectPage 分页查询，自定义 XML COUNT 查询未 selectPageCount
     * </p>
     *
     * @param mappedStatement
     * @param boundSql
     * @param sqlInfo
     * @param parameter
     * @return
     */
    private MappedStatement getCountMappedStatement(MappedStatement mappedStatement, BoundSql boundSql, SqlInfo sqlInfo, Object parameter) {
        Configuration configuration = mappedStatement.getConfiguration();
        BoundSql countBoundSql = new BoundSql(configuration, sqlInfo.getSql(), boundSql.getParameterMappings(), parameter);
        String countStatementId = mappedStatement.getId() + "Count";
        MappedStatement countMappedStatement = null;
        try {
            countMappedStatement = configuration.getMappedStatement(countStatementId, false);
        } catch (Throwable t) {
            if (null == countMappedStatement) {
                // 查询结果集
                SqlSource sqlsource = new StaticSqlSource(configuration, countBoundSql.getSql(), countBoundSql.getParameterMappings());
                MappedStatement.Builder builder = new MappedStatement.Builder(configuration, countStatementId, sqlsource,
                    SqlCommandType.SELECT);
                mappedStatement = builder.build();
            }
        }
        return mappedStatement;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler || target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties prop) {
        String dialectType = prop.getProperty("dialectType");
        String dialectClazz = prop.getProperty("dialectClazz");
        String localPage = prop.getProperty("localPage");

        if (StringUtils.isNotEmpty(dialectType)) {
            this.dialectType = dialectType;
        }
        if (StringUtils.isNotEmpty(dialectClazz)) {
            this.dialectClazz = dialectClazz;
        }
        if (StringUtils.isNotEmpty(localPage)) {
            this.localPage = Boolean.valueOf(localPage);
        }
    }

    public PaginationInterceptor setDialectType(String dialectType) {
        this.dialectType = dialectType;
        return this;
    }

    public PaginationInterceptor setDialectClazz(String dialectClazz) {
        this.dialectClazz = dialectClazz;
        return this;
    }

    public PaginationInterceptor setOverflowCurrent(boolean overflowCurrent) {
        this.overflowCurrent = overflowCurrent;
        return this;
    }

    public PaginationInterceptor setSqlParser(ISqlParser sqlParser) {
        this.sqlParser = sqlParser;
        return this;
    }

    public PaginationInterceptor setLocalPage(boolean localPage) {
        this.localPage = localPage;
        return this;
    }
}
