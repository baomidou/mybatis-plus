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
