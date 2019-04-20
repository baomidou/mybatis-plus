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
package com.baomidou.mybatisplus.dts.parser;

/**
 * 可靠消息事务解析器
 *
 * @author jobob
 * @since 2019-04-18
 */
public interface IDtsParser {

    /**
     * JSON 字符串转为对象
     *
     * @param jsonStr   JSON 字符串
     * @param valueType 转换对象类
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T readValue(String jsonStr, Class<T> valueType) throws Exception;

    /**
     * 对象转换为 JSON 字符串
     *
     * @param object 转换对象
     * @return
     * @throws Exception
     */
    String toJSONString(Object object) throws Exception;
}
