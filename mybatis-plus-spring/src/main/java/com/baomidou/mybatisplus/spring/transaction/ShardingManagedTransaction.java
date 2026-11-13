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

import com.baomidou.mybatisplus.core.executor.CacheKeyCustomizer;
import com.baomidou.mybatisplus.extension.plugins.datasource.ShardingContext;
import com.baomidou.mybatisplus.extension.plugins.datasource.ShardingDatasource;
import com.baomidou.mybatisplus.extension.plugins.datasource.ShardingGroupProperty;
import org.apache.ibatis.cache.CacheKey;
import org.mybatis.spring.transaction.SpringManagedTransaction;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;

/**
 * 分库事务管理
 * <p>
 * Spring 本地事务复用路由数据源绑定的连接，不允许切换到其他分库。 非 Spring 事务按路由 KEY 维护多个物理连接，同组写后读绑定到写连接。 跨库提交为 best-effort 语义，并非分布式事务。
 *
 * @author hubin
 * @since 3.5.18
 */
public class ShardingManagedTransaction extends SpringManagedTransaction implements CacheKeyCustomizer {

    private final DataSource routingDataSource;
    private final Map<String, Connection> connections = new ConcurrentHashMap<>();
    private final Map<String, DataSource> connectionDataSources = new ConcurrentHashMap<>();
    private final Map<String, String> writeRoutes = new ConcurrentHashMap<>();

    public ShardingManagedTransaction(DataSource dataSource) {
        super(dataSource);
        this.routingDataSource = dataSource;
    }

    private String effectiveDatabaseKey() {
        String routeKey = ShardingContext.currentOrPrimary();
        if (routeKey == null) {
            throw new IllegalStateException("No datasource route is available");
        }
        String databaseKey = ShardingContext.stripWriteFlag(routeKey);
        Object resource = TransactionSynchronizationManager.getResource(routingDataSource);
        if (resource instanceof ConnectionHolder) {
            String boundKey = ((ShardingDatasource) routingDataSource)
                    .getDatabaseKey(((ConnectionHolder) resource).getConnection());
            if (!databaseKey.equals(boundKey) && !isReplicaRead(routeKey, boundKey)) {
                throw new IllegalStateException("Cannot switch datasource from " + boundKey + " to " + databaseKey
                        + " within a Spring-managed transaction");
            }
            return boundKey;
        }
        String group = group(databaseKey);
        String writeKey = group == null ? null : writeRoutes.get(group);
        if (writeKey != null) {
            Connection connection = connections.get(writeKey);
            try {
                if (connection != null && !connection.isClosed()) {
                    return writeKey;
                }
            } catch (SQLException e) {
                throw new IllegalStateException("Cannot inspect write connection for " + writeKey, e);
            }
            writeRoutes.remove(group, writeKey);
        }
        return databaseKey;
    }

    private static String group(String databaseKey) {
        int separator = databaseKey.indexOf('#');
        return separator < 0 ? null : databaseKey.substring(0, separator);
    }

    private static boolean isReplicaRead(String routeKey, String boundKey) {
        if (boundKey == null || ShardingContext.isWriteKey(routeKey)) {
            return false;
        }
        String group = group(boundKey);
        if (group == null || !group.equals(group(routeKey))) {
            return false;
        }
        ShardingGroupProperty property = ShardingContext.group(group);
        return property != null && property.getSlaveKeys() != null
                && property.getMasterKeys().contains(boundKey.substring(group.length() + 1))
                && property.getSlaveKeys().contains(routeKey.substring(group.length() + 1));
    }

    @Override
    public void customizeCacheKey(CacheKey cacheKey) {
        if (routingDataSource instanceof ShardingDatasource) {
            cacheKey.update(ShardingManagedTransaction.class.getName());
            cacheKey.update(effectiveDatabaseKey());
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (!(routingDataSource instanceof ShardingDatasource)) {
            return super.getConnection();
        }
        String datasourceKey = effectiveDatabaseKey();
        if (TransactionSynchronizationManager.hasResource(routingDataSource)) {
            return super.getConnection();
        }
        Connection connection = connections.get(datasourceKey);
        if (connection == null || connection.isClosed()) {
            DataSource physical = ((ShardingDatasource) routingDataSource).getDataSources().get(datasourceKey);
            if (physical == null) {
                throw new SQLException("Unknown datasource route: " + datasourceKey);
            }
            connection = DataSourceUtils.getConnection(physical);
            connections.put(datasourceKey, connection);
            connectionDataSources.put(datasourceKey, physical);
        }
        String group = group(datasourceKey);
        if (ShardingContext.isWriteKey(ShardingContext.currentOrPrimary()) && group != null) {
            writeRoutes.put(group, datasourceKey);
        }
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        super.commit();
        for (Map.Entry<String, Connection> entry : connections.entrySet()) {
            Connection connection = entry.getValue();
            if (!DataSourceUtils.isConnectionTransactional(connection, connectionDataSources.get(entry.getKey()))
                    && !connection.getAutoCommit()) {
                connection.commit();
            }
        }
    }

    @Override
    public void rollback() throws SQLException {
        super.rollback();
        for (Map.Entry<String, Connection> entry : connections.entrySet()) {
            Connection connection = entry.getValue();
            if (!DataSourceUtils.isConnectionTransactional(connection, connectionDataSources.get(entry.getKey()))
                    && !connection.getAutoCommit()) {
                connection.rollback();
            }
        }
    }

    @Override
    public void close() throws SQLException {
        super.close();
        for (Map.Entry<String, Connection> entry : connections.entrySet()) {
            DataSourceUtils.releaseConnection(entry.getValue(), connectionDataSources.get(entry.getKey()));
        }
        connections.clear();
        connectionDataSources.clear();
        writeRoutes.clear();
    }

    @Override
    public Integer getTimeout() {
        ConnectionHolder holder = (ConnectionHolder) TransactionSynchronizationManager.getResource(routingDataSource);
        return holder != null && holder.hasTimeout() ? holder.getTimeToLiveInSeconds() : null;
    }
}
