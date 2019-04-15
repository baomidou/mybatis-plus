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
package com.baomidou.mybatisplus.core.toolkit.condition;

/**
 * 谨慎的传递泛型
 * <p>主要的条件拼接都是这货干活</p>
 * <p>Create by HCL at 2018/05/29</p>
 */
public abstract class AbstractCondition<This, CLASS, COLUMN>
    implements ICondition<This, CLASS, COLUMN> {

    private final StringBuilder sb = new StringBuilder();

    @Override
    public This eq(COLUMN column, Object value) {
        sb.append(getColumn(column)).append(" = ").append(value);
        return typedSelf();
    }

    protected abstract String getColumn(COLUMN column);

    @SuppressWarnings("unchecked")
    protected This typedSelf() {
        return (This) this;
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
