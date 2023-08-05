package com.baomidou.mybatisplus.extension.parser.cache;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;

/**
 * @author miemie
 * @since 2023-08-05
 */
public interface JsqlParseCache {

    void putStatement(String sql, Statement value);

    void putStatements(String sql, Statements value);

    Statement getStatement(String sql);

    Statements getStatements(String sql);
}
