package com.baomidou.mybatisplus.plugins.parser;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * <p>
 * 抽象 SQL 解析类
 * </p>
 */
public abstract class AbstractSqlParser {

    // 日志
    protected static final Log logger = LogFactory.getLog(AbstractSqlParser.class);
    private String sql;// SQL 语句
    private String dbType; // 数据库类型

    public AbstractSqlParser(String sql, String dbType) {
        this.sql = sql;
        this.dbType = dbType;
    }

    /**
     * <p>
     * 获取优化 SQL 方法
     * </p>
     *
     * @return SQL 信息
     */
    public abstract SqlInfo optimizeSql();

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
}
