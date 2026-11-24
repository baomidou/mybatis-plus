package com.baomidou.mybatisplus.test.json;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.test.DbTestSupport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2024年3月4日
 */
@Tag("integration")
public class JacksonTest extends DbTestSupport<JacksonTest.JackJsonEntityMapper> {

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists t_jackson_entity", """
                CREATE TABLE IF NOT EXISTS t_jackson_entity (
                    id VARCHAR(32) NOT NULL,
                    name VARCHAR(30) NULL DEFAULT NULL,
                    card VARCHAR(255) NULL DEFAULT NULL,
                    attr VARCHAR(255) NULL DEFAULT NULL,
                    attr2 VARCHAR(255) NULL DEFAULT NULL,
                    attr3 VARCHAR(255) NULL DEFAULT NULL,
                    attr4 VARCHAR(255) NULL DEFAULT NULL,
                    PRIMARY KEY (id)
                )
                """);
    }

    @Test
    void shouldSerializeAndDeserializeWithJackson() {
        doTest(mapper -> {
            var entity = new JackJsonEntity("123", "秋秋", new JackJsonEntity.Card("1", "1111"),
                    Arrays.asList(new JackJsonEntity.Attr("age", "18"), new JackJsonEntity.Attr("sex", "女")),
                    new HashMap<>(Map.of("test", new JackJsonEntity.Attr("小红", "1"))),
                    new HashMap<>(Map.of("name", "1", "test2", "2")), new HashMap<>(Map.of("test1", "1", "test2", 2)));
            mapper.insert(entity);
            JackJsonEntity jackJsonEntity = mapper.selectById(entity.getId());
            assertThat(jackJsonEntity.getName()).isEqualTo("秋秋");
            assertThat(jackJsonEntity.getAttr().size()).isEqualTo(2);
            assertThat(jackJsonEntity.getCard()).isNotNull();
            assertThat(jackJsonEntity.getAttr2().size()).isEqualTo(1);
            assertThat(jackJsonEntity.getAttr2().get("test").getName()).isEqualTo("小红");
            assertThat(jackJsonEntity.getAttr3().size()).isEqualTo(2);
            assertThat(jackJsonEntity.getAttr3().get("name")).isEqualTo("1");
            assertThat(jackJsonEntity.getAttr3().get("test2")).isEqualTo("2");
            assertThat(jackJsonEntity.getAttr4().size()).isEqualTo(2);
            assertThat(jackJsonEntity.getAttr4().get("test2")).isEqualTo(2);
        });

    }

    public interface JackJsonEntityMapper extends BaseMapper<JackJsonEntity> {

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @TableName(value = "t_jackson_entity", autoResultMap = true)
    public static class JackJsonEntity {

        @TableId(type = IdType.INPUT)
        private String id;

        private String name;

        @TableField(typeHandler = JacksonTypeHandler.class)
        private Card card;

        @TableField(typeHandler = JacksonTypeHandler.class)
        private List<Attr> attr;

        @TableField(typeHandler = JacksonTypeHandler.class)
        private Map<String, Attr> attr2;

        @TableField(typeHandler = JacksonTypeHandler.class)
        private Map<String, String> attr3;

        @TableField(typeHandler = JacksonTypeHandler.class)
        private Map<String, Object> attr4;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Attr {

            private String name;

            private String value;

        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Card {

            private String id;

            private String value;

        }

    }
}
