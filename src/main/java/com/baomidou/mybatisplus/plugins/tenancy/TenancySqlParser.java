package com.baomidou.mybatisplus.plugins.tenancy;

import com.baomidou.mybatisplus.plugins.parser.AbstractSqlParser;
import com.baomidou.mybatisplus.plugins.parser.SqlInfo;

import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.update.Update;

/**
 * Created by jobob on 17/6/20.
 */
public class TenancySqlParser extends AbstractSqlParser {

    public TenancySqlParser(String sql, String dbType) {
        super(sql, dbType);
    }

    @Override
    public SqlInfo optimizeSql() {
        return null;
    }

    /**
     * <p>
     * select 语句处理
     * </p>
     *
     * @param selectBody
     */
    public void processSelectBody(SelectBody selectBody) {

    }

    /**
     * <p>
     * insert 语句处理
     * </p>
     *
     * @param insert
     */
    public void processInsert(Insert insert) {

    }

    /**
     * <p>
     * update 语句处理
     * </p>
     *
     * @param update
     */
    public void processUpdate(Update update) {

    }

}
