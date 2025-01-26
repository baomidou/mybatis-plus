package com.baomidou.mybatisplus.generator.jdbc;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class DatabaseMetaDataWrapperTest {

    @Test
    void test() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder("jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "").build();
        DatabaseMetaDataWrapper databaseMetaDataWrapper = new DatabaseMetaDataWrapper(dataSourceConfig.getConn(), dataSourceConfig.getSchemaName());
        Map<String, DatabaseMetaDataWrapper.Column> columnsInfo = databaseMetaDataWrapper.getColumnsInfo(null, null, "USERS", true);
        Assertions.assertNotNull(columnsInfo);
        DatabaseMetaDataWrapper.Column name = columnsInfo.get("user_name");
        Assertions.assertTrue(name.isNullable());
        Assertions.assertEquals(JdbcType.VARCHAR, name.getJdbcType());
    }

}
