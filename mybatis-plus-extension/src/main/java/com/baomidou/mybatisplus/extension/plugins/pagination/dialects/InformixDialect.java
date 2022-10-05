package com.baomidou.mybatisplus.extension.plugins.pagination.dialects;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;
public class InformixDialect implements IDialect{
    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        /*StringBuilder ret = new StringBuilder();
        ret.append(String.format("select skip %s first %s ", FIRST_MARK+"",SECOND_MARK+""));
        ret.append(originalSql.replaceFirst("(?i)select", ""));
        return new DialectModel(ret.toString(), offset, limit).setConsumerChain();*/
        StringBuilder ret = new StringBuilder();
        ret.append(String.format("select skip %s first %s ", offset+"",limit+""));
        ret.append(originalSql.replaceFirst("(?i)select", ""));
        return new DialectModel(ret.toString());
    }
}