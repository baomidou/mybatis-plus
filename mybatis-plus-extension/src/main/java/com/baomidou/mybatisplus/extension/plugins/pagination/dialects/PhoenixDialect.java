package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;

/**
 * @author: fly
 * Created date: 2019/12/20 11:39
 */
public class PhoenixDialect implements IDialect {
    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        String sql = originalSql + " limit " + FIRST_MARK + " offset " + SECOND_MARK;
        return new DialectModel(sql, limit, offset).setConsumerChain();
    }
}
