package com.baomidou.mybatisplus.core.toolkit;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import lombok.Getter;
import lombok.Setter;

/**
 * 测试 bean utils
 *
 * @author HCL
 * 2018/7/24 17:44
 */
public class BeanUtilsTest {

    /**
     * 测试 beanToMap
     */
    @Test
    public void beanMapConvertTest() {
        Map<String, Object> map = BeanUtils.beanToMap(new User() {{
            setId(123);
            setName("baomidou");
        }});
        assertEquals(2, map.keySet().size());
        assertEquals(123, map.get("id"));
        assertEquals("baomidou", map.get("name"));
        // 测试反向转换过程
        User user = BeanUtils.mapToBean(map, User.class);
        assertEquals(123, user.getId());
        assertEquals("baomidou", user.getName());

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
