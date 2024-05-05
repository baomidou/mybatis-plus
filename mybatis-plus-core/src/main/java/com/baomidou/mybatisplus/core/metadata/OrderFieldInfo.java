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

package com.baomidou.mybatisplus.core.metadata;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import lombok.Data;

/**
 * @author nieqiurong
 * @since 3.5.4
 */
@Data
public class OrderFieldInfo {

    /**
     * 字段
     */
    private String column;

    /**
     * 排序类型
     */
    private String type;

    /**
     * 排序顺序
     */
    private short sort;


    public OrderFieldInfo(String column, boolean asc, short orderBySort) {
        this.column = column;
        this.type = asc ? Constants.ASC : Constants.DESC;
        this.sort = orderBySort;
    }

}
