package com.baomidou.mybatisplus.test.sharding;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author zengzhihong
 */
public interface ShardingOrderMapper extends BaseMapper<ShardingOrder> {

    int insert1(@Param("orderId") Long orderId);
}
