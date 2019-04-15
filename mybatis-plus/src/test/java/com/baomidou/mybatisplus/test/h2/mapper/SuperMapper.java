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
package com.baomidou.mybatisplus.test.h2.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 自定义父类 SuperMapper
 *
 * @author hubin
 * @since 2017-06-26
 */
public interface SuperMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {

    /**
     * 这里注入自定义的公共方法
     */

    int alwaysUpdateSomeColumnById(@Param("et") T entity);

    int deleteByIdWithFill(T entity);

    int insertBatchSomeColumn(List<T> entityList);
}
