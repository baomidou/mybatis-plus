package com.baomidou.mybatisplus.core.toolkit.sql;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.LocalDateTypeHandler;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author miemie
 * @since 2021-01-27
 */
class SqlScriptUtilsTest {

    @Test
    void convertParamMapping() {
        assertThat(SqlScriptUtils.convertParamMapping(LocalDateTypeHandler.class, JdbcType.DATE,2))
            .isEqualTo("typeHandler=org.apache.ibatis.type.LocalDateTypeHandler,jdbcType=DATE,numericScale=2");
    }
}
