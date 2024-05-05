package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;

import java.util.Map;

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
    @EnabledOnJre(JRE.JAVA_8)
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
