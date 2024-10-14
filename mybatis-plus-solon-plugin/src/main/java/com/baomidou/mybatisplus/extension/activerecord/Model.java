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
package com.baomidou.mybatisplus.extension.activerecord;

import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;

/**
 * ActiveRecord 模式 CRUD
 * <p>
 * 必须存在对应的原始mapper并继承baseMapper并且可以使用的前提下
 * 才能使用此 AR 模式 !!!
 * </p>
 *
 * @param <T>
 * @author hubin
 * @since 2016-11-06
 */
public abstract class Model<T extends Model<?>> extends AbstractModel<T> {

    /**
     * 执行 SQL
     */
    public SqlRunner sql() {
        return new SqlRunner(this.entityClass);
    }
}
