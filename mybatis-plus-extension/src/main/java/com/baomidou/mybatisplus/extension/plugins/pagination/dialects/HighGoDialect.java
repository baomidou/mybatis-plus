package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

/**
 * HighGo 数据库分页语句组装实现
 *
 * @author ivan
 * @since 2021-04-06
 */
public class HighGoDialect implements IDialect {

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
