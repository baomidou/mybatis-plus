package com.baomidou.mybatisplus.generator.config.querys;

/**
 * ClickHouse 表数据查询
 *
 * @author zhoumingyu
 * @since 2020-11-09
 */
public class ClickHouseQuery extends AbstractDbQuery {
    @Override
    public String tablesSql() {
        return "select * from `system`.tables t where 1=1 ";
    }

    @Override
    public String tableFieldsSql() {
        return "select * from `system`.columns c where `table` ='%s'";
    }

    @Override
    public String tableName() {
        return "name";
    }

    @Override
    public String tableComment() {
        return null;
    }

    @Override
    public String fieldName() {
        return "name";
    }

    @Override
    public String fieldType() {
        return "type";
    }

    @Override
    public String fieldComment() {
        return null;
    }

    @Override
    public String fieldKey() {
        return "default_kind";
    }
}
