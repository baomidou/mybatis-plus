package com.baomidou.mybatisplus.test.replaceplaceholder;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.ReplacePlaceholderInnerInterceptor;
import com.baomidou.mybatisplus.test.DbTestSupport;
import lombok.Data;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.plugin.Interceptor;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-23
 */
@Tag("integration")
public class ReplacePlaceholderTest extends DbTestSupport<ReplacePlaceholderTest.EntityMapper> {

    @Test
    void replace() {
        doTest(i -> {
            List<Entity> list = i.selectAll();
            assertThat(list.get(0).getId()).isNull();
            assertThat(list.get(0).getName()).isNull();
            assertThat(list.get(0).getAge()).isNotNull();
            list = i.selectAll2();
            assertThat(list.get(0).getEs().getId()).isNull();
            assertThat(list.get(0).getEs().getName()).isNotBlank();
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
        return """
                insert into entity(id,name,age) values
                    (1,'1',1), (2,'2',2);
                insert into entity_sub(id,name) values
                    (1,'1'), (2,'2');
                """;
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity", "drop table if exists entity_sub", """
                CREATE TABLE IF NOT EXISTS entity (
                    id BIGINT NOT NULL,
                    name VARCHAR(30) NULL DEFAULT NULL,
                    age int NULL DEFAULT NULL,
                    PRIMARY KEY (id)
                )
                """, """
                CREATE TABLE IF NOT EXISTS entity_sub (
                    id BIGINT NOT NULL,
                    name VARCHAR(30) NULL DEFAULT NULL,
                    PRIMARY KEY (id)
                )
                """);
    }

    @Override
    protected List<Class<?>> otherMapper() {
        return List.of(EntitySubMapper.class);
    }

    public interface EntityMapper extends BaseMapper<Entity> {

        @Select("select {@entity-name,id} from entity")
        List<Entity> selectAll();

        @Select("select {@entity:e},{@entity_sub-id:es:es} from entity e left join entity_sub es on e.id = es.id")
        List<Entity> selectAll2();
    }

    public interface EntitySubMapper extends BaseMapper<Entity.EntitySub> {
    }

    @Data
    public static class Entity {

        private Long id;

        @TableField("`name`")
        private String name;

        private Integer age;

        @TableField(exist = false)
        private EntitySub es;

        @Data
        public static class EntitySub {

            private Long id;
            private String name;
        }
    }
}
