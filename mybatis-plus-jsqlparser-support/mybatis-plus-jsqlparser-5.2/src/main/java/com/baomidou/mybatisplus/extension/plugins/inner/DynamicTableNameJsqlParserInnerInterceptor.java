/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.extension.DynamicTableNameHandler;
import com.baomidou.mybatisplus.extension.parser.JsqlParserGlobal;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import lombok.Setter;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.UnsupportedStatement;

/**
 * 动态表处理器 (基于JsqlParser解析器)
 * <p>默认情况下,如果JsqlParser解析不了,则调用父类的解析进行处理</p>
 *
 * @author nieqiurong
 * @see DynamicTableNameHandler
 * @since 3.5.11
 */
@Setter
public class DynamicTableNameJsqlParserInnerInterceptor extends DynamicTableNameInnerInterceptor {

    /**
     * 是否忽略解析异常
     */
    private boolean ignoreException = false;

    /**
     * 当JsqlParser无法解析语句时是否进行调用父类继续解析处理
     *
     * @see com.baomidou.mybatisplus.core.toolkit.TableNameParser
     */
    private boolean shouldFallback = true;


    @Deprecated
    public DynamicTableNameJsqlParserInnerInterceptor() {
    }

    public DynamicTableNameJsqlParserInnerInterceptor(TableNameHandler tableNameHandler) {
        super(tableNameHandler);
    }

    @Override
    protected String processTableName(String sql) {
        boolean unsupported = false;
        try {
            Statement statement = JsqlParserGlobal.parse(sql);
            statement.accept(new DynamicTableNameHandler(sql, super.getTableNameHandler()));
            if (statement instanceof UnsupportedStatement) {
                unsupported = true;
                return super.processTableName(sql);
            }
            return statement.toString();
        } catch (Exception exception) {
            return handleFallback(unsupported, sql, exception);
        }
    }

    private String handleFallback(boolean unsupported, String sql, Exception originalException) {
        Exception exception = originalException;
        if (!unsupported || shouldFallback) {
            try {
                return super.processTableName(sql);
            } catch (Exception e) {
                exception = e;
            }
        }
        if (ignoreException) {
            return sql;
        }
        throw new MybatisPlusException("Table name processing failed : ", exception);
    }

}
