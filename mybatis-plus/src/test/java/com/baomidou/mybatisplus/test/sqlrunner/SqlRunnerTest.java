package com.baomidou.mybatisplus.test.sqlrunner;

import com.baomidou.mybatisplus.core.injector.SqlRunnerInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.baomidou.mybatisplus.spring.toolkit.SqlRunner;
import com.baomidou.mybatisplus.test.DbTestSupport;
import org.apache.ibatis.plugin.Interceptor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2021-03-16
 */
public class SqlRunnerTest extends DbTestSupport<EntityMapper> {

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

    @Test
    void insertWithSingleNullParameter() {
        assertThat(SqlRunner.db().insert("insert into entity(id,name) values(6,{0})", (Object) null)).isTrue();
        assertThat(SqlRunner.db().selectCount("select count(*) from entity where id = 6 and name is null"))
                .isEqualTo(1);
    }

    @Test
    void updateWithSingleNullParameter() {
        assertThat(SqlRunner.db().update("update entity set name = {0} where id = 1", (Object) null)).isTrue();
        assertThat(SqlRunner.db().selectCount("select count(*) from entity where id = 1 and name is null"))
                .isEqualTo(1);
    }

    @Test
    void selectPageWithSingleNullParameter() {
        Page<Map<String, Object>> page = SqlRunner.db()
                .selectPage(new Page<>(1, 2), "select * from entity where {0} is null order by id", (Object) null);
        assertThat(page.getTotal()).isEqualTo(5);
        assertThat(page.getRecords()).hasSize(2);
    }

    @Test
    void singleContainerParametersStillResolveNullElements() {
        assertThat(SqlRunner.db().insert("insert into entity(id,name) values({0},{1})", (Object) new Object[]{6, null}))
                .isTrue();
        assertThat(SqlRunner.db().insert("insert into entity(id,name) values({0},{1})", Arrays.asList(7, null)))
                .isTrue();
        Map<String, Object> values = new HashMap<>();
        values.put("id", 8);
        values.put("name", null);
        assertThat(SqlRunner.db().insert("insert into entity(id,name) values({id},{name})", values)).isTrue();
        assertThat(SqlRunner.db().selectCount("select count(*) from entity where name is null")).isEqualTo(3);
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
        return Arrays.asList(
                "drop table if exists entity", "CREATE TABLE IF NOT EXISTS entity (" + "id BIGINT NOT NULL,"
                        + "name VARCHAR(30) NULL DEFAULT NULL," + "PRIMARY KEY (id))");
    }
}
