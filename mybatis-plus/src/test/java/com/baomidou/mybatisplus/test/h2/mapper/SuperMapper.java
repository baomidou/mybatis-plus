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
package com.baomidou.mybatisplus.test.h2.mapper;

/**
 * 自定义父类 SuperMapper
 *
 * @author hubin
 * @since 2017-06-26
 */
public interface SuperMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {

    // 这里可以写 mapper 层公共方法、 注意！！ 多泛型的时候请将泛型T放在第一位.
}
