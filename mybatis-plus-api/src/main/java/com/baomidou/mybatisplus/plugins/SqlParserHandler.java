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
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.plugins;

import java.util.List;

import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.plugins.parser.ISqlParser;
import com.baomidou.mybatisplus.plugins.parser.ISqlParserFilter;
import com.baomidou.mybatisplus.plugins.parser.SqlInfo;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.PluginUtils;

/**
 * <p>
 * SQL 解析处理器
 * </p>
 *
 * @author hubin
 * @Date 2016-08-31
 */
public abstract class SqlParserHandler {

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
                int flag = 0;// 标记是否修改过 SQL
                String originalSql = (String) metaObject.getValue(PluginUtils.DELEGATE_BOUNDSQL_SQL);
                for (ISqlParser sqlParser : this.sqlParserList) {
                    SqlInfo sqlInfo = sqlParser.optimizeSql(metaObject, originalSql);
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

    public List<ISqlParser> getSqlParserList() {
        return sqlParserList;
    }

    public SqlParserHandler setSqlParserList(List<ISqlParser> sqlParserList) {
        this.sqlParserList = sqlParserList;
        return this;
    }

    public ISqlParserFilter getSqlParserFilter() {
        return sqlParserFilter;
    }

    public void setSqlParserFilter(ISqlParserFilter sqlParserFilter) {
        this.sqlParserFilter = sqlParserFilter;
    }
}
