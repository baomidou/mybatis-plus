package com.baomidou.mybatisplus.extension.parser;

import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * @author miemie
 * @since 2020-06-22
 */
public abstract class JsqlParserSupport {

    /**
     * 日志
     */
    protected final Log logger = LogFactory.getLog(this.getClass());

    public String parserSingle(String sql, Object obj) {
        if (logger.isDebugEnabled()) {
            logger.debug("Original SQL: " + sql);
        }
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            return processParser(statement, 0, obj);
        } catch (JSQLParserException e) {
            throw ExceptionUtils.mpe("Failed to process, Error SQL: %s", e, sql);
        }
    }

    public String parserMulti(String sql, Object obj) {
        if (logger.isDebugEnabled()) {
            logger.debug("Original SQL: " + sql);
        }
        try {
            // fixed github pull/295
            StringBuilder sb = new StringBuilder();
            Statements statements = CCJSqlParserUtil.parseStatements(sql);
            int i = 0;
            for (Statement statement : statements.getStatements()) {
                if (i > 0) {
                    sb.append(StringPool.SEMICOLON);
                }
                sb.append(processParser(statement, i, obj));
                i++;
            }
            return sb.toString();
        } catch (JSQLParserException e) {
            throw ExceptionUtils.mpe("Failed to process, Error SQL: %s", e, sql);
        }
    }

    /**
     * 执行 SQL 解析
     *
     * @param statement JsqlParser Statement
     * @return sql
     */
    protected String processParser(Statement statement, int index, Object obj) {
        if (statement instanceof Insert) {
            this.processInsert((Insert) statement, index, obj);
        } else if (statement instanceof Select) {
            this.processSelect((Select) statement, index, obj);
        } else if (statement instanceof Update) {
            this.processUpdate((Update) statement, index, obj);
        } else if (statement instanceof Delete) {
            this.processDelete((Delete) statement, index, obj);
        }
        final String sql = statement.toString();
        if (logger.isDebugEnabled()) {
            logger.debug("parser sql: " + sql);
        }
        return sql;
    }

    /**
     * 新增
     */
    protected void processInsert(Insert insert, int index, Object obj) {
        throw new UnsupportedOperationException();
    }

    /**
     * 删除
     */
    protected void processDelete(Delete delete, int index, Object obj) {
        throw new UnsupportedOperationException();
    }

    /**
     * 更新
     */
    protected void processUpdate(Update update, int index, Object obj) {
        throw new UnsupportedOperationException();
    }

    /**
     * 查询
     */
    protected void processSelect(Select select, int index, Object obj) {
        throw new UnsupportedOperationException();
    }
}
