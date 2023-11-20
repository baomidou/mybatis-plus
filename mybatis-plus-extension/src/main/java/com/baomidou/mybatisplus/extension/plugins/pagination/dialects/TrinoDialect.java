package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

/**
 * Trino 数据库分页语句组装实现
 *
 * @author hushunbo
 * @since 2023-10-06
 */
public class TrinoDialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        StringBuilder sql = new StringBuilder(originalSql);
        if (offset != 0L) {
            sql.append(" OFFSET ").append(FIRST_MARK);
            sql.append(" LIMIT ").append(SECOND_MARK);
            return new DialectModel(sql.toString(), offset, limit).setConsumerChain();
        } else {
            sql.append(" LIMIT ").append(FIRST_MARK);
            return new DialectModel(sql.toString(), limit).setConsumer(true);
        }
    }
}
