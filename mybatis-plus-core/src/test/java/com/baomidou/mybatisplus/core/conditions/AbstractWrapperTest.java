package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2023-08-01
 */
@Tag("unit")
class AbstractWrapperTest {

    @Test
    void formatSqlMaybeWithParam() {
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        String s = wrapper.formatSqlMaybeWithParam("c={0}", 1);
        assertThat(s).isEqualTo("c=#{ew.paramNameValuePairs.MPGENVAL1}");

        s = wrapper.formatSqlMaybeWithParam("c={0,javaType=int}", 1);
        assertThat(s).isEqualTo("c=#{ew.paramNameValuePairs.MPGENVAL2,javaType=int}");

        s = wrapper.formatSqlMaybeWithParam("c={0,javaType=int} and b={1,jdbcType=NUMERIC} pp", 1, 2);
        assertThat(s).isEqualTo(
                "c=#{ew.paramNameValuePairs.MPGENVAL3,javaType=int} "
                        + "and b=#{ew.paramNameValuePairs.MPGENVAL4,jdbcType=NUMERIC} pp");

        s = wrapper
                .formatSqlMaybeWithParam("c={0,javaType=int,jdbcType=NUMERIC,typeHandler=xxx.xxx.MyTypeHandler} pp", 1);
        assertThat(s).isEqualTo(
                "c=#{ew.paramNameValuePairs.MPGENVAL5,javaType=int,jdbcType=NUMERIC,typeHandler=xxx.xxx.MyTypeHandler} pp");

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
    }

    @Test
    void repeatedParameterCanMixDefaultAndExplicitMappings() {
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        String sql = wrapper.formatSqlMaybeWithParam("a={0} OR b={0,jdbcType=INTEGER}", 42);
        Configuration configuration = new Configuration();
        Map<String, Object> parameters = Collections.singletonMap("ew", wrapper);
        BoundSql boundSql = new XMLLanguageDriver()
                .createSqlSource(configuration, "<script>SELECT * FROM records WHERE " + sql + "</script>", Map.class)
                .getBoundSql(parameters);

        assertThat(boundSql.getSql()).isEqualTo("SELECT * FROM records WHERE a=? OR b=?");
        assertThat(boundSql.getParameterMappings()).hasSize(2);
        assertThat(boundSql.getParameterMappings().get(0).getJdbcType()).isNull();
        assertThat(boundSql.getParameterMappings().get(1).getJdbcType()).isEqualTo(JdbcType.INTEGER);
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            assertThat(configuration.newMetaObject(parameters).getValue(mapping.getProperty())).isEqualTo(42);
        }
    }

    @Test
    void repeatedParameterCanUseDifferentExplicitMappings() {
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        String sql = wrapper.formatSqlMaybeWithParam(
                "a={0,jdbcType=INTEGER} OR b={0,jdbcType=BIGINT} OR c={0,jdbcType=BIGINT}", 42);

        assertThat(sql).isEqualTo(
                "a=#{ew.paramNameValuePairs.MPGENVAL1,jdbcType=INTEGER} "
                        + "OR b=#{ew.paramNameValuePairs.MPGENVAL2,jdbcType=BIGINT} "
                        + "OR c=#{ew.paramNameValuePairs.MPGENVAL2,jdbcType=BIGINT}");
        assertThat(wrapper.getParamNameValuePairs()).hasSize(2);
        assertThat(wrapper.getParamNameValuePairs()).containsEntry("MPGENVAL1", 42).containsEntry("MPGENVAL2", 42);
    }

    @Test
    void repeatedPlainParametersKeepTheSameBinding() {
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        String sql = wrapper.formatSqlMaybeWithParam("a={0} OR b={0}", 42);

        assertThat(sql).isEqualTo("a=#{ew.paramNameValuePairs.MPGENVAL1} OR b=#{ew.paramNameValuePairs.MPGENVAL1}");
        assertThat(wrapper.getParamNameValuePairs()).containsOnlyKeys("MPGENVAL1");
    }

    @Test
    void changingParamAliasReachesUnrenderedNestedConditions() {
        QueryWrapper<Object> wrapper = new QueryWrapper<>()
                .eq("status", 1).and(nested -> nested.eq("id", 2).or(deeper -> deeper.eq("id", 3)));
        wrapper.setParamAlias("filter");

        assertThat(wrapper.getSqlSegment()).isEqualTo(
                "(status = #{filter.paramNameValuePairs.MPGENVAL1} AND (id = #{filter.paramNameValuePairs.MPGENVAL2} "
                        + "OR (id = #{filter.paramNameValuePairs.MPGENVAL3})))");
    }

    @Test
    void changingParamAliasSurvivesRenderingAndAppendingConditions() {
        QueryWrapper<Object> wrapper = new QueryWrapper<>().setParamAlias("original")
                .and(nested -> nested.eq("id", 1).or(deeper -> deeper.eq("id", 2)));
        assertThat(wrapper.getSqlSegment()).contains("#{original.paramNameValuePairs.");
        wrapper.setParamAlias("filter");
        wrapper.eq("status", 3);

        assertThat(wrapper.getSqlSegment())
                .doesNotContain("#{original.", "#{ew.")
                .contains("#{filter.paramNameValuePairs.");
        assertThat(wrapper.getTargetSql()).isEqualTo("((id = ? OR (id = ?)) AND status = ?)");
        wrapper.setParamAlias("second");
        wrapper.eq("enabled", 4);
        assertThat(wrapper.getSqlSegment())
                .doesNotContain("#{filter.", "#{original.", "#{ew.")
                .contains("#{second.paramNameValuePairs.");
        assertThat(wrapper.getTargetSql()).isEqualTo("((id = ? OR (id = ?)) AND status = ? AND enabled = ?)");
    }

}
