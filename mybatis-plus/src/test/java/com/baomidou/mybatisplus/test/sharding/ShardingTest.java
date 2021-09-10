package com.baomidou.mybatisplus.test.sharding;

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
import org.apache.ibatis.plugin.Interceptor;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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

            long row = m.selectCount(wrapper);
            assertThat(row).as("查询记录数").isEqualTo(1);
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
