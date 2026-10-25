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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * 数据源提供者
 *
 * @author hubin
 * @since 3.5.18
 */
public class DriverManagerDataSourceProvider implements IDataSourceProvider {

    @Override
    public DataSource createDataSource(String group, DataSourceProperty property) throws SQLException {
        if (property.getDriverClassName() != null && !property.getDriverClassName().trim().isEmpty()) {
            try {
                Class.forName(property.getDriverClassName());
            } catch (ClassNotFoundException e) {
                throw new SQLException("JDBC driver not found: " + property.getDriverClassName(), e);
            }
        }
        return new DriverManagerDataSource(property);
    }

    private static final class DriverManagerDataSource implements DataSource {
        private final DataSourceProperty property;
        private PrintWriter logWriter;
        private int loginTimeout;

        private DriverManagerDataSource(DataSourceProperty property) {
            this.property = property;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(property.getUrl(), property.getUsername(), property.getPassword());
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return DriverManager.getConnection(property.getUrl(), username, password);
        }

        @Override
        public PrintWriter getLogWriter() {
            return logWriter;
        }

        @Override
        public void setLogWriter(PrintWriter out) {
            this.logWriter = out;
        }

        @Override
        public void setLoginTimeout(int seconds) {
            this.loginTimeout = seconds;
            DriverManager.setLoginTimeout(seconds);
        }

        @Override
        public int getLoginTimeout() {
            return loginTimeout;
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return Logger.getGlobal();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            if (iface.isInstance(this)) return iface.cast(this);
            throw new SQLException("Not a wrapper for " + iface);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) {
            return iface.isInstance(this);
        }
    }
}
