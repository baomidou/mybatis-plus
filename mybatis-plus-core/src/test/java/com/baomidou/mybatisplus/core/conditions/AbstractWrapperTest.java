package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2023-08-01
 */
class AbstractWrapperTest {

    @Test
    void formatSqlMaybeWithParam() {
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        String s = wrapper.formatSqlMaybeWithParam("c={0}", 1);
        assertThat(s).isEqualTo("c=#{ew.paramNameValuePairs.MPGENVAL1}");

        s = wrapper.formatSqlMaybeWithParam("c={0,javaType=int}", 1);
        assertThat(s).isEqualTo("c=#{ew.paramNameValuePairs.MPGENVAL2,javaType=int}");

        s = wrapper.formatSqlMaybeWithParam("c={0,javaType=int} and b={1,jdbcType=NUMERIC} pp", 1, 2);
        assertThat(s).isEqualTo("c=#{ew.paramNameValuePairs.MPGENVAL3,javaType=int} " +
            "and b=#{ew.paramNameValuePairs.MPGENVAL4,jdbcType=NUMERIC} pp");

        s = wrapper.formatSqlMaybeWithParam("c={0,javaType=int,jdbcType=NUMERIC,typeHandler=xxx.xxx.MyTypeHandler} pp", 1);
        assertThat(s).isEqualTo("c=#{ew.paramNameValuePairs.MPGENVAL5,javaType=int,jdbcType=NUMERIC,typeHandler=xxx.xxx.MyTypeHandler} pp");

        Exception ex = null;
        try {
            wrapper.formatSqlMaybeWithParam("c={1} pp", 1);
        } catch (Exception e) {
            ex = e;
        }
        assertThat(ex).isNotNull();

        try {
            wrapper.formatSqlMaybeWithParam("c={1}", 1);
        } catch (Exception e) {
            ex = e;
        }
        assertThat(ex).isNotNull();
        System.out.println(ex.getMessage());
    }
}
