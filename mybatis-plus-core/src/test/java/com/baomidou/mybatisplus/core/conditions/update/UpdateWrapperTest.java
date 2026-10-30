package com.baomidou.mybatisplus.core.conditions.update;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.WrapperTestSupport;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.test.User;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author miemie
 * @since 2021-01-27
 */
@Tag("unit")
class UpdateWrapperTest extends WrapperTestSupport {

    @Test
    void parameterAliasReachesSharedLambdaUpdateView() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), User.class);
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        LambdaUpdateWrapper<User> lambda = wrapper.lambda();
        lambda.set(User::getName, "updated").and(nested -> nested.eq(User::getId, 1));
        wrapper.setParamAlias("filter");

        assertThat(lambda.getParamAlias()).isEqualTo("filter");
        assertThat(wrapper.getSqlSet()).isEqualTo("username=#{filter.paramNameValuePairs.MPGENVAL1}");
        assertThat(wrapper.getSqlSegment()).isEqualTo("((id = #{filter.paramNameValuePairs.MPGENVAL2}))");
        lambda.eq(User::getRoleId, 2);
        assertThat(wrapper.getSqlSegment()).isEqualTo(
                "((id = #{filter.paramNameValuePairs.MPGENVAL2}) AND role_id = #{filter.paramNameValuePairs.MPGENVAL3})");
    }

    @Test
    void test1() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<User>().checkSqlInjection().eq("hi=1 or a", 123)
                .set("name", "a", "typeHandler=org.apache.ibatis.type.StringTypeHandler")
                .set("name", "a", "typeHandler=org.apache.ibatis.type.StringTypeHandler,jdbcType=VARCHAR").set(
                        "name", "a",
                        "typeHandler=org.apache.ibatis.type.StringTypeHandler,jdbcType=VARCHAR,numericScale=2");
        logSqlSet(
                "ss", wrapper,
                "name=#{ew.paramNameValuePairs.MPGENVAL1,typeHandler=org.apache.ibatis.type.StringTypeHandler},"
                        + "name=#{ew.paramNameValuePairs.MPGENVAL2,typeHandler=org.apache.ibatis.type.StringTypeHandler,jdbcType=VARCHAR},"
                        + "name=#{ew.paramNameValuePairs.MPGENVAL3,typeHandler=org.apache.ibatis.type.StringTypeHandler,jdbcType=VARCHAR,numericScale=2}");
        logParams(wrapper);
        assertThatThrownBy(wrapper::getSqlSegment).isInstanceOf(MybatisPlusException.class);
    }
}
