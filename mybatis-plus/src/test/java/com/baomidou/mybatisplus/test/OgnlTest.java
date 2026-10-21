package com.baomidou.mybatisplus.test;

import lombok.Data;
import org.apache.ibatis.scripting.xmltags.OgnlCache;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiuqiu
 */
class OgnlTest {

    @Data
    private static class Bean {

        private String name;
        private Map<String, Object> properties;
    }

    /**
     * size keys keySet values isEmpty 这五个key值需要注意一下.
     *
     * @see org.apache.ibatis.ognl.MapPropertyAccessor#getProperty
     */
    @Test
    void test() {
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("color", "yellow");
        propertiesMap.put("size", "xxxL");
        assertThat(OgnlCache.getValue("color", propertiesMap)).isEqualTo("yellow");
        assertThat(OgnlCache.getValue("size", propertiesMap)).isEqualTo(2);
        assertThat((Boolean) OgnlCache.getValue("isEmpty", propertiesMap)).isFalse();
        assertThat(OgnlCache.getValue("['isEmpty']", propertiesMap)).isNull();
        assertThat(OgnlCache.getValue("['size']", propertiesMap)).isEqualTo("xxxL");
        assertThat(OgnlCache.getValue("['color']", propertiesMap)).isEqualTo("yellow");
        Bean bean = new Bean();
        bean.setName("靓仔");
        bean.setProperties(propertiesMap);
        assertThat(OgnlCache.getValue("name", bean)).isEqualTo("靓仔");
        assertThat(OgnlCache.getValue("['name']", bean)).isEqualTo("靓仔");
        assertThat(OgnlCache.getValue("properties.size", bean)).isEqualTo(2);
        assertThat(OgnlCache.getValue("properties['size']", bean)).isEqualTo("xxxL");
        assertThat(OgnlCache.getValue("properties.color", bean)).isEqualTo("yellow");
        assertThat(OgnlCache.getValue("properties['color']", bean)).isEqualTo("yellow");
        assertThat((Boolean) OgnlCache.getValue("properties.isEmpty", bean)).isFalse();
        assertThat(OgnlCache.getValue("properties['isEmpty']", bean)).isNull();
    }
}
