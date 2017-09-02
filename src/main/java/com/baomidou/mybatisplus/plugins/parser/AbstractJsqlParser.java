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
package com.baomidou.mybatisplus.plugins.parser;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

/**
 * <p>
 * 抽象 SQL 解析类
 * </p>
 *
 * @author hubin
 * @Date 2017-06-20
 */
public abstract class AbstractJsqlParser implements ISqlParser {

    // 日志
    protected final Log logger = LogFactory.getLog(this.getClass());

    /**
     * <p>
     * 获取优化 SQL 方法
     * </p>
     *
     * @param metaObject 元对象
     * @param sql        SQL 语句
     * @return SQL 信息
     */

    @Override
    public SqlInfo optimizeSql(MetaObject metaObject, String sql) {
        if (this.allowProcess(metaObject)) {
            try {
                Statement statement = CCJSqlParserUtil.parse(sql);
                logger.debug("Original SQL: " + sql);
                if (null != statement) {
                    return this.processParser(statement);
                }
            } catch (JSQLParserException e) {
                throw new MybatisPlusException("Failed to process, please exclude the tableName or statementId.\n Error SQL: " + sql, e);
            }
        }
        return null;
    }

    /**
     * <p>
     * 执行 SQL 解析
     * </p>
     *
     * @param statement JsqlParser Statement
     * @return
     */
    public abstract SqlInfo processParser(Statement statement);

    /**
     * <p>
     * 判断是否允许执行<br>
     * 例如：逻辑删除只解析 delete , update 操作
     * </p>
     *
     * @param metaObject 元对象
     * @return true
     */
    public boolean allowProcess(MetaObject metaObject) {
        return true;
    }
}
