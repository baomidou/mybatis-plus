package com.baomidou.mybatisplus.core.toolkit;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 测试 bean utils
 *
 * @author HCL
 * 2018/7/24 17:44
 */
class BeanUtilsTest {

    /**
     * 测试 beanToMap
     */
    @Test
    void beanMapConvertTest() {
        Map<String, Object> map = BeanUtils.beanToMap(new User() {{
            setId(123);
            setName("baomidou");
        }});
        Assertions.assertEquals(2, map.keySet().size());
        Assertions.assertEquals(123, map.get("id"));
        Assertions.assertEquals("baomidou", map.get("name"));
        // 测试反向转换过程
        User user = BeanUtils.mapToBean(map, User.class);
        Assertions.assertEquals(123, user.getId());
        Assertions.assertEquals("baomidou", user.getName());

    }

    /**
     * 自定义实体类用于测试
     */
    @Getter
    @Setter
    public static class User {
        private String name;
        private int id;
    }

}
