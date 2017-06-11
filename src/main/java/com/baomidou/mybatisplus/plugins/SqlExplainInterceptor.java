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
import java.util.Properties;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;

import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.enums.DBType;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.baomidou.mybatisplus.toolkit.VersionUtils;

/**
 * <p>
 * SQL 执行分析拦截器【 目前只支持 MYSQL-5.6.3 以上版本 】
 * </p>
 *
 * @author hubin
 * @Date 2016-08-16
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class SqlExplainInterceptor implements Interceptor {

    private static final Log logger = LogFactory.getLog(SqlExplainInterceptor.class);
    /**
     * Mysql支持分析SQL的最小版本
     */
    private final String minMySQLVersion = "5.6.3";
    /**
     * 发现执行全表 delete update 语句是否停止执行
     */
    private boolean stopProceed = false;

    public Object intercept(Invocation invocation) throws Throwable {
        /**
         * 处理 DELETE UPDATE 语句
         */
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        if (ms.getSqlCommandType() == SqlCommandType.DELETE || ms.getSqlCommandType() == SqlCommandType.UPDATE) {
            Executor executor = (Executor) invocation.getTarget();
            Configuration configuration = ms.getConfiguration();
            Object parameter = invocation.getArgs()[1];
            BoundSql boundSql = ms.getBoundSql(parameter);
            Connection connection = executor.getTransaction().getConnection();
            String databaseVersion = connection.getMetaData().getDatabaseProductVersion();
            if (GlobalConfiguration.getDbType(configuration).equals(DBType.MYSQL)
                    && VersionUtils.compare(minMySQLVersion, databaseVersion)) {
                logger.warn("Warn: Your mysql version needs to be greater than '5.6.3' to execute of Sql Explain!");
                return invocation.proceed();
            }
            /**
             * 执行 SQL 分析
             */
            sqlExplain(configuration, ms, boundSql, connection, parameter);
        }
        return invocation.proceed();
    }

    /**
     * <p>
     * 判断是否执行 SQL
     * </p>
     *
     * @param configuration
     * @param mappedStatement
     * @param boundSql
     * @param connection
     * @param parameter
     * @return
     * @throws Exception
     */
    protected void sqlExplain(Configuration configuration, MappedStatement mappedStatement, BoundSql boundSql,
                              Connection connection, Object parameter) {
        StringBuilder explain = new StringBuilder("EXPLAIN ");
        explain.append(boundSql.getSql());
        String sqlExplain = explain.toString();
        StaticSqlSource sqlsource = new StaticSqlSource(configuration, sqlExplain, boundSql.getParameterMappings());
        MappedStatement.Builder builder = new MappedStatement.Builder(configuration, "explain_sql", sqlsource,
                SqlCommandType.SELECT);
        builder.resultMaps(mappedStatement.getResultMaps()).resultSetType(mappedStatement.getResultSetType())
                .statementType(mappedStatement.getStatementType());
        MappedStatement query_statement = builder.build();
        DefaultParameterHandler handler = new DefaultParameterHandler(query_statement, parameter, boundSql);
        try (PreparedStatement stmt = connection.prepareStatement(sqlExplain)) {
            handler.setParameters(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (!"Using where".equals(rs.getString("Extra"))) {
                        if (this.isStopProceed()) {
                            throw new MybatisPlusException("Error: Full table operation is prohibited. SQL: " + boundSql.getSql());
                        }
                        break;
                    }
                }
            }


        } catch (Exception e) {
            throw new MybatisPlusException(e);
        }
    }

    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    public void setProperties(Properties prop) {
        String stopProceed = prop.getProperty("stopProceed");
        if (StringUtils.isNotEmpty(stopProceed)) {
            this.stopProceed = Boolean.valueOf(stopProceed);
        }
    }

    public boolean isStopProceed() {
        return stopProceed;
    }

    public void setStopProceed(boolean stopProceed) {
        this.stopProceed = stopProceed;
    }

}