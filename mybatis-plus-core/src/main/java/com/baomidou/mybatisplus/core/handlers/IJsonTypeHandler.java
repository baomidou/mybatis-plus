/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.handlers;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * Json类型处理器接口(实现类确保为多例状态).
 * <p>
 * 注意:查询返回时需要使用resultMap
 *
 * <pre>
 * Example:
 *     &lt;result property="xx" column="xx" javaType="list" typeHandler="com.baomidou.mybatisplus.extension.handlers.GsonTypeHandler"/&gt;
 *     &lt;result property="xx" column="xx" typeHandler="com.baomidou.mybatisplus.extension.handlers.GsonTypeHandler"/&gt;
 * </pre>
 * </p>
 *
 * @author nieqiurong 2024年3月4日
 * @see TableName#autoResultMap() 自动构建
 * @see TableName#resultMap() 手动指定
 * @since 3.5.6
 */
public interface IJsonTypeHandler<T> {

    /**
     * 反序列化json
     *
     * @param json json字符串
     * @return T
     */
    T parse(String json);

    /**
     * 序列化json
     *
     * @param obj 对象信息
     * @return json字符串
     */
    String toJson(T obj);

}
