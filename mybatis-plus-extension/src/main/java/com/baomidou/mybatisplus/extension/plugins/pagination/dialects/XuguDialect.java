package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

public class XuguDialect implements IDialect{

    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        String sql = originalSql + " LIMIT " + FIRST_MARK + StringPool.COMMA + SECOND_MARK;
        return new DialectModel(sql, offset, limit).setConsumerChain();
    }
}
