package com.baomidou.mybatisplus.test.h2.sharding.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.test.h2.sharding.model.ShardingOrder;
import org.apache.ibatis.annotations.Param;

/**
 * @author zengzhihong
 */
public interface ShardingOrderMapper extends BaseMapper<ShardingOrder> {

    int insert1(@Param("orderId") Long orderId);


}
