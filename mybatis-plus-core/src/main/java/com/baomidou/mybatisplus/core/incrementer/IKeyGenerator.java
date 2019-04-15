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
package com.baomidou.mybatisplus.core.incrementer;


/**
 * 表主键生成器接口 (sql)
 *
 * @author hubin
 * @since 2017-05-08
 */
public interface IKeyGenerator {

    /**
     * 执行 key 生成 SQL
     *
     * @param incrementerName 序列名称
     * @return sql
     */
    String executeSql(String incrementerName);
}
