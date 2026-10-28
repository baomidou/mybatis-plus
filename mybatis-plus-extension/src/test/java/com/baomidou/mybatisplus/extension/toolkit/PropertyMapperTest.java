package com.baomidou.mybatisplus.extension.toolkit;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * 拦截器属性按别名分组的规则：{@code @别名=类名}，{@code 别名:属性=值}
 */
@Tag("unit")
class PropertyMapperTest {

    @Test
    void keysExposeOnlyStringProperties() {
        Properties properties = new Properties();
        properties.setProperty("present", "1");
        assertThat(PropertyMapper.newInstance(properties).keys()).containsExactly("present");
    }

    @Test
    void whenNotBlankSkipsMissingAndBlankValues() {
        Properties properties = new Properties();
        properties.setProperty("present", "value");
        properties.setProperty("blank", "   ");
        List<String> seen = new ArrayList<>();
        PropertyMapper mapper = PropertyMapper.newInstance(properties);

        assertThat(mapper.whenNotBlank("missing", seen::add)).isSameAs(mapper);
        assertThat(mapper.whenNotBlank("blank", seen::add)).isSameAs(mapper);
        assertThat(mapper.whenNotBlank("present", seen::add)).isSameAs(mapper);

        assertThat(seen).containsExactly("value");
    }

    @Test
    void whenNotBlankMapsTheValueBeforeConsumingIt() {
        Properties properties = new Properties();
        properties.setProperty("size", "12");
        List<Integer> seen = new ArrayList<>();

        PropertyMapper.newInstance(properties).whenNotBlank("size", Integer::valueOf, seen::add);

        assertThat(seen).containsExactly(12);
    }

    @Test
    void groupSplitsAliasesFromTheirProperties() {
        Properties properties = new Properties();
        properties.setProperty("@page", "com.foo.Page");
        properties.setProperty("page:limit", "100");
        properties.setProperty("page:overflow", "false");
        properties.setProperty("@tenant", "com.foo.Tenant");
        properties.setProperty("tenant:tenant-id-column", "tenant_id");
        properties.setProperty("unrelated", "x");

        Map<String, Properties> group = PropertyMapper.newInstance(properties).group("@");

        assertThat(group).containsOnlyKeys("com.foo.Page", "com.foo.Tenant");
        assertThat(group.get("com.foo.Page")).containsOnly(entry("limit", "100"), entry("overflow", "false"));
        assertThat(group.get("com.foo.Tenant")).containsOnly(entry("tenant-id-column", "tenant_id"));
    }

    @Test
    void groupKeepsAliasesWithoutAnyProperty() {
        Properties properties = new Properties();
        properties.setProperty("@page", "com.foo.Page");

        Map<String, Properties> group = PropertyMapper.newInstance(properties).group("@");

        assertThat(group).containsOnlyKeys("com.foo.Page");
        assertThat(group.get("com.foo.Page")).isEmpty();
    }

    @Test
    void groupReturnsEmptyMapWhenNoAliasIsPresent() {
        Properties properties = new Properties();
        properties.setProperty("page:limit", "100");

        assertThat(PropertyMapper.newInstance(properties).group("@")).isEmpty();
    }
}
