package com.baomidou.mybatisplus.core.toolkit.sql;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.LocalDateTypeHandler;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2021-01-27
 */
@Tag("unit")
class SqlScriptUtilsTest {

    @Test
    void convertParamMapping() {
        assertThat(SqlScriptUtils.convertParamMapping(LocalDateTypeHandler.class, JdbcType.DATE, 2))
                .isEqualTo("typeHandler=org.apache.ibatis.type.LocalDateTypeHandler,jdbcType=DATE,numericScale=2");
    }
}
