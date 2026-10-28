package com.baomidou.mybatisplus.extension.ddl;

import org.h2.Driver;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Tag("unit")
public class DdlScriptTest {

    private static final String URL = "jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";

    private final List<String> errors = new ArrayList<>();

    private DdlScript ddlScript() {
        return new DdlScript(Driver.class.getName(), URL, "sa", "", true)
                .scriptRunner(scriptRunner -> scriptRunner.setLogWriter(null));
    }

    /**
     * 特征化 DdlScript 的结果上报:带消费者时成功返回 true 且不回调,失败返回 false 并把异常信息交给消费者;不带消费者的重载直接把异常抛出。
     */
    @Test
    void shouldRunSqlScriptAndHandleSuccessAndErrorWithConsumer() throws Exception {
        DdlScript ddlScript = ddlScript();
        assertThat(ddlScript.run("select 1 from dual;", errors::add)).isTrue();
        assertThat(ddlScript.run("select 2 from dual;select 3 from dual;", errors::add)).isTrue();
        assertThat(ddlScript.run("select 4 from dual;\nselect 5 from dual;\n", errors::add)).isTrue();
        assertThat(ddlScript.run("-- 注释也算一行\nselect 6 from dual;", errors::add)).isTrue();
        assertThat(ddlScript.run("", errors::add)).as("空脚本不该报错").isTrue();
        assertThat(errors).isEmpty();

        // 分隔符必须和脚本一致,否则整行当成缺结尾符
        assertThat(ddlScript.run("select 7 from dual", errors::add)).isFalse();
        assertThat(errors).hasSize(1).first().asString().contains("Line missing end-of-line terminator (;)");
        errors.clear();
        assertThat(ddlScript.run("select 8 from dual;", "#", errors::add)).isFalse();
        assertThat(errors).hasSize(1).first().asString().contains("Line missing end-of-line terminator (#)");
        errors.clear();
        assertThat(ddlScript.run("select 9 from dual#", "#", errors::add)).isTrue();
        assertThat(errors).isEmpty();

        // 出错只回调,不抛异常
        assertThat(ddlScript.run("select * from zz_missing_table;", errors::add)).isFalse();
        assertThat(errors).hasSize(1).first().asString().contains("zz_missing_table");
        errors.clear();
        assertThatExceptionOfType(org.apache.ibatis.jdbc.RuntimeSqlException.class).as("没有消费者的重载要把异常抛出来")
                .isThrownBy(() -> ddlScript.run("select * from zz_missing_table;"));
    }

    /**
     * DdlHelper 建的 ScriptRunner 是 stopOnError,首条出错语句之后的脚本不再执行。
     */
    @Test
    void abortOnFirstError() {
        DdlScript ddlScript = ddlScript();
        assertThat(ddlScript.run("drop table if exists zz_ddl_probe;", errors::add)).isTrue();
        errors.clear();
        assertThat(ddlScript.run("select * from zz_missing_table;create table zz_ddl_probe (id int);", errors::add))
                .isFalse();
        assertThat(errors).hasSize(1);
        errors.clear();
        assertThat(ddlScript.run("insert into zz_ddl_probe values (1);", errors::add)).as("出错语句之后的 create 不该执行")
                .isFalse();
        assertThat(errors).hasSize(1).first().asString().contains("zz_ddl_probe");
        errors.clear();
        assertThat(ddlScript.run("drop table if exists zz_ddl_probe;", errors::add)).isTrue();
        assertThat(errors).isEmpty();
    }

}
