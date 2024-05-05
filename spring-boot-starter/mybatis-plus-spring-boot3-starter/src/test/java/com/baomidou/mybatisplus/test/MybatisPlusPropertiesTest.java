package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 属性测试
 *
 * @author nieqiurong 2019/5/4.
 */
class MybatisPlusPropertiesTest {

    @Test
    void resolveMapperLocationsTest() {
        MybatisPlusProperties mybatisPlusProperties = new MybatisPlusProperties();
        //默认扫描 classpath*:/mapper/**/*.xml
        Assertions.assertEquals(mybatisPlusProperties.getMapperLocations()[0], "classpath*:/mapper/**/*.xml");
        Assertions.assertEquals(2, mybatisPlusProperties.resolveMapperLocations().length);
        //扫描不存在的路径
        mybatisPlusProperties.setMapperLocations(new String[]{"classpath:mybatis-plus/*.xml"});
        Assertions.assertEquals(mybatisPlusProperties.resolveMapperLocations().length, 0);
    }

}
