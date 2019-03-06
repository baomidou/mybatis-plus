/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.toolkit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试
 *
 * @author HCL
 * 2018/7/26 9:53
 */
class ClassUtilsTest {

    /**
     * 测试实例化方法
     */
    @Test
    void newInstanceTest() {
        // 根据设计，只要能实例化，并且 对象不为 null ， 即认为执行成功
        Demo demo = ClassUtils.newInstance(Demo.class);
        Assertions.assertNotNull(demo);

        Map<?,?> map = ClassUtils.newInstance(HashMap.class);
        Assertions.assertNotNull(map);
    }

    /**
     * 实例化的 demo class
     */
    private static class Demo {}
}
