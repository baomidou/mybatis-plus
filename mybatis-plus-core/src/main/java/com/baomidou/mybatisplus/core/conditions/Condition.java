/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR EntityWrapperS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.conditions;

/**
 * <p>
 * 条件查询构造器
 * </p>
 *
 * @author hubin Caratacus
 * @date 2016-11-7
 */
@SuppressWarnings({"rawtypes", "serial"})
public abstract class Condition {

    /**
     * 构建一个Empty条件构造 避免传递参数使用null
     */
    public static final Wrapper EMPTY = Wrapper.getInstance();

    /**
     * 构造一个空的Wrapper<T></>
     *
     * @param <T>
     * @return
     */
    public static <T> Wrapper<T> empty() {
        return (Wrapper<T>) EMPTY;
    }

    /**
     * 构造一个EntityWrapper<T></>
     *
     * @param <T>
     * @return
     */
    public static <T> EntityWrapper<T> entityWrapper() {
        return entityWrapper(null);
    }

    /**
     * 构造一个EntityWrapper<T></>
     *
     * @param <T>
     * @return
     */
    public static <T> EntityWrapper<T> entityWrapper(T entity) {
        return new EntityWrapper<>(entity);
    }

}
