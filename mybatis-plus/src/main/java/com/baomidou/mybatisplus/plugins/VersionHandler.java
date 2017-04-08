/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.plugins;

import java.lang.reflect.Field;

/**
 * <p>
 * 乐观锁处理器,底层接口
 * </p>
 *
 * @author TaoYu 小锅盖
 * @since 2017-04-08
 */
public interface VersionHandler<T> {

    /**
     * 根据类型,使得对象对应的字段+1
     */
    void plusVersion(Object paramObj, Field field, T versionValue) throws Exception;
}