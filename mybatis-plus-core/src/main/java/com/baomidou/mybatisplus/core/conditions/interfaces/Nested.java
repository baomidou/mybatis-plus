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
package com.baomidou.mybatisplus.core.conditions.interfaces;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 查询条件封装
 * <p>嵌套</p>
 * <li>泛型 Param 是具体需要运行函数的类(也是 wrapper 的子类)</li>
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
public interface Nested<Param, Children> extends Serializable {

    /**
     * ignore
     */
    default Children and(Function<Param, Param> func) {
        return and(true, func);
    }

    /**
     * AND 嵌套
     * <p>
     * 例: and(i -&gt; i.eq("name", "李白").ne("status", "活着"))
     * </p>
     *
     * @param condition 执行条件
     * @param func      函数
     * @return children
     */
    Children and(boolean condition, Function<Param, Param> func);

    /**
     * ignore
     */
    default Children or(Function<Param, Param> func) {
        return or(true, func);
    }

    /**
     * OR 嵌套
     * <p>
     * 例: or(i -&gt; i.eq("name", "李白").ne("status", "活着"))
     * </p>
     *
     * @param condition 执行条件
     * @param func      函数
     * @return children
     */
    Children or(boolean condition, Function<Param, Param> func);

    /**
     * ignore
     */
    default Children nested(Function<Param, Param> func) {
        return nested(true, func);
    }

    /**
     * 正常嵌套 不带 AND 或者 OR
     * <p>
     * 例: nested(i -&gt; i.eq("name", "李白").ne("status", "活着"))
     * </p>
     *
     * @param condition 执行条件
     * @param func      函数
     * @return children
     */
    Children nested(boolean condition, Function<Param, Param> func);
}
