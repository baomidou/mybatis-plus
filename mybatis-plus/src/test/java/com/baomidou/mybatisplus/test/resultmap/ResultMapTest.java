package com.baomidou.mybatisplus.test.resultmap;

import com.baomidou.mybatisplus.test.BaseDbTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-23
 */
public class ResultMapTest extends BaseDbTest<EntityMapper> {

    @Test
    void test() {
        doTestAutoCommit(m -> m.insert(new Entity().setGg1(new Entity.Gg("老王"))
            .setStr(new String[]{"hello"})
            .setGg2(new Entity.Gg("老李"))
            .setGg3(List.of(new Entity.Gg("老张")))
            .setGg4(List.of(new Entity.Gg4("秋秋", new Entity.Gg("小红"), List.of(new Entity.Gg("小猫")), Map.of("test", new Entity.Gg("小明")))))
        ));

        doTest(m -> {
            Entity entity = m.selectOne(null);
            assertThat(entity).as("插入正常").isNotNull();
            assertThat(entity.getGg1()).as("typeHandler正常").isNotNull();
            assertThat(entity.getGg1().getName()).as("是老王").isEqualTo("老王");

            assertThat(entity.getGg2()).as("typeHandler正常").isNotNull();
            assertThat(entity.getGg2().getName()).as("是老李").isEqualTo("老李");

            assertThat(entity.getGg3()).as("typeHandler正常").isNotNull();
            assertThat(entity.getGg3().getFirst().getName()).as("是老张").isEqualTo("老张");

            assertThat(entity.getGg4()).as("typeHandler正常").isNotNull();
            assertThat(entity.getGg4().getFirst().getName()).as("是秋秋").isEqualTo("秋秋");
            assertThat(entity.getGg4().getFirst().getGg().getName()).as("是小红").isEqualTo("小红");
            assertThat(entity.getGg4().getFirst().getGgList().getFirst().getName()).as("是小猫").isEqualTo("小猫");
            assertThat(entity.getGg4().getFirst().getGgMap().get("test").getName()).as("是小明").isEqualTo("小明");

            assertThat(entity.getStr()).as("typeHandler正常").isNotNull();
            assertThat(entity.getStr()[0]).isEqualTo("hello");
        });
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity",
            "CREATE TABLE IF NOT EXISTS entity (\n" +
                "id BIGINT(20) NOT NULL,\n" +
                "gg1 VARCHAR(255) NULL DEFAULT NULL,\n" +
                "gg2 VARCHAR(255) NULL DEFAULT NULL,\n" +
                "gg3 VARCHAR(255) NULL DEFAULT NULL,\n" +
                "gg4 VARCHAR(255) NULL DEFAULT NULL,\n" +
                "str VARCHAR(255) NULL DEFAULT NULL,\n" +
                "PRIMARY KEY (id)" +
                ")");
    }

    @Override
    protected String mapperXml() {
        return "com/baomidou/mybatisplus/test/resultmap/EntityMapper.xml";
    }
}
