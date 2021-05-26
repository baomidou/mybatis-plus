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
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.conditions.ChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.apache.ibatis.session.defaults.DefaultSqlSession;

import java.util.List;
import java.util.Optional;

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
     * 获取单个
     *
     * @return 单个
     */
    default T one() {
        return getBaseMapper().selectOne(getWrapper());
    }

    /**
     * 查询一条，忽略 mybatis Expected one result (or null) to be returned by selectOne(), but found: .. ,默认获取第一条记录
     *
     * @return 单个
     * @see DefaultSqlSession#selectOne(java.lang.String, java.lang.Object)  code:list.get(0)
     */
    default T first() {
        List<T> list = this.list();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
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
     * @return true：存在
     */
    default boolean isExists() {
        int count = this.count();
        return count > 0;
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
