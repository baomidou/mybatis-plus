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
package com.baomidou.mybatisplus.extension.plugins.tenant;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ValueListExpression;

/**
 * 租户处理器（ TenantId 行级 ）
 *
 * @author hubin
 * @since 2017-08-31
 * @deprecated 3.3.3 @2020-07-24 use {@link MybatisPlusInterceptor} {@link TenantLineInnerInterceptor} {@link TenantLineHandler}
 */
@Deprecated
public interface TenantHandler {

    /**
     * 获取租户 ID 值表达式，支持多个 ID 条件查询
     * <p>
     * 支持自定义表达式，比如：tenant_id in (1,2) @since 2019-8-2
     * 多参请使用 {@link ValueListExpression}
     *
     * @param select 参数 true 表示为 select 下的 where 条件,false 表示 insert/update/delete 下的条件
     *               只有 select 下才允许多参,否则只支持单参
     * @return 租户 ID 值表达式
     */
    Expression getTenantId(boolean select);

    /**
     * 获取租户字段名
     * <p>
     * 默认字段名叫: tenant_id
     *
     * @return 租户字段名
     */
    default String getTenantIdColumn() {
        return "tenant_id";
    }

    /**
     * 根据表名判断是否进行过滤
     * <p>
     * 默认都要进行解析
     *
     * @param tableName 表名
     * @return 是否进行过滤, true:表示忽略，false:需要解析多租户字段
     */
    default boolean doTableFilter(String tableName) {
        return false;
    }
}
