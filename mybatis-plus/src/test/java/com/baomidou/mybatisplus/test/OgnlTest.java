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
        Assertions.assertEquals(OgnlCache.getValue("color", propertiesMap), "yellow");
        Assertions.assertEquals(OgnlCache.getValue("size", propertiesMap), 2);
        Assertions.assertFalse((Boolean) OgnlCache.getValue("isEmpty", propertiesMap));
        Assertions.assertNull(OgnlCache.getValue("['isEmpty']", propertiesMap));
        Assertions.assertEquals(OgnlCache.getValue("['size']", propertiesMap), "xxxL");
        Assertions.assertEquals(OgnlCache.getValue("['color']", propertiesMap), "yellow");
        Bean bean = new Bean();
        bean.setName("靓仔");
        bean.setProperties(propertiesMap);
        Assertions.assertEquals(OgnlCache.getValue("name", bean), "靓仔");
        Assertions.assertEquals(OgnlCache.getValue("['name']", bean), "靓仔");
        Assertions.assertEquals(OgnlCache.getValue("properties.size", bean), 2);
        Assertions.assertEquals(OgnlCache.getValue("properties['size']", bean), "xxxL");
        Assertions.assertEquals(OgnlCache.getValue("properties.color", bean), "yellow");
        Assertions.assertEquals(OgnlCache.getValue("properties['color']", bean), "yellow");
        Assertions.assertFalse((Boolean) OgnlCache.getValue("properties.isEmpty", bean));
        Assertions.assertNull(OgnlCache.getValue("properties['isEmpty']", bean));
    }
}
