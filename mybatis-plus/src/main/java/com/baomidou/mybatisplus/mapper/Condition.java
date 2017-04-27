/**
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
 * WARRANTIES OR EntityWrapperS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.mapper;

import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * 条件查询构造器
 * </p>
 *
 * @author hubin Caratacus
 * @date 2016-11-7
 */
@SuppressWarnings({"rawtypes", "serial"})
public class Condition extends Wrapper {

    /**
     * 构建一个Empty条件构造 避免传递参数使用null
     */
    public static final Condition EMPTY = Condition.create();

    /**
     * 获取实例
     */
    @Deprecated
    public static Condition instance() {
        return Condition.create();
    }

    /**
     * 获取实例
     */
    public static Condition create() {
        return new Condition();
    }

    /**
     * SQL 片段
     */
    @Override
    public String getSqlSegment() {
        if (SqlHelper.isEmptyOfWrapper(this)) {
            return null;
        }
        /*
         * 无条件
		 */
        String sqlWhere = sql.toString();
        if (StringUtils.isEmpty(sqlWhere)) {
            return null;
        }
        /*
         * 根据当前实体判断是否需要将WHERE替换成 AND 增加实体不为空但所有属性为空的情况
		 */
        if (isWhere != null) {
            sqlWhere = isWhere ? sqlWhere : sqlWhere.replaceFirst("WHERE", AND_OR);
        }
        return sqlWhere;
    }
}
