package com.baomidou.mybatisplus.test.h2.sharding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.baomidou.mybatisplus.test.h2.sharding.mapper.ShardingOrderMapper;
import com.baomidou.mybatisplus.test.h2.sharding.model.ShardingOrder;

/**
 * @author zengzhihong
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-sharding-h2.xml"})
public class ShardingTest {

    private Log log = LogFactory.getLog(getClass());

    @Autowired
    ShardingOrderMapper shardingOrderMapper;

    private final LocalDateTime date1 = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
    private final LocalDateTime date2 = LocalDateTime.of(2021, 2, 1, 0, 0, 0);
    private final LocalDateTime date3 = LocalDateTime.of(2021, 3, 1, 0, 0, 0);

    @Test
    void insertTest() {
        ShardingOrder shardingOrder1 =
                new ShardingOrder().setOrderId(1L).setSubject("test1").setCreateTime(date1);
        shardingOrderMapper.insert(shardingOrder1);
        shardingOrderMapper.insert1(2L);
    }

    @Test
    void insertBatchTest() {
        ShardingOrder shardingOrder1 =
                new ShardingOrder().setOrderId(1L).setSubject("test1").setCreateTime(date1);
        ShardingOrder shardingOrder2 =
                new ShardingOrder().setOrderId(2L).setSubject("test2").setCreateTime(date2);
        ShardingOrder shardingOrder3 =
                new ShardingOrder().setOrderId(3L).setSubject("test3").setCreateTime(date3);
        List<ShardingOrder> list = new ArrayList<>();
        list.add(shardingOrder1);
        list.add(shardingOrder2);
        list.add(shardingOrder3);
        String sqlStatement = SqlHelper.getSqlStatement(ShardingOrderMapper.class, SqlMethod.INSERT_ONE);
        SqlHelper.executeBatch(ShardingOrder.class, log, list, 1000,
                (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));

    }

    @Test
    void selectTest() {
        insertBatchTest();
        shardingOrderMapper.selectPage(new Page<>(),
                Wrappers.<ShardingOrder>lambdaQuery().eq(ShardingOrder::getOrderId, 1L));

        Map<String, Object> params = new HashMap<>();
        params.put("order_id", 1L);
        params.put("create_time", date1);
        shardingOrderMapper.selectByMap(params);

        shardingOrderMapper.selectById(1L);

        shardingOrderMapper.selectList(Wrappers.<ShardingOrder>lambdaQuery().eq(ShardingOrder::getCreateTime, date1)
                .in(ShardingOrder::getOrderId, Arrays.asList(1L, 2L)));

        shardingOrderMapper.selectMaps(Wrappers.<ShardingOrder>lambdaQuery().in(ShardingOrder::getOrderId, 3L
        ).eq(ShardingOrder::getCreateTime,
                date1));

        shardingOrderMapper.selectBatchIds(Arrays.asList(1L, 2L));

        final Integer count =
                shardingOrderMapper.selectCount(Wrappers.<ShardingOrder>lambdaQuery().eq(ShardingOrder::getOrderId, 1L
                ).eq(ShardingOrder::getCreateTime,
                        date1));

        shardingOrderMapper.selectMapsPage(new Page<>(),
                Wrappers.<ShardingOrder>lambdaQuery().eq(ShardingOrder::getOrderId, 1L));

        shardingOrderMapper.selectOne(Wrappers.<ShardingOrder>lambdaQuery().eq(ShardingOrder::getOrderId, 1L));

        shardingOrderMapper.selectPage(new Page<>(),
                Wrappers.<ShardingOrder>lambdaQuery().eq(ShardingOrder::getOrderId, 1L));

    }


    @Test
    void updateTest() {
        ShardingOrder order = new ShardingOrder();
        order.setOrderId(1L);
        order.setSubject("66");
        shardingOrderMapper.update(order, Wrappers.<ShardingOrder>lambdaQuery().eq(ShardingOrder::getOrderId, 1L));
        shardingOrderMapper.updateById(order);
    }

    @Test
    void deleteTest() {
        shardingOrderMapper.delete(Wrappers.<ShardingOrder>lambdaQuery().eq(ShardingOrder::getOrderId, 1L));
        shardingOrderMapper.deleteById(1L);
        shardingOrderMapper.deleteBatchIds(Arrays.asList(1L, 2L));
        Map<String, Object> params = new HashMap<>();
        params.put("order_id", 1L);
        shardingOrderMapper.deleteByMap(params);
    }

}
