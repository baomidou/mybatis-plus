/*
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
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension.handlers;

import java.util.List;

import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.core.parser.ISqlParserFilter;
import com.baomidou.mybatisplus.core.parser.SqlInfo;
import com.baomidou.mybatisplus.core.parser.SqlParserHelper;
import com.baomidou.mybatisplus.core.parser.SqlParserInfo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * SQL 解析处理器
 * </p>
 *
 * @author hubin
 * @since 2016-08-31
 */
@Data
@Accessors(chain = true)
public abstract class AbstractSqlParserHandler {

    private List<ISqlParser> sqlParserList;
    private ISqlParserFilter sqlParserFilter;

    /**
     * 拦截 SQL 解析执行
     */
    protected void sqlParser(MetaObject metaObject) {
        if (null != metaObject) {
            if (null != this.sqlParserFilter && this.sqlParserFilter.doFilter(metaObject)) {
                return;
            }
            // SQL 解析
            if (CollectionUtils.isNotEmpty(this.sqlParserList)) {
                // @SqlParser(filter = true) 跳过该方法解析
                SqlParserInfo sqlParserInfo = SqlParserHelper.getSqlParserInfo(metaObject);
                if (null != sqlParserInfo && sqlParserInfo.getFilter()) {
                    return;
                }
                // 标记是否修改过 SQL
                int flag = 0;
                String originalSql = (String) metaObject.getValue(PluginUtils.DELEGATE_BOUNDSQL_SQL);
                for (ISqlParser sqlParser : this.sqlParserList) {
                    SqlInfo sqlInfo = sqlParser.parser(metaObject, originalSql);
                    if (null != sqlInfo) {
                        originalSql = sqlInfo.getSql();
                        ++flag;
                    }
                }
                if (flag >= 1) {
                    metaObject.setValue(PluginUtils.DELEGATE_BOUNDSQL_SQL, originalSql);
                }
            }
        }
    }
}
