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
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import com.baomidou.mybatisplus.plugins.parser.AbstractSqlParser;
import com.baomidou.mybatisplus.plugins.parser.SqlInfo;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.PluginUtils;

/**
 * <p>
 * SQL 解析拦截器
 * </p>
 *
 * @author hubin
 * @Date 2016-08-31
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class SqlParserInterceptor implements Interceptor {

    // 日志
    private static final Log logger = LogFactory.getLog(SqlParserInterceptor.class);
    private static final String DELEGATE_BOUNDSQL_SQL = "delegate.boundSql.sql";
    // SQL 解析
    private List<AbstractSqlParser> sqlParserList;

    /**
     * 拦截 SQL 解析执行
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        // SQL 解析
        if (CollectionUtils.isNotEmpty(sqlParserList)) {
            String originalSql = (String) metaObject.getValue(DELEGATE_BOUNDSQL_SQL);
            for (AbstractSqlParser sqlParser : sqlParserList) {
                SqlInfo sqlInfo = sqlParser.optimizeSql(metaObject, originalSql);
                if (null != sqlInfo) {
                    originalSql = sqlInfo.getSql();
                }
            }
            metaObject.setValue(DELEGATE_BOUNDSQL_SQL, originalSql);
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties prop) {
        // to do nothing
    }

    public List<AbstractSqlParser> getSqlParserList() {
        return sqlParserList;
    }

    public void setSqlParserList(List<AbstractSqlParser> sqlParserList) {
        this.sqlParserList = sqlParserList;
    }
}
