package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试 bean utils
 *
 * @author HCL <br>
 *         2018/7/24 17:44
 */
class BeanUtilsTest {

    /**
     * 测试 beanToMap
     */
    @Test
    void beanMapConvertTest() {
        Map<String, Object> map = BeanUtils.beanToMap(new User() {

            {
                setId(123);
                setName("baomidou");
            }
        });
        assertThat(map.size()).isEqualTo(2);
        assertThat(map.get("id")).isEqualTo(123);
        assertThat(map.get("name")).isEqualTo("baomidou");
        // 测试反向转换过程
        User user = BeanUtils.mapToBean(map, User.class);
        assertThat(user.getId()).isEqualTo(123);
        assertThat(user.getName()).isEqualTo("baomidou");

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
