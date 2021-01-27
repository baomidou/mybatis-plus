package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.test.User;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.StringTypeHandler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author miemie
 * @since 2021-01-27
 */
class UpdateWrapperTest {

    private void logSqlSet(String explain, Update<?, ?> wrapper, String targetSql) {
        System.out.printf(" ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓   ->(%s)<-   ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓%n", explain);
        System.out.println(wrapper.getSqlSet());
        Assertions.assertThat(wrapper.getSqlSet().trim()).isEqualTo(targetSql);
    }

    private <T> void logParams(UpdateWrapper<T> wrapper) {
        wrapper.getParamNameValuePairs().forEach((k, v) ->
            System.out.println("key: '" + k + "'\t\tvalue: '" + v + StringPool.SINGLE_QUOTE));
    }

    @Test
    void test1() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<User>()
            .set("name", "a", StringTypeHandler.class)
            .set("name", "a", StringTypeHandler.class, JdbcType.VARCHAR)
            .set("name", "a", StringTypeHandler.class, JdbcType.VARCHAR, 2);
        logSqlSet("ss", wrapper,
            "name=#{ew.paramNameValuePairs.MPGENVAL1,typeHandler=org.apache.ibatis.type.StringTypeHandler}," +
                "name=#{ew.paramNameValuePairs.MPGENVAL2,typeHandler=org.apache.ibatis.type.StringTypeHandler,jdbcType=VARCHAR}," +
                "name=#{ew.paramNameValuePairs.MPGENVAL3,typeHandler=org.apache.ibatis.type.StringTypeHandler,jdbcType=VARCHAR,numericScale=2}");
        logParams(wrapper);
    }
}
