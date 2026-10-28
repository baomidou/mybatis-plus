/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.toolkit.support;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Groovy 脚本中 lambda 代理解析测试。
 * <p>
 * 通过模拟 Groovy MethodClosure 代理结构（无需引入 Groovy 运行时依赖）， <br>
 * 验证 {@link GroovyLambdaMeta} 及 {@link LambdaUtils#extract} 的正确性。
 */
@Tag("unit")
class GroovyLambdaMetaTest {

    // ----- mock Groovy delegate (MethodClosure-like) -----

    static class MockMethodClosure {

        private final String method;
        private final Object owner;

        MockMethodClosure(String method, Object owner) {
            this.method = method;
            this.owner = owner;
        }

        public String getMethod() {
            return method;
        }

        public Object getOwner() {
            return owner;
        }
    }

    // ----- mock Groovy ConvertedClosure-like handler -----

    static class MockGroovyHandler implements InvocationHandler {

        private final MockMethodClosure delegate;

        MockGroovyHandler(String methodName, Class<?> ownerClass) {
            this.delegate = new MockMethodClosure(methodName, ownerClass);
        }

        @SuppressWarnings("unused")
        public MockMethodClosure getDelegate() {
            return delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return null;
        }
    }

    /**
     * 创建一个模拟 Groovy 代理，代理目标接口为 {@link SFunction}。
     */
    @SuppressWarnings("unchecked")
    private static <T> SFunction<T, ?> createGroovyProxy(String methodName, Class<?> ownerClass) {
        MockGroovyHandler handler = new MockGroovyHandler(methodName, ownerClass);
        return (SFunction<T, ?>) Proxy
                .newProxyInstance(ownerClass.getClassLoader(), new Class[]{SFunction.class}, handler);
    }

    // ----- entity for testing -----

    static class TestEntity {

        private String name;

        public String getName() {
            return name;
        }
    }

    // ----- tests -----

    @Test
    void testGroovyLambdaMetaClassOwner() {
        // Simulate: TestEntity::getName  in a Groovy script
        SFunction<TestEntity, ?> proxy = createGroovyProxy("getName", TestEntity.class);

        GroovyLambdaMeta meta = new GroovyLambdaMeta((Proxy) proxy);

        assertThat(meta.getImplMethodName()).isEqualTo("getName");
        assertThat(meta.getInstantiatedClass()).isSameAs(TestEntity.class);
        assertThat(meta.toString()).isEqualTo("TestEntity::getName");
    }

    @Test
    void testGroovyLambdaMetaInstanceOwner() {
        // When the Groovy owner is an instance (e.g. obj::getName), owner.getClass() should be used
        TestEntity instance = new TestEntity();
        SFunction<TestEntity, ?> proxy = createGroovyProxy("getName", instance.getClass());

        GroovyLambdaMeta meta = new GroovyLambdaMeta((Proxy) proxy);

        assertThat(meta.getImplMethodName()).isEqualTo("getName");
        assertThat(meta.getInstantiatedClass()).isSameAs(TestEntity.class);
    }

    @Test
    void testLambdaUtilsExtractGroovyProxy() {
        // Ensure LambdaUtils.extract routes to GroovyLambdaMeta for non-MethodHandleProxy proxies
        SFunction<TestEntity, ?> proxy = createGroovyProxy("getName", TestEntity.class);

        LambdaMeta meta = LambdaUtils.extract(proxy);

        assertThat(meta).isNotNull();
        assertThat(meta.getInstantiatedClass()).isSameAs(TestEntity.class);
        assertThat(meta.getImplMethodName()).isEqualTo("getName");
    }

    @Test
    void testGroovyLambdaMetaUnsupportedProxyThrows() {
        // A proxy whose handler has no getDelegate() should throw MybatisPlusException
        @SuppressWarnings("unchecked")
        SFunction<TestEntity, ?> proxy = (SFunction<TestEntity, ?>) Proxy
                .newProxyInstance(TestEntity.class.getClassLoader(), new Class[]{SFunction.class}, (p, m, args) -> null // plain handler, no getDelegate()
                );

        assertThatThrownBy(() -> new GroovyLambdaMeta((Proxy) proxy)).isInstanceOf(MybatisPlusException.class);
    }

}
