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

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import lombok.Getter;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LambdaUtilsTest {

    @Test
    void testResolve() {
        SerializedLambda lambda = LambdaUtils.resolve(TestPojo::getId);
        Assertions.assertEquals(TestPojo.class.getName(), lambda.getImplClassName());
        Assertions.assertEquals("getId", lambda.getImplMethodName());
        Assertions.assertEquals("id", PropertyNamer.methodToProperty(lambda.getImplMethodName()));

        Cond<TestPojo> cond = new Cond<>();
        System.out.println(cond.eq(TestPojo::getId, 123).toString());

        // 如果连着写，必须指定后者的泛型
        new Cond<>().eq(TestPojo::getId, 123)
            .eq(TestPojo::getId, 123)
            .eq(TestPojo::getId, 123);

        new Cond<TestPojo>() {{
            eq(TestPojo::getId, 123);
            eq(TestPojo::getId, 123);
            eq(TestPojo::getId, 456);
        }};
    }

    /**
     * 在 Java 中，一般来讲，只要是泛型，肯定是引用类型，但是为了避免翻车，还是测试一下
     */
    @Test
    void test() {
        assertInstantiatedMethodTypeIsReference(LambdaUtils.resolve(TestPojo::getId));
        assertInstantiatedMethodTypeIsReference(LambdaUtils.resolve(Integer::byteValue));
    }

    private void assertInstantiatedMethodTypeIsReference(SerializedLambda lambda) {
        Assertions.assertNotNull(lambda.getInstantiatedMethodType());
    }


    @Getter
    private class TestPojo {

        private int id;
    }

    public class Cond<T> {

        private StringBuilder sb = new StringBuilder();

        // 这个 TYPE 类型和 T 就没有关系了 ；
        // 如果有需要的话，使用 extends 来建立关系，保证class一致，稍微做点编写检查
        <TYPE extends T> Cond<T> eq(SFunction<TYPE, ?> prop, Object val) {
            SerializedLambda lambda = LambdaUtils.resolve(prop);
            this.sb.append(lambda.getImplMethodName()).append(" = ").append(val);
            return this;
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }
}
