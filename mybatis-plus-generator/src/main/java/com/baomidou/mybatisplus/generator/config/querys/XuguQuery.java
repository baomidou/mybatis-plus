/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
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
 * <p>
 * Xugu 表数据查询
 * </p>
 *
 * @author unique1319 lanjerry
 * @since 2020-10-26
 */
public class XuguQuery extends AbstractDbQuery {

    @Override
    public String tablesSql() {
        return "SELECT * FROM ALL_TABLES WHERE 1 = 1";
    }

    @Override
    public String tableFieldsSql() {
        return "SELECT B.COL_NAME,B.TYPE_NAME,B.COMMENTS, '' AS KEY FROM ALL_TABLES A INNER JOIN ALL_COLUMNS B ON A.TABLE_ID = B.TABLE_ID WHERE A.TABLE_NAME = '%s'";
    }

    @Override
    public String tableName() {
        return "TABLE_NAME";
    }

    @Override
    public String tableComment() {
        return "COMMENTS";
    }

    @Override
    public String fieldName() {
        return "COL_NAME";
    }

    @Override
    public String fieldType() {
        return "TYPE_NAME";
    }

    @Override
    public String fieldComment() {
        return "COMMENTS";
    }

    @Override
    public String fieldKey() {
        return "KEY";
    }
}
