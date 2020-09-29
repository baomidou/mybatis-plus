/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.generator.config.querys;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.IDbQuery;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 装饰DbQuery
 *
 * @author nieqiurong 2020/9/17.
 */
public class DecoratorDbQuery extends AbstractDbQuery {

    private final IDbQuery dbQuery;
    private final DataSourceConfig dataSourceConfig;
    private final Connection connection;
    private final DbType dbType;
    private final StrategyConfig strategyConfig;
    private String schema;

    public DecoratorDbQuery(IDbQuery dbQuery, DataSourceConfig dataSourceConfig, StrategyConfig strategyConfig) {
        this.dbQuery = dbQuery;
        this.dataSourceConfig = dataSourceConfig;
        this.connection = dataSourceConfig.getConn();
        this.dbType = dataSourceConfig.getDbType();
        this.strategyConfig = strategyConfig;
        this.schema = dataSourceConfig.getSchemaName();
    }

    @Override
    public String tablesSql() {
        String tablesSql = dbQuery.tablesSql();
        String schema = dataSourceConfig.getSchemaName();
        if (DbType.POSTGRE_SQL == dbType) {
            if (schema == null) {
                //pg 默认 schema=public
                this.schema = "public";
            }
            tablesSql = String.format(tablesSql, schema);
        } else if (DbType.KINGBASE_ES == dbType) {
            if (schema == null) {
                //kingbase 默认 schema=PUBLIC
                this.schema = "PUBLIC";
            }
            tablesSql = String.format(tablesSql, schema);
        } else if (DbType.DB2 == dbType) {
            if (schema == null) {
                //db2 默认 schema=current schema
                this.schema = "current schema";
            }
            tablesSql = String.format(tablesSql, schema);
        }
        //oracle数据库表太多，出现最大游标错误
        else if (DbType.ORACLE == dbType) {
            //oracle 默认 schema=username
            if (schema == null) {
                this.schema = dataSourceConfig.getUsername().toUpperCase();
            }
            tablesSql = String.format(tablesSql, schema);
        }
        if (strategyConfig.isEnableSqlFilter()) {
            StringBuilder sql = new StringBuilder(tablesSql);
            boolean isInclude = strategyConfig.getInclude().size() > 0;
            boolean isExclude = strategyConfig.getExclude().size() > 0;
            if (strategyConfig.getLikeTable() != null) {
                sql.append(" AND ").append(dbQuery.tableName()).append(" LIKE '").append(strategyConfig.getLikeTable().getValue()).append("'");
            } else if (strategyConfig.getNotLikeTable() != null) {
                sql.append(" AND ").append(dbQuery.tableName()).append(" NOT LIKE '").append(strategyConfig.getNotLikeTable().getValue()).append("'");
            }
            if (isInclude) {
                sql.append(" AND ").append(dbQuery.tableName()).append(" IN (")
                    .append(strategyConfig.getInclude().stream().map(tb -> "'" + tb + "'").collect(Collectors.joining(","))).append(")");
            } else if (isExclude) {
                sql.append(" AND ").append(dbQuery.tableName()).append(" NOT IN (")
                    .append(strategyConfig.getExclude().stream().map(tb -> "'" + tb + "'").collect(Collectors.joining(","))).append(")");
            }
            return sql.toString();
        }
        return tablesSql;
    }

    @Override
    public String tableFieldsSql() {
        return dbQuery.tableFieldsSql();
    }

    /**
     * 扩展{@link #tableFieldsSql()}方法
     *
     * @param tableName 表名
     * @return 查询表字段语句
     */
    public String tableFieldsSql(String tableName) {
        String tableFieldsSql = this.tableFieldsSql();
        if (DbType.KINGBASE_ES == dbType) {
            tableFieldsSql = String.format(tableFieldsSql, this.schema, tableName);
        } else if (DbType.DB2 == dbType) {
            tableFieldsSql = String.format(tableFieldsSql, this.schema, tableName);
        } else if (DbType.ORACLE == dbType) {
            tableName = tableName.toUpperCase();
            tableFieldsSql = String.format(tableFieldsSql.replace("#schema", this.schema), tableName);
        } else if (DbType.DM == dbType) {
            tableName = tableName.toUpperCase();
            tableFieldsSql = String.format(tableFieldsSql, tableName);
        } else {
            tableFieldsSql = String.format(tableFieldsSql, tableName);
        }
        return tableFieldsSql;
    }

    @Override
    public String tableName() {
        return dbQuery.tableName();
    }

    @Override
    public String tableComment() {
        return dbQuery.tableComment();
    }

    @Override
    public String fieldName() {
        return dbQuery.fieldName();
    }

    @Override
    public String fieldType() {
        return dbQuery.fieldType();
    }

    @Override
    public String fieldComment() {
        return dbQuery.fieldComment();
    }

    @Override
    public String fieldKey() {
        return dbQuery.fieldKey();
    }

    @Override
    public boolean isKeyIdentity(ResultSet results) {
        try {
            return dbQuery.isKeyIdentity(results);
        } catch (SQLException e) {
            // ignore 这个看到在查H2的时候出了异常，先忽略这个异常了.
        }
        return false;
    }

    @Override
    public String[] fieldCustom() {
        return dbQuery.fieldCustom();
    }

    public Map<String, Object> getCustomFields(ResultSet resultSet) {
        String[] fcs = this.fieldCustom();
        if (null != fcs) {
            Map<String, Object> customMap = CollectionUtils.newHashMapWithExpectedSize(fcs.length);
            for (String fc : fcs) {
                try {
                    customMap.put(fc, resultSet.getObject(fc));
                } catch (SQLException sqlException) {
                    //ignore
                    customMap.put(fc, null);
                }
            }
            return customMap;
        }
        return Collections.emptyMap();
    }

    public void query(String sql, Consumer<ResultSetWrapper> consumer) throws SQLException {
        if (StringUtils.isNotBlank(this.schema)) {
            //切换至指定schema.
            connection.setSchema(this.schema);
        }
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                consumer.accept(new ResultSetWrapper(resultSet, this));
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        Optional.ofNullable(connection).ifPresent((con) -> {
            try {
                con.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        });
    }

    public static class ResultSetWrapper {

        private final IDbQuery dbQuery;

        private final ResultSet resultSet;

        ResultSetWrapper(ResultSet resultSet, IDbQuery dbQuery) {
            this.resultSet = resultSet;
            this.dbQuery = dbQuery;
        }

        public ResultSet getResultSet() {
            return resultSet;
        }

        public String getStringResult(String columnLabel) {
            try {
                return resultSet.getString(columnLabel);
            } catch (SQLException sqlException) {
                throw new RuntimeException(String.format("读取[%s]字段出错!", columnLabel), sqlException);
            }
        }

        public String getFiledComment() {
            return getComment(dbQuery.fieldComment());

        }

        private String getComment(String columnLabel) {
            return StringUtils.isNotBlank(columnLabel) ? formatComment(getStringResult(columnLabel)) : StringPool.EMPTY;
        }

        public String getTableComment() {
            return getComment(dbQuery.tableComment());
        }

        public String formatComment(String comment) {
            return StringUtils.isBlank(comment) ? StringPool.EMPTY : comment.replaceAll("\r\n", "\t");
        }
    }
}
