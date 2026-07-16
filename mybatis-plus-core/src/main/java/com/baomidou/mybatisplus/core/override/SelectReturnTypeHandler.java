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
package com.baomidou.mybatisplus.core.override;

import org.apache.ibatis.binding.MapperMethod;

import java.util.Map;

/**
 * Mapper 方法返回值类型处理器（仅限 SELECT 场景）。
 * <p>{@link #selectMethod()} 由 {@link MybatisMapperMethod} 调用，决定 selectList 还是 selectOne；
 * {@link #transform(Object, Object[], MapperMethod.MethodSignature, Map)} 由 {@link MybatisMapperMethod}
 * 在拦截器链全部执行完毕后调用，完成结果类型转换。</p>
 *
 * <p>示例：</p>
 * <pre>{@code
 * public class MyHandler implements SelectReturnTypeHandler {
 *     public SelectMethod selectMethod() { return SelectMethod.SELECT_LIST; }
 *     public Class<?> getType() { return MyResult.class; }
 *     public Object transform(Object queryResult, Map<String, Object> sharedData) {
 *         return new MyResult((List<?>) queryResult);
 *     }
 * }
 * SelectReturnTypeHandlerRegistry.register(new MyHandler());
 * }</pre>
 *
 * @author shanhy
 * @since 3.5.18
 */
public interface SelectReturnTypeHandler {

    enum SelectMethod {
        /** 调用 sqlSession.selectList，查询结果类型为 List */
        SELECT_LIST,
        /** 调用 sqlSession.selectOne，查询结果类型为单个对象 */
        SELECT_ONE
    }

    /**
     * 返回该处理器所需的查询方法，由 {@link MybatisMapperMethod} 调用。
     */
    SelectMethod selectMethod();

    /**
     * 返回该处理器绑定的目标类型，由 {@link SelectReturnTypeHandlerRegistry} 用于建立类型→处理器的映射。
     */
    Class<?> getType();

    /**
     * 将原始查询结果转换为目标返回类型。
     * <p>由 {@link MybatisMapperMethod} 在拦截器链全部执行完毕后调用。
     * executeSharedData 中包含各拦截器在 willDoQuery 阶段写入的数据。</p>
     *
     * @param queryResult       原始查询结果（List 或 单个对象，取决于 selectMethod）
     * @param args              Mapper 方法原始参数数组
     * @param method            MyBatis MethodSignature
     * @param executeSharedData 拦截器共享数据（willDoQuery 写入，transform 读取）
     * @return 转换后的结果
     */
    Object transform(Object queryResult, Object[] args, MapperMethod.MethodSignature method,
                     Map<String, Object> executeSharedData);
}
