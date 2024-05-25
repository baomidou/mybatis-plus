package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.test.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author miemie
 * @since 2021-01-27
 */
class UpdateWrapperTest extends BaseWrapperTest {

    @Test
    void test1() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<User>().checkSqlInjection().eq("hi=1 or a", 123)
            .set("name", "a", "typeHandler=org.apache.ibatis.type.StringTypeHandler")
            .set("name", "a", "typeHandler=org.apache.ibatis.type.StringTypeHandler,jdbcType=VARCHAR")
            .set("name", "a", "typeHandler=org.apache.ibatis.type.StringTypeHandler,jdbcType=VARCHAR,numericScale=2");
        logSqlSet("ss", wrapper,
            "name=#{ew.paramNameValuePairs.MPGENVAL1,typeHandler=org.apache.ibatis.type.StringTypeHandler}," +
                "name=#{ew.paramNameValuePairs.MPGENVAL2,typeHandler=org.apache.ibatis.type.StringTypeHandler,jdbcType=VARCHAR}," +
                "name=#{ew.paramNameValuePairs.MPGENVAL3,typeHandler=org.apache.ibatis.type.StringTypeHandler,jdbcType=VARCHAR,numericScale=2}");
        logParams(wrapper);
        Assertions.assertThrows(MybatisPlusException.class, wrapper::getSqlSegment);
    }
}
