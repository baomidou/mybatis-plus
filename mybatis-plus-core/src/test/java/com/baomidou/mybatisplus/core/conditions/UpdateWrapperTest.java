package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.test.User;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.StringTypeHandler;
import org.junit.jupiter.api.Test;

/**
 * @author miemie
 * @since 2021-01-27
 */
class UpdateWrapperTest extends BaseWrapperTest {

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
