/*
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
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.conditions;

import java.io.Serializable;

import com.baomidou.mybatisplus.core.toolkit.SerializationUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

/**
 * <p>
 * 条件构造抽象类
 * </p>
 *
 * @author hubin
 * @since 2018-05-25
 */
@SuppressWarnings("serial")
public abstract class Wrapper<T> implements ISqlSegment, Serializable {

    /**
     * <p>
     * 实体对象（子类实现）
     * </p>
     *
     * @return 泛型 T
     */
    public abstract T getEntity();

    /**
     * 查询条件 SQL 片段（子类实现）
     */
    public String getSqlSelect() {
        return null;
    }

    /**
     * 更新 SQL 片段（子类实现）
     */
    public String getSqlSet() {
        return null;
    }

    /**
     * 查询条件为空
     */
    public boolean isEmptyOfWhere() {
        return StringUtils.isEmpty(getSqlSegment()) && null == getEntity();
    }

    @Override
    public Wrapper<T> clone() {
        return SerializationUtils.clone(this);
    }
}

