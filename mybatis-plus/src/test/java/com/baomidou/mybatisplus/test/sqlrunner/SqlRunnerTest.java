package com.baomidou.mybatisplus.test.sqlrunner;

import com.baomidou.mybatisplus.core.injector.SqlRunnerInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.baomidou.mybatisplus.test.BaseDbTest;
import org.apache.ibatis.plugin.Interceptor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2021-03-16
 */
public class SqlRunnerTest extends BaseDbTest<EntityMapper> {

    public SqlRunnerTest() {
        SqlHelper.FACTORY = sqlSessionFactory;
        new SqlRunnerInjector().inject(sqlSessionFactory.getConfiguration());
    }

    @Test
    void test() {
        assertThat(SqlRunner.db().insert("insert into entity(id,name) values({0},{1})", 6, "6")).isTrue();

        assertThat(SqlRunner.db().update("update entity set name = {0} where id = {1}", "老王", 6)).isTrue();

        assertThat(SqlRunner.db().delete("delete from entity where id = {0}", 6)).isTrue();
    }

    @Override
    protected List<Interceptor> interceptors() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return Collections.singletonList(interceptor);
    }

    @Override
    protected String tableDataSql() {
        return "insert into entity(id,name) values(1,'1'),(2,'2'),(3,'3'),(4,'4'),(5,'5');";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity", "CREATE TABLE IF NOT EXISTS entity (" +
            "id BIGINT NOT NULL," +
            "name VARCHAR(30) NULL DEFAULT NULL," +
            "PRIMARY KEY (id))");
    }
}
