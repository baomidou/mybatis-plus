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

import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import lombok.Getter;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 测试 Lambda 解析类
 */
class LambdaUtilsTest {

    /**
     * 测试解析
     */
    @Test
    void testResolve() {
        SerializedLambda lambda = LambdaUtils.resolve(TestModel::getId);
        Assertions.assertEquals(TestModel.class.getName(), lambda.getImplClassName());
        Assertions.assertEquals("getId", lambda.getImplMethodName());
        Assertions.assertEquals("id", PropertyNamer.methodToProperty(lambda.getImplMethodName()));
    }

    /**
     * 在 Java 中，一般来讲，只要是泛型，肯定是引用类型，但是为了避免翻车，还是测试一下
     */
    @Test
    void test() {
        assertInstantiatedMethodTypeIsReference(LambdaUtils.resolve(TestModel::getId));
        assertInstantiatedMethodTypeIsReference(LambdaUtils.resolve(Integer::byteValue));
    }

    /**
     * 断言当前方法所在实例的方法类型为引用类型
     *
     * @param lambda 解析后的 lambda
     */
    private void assertInstantiatedMethodTypeIsReference(SerializedLambda lambda) {
        Assertions.assertNotNull(lambda.getInstantiatedMethodType());
    }

    @Getter
    private static class TestModel {
        private int id;
    }

}
