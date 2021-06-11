package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.test.User;
import org.junit.jupiter.api.Test;

/**
 * @author miemie
 * @since 2021-01-27
 */
class UpdateWrapperTest extends BaseWrapperTest {

    @Test
    void testColumnToString() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<User>(StringUtils::camelToUnderline)
            .set(User.Fields.id, 1)
            .set(User.Fields.roleId, 12)
            .eq(User.Fields.id, 2)
            .eq(User.Fields.roleId, 12);

        logSqlSet("xx", wrapper, "id=#{ew.paramNameValuePairs.MPGENVAL1},role_id=#{ew.paramNameValuePairs.MPGENVAL2}");
        logSqlWhere("xx", wrapper, "(id = ? AND role_id = ?)");
        logParams(wrapper);
    }

    @Test
    void test1() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<User>()
            .set("name", "a", "typeHandler=org.apache.ibatis.type.StringTypeHandler")
            .set("name", "a", "typeHandler=org.apache.ibatis.type.StringTypeHandler,jdbcType=VARCHAR")
            .set("name", "a", "typeHandler=org.apache.ibatis.type.StringTypeHandler,jdbcType=VARCHAR,numericScale=2");
        logSqlSet("ss", wrapper,
            "name=#{ew.paramNameValuePairs.MPGENVAL1,typeHandler=org.apache.ibatis.type.StringTypeHandler}," +
                "name=#{ew.paramNameValuePairs.MPGENVAL2,typeHandler=org.apache.ibatis.type.StringTypeHandler,jdbcType=VARCHAR}," +
                "name=#{ew.paramNameValuePairs.MPGENVAL3,typeHandler=org.apache.ibatis.type.StringTypeHandler,jdbcType=VARCHAR,numericScale=2}");
        logParams(wrapper);
    }
}
