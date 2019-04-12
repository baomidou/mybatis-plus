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
package com.baomidou.mybatisplus.core.injector;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 带有进行字段筛选功能
 *
 * @author miemie
 * @since 2019-04-12
 */
public abstract class ChooseTableField<Children> extends AbstractMethod {

    /**
     * 筛选条件集合
     */
    private List<Predicate<TableFieldInfo>> predicates = new ArrayList<>();

    /**
     * 设置字段删选函数
     * <p> 注意: 推荐设置的函数是: <strong>过滤掉</strong>不要的字段 </p>
     *
     * @param predicate 函数
     */
    @SuppressWarnings("unchecked")
    public Children addPredicate(Predicate<TableFieldInfo> predicate) {
        Assert.notNull(predicate, "this predicate can not be null !");
        this.predicates.add(predicate);
        return (Children) this;
    }

    /**
     * 进行字段筛选
     */
    protected Stream<TableFieldInfo> filters(List<TableFieldInfo> fieldInfos) {
        Stream<TableFieldInfo> stream = fieldInfos.stream();
        for (Predicate<TableFieldInfo> predicate : predicates) {
            stream = stream.filter(predicate);
        }
        return stream;
    }
}
