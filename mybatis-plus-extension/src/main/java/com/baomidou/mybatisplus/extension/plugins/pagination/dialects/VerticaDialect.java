package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

/**
 * Vertica 数据库分页语句组装实现
 *
 * @author msJava
 * @Date 2022-02-08
 */
public class VerticaDialect implements IDialect{

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        StringBuilder sql = new StringBuilder(originalSql).append(" LIMIT ").append(FIRST_MARK);
        if (offset != 0L) {
            sql.append(" OFFSET ").append(SECOND_MARK);
            return new DialectModel(sql.toString(), limit, offset).setConsumerChain();
        } else {
            return new DialectModel(sql.toString(), limit).setConsumer(true);
        }
    }
}
