package com.baomidou.mybatisplus.core.metadata;

import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieiqurong
 */
@Tag("unit")
public class TableInfoTest {

    static class Demo {

        String name;

    }

    @Test
    void shouldCreateTableInfoAndSetPropertyValues() {
        TableInfo tableInfo;
        Configuration configuration = new Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        tableInfo = new TableInfo(configuration, Demo.class);
        Demo demo = tableInfo.newInstance();
        tableInfo.setPropertyValue(demo, "name", "test");
        assertThat(tableInfo.getPropertyValue(demo, "name")).isEqualTo("test");
        assertThat(tableInfo.isUnderCamel()).isTrue();
        assertThat(tableInfo.getReflector()).isNotNull();
        tableInfo = new TableInfo(configuration, Object.class);
        assertThat(tableInfo.isUnderCamel()).isTrue();
        assertThat(tableInfo.getReflector()).isNotNull();
    }

}
