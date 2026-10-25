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
package com.baomidou.mybatisplus.autoconfigure;

import com.baomidou.mybatisplus.extension.plugins.datasource.*;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.ShardingInnerInterceptor;
import com.baomidou.mybatisplus.spring.transaction.ShardingManagedTransactionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分库插件自动配置
 *
 * @author hubin
 * @since 3.5.18
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "mybatis-plus.sharding", name = "primary")
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class ShardingAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "mybatis-plus.sharding")
    @ConditionalOnMissingBean
    public ShardingProperties shardingProperties() {
        return new ShardingProperties();
    }

    @Bean
    @ConditionalOnMissingBean(IDataSourceProvider.class)
    public IDataSourceProvider dataSourceProvider() {
        return new DriverManagerDataSourceProvider();
    }

    @Bean
    @ConditionalOnMissingBean(IShardingProcessor.class)
    public IShardingProcessor shardingProcessor() {
        return new DefaultShardingProcessor();
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(DataSource.class)
    public ShardingDatasource shardingDatasource(ShardingProperties properties, IDataSourceProvider provider,
                                                            ObjectProvider<IShardingStrategy> strategies) throws SQLException {
        IShardingStrategy strategy = strategies.getIfAvailable();
        if (strategy != null) {
            properties.setShardingStrategy(strategy);
        }
        if (properties.getDatasource().isEmpty()) {
            throw new IllegalArgumentException("mybatis-plus.sharding.datasource cannot be empty");
        }
        Map<String, DataSource> sources = new ConcurrentHashMap<>();
        Map<String, DataSourceProperty> sourceProperties = new ConcurrentHashMap<>();
        properties.getDatasource().forEach((group, list) -> {
            if (list == null || list.isEmpty()) {
                throw new IllegalArgumentException("Datasource group cannot be empty: " + group);
            }
            for (DataSourceProperty property : list) {
                if (property.getKey() == null || property.getKey().trim().isEmpty()) {
                    throw new IllegalArgumentException("Datasource key cannot be empty");
                }
                String key = ShardingKey.getDatabaseKey(group, property.getKey());
                if (sources.containsKey(key)) {
                    throw new IllegalArgumentException("Duplicate datasource key: " + key);
                }
                try {
                    sources.put(key, provider.createDataSource(group, property));
                    sourceProperties.put(key, property);
                } catch (SQLException e) {
                    throw new IllegalStateException("Cannot create datasource " + key, e);
                }
            }
        });
        return new ShardingDatasource(provider, sources, sourceProperties, properties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "shardingInnerInterceptor")
    @ConditionalOnBean(ShardingDatasource.class)
    public InnerInterceptor shardingInnerInterceptor(IShardingProcessor processor) {
        return new ShardingInnerInterceptor(processor);
    }

    @Bean
    @ConditionalOnMissingBean(TransactionFactory.class)
    @ConditionalOnBean(ShardingDatasource.class)
    public TransactionFactory shardingManagedTransactionFactory() {
        return new ShardingManagedTransactionFactory();
    }
}
