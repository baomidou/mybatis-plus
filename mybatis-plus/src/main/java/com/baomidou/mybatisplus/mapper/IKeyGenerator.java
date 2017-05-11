/**
 * Copyright (c) 2011-2016, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.mapper;


import com.baomidou.mybatisplus.entity.TableInfo;

/**
 * <p>
 * 表关键词 key 生成器接口
 * </p>
 *
 * @author hubin
 * @Date 2017-05-08
 */
public interface IKeyGenerator {

    /**
     * <p>
     * 执行 key 生成 SQL
     * </p>
     *
     * @param tableInfo 数据库表反射信息
     * @return
     */
    String executeSql(TableInfo tableInfo);

}
