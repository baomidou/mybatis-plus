package com.baomidou.mybatisplus.test.extension.plugins.datasource;

import com.baomidou.mybatisplus.extension.plugins.datasource.DataSourceProperty;
import com.baomidou.mybatisplus.extension.plugins.datasource.DbCluster;
import com.baomidou.mybatisplus.extension.plugins.datasource.DriverManagerDataSourceProvider;
import com.baomidou.mybatisplus.extension.plugins.datasource.ShardingContext;
import com.baomidou.mybatisplus.extension.plugins.datasource.ShardingDatasource;
import com.baomidou.mybatisplus.extension.plugins.datasource.ShardingProperties;
import com.baomidou.mybatisplus.extension.plugins.datasource.ShardingKey;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShardingDatasourceTest {
    @AfterEach void clearRoute() { ShardingContext.clear(); }

    @Test void routesReadsToSlavesAndWritesToMasters() {
        DataSourceProperty master = property("m1", DbCluster.master);
        DataSourceProperty slave = property("s1", DbCluster.slave);
        ShardingProperties properties = new ShardingProperties();
        properties.setPrimary("orders");
        properties.setDatasource(Collections.singletonMap("orders", Arrays.asList(master, slave)));
        ShardingDatasource router = new ShardingDatasource(new DriverManagerDataSourceProvider(), new HashMap<>(), new HashMap<>(), properties);

        ShardingContext.group("orders").getDatasourceKeys(SqlCommandType.SELECT, "orders").forEach(key -> assertEquals("s1", key));
        ShardingContext.group("orders").getDatasourceKeys(SqlCommandType.UPDATE, "orders").forEach(key -> assertEquals("m1", key));
        ShardingKey.change("orders", SqlCommandType.SELECT, group -> group.getSlaveKeys().get(0));
        assertEquals("orders#s1", ShardingContext.currentOrPrimary());
        ShardingKey.change("orders", SqlCommandType.UPDATE, group -> group.getMasterKeys().get(0));
        assertEquals("orders#m1#IUD", ShardingContext.currentOrPrimary());
    }

    private DataSourceProperty property(String key, DbCluster cluster) {
        DataSourceProperty property = new DataSourceProperty();
        property.setKey(key);
        property.setCluster(cluster);
        return property;
    }
}
