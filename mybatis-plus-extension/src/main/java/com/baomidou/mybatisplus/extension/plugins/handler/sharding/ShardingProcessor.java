package com.baomidou.mybatisplus.extension.plugins.handler.sharding;

import java.util.List;
import java.util.Map;

/**
 * @author zengzhihong
 */
public interface ShardingProcessor {

    /**
     * 分表执行
     *
     * @param strategy       策略
     * @param shardingValues 分片字段和字段值
     * @return 真实表名
     */
    String doSharding(ShardingStrategy strategy, Map<String, List<Object>> shardingValues);

}
