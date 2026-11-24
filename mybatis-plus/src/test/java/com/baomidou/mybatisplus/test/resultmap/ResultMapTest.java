package com.baomidou.mybatisplus.test.resultmap;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.handlers.GsonTypeHandler;
import com.baomidou.mybatisplus.test.DbTestSupport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-23
 */
@Tag("integration")
public class ResultMapTest extends DbTestSupport<ResultMapTest.EntityMapper> {

    @Test
    void shouldMapNestedResultsWithTypeHandlers() {
        doTestAutoCommit(
                m -> m.insert(
                        new Entity().setGg1(new Entity.Gg("老王")).setStr(new String[]{"hello"})
                                .setGg2(new Entity.Gg("老李")).setGg3(List.of(new Entity.Gg("老张"))).setGg4(
                                        List.of(
                                                new Entity.Gg4("秋秋", new Entity.Gg("小红"), List.of(new Entity.Gg("小猫")),
                                                        Map.of("test", new Entity.Gg("小明")))))));

        doTest(m -> {
            Entity entity = m.selectOne(null);
            assertThat(entity).as("插入正常").isNotNull();
            assertThat(entity.getGg1()).as("typeHandler正常").isNotNull();
            assertThat(entity.getGg1().getName()).as("是老王").isEqualTo("老王");

            assertThat(entity.getGg2()).as("typeHandler正常").isNotNull();
            assertThat(entity.getGg2().getName()).as("是老李").isEqualTo("老李");

            assertThat(entity.getGg3()).as("typeHandler正常").isNotNull();
            assertThat(entity.getGg3().get(0).getName()).as("是老张").isEqualTo("老张");

            assertThat(entity.getGg4()).as("typeHandler正常").isNotNull();
            assertThat(entity.getGg4().get(0).getName()).as("是秋秋").isEqualTo("秋秋");
            assertThat(entity.getGg4().get(0).getGg().getName()).as("是小红").isEqualTo("小红");
            assertThat(entity.getGg4().get(0).getGgList().get(0).getName()).as("是小猫").isEqualTo("小猫");
            assertThat(entity.getGg4().get(0).getGgMap().get("test").getName()).as("是小明").isEqualTo("小明");

            assertThat(entity.getStr()).as("typeHandler正常").isNotNull();
            assertThat(entity.getStr()[0]).isEqualTo("hello");
        });
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity", """
                CREATE TABLE IF NOT EXISTS entity (
                    id BIGINT(20) NOT NULL,
                    gg1 VARCHAR(255) NULL DEFAULT NULL,
                    gg2 VARCHAR(255) NULL DEFAULT NULL,
                    gg3 VARCHAR(255) NULL DEFAULT NULL,
                    gg4 VARCHAR(255) NULL DEFAULT NULL,
                    str VARCHAR(255) NULL DEFAULT NULL,
                    PRIMARY KEY (id)
                )
                """);
    }

    public interface EntityMapper extends BaseMapper<Entity> {
    }

    @Data
    @Accessors(chain = true)
    @TableName(resultMap = "resultMap")
    public static class Entity {

        private Long id;

        @TableField(typeHandler = GsonTypeHandler.class)
        private Gg gg1;

        @TableField(typeHandler = GsonTypeHandler.class)
        private Gg gg2;

        @TableField(typeHandler = GsonTypeHandler.class)
        private List<Gg> gg3;

        @TableField(typeHandler = GsonTypeHandler.class)
        private List<Gg4> gg4;

        @TableField(typeHandler = GsonTypeHandler.class)
        private String[] str;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Gg {

            private String name;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Gg4 {

            private String name;
            private Gg gg;
            private List<Gg> ggList;
            private Map<String, Gg> ggMap;
        }
    }
}
