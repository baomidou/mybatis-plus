package com.baomidou.mybatisplus.test.replaceselectbody;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.ReplaceSelectBodyInnerInterceptor;
import com.baomidou.mybatisplus.test.BaseDbTest;
import org.apache.ibatis.plugin.Interceptor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author miemie
 * @since 2020-06-23
 */
public class ReplaceSelectBodyTest extends BaseDbTest<EntityMapper> {

    @Test
    void replace() {
        doTest(i -> {
            i.selectAll();
            i.selectAll();
            i.selectAll();
            i.selectAll();
            i.selectAll2();
            i.selectAll2();
            i.selectAll2();
            i.selectAll2();
        });
    }

    @Override
    protected List<Interceptor> interceptors() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new ReplaceSelectBodyInnerInterceptor());
        return Collections.singletonList(interceptor);
    }

    @Override
    protected String tableDataSql() {
        return "insert into entity(id,name) values(1,'1'),(2,'2');";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity", "CREATE TABLE IF NOT EXISTS entity (" +
            "id BIGINT NOT NULL," +
            "name VARCHAR(30) NULL DEFAULT NULL," +
            "PRIMARY KEY (id))");
    }
}
