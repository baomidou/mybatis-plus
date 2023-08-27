/*
 * Copyright (c) 2011-2023, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.parser;

import com.baomidou.mybatisplus.extension.parser.cache.JsqlParseCache;
import lombok.Setter;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;

/**
 * @author miemie
 * @since 2023-08-05
 */
public class JsqlParserGlobal {
    @Setter
    private static JsqlParserFunction<String, Statement> parserSingleFunc = CCJSqlParserUtil::parse;
    @Setter
    private static JsqlParserFunction<String, Statements> parserMultiFunc = CCJSqlParserUtil::parseStatements;
    @Setter
    private static JsqlParseCache jsqlParseCache;

    public static Statement parse(String sql) throws JSQLParserException {
        if (jsqlParseCache == null) {
            return parserSingleFunc.apply(sql);
        }
        Statement statement = jsqlParseCache.getStatement(sql);
        if (statement == null) {
            statement = parserSingleFunc.apply(sql);
            jsqlParseCache.putStatement(sql, statement);
        }
        return statement;
    }

    public static Statements parseStatements(String sql) throws JSQLParserException {
        if (jsqlParseCache == null) {
            return parserMultiFunc.apply(sql);
        }
        Statements statements = jsqlParseCache.getStatements(sql);
        if (statements == null) {
            statements = parserMultiFunc.apply(sql);
            jsqlParseCache.putStatements(sql, statements);
        }
        return statements;
    }
}
