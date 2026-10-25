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
package com.baomidou.mybatisplus.extension.plugins.datasource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.function.Function;

/**
 * 分库数据源
 *
 * @author hubin
 * @since 3.5.18
 */
public class ShardingDatasource implements DataSource {
    private final IDataSourceProvider provider;
    private final Map<String, DataSource> dataSources;
    private final Map<String, DataSourceProperty> properties;

    public ShardingDatasource(IDataSourceProvider provider, Map<String, DataSource> dataSources,
                              Map<String, DataSourceProperty> properties, ShardingProperties shardingProperties) {
        this.provider = provider;
        this.dataSources = new ConcurrentHashMap<>(dataSources == null ? Collections.emptyMap() : dataSources);
        this.properties = new ConcurrentHashMap<>(properties == null ? Collections.emptyMap() : properties);
        ShardingHelper.initDbSharding(shardingProperties);
    }

    public DataSource determineDataSource() {
        String key = ShardingContext.currentOrPrimary();
        DataSource dataSource = key == null ? null : dataSources.get(ShardingContext.stripWriteFlag(key));
        if (dataSource == null) {
            throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + key + "]");
        }
        return dataSource;
    }

    public Map<String, DataSource> getDataSources() {
        return Collections.unmodifiableMap(dataSources);
    }

    public DataSourceProperty getDataSourcePropertyByKey(String key) {
        return properties.get(key);
    }

    public DataSource getDataSource(String group, String key) {
        return dataSources.get(ShardingKey.getDatabaseKey(group, key));
    }

    public ShardingDatasource addDataSource(String group, String key, DataSource dataSource) {
        dataSources.put(ShardingKey.getDatabaseKey(group, key), dataSource);
        return this;
    }

    public ShardingDatasource removeDataSource(String group, String key) {
        dataSources.remove(ShardingKey.getDatabaseKey(group, key));
        return this;
    }

    public void change(String group, String key, Function<String, DataSourceProperty> propertyFactory) throws SQLException {
        String databaseKey = ShardingKey.getDatabaseKey(group, key);
        try {
            dataSources.computeIfAbsent(databaseKey, lookupKey -> {
                DataSourceProperty property = propertyFactory.apply(lookupKey);
                try {
                    DataSource dataSource = provider.createDataSource(group, property);
                    properties.put(lookupKey, property);
                    return dataSource;
                } catch (SQLException e) {
                    throw new DataSourceCreateException(lookupKey, e);
                }
            });
        } catch (DataSourceCreateException e) {
            throw e.getCause();
        }
        ShardingContext.set(databaseKey);
    }

    private static class DataSourceCreateException extends RuntimeException {
        private final SQLException cause;

        DataSourceCreateException(String databaseKey, SQLException cause) {
            super("Cannot create datasource " + databaseKey, cause);
            this.cause = cause;
        }

        @Override
        public synchronized SQLException getCause() {
            return cause;
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return determineDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return determineDataSource().getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return determineDataSource().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        determineDataSource().setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        determineDataSource().setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return determineDataSource().getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return determineDataSource().getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) return iface.cast(this);
        return determineDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this) || determineDataSource().isWrapperFor(iface);
    }
}
