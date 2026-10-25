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
package com.baomidou.mybatisplus.spring.transaction;

import com.baomidou.mybatisplus.extension.plugins.datasource.ShardingContext;
import com.baomidou.mybatisplus.extension.plugins.datasource.ShardingDatasource;
import org.mybatis.spring.transaction.SpringManagedTransaction;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分库事务管理
 * <p>同一事务内按路由 KEY 维护多个物理连接：同组写后读会绑定到写连接以保证读己之写；
 * 提交/回滚时对各连接逐个执行，任一失败不会回滚已提交的其他连接，
 * 跨库场景为最终一致性的 best-effort 语义，并非分布式事务。
 *
 * @author hubin
 * @since 3.5.18
 */
public class ShardingManagedTransaction extends SpringManagedTransaction {
    private final DataSource routingDataSource;
    private final Map<String, Connection> connections = new ConcurrentHashMap<>();
    private final Map<String, String> writeRoutes = new ConcurrentHashMap<>();
    private volatile Boolean springManaged;

    public ShardingManagedTransaction(DataSource dataSource) {
        super(dataSource);
        this.routingDataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        String routeKey = ShardingContext.currentOrPrimary();
        if (!(routingDataSource instanceof ShardingDatasource)) {
            return super.getConnection();
        }
        if (routeKey == null) {
            throw new SQLException("No datasource route is available");
        }
        String datasourceKey = ShardingContext.stripWriteFlag(routeKey);
        int separator = datasourceKey.indexOf('#');
        String group = separator < 0 ? null : datasourceKey.substring(0, separator);
        boolean writeRoute = ShardingContext.isWriteKey(routeKey);
        if (group != null) {
            String writeRouteKey = writeRoutes.get(group);
            if (writeRouteKey != null) {
                Connection writeConnection = connections.get(writeRouteKey);
                if (writeConnection != null && !writeConnection.isClosed()) {
                    return writeConnection;
                }
                writeRoutes.remove(group, writeRouteKey);
            }
        }
        Connection connection = connections.get(datasourceKey);
        if (connection == null || connection.isClosed()) {
            DataSource physical = ((ShardingDatasource) routingDataSource).getDataSources().get(datasourceKey);
            if (physical == null) {
                throw new SQLException("Unknown datasource route: " + datasourceKey);
            }
            connection = DataSourceUtils.getConnection(physical);
            connections.put(datasourceKey, connection);
            if (springManaged == null) {
                springManaged = DataSourceUtils.isConnectionTransactional(connection, physical);
            }
        }
        if (writeRoute && group != null) {
            writeRoutes.put(group, datasourceKey);
        }
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        if (!Boolean.TRUE.equals(springManaged)) {
            for (Connection connection : connections.values()) {
                if (!connection.getAutoCommit()) {
                    connection.commit();
                }
            }
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (!Boolean.TRUE.equals(springManaged)) {
            for (Connection connection : connections.values()) {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                }
            }
        }
    }

    @Override
    public void close() throws SQLException {
        if (routingDataSource instanceof ShardingDatasource) {
            ShardingDatasource router = (ShardingDatasource) routingDataSource;
            for (Map.Entry<String, Connection> entry : connections.entrySet()) {
                DataSource physical = router.getDataSources().get(entry.getKey());
                if (physical != null) {
                    DataSourceUtils.releaseConnection(entry.getValue(), physical);
                }
            }
            connections.clear();
            writeRoutes.clear();
        } else {
            super.close();
        }
    }

    @Override
    public Integer getTimeout() {
        return null;
    }
}
