package com.baomidou.mybatisplus.generator.jdbc;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseMetaDataWrapperTest {

    @Test
    void test() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(
                "jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "").build();
        DatabaseMetaDataWrapper databaseMetaDataWrapper = new DatabaseMetaDataWrapper(dataSourceConfig.getConn(),
                dataSourceConfig.getSchemaName());
        Map<String, DatabaseMetaDataWrapper.Column> columnsInfo = databaseMetaDataWrapper
                .getColumnsInfo(null, null, "USERS", true);
        assertThat(columnsInfo).isNotNull();
        DatabaseMetaDataWrapper.Column name = columnsInfo.get("user_name");
        assertThat(name.isNullable()).isTrue();
        assertThat(name.getJdbcType()).isEqualTo(JdbcType.VARCHAR);
    }

}
