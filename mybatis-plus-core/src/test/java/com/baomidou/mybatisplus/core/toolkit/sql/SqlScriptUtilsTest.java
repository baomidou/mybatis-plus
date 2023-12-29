package com.baomidou.mybatisplus.core.toolkit.sql;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.LocalDateTypeHandler;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void convertBind(){
        assertThat(SqlScriptUtils.convertBind("hello", "'world'"))
            .isEqualTo("<bind name=\"hello\" value=\"'world'\"/>");
    }

    @Test
    void convertBinds(){
        assertThat(SqlScriptUtils.convertBinds(Map.of("hello", "'world'")))
            .isEqualTo("<bind name=\"hello\" value=\"'world'\"/>");
        assertThat(SqlScriptUtils.convertBinds(Map.of("hello", "'world'","hello2","'world2'")))
            .contains("<bind name=\"hello\" value=\"'world'\"/>","<bind name=\"hello2\" value=\"'world2'\"/>");
    }
}
