package com.baomidou.mybatisplus.extension.plugins.datasource;

import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class ShardingDatasourceTest {

    @AfterEach
    void clearRoute() {
        ShardingContext.clear();
    }

    @Test
    void routesReadsToSlavesAndWritesToMasters() {
        DataSourceProperty master = property("m1", DbCluster.master);
        DataSourceProperty slave = property("s1", DbCluster.slave);
        ShardingProperties properties = new ShardingProperties();
        properties.setPrimary("orders");
        properties.setDatasource(Collections.singletonMap("orders", Arrays.asList(master, slave)));
        ShardingDatasource router = new ShardingDatasource(new DriverManagerDataSourceProvider(), new HashMap<>(), new HashMap<>(), properties);

        ShardingContext.group("orders").getDatasourceKeys(SqlCommandType.SELECT, "orders")
            .forEach(key -> assertThat(key).isEqualTo("s1"));
        ShardingContext.group("orders").getDatasourceKeys(SqlCommandType.UPDATE, "orders")
            .forEach(key -> assertThat(key).isEqualTo("m1"));
        ShardingKey.change("orders", SqlCommandType.SELECT, group -> group.getSlaveKeys().get(0));
        assertThat(ShardingContext.currentOrPrimary()).isEqualTo("orders#s1");
        ShardingKey.change("orders", SqlCommandType.UPDATE, group -> group.getMasterKeys().get(0));
        assertThat(ShardingContext.currentOrPrimary()).isEqualTo("orders#m1#IUD");
    }

    private DataSourceProperty property(String key, DbCluster cluster) {
        DataSourceProperty property = new DataSourceProperty();
        property.setKey(key);
        property.setCluster(cluster);
        return property;
    }
}
