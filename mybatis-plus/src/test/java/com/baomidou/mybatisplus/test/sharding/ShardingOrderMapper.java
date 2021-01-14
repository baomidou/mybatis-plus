package com.baomidou.mybatisplus.test.sharding;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author zengzhihong
 */
public interface ShardingOrderMapper extends BaseMapper<ShardingOrder> {

    int insert1(@Param("orderId") Long orderId);
}
