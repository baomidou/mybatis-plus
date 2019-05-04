/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.starter;

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
