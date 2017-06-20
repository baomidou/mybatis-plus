/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.plugins.pagination.optimize;

import com.alibaba.druid.sql.PagerUtils;
import com.baomidou.mybatisplus.parser.AbstractSqlParser;
import com.baomidou.mybatisplus.parser.SqlInfo;

/**
 * <p>
 * Ali Druid Count Optimize
 * </p>
 *
 * @author hubin
 * @Date 2017-06-20
 */
public class AliDruidCountOptimize extends AbstractSqlParser {

    public AliDruidCountOptimize(String sql, String dbType) {
        super(sql, dbType);
    }

    @Override
    public SqlInfo optimizeSql() {
        String sql = this.getSql();
        String dbType = this.getDbType();
        if (logger.isDebugEnabled()) {
            logger.debug(" AliDruidCountOptimize sql=" + sql + ", dbType=" + dbType);
        }
        SqlInfo sqlInfo = SqlInfo.newInstance();
        // 调整SQL便于解析
        String tempSql = sql.replaceAll("(?i)ORDER[\\s]+BY", "ORDER BY");
        int orderByIndex = tempSql.toUpperCase().lastIndexOf("ORDER BY");
        sqlInfo.setOrderBy(orderByIndex > -1);
        sqlInfo.setSql(PagerUtils.count(sql, dbType));
        return sqlInfo;
    }
}
