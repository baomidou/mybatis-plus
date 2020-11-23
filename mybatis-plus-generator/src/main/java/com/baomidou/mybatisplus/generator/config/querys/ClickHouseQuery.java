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
package com.baomidou.mybatisplus.generator.config.querys;

/**
 * ClickHouse 表数据查询
 *
 * @author zhoumingyu
 * @since 2020-11-09
 */
public class ClickHouseQuery extends AbstractDbQuery {
    @Override
    public String tablesSql() {
        return "select * from `system`.tables t where 1=1 ";
    }

    @Override
    public String tableFieldsSql() {
        return "select * from `system`.columns c where `table` ='%s'";
    }

    @Override
    public String tableName() {
        return "name";
    }

    @Override
    public String tableComment() {
        return null;
    }

    @Override
    public String fieldName() {
        return "name";
    }

    @Override
    public String fieldType() {
        return "type";
    }

    @Override
    public String fieldComment() {
        return null;
    }

    @Override
    public String fieldKey() {
        return "default_kind";
    }
}
