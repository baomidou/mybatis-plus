package com.baomidou.mybatisplus.test.autoresultmap;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-23
 */
@Tag("integration")
public class AutoResultMapTest extends DbTestSupport<AutoResultMapTest.EntityMapper> {

    @Test
    void shouldAutoMapResultWithTypeHandler() {
        doTestAutoCommit(m -> m.insert(new Entity().setName("老王").setGg(new Entity.Gg("老王"))));
        doTest(m -> {
            Entity entity = m.selectOne(null);
            assertThat(entity).as("插入正常").isNotNull();
            assertThat(entity.getName()).as("名称不一致正常").isNotNull();
            assertThat(entity.getGg()).as("typeHandler正常").isNotNull();
            assertThat(entity.getGg().getName()).as("是老王").isEqualTo("老王");
        });
        doTest(m -> {
            Entity entity = new Entity().setName("老王");
            m.selectOne(Wrappers.lambdaQuery(entity).ne(Entity::getId, 1));
        });
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity", """
                CREATE TABLE IF NOT EXISTS entity (
                    id BIGINT(20) NOT NULL,
                    x_name VARCHAR(20) NOT NULL,
                    gg VARCHAR(255) NULL DEFAULT NULL,
                    PRIMARY KEY (id)
                )
                """);
    }

    public interface EntityMapper extends BaseMapper<Entity> {
    }

    @Data
    @Accessors(chain = true)
    @TableName(autoResultMap = true)
    public static class Entity {

        private Long id;

        @TableField("x_name")
        private String name;

        @TableField(typeHandler = GsonTypeHandler.class)
        private Gg gg;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Gg {

            private String name;
        }
    }
}
