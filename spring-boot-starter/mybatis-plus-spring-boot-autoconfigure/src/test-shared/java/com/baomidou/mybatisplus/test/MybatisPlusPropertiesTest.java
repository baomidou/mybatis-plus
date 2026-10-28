package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 属性测试
 *
 * @author nieqiurong 2019/5/4.
 */
@Tag("integration")
class MybatisPlusPropertiesTest {

    @Test
    void resolveMapperLocationsTest() {
        MybatisPlusProperties mybatisPlusProperties = new MybatisPlusProperties();
        // 默认扫描 classpath*:/mapper/**/*.xml
        assertThat(mybatisPlusProperties.getMapperLocations()[0]).isEqualTo("classpath*:/mapper/**/*.xml");
        assertThat(mybatisPlusProperties.resolveMapperLocations().length).isEqualTo(2);
        // 扫描不存在的路径
        mybatisPlusProperties.setMapperLocations(new String[]{"classpath:mybatis-plus/*.xml"});
        assertThat(mybatisPlusProperties.resolveMapperLocations().length).isEqualTo(0);
    }
}
