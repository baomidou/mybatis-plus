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
package com.baomidou.mybatisplus.core.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 受限于SqlSession#selectList(java.lang.String, java.lang.Object)
 *
 * @author nieqiuqiu
 * @since 3.3.0
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class PageList<T> extends ArrayList<T> {

    /**
     * 记录数
     */
    private List<T> records;

    /**
     * 总数
     */
    private long total;

}
