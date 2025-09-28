package com.baomidou.mybatisplus.test;

import lombok.Data;
import org.apache.ibatis.scripting.xmltags.OgnlCache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

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
        Assertions.assertEquals("yellow", OgnlCache.getValue("color", propertiesMap));
        Assertions.assertEquals(2, OgnlCache.getValue("size", propertiesMap));
        Assertions.assertFalse((Boolean) OgnlCache.getValue("isEmpty", propertiesMap));
        Assertions.assertNull(OgnlCache.getValue("['isEmpty']", propertiesMap));
        Assertions.assertEquals("xxxL", OgnlCache.getValue("['size']", propertiesMap));
        Assertions.assertEquals("yellow", OgnlCache.getValue("['color']", propertiesMap));
        Bean bean = new Bean();
        bean.setName("靓仔");
        bean.setProperties(propertiesMap);
        Assertions.assertEquals("靓仔", OgnlCache.getValue("name", bean));
        Assertions.assertEquals("靓仔", OgnlCache.getValue("['name']", bean));
        Assertions.assertEquals(2, OgnlCache.getValue("properties.size", bean));
        Assertions.assertEquals("xxxL", OgnlCache.getValue("properties['size']", bean));
        Assertions.assertEquals("yellow", OgnlCache.getValue("properties.color", bean));
        Assertions.assertEquals("yellow", OgnlCache.getValue("properties['color']", bean));
        Assertions.assertFalse((Boolean) OgnlCache.getValue("properties.isEmpty", bean));
        Assertions.assertNull(OgnlCache.getValue("properties['isEmpty']", bean));
    }
}
