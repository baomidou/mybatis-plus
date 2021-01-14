package com.baomidou.mybatisplus.extension.plugins.handler.sharding;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.StringPool;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zengzhihong
 */
@EqualsAndHashCode
public class ShardingStrategy {

    /**
     * 逻辑表名 如:order_info
     */
    @Getter
    @Setter
    private String logicTable;

    /**
     * 分片字段 , 隔开 如: id,create_time
     */
    private String column;

    /**
     * 分片规则 {@link ShardingRuleEnum}
     */
    @Getter
    @Setter
    private ShardingRuleEnum rule;

    @Getter
    @Setter
    private Class<? extends ShardingProcessor> processor;

    private List<String> shardingColumnList;

    public ShardingStrategy(String logicTable, String column, Class<? extends ShardingProcessor> processor) {
        this(logicTable, column, ShardingRuleEnum.ABSOLUTE, processor);
    }

    public ShardingStrategy(String logicTable, String column, ShardingRuleEnum rule, Class<? extends ShardingProcessor> processor) {
        this.logicTable = logicTable;
        this.rule = rule;
        this.processor = processor;
        this.setColumn(column);
    }

    public void setColumn(String column) {
        this.column = column;
        this.shardingColumnList = Arrays.asList(this.column.split(StringPool.COMMA));
    }

    public boolean containsColumn(String column) {
        return shardingColumnList.contains(column);
    }

}
