package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

/**
 * 神通数据分页方言
 *
 * @author wangheli
 * @version 1.0.0
 * @since 2020/7/25 9:13
 */
public class OscarDialect implements IDialect {

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        String sql = originalSql + " LIMIT " + FIRST_MARK + " OFFSET " + SECOND_MARK;
        return new DialectModel(sql, offset, limit).setConsumerChain();
    }

}
