package com.baomidou.mybatisplus.test.sharding;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.plugin.Interceptor;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.sharding.ShardingProcessor;
import com.baomidou.mybatisplus.extension.plugins.handler.sharding.ShardingRuleEnum;
import com.baomidou.mybatisplus.extension.plugins.handler.sharding.ShardingStrategy;
import com.baomidou.mybatisplus.extension.plugins.inner.ShardingInnerInterceptor;
import com.baomidou.mybatisplus.test.BaseDbTest;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhihong
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ShardingTest extends BaseDbTest<ShardingOrderMapper> {

    @Test
    void test() {
        String logicTableName = "sharding_order";
        long orderId = IdWorker.getId();
        long mod = orderId % 3;
        String actualTableName = logicTableName + "_" + mod;
        System.out.println("\n\n\n逻辑表名:" + logicTableName + ",orderId:" + orderId + ",取模：" + mod + ",真实表名:" + actualTableName + "\n\n\n");

        ShardingOrder entity = new ShardingOrder().setOrderId(orderId).setSubject("test1").setCreateTime(LocalDateTime.now());
        doTestAutoCommit(m -> {
            m.insert(entity);

            Wrapper<ShardingOrder> wrapper = Wrappers.<ShardingOrder>lambdaQuery().eq(ShardingOrder::getOrderId, orderId);
            int affectedRow = m.update(entity, wrapper);
            assertThat(affectedRow).as("更新成功").isEqualTo(1);

            affectedRow = m.selectCount(wrapper);
            assertThat(affectedRow).as("查询记录数").isEqualTo(1);
        });

        doTest(m -> {
            final List<ShardingOrder> orderList = jdbcTemplate.query("SELECT * FROM " + actualTableName, (rs, rowNum) -> {
                ShardingOrder order = new ShardingOrder();
                order.setOrderId(rs.getLong("order_id"));
                return order;
            });
            assertThat(orderList.size()).as("jdbc查询记录数").isEqualTo(1);
            assertThat(orderList.get(0).getOrderId()).isEqualTo(orderId);

            int affectedRow = jdbcTemplate.update("UPDATE " + actualTableName + " SET subject = ?", "test");
            assertThat(affectedRow).as("jdbc更新成功").isEqualTo(1);

        });

        doTestAutoCommit(m -> {
            int affectedRow = m.delete(Wrappers.<ShardingOrder>lambdaQuery().eq(ShardingOrder::getOrderId, orderId));
            assertThat(affectedRow).as("删除成功").isEqualTo(1);
        });
    }

//    @Test
//    void insertBatchTest() {
//        ShardingOrder shardingOrder1 =
//            new ShardingOrder().setOrderId(1L).setSubject("test1").setCreateTime(date1);
//        ShardingOrder shardingOrder2 =
//            new ShardingOrder().setOrderId(2L).setSubject("test2").setCreateTime(date2);
//        ShardingOrder shardingOrder3 =
//            new ShardingOrder().setOrderId(3L).setSubject("test3").setCreateTime(date3);
//        List<ShardingOrder> list = new ArrayList<>();
//        list.add(shardingOrder1);
//        list.add(shardingOrder2);
//        list.add(shardingOrder3);
//        String sqlStatement = SqlHelper.getSqlStatement(ShardingOrderMapper.class, SqlMethod.INSERT_ONE);
//        SqlHelper.executeBatch(ShardingOrder.class, log, list, 1000,
//            (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
//
//    }

//    @Test
//    void selectTest() {
//        insertBatchTest();
//        shardingOrderMapper.selectPage(new Page<>(),
//            Wrappers.<ShardingOrder>lambdaQuery().eq(ShardingOrder::getOrderId, 1L));
//
//        Map<String, Object> params = new HashMap<>();
//        params.put("order_id", 1L);
//        params.put("create_time", date1);
//        shardingOrderMapper.selectByMap(params);
//
//        shardingOrderMapper.selectById(1L);
//
//        shardingOrderMapper.selectList(Wrappers.<ShardingOrder>lambdaQuery().eq(ShardingOrder::getCreateTime, date1)
//            .in(ShardingOrder::getOrderId, Arrays.asList(1L, 2L)));
//
//        shardingOrderMapper.selectMaps(Wrappers.<ShardingOrder>lambdaQuery().in(ShardingOrder::getOrderId, 3L
//        ).eq(ShardingOrder::getCreateTime,
//            date1));
//
//        shardingOrderMapper.selectBatchIds(Arrays.asList(1L, 2L));
//
//        final Integer count =
//            shardingOrderMapper.selectCount(Wrappers.<ShardingOrder>lambdaQuery().eq(ShardingOrder::getOrderId, 1L
//            ).eq(ShardingOrder::getCreateTime,
//                date1));
//
//        shardingOrderMapper.selectMapsPage(new Page<>(),
//            Wrappers.<ShardingOrder>lambdaQuery().eq(ShardingOrder::getOrderId, 1L));
//
//        shardingOrderMapper.selectOne(Wrappers.<ShardingOrder>lambdaQuery().eq(ShardingOrder::getOrderId, 1L));
//
//        shardingOrderMapper.selectPage(new Page<>(),
//            Wrappers.<ShardingOrder>lambdaQuery().eq(ShardingOrder::getOrderId, 1L));
//
//    }
//
//
//    @Test
//    void updateTest() {
//        ShardingOrder order = new ShardingOrder();
//        order.setOrderId(1L);
//        order.setSubject("66");
//        shardingOrderMapper.update(order, Wrappers.<ShardingOrder>lambdaQuery().eq(ShardingOrder::getOrderId, 1L));
//        shardingOrderMapper.updateById(order);
//    }
//
//    @Test
//    void deleteTest() {
//        shardingOrderMapper.delete(Wrappers.<ShardingOrder>lambdaQuery().eq(ShardingOrder::getOrderId, 1L));
//        shardingOrderMapper.deleteById(1L);
//        shardingOrderMapper.deleteBatchIds(Arrays.asList(1L, 2L));
//        Map<String, Object> params = new HashMap<>();
//        params.put("order_id", 1L);
//        shardingOrderMapper.deleteByMap(params);
//    }


    @Override
    protected List<Interceptor> interceptors() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        final ShardingStrategy orderShardingHashStrategy = new ShardingStrategy("sharding_order",
                "order_id", ShardingRuleEnum.ABSOLUTE, OrderShardingProcessor.class);
        interceptor.addInnerInterceptor(new ShardingInnerInterceptor(orderShardingHashStrategy));
        return Collections.singletonList(interceptor);
    }

    @Override
    protected List<String> tableSql() {
        return Lists.newArrayList("CREATE TABLE IF NOT EXISTS sharding_order_0\n" +
                        "            (\n" +
                        "                order_id     BIGINT(20)  NOT NULL AUTO_INCREMENT,\n" +
                        "                subject   VARCHAR(30) NULL DEFAULT NULL,\n" +
                        "                create_time  DATETIME      NULL,\n" +
                        "                PRIMARY KEY (order_id)\n" +
                        "            )",
                "CREATE TABLE IF NOT EXISTS sharding_order_1\n" +
                        "        (\n" +
                        "            order_id     BIGINT(20)  NOT NULL AUTO_INCREMENT,\n" +
                        "            subject   VARCHAR(30) NULL DEFAULT NULL,\n" +
                        "            create_time  DATETIME      NULL,\n" +
                        "        PRIMARY KEY (order_id)\n" +
                        ")",
                "CREATE TABLE IF NOT EXISTS sharding_order_2\n" +
                        "        (\n" +
                        "            order_id     BIGINT(20)  NOT NULL AUTO_INCREMENT,\n" +
                        "            subject   VARCHAR(30) NULL DEFAULT NULL,\n" +
                        "            create_time  DATETIME      NULL,\n" +
                        "        PRIMARY KEY (order_id)\n" +
                        ")");
    }

    static class OrderShardingProcessor implements ShardingProcessor {

        @Override
        public String doSharding(ShardingStrategy strategy, Map<String, List<Object>> shardingValues) {
            final List<Object> values = shardingValues.get("order_id");
            // value是一个集合 比如 in查询
            // 不管有几个value 此处最终return一个真实表名
            // 未携带分配字段会报错 所以shardingValues 一定是 notEmpty
            return "sharding_order_" + (Long) values.get(0) % 3;
        }
    }
}
