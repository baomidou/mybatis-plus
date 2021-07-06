/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.conditions.query;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.ChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 具有查询方法的定义
 *
 * @author miemie
 * @since 2018-12-19
 */
public interface ChainQuery<T> extends ChainWrapper<T> {

    /**
     * 获取集合
     *
     * @return 集合
     */
    default List<T> list() {
        return getBaseMapper().selectList(getWrapper());
    }

    /**
     * 获取集合Stream
     *
     * @return 集合Stream
     * @author dingqianwen
     */
    default Stream<T> listStream() {
        return this.list().stream();
    }

    /**
     * 转换返回类型
     * <p>
     * 转为其他对象并设置参数
     * <pre>
     * {@code
     *       ChainQuery<User> chainQuery = ...;
     *       List<UserVo> userVos = chainQuery.listConvert(e -> {
     *             UserVo userVo = new UserVo();
     *             userVo.setUsername(e.getUsername());
     *             userVo.setId(e.getId());
     *             // ...
     *             userVo.setExpand("");
     *             return userVo;
     *         });
     * }
     * </pre>
     *
     * <p>
     * 获取指定字段列表
     * <pre>
     * {@code
     *       ChainQuery<User> chainQuery = ...;
     *       List<Integer> userIds = chainQuery.listConvert(User::getId);
     * }
     * </pre>
     *
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element
     * @return r
     * @author dingqianwen
     */
    default <R> List<R> listConvert(Function<? super T, ? extends R> mapper) {
        return this.listStream().map(mapper).collect(Collectors.toList());
    }

    /**
     * 获取单个
     *
     * @return 单个
     */
    default T one() {
        return getBaseMapper().selectOne(getWrapper());
    }

    /**
     * 获取单个
     *
     * @return 单个
     * @since 3.3.0
     */
    default Optional<T> oneOpt() {
        return Optional.ofNullable(one());
    }

    /**
     * 获取 count
     *
     * @return count
     */
    default Integer count() {
        return SqlHelper.retCount(getBaseMapper().selectCount(getWrapper()));
    }

    /**
     * 判断数据是否存在
     *
     * @return true 存在 false 不存在
     * @author dingqianwen
     */
    default boolean exists() {
        return this.count() > 0;
    }

    /**
     * 获取分页数据
     *
     * @param page 分页条件
     * @return 分页数据
     */
    default <E extends IPage<T>> E page(E page) {
        return getBaseMapper().selectPage(page, getWrapper());
    }

}
