package com.baomidou.mybatisplus.test.replaceplaceholder;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.ReplacePlaceholderInnerInterceptor;
import com.baomidou.mybatisplus.test.BaseDbTest;
import org.apache.ibatis.plugin.Interceptor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-23
 */
public class ReplacePlaceholderTest extends BaseDbTest<EntityMapper> {

    @Test
    void replace() {
        doTest(i -> {
            List<Entity> list = i.selectAll();
            System.out.println(list);
            assertThat(list.getFirst().getId()).isNull();
            assertThat(list.getFirst().getName()).isNull();
            assertThat(list.getFirst().getAge()).isNotNull();
            list = i.selectAll2();
            System.out.println(list);
            assertThat(list.getFirst().getEs().getId()).isNull();
            assertThat(list.getFirst().getEs().getName()).isNotBlank();
        });
    }

    @Override
    protected List<Interceptor> interceptors() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new ReplacePlaceholderInnerInterceptor("\""));
        return Collections.singletonList(interceptor);
    }

    // 全局 gradle test 不支持,全局缓存串台了
//    @Override
//    protected GlobalConfig globalConfig() {
//        GlobalConfig config = super.globalConfig();
//        config.getDbConfig().setReplacePlaceholder(true);
//        return config;
//    }

    @Override
    protected String tableDataSql() {
        return "insert into entity(id,name,age) values(1,'1',1),(2,'2',2);" +
            "insert into entity_sub(id,name) values(1,'1'),(2,'2');";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity", "drop table if exists entity_sub",
            "CREATE TABLE IF NOT EXISTS entity (" +
                "id BIGINT NOT NULL," +
                "name VARCHAR(30) NULL DEFAULT NULL," +
                "age int NULL DEFAULT NULL," +
                "PRIMARY KEY (id))",
            "CREATE TABLE IF NOT EXISTS entity_sub (" +
                "id BIGINT NOT NULL," +
                "name VARCHAR(30) NULL DEFAULT NULL," +
                "PRIMARY KEY (id))");
    }

    @Override
    protected List<Class<?>> otherMapper() {
        return List.of(EntitySubMapper.class);
    }
}
