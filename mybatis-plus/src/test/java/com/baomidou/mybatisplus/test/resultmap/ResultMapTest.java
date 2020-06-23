package com.baomidou.mybatisplus.test.resultmap;

import com.baomidou.mybatisplus.test.BaseDbTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-23
 */
public class ResultMapTest extends BaseDbTest<EntityMapper> {

    @Test
    void test() {
        doTestAutoCommit(m -> m.insert(new Entity().setGg1(new Entity.Gg("老王"))
            .setGg2(new Entity.Gg("老李"))));

        doTest(m -> {
            Entity entity = m.selectOne(null);
            assertThat(entity).as("插入正常").isNotNull();
            assertThat(entity.getGg1()).as("typeHandler正常").isNotNull();
            assertThat(entity.getGg1().getName()).as("是老王").isEqualTo("老王");

            assertThat(entity.getGg2()).as("typeHandler正常").isNotNull();
            assertThat(entity.getGg2().getName()).as("是老李").isEqualTo("老李");
        });
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity",
            "CREATE TABLE IF NOT EXISTS entity (\n" +
                "id BIGINT(20) NOT NULL,\n" +
                "gg1 VARCHAR(255) NULL DEFAULT NULL,\n" +
                "gg2 VARCHAR(255) NULL DEFAULT NULL,\n" +
                "PRIMARY KEY (id)" +
                ")");
    }

    @Override
    protected String mapperXml() {
        return "com/baomidou/mybatisplus/test/resultmap/entityMapper.xml";
    }
}
