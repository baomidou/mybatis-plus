package com.baomidou.mybatisplus.test.tenant;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.test.BaseDbTest;
import net.sf.jsqlparser.expression.LongValue;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.plugin.Interceptor;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that the multi-tenant plugin correctly injects tenant conditions into
 * nested SELECT statements that are triggered by {@code @Many} / {@code @One}
 * annotations.  Prior to the fix, nested selects executed via the
 * {@code DefaultResultSetHandler} bypassed the {@code Executor} interceptor
 * chain, so no tenant condition was added to those queries.
 *
 * @see <a href="https://github.com/baomidou/mybatis-plus/issues/6899">Issue #6899</a>
 */
public class TenantManyTest extends BaseDbTest<TenantManyTest.OrderMapper> {

    // ─── Entity classes ────────────────────────────────────────────────────────

    public static class Order implements Serializable {
        private Long id;
        private String name;
        private Integer tenantId;
        private List<OrderItem> items;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getTenantId() { return tenantId; }
        public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
        public List<OrderItem> getItems() { return items; }
        public void setItems(List<OrderItem> items) { this.items = items; }
    }

    public static class OrderItem implements Serializable {
        private Long id;
        private Long orderId;
        private String product;
        private Integer tenantId;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getProduct() { return product; }
        public void setProduct(String product) { this.product = product; }
        public Integer getTenantId() { return tenantId; }
        public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    }

    // ─── Mapper interfaces ─────────────────────────────────────────────────────

    public interface OrderItemMapper {
        @Select("SELECT id, order_id, product, tenant_id FROM order_item WHERE order_id = #{orderId}")
        List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
    }

    @Results(id = "orderWithItems", value = {
        @Result(column = "id", property = "id"),
        @Result(column = "name", property = "name"),
        @Result(column = "tenant_id", property = "tenantId"),
        @Result(
            column = "id",
            property = "items",
            many = @Many(select = "com.baomidou.mybatisplus.test.tenant.TenantManyTest$OrderItemMapper.findByOrderId")
        )
    })
    @Select("SELECT id, name, tenant_id FROM t_order WHERE id = #{id}")
    public interface OrderMapper {
        Order findWithItems(@Param("id") Long id);

        @Select("SELECT id, name, tenant_id FROM t_order WHERE id = #{id}")
        Order findSimple(@Param("id") Long id);
    }

    // ─── Test ──────────────────────────────────────────────────────────────────

    @Test
    void nestedSelectViaManyIsFilteredByTenant() {
        // Insert data for tenant 1
        jdbcTemplate.execute("INSERT INTO t_order (id, name, tenant_id) VALUES (1, 'order-A', 1)");
        jdbcTemplate.execute("INSERT INTO order_item (id, order_id, product, tenant_id) VALUES (10, 1, 'apple', 1)");

        // Insert data for tenant 2 (should be invisible to queries under tenant 1)
        jdbcTemplate.execute("INSERT INTO t_order (id, name, tenant_id) VALUES (2, 'order-B', 2)");
        jdbcTemplate.execute("INSERT INTO order_item (id, order_id, product, tenant_id) VALUES (20, 1, 'banana', 2)");
        jdbcTemplate.execute("INSERT INTO order_item (id, order_id, product, tenant_id) VALUES (21, 2, 'cherry', 2)");

        doTest(mapper -> {
            // The main SELECT (t_order) gets the tenant filter from beforeQuery.
            Order order = mapper.findWithItems(1L);
            assertThat(order).as("Order for tenant 1 should be found").isNotNull();
            assertThat(order.getTenantId()).isEqualTo(1);

            // The nested SELECT (order_item via @Many) must also get the tenant filter.
            // Without the fix, items with tenant_id = 2 (id=20) would also be returned.
            assertThat(order.getItems()).as("@Many nested select must respect tenant filter").hasSize(1);
            assertThat(order.getItems().get(0).getTenantId()).isEqualTo(1);
            assertThat(order.getItems().get(0).getProduct()).isEqualTo("apple");
        });
    }

    // ─── Test infrastructure ───────────────────────────────────────────────────

    @Override
    protected List<Interceptor> interceptors() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // Tenant 1 is the active tenant for all queries
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(() -> new LongValue(1)));
        return Collections.singletonList(interceptor);
    }

    @Override
    protected List<Class<?>> otherMapper() {
        return Collections.singletonList(OrderItemMapper.class);
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList(
            "DROP TABLE IF EXISTS t_order",
            "CREATE TABLE t_order (" +
                "id BIGINT NOT NULL, " +
                "name VARCHAR(50), " +
                "tenant_id INTEGER NOT NULL, " +
                "PRIMARY KEY (id)" +
                ")",
            "DROP TABLE IF EXISTS order_item",
            "CREATE TABLE order_item (" +
                "id BIGINT NOT NULL, " +
                "order_id BIGINT NOT NULL, " +
                "product VARCHAR(50), " +
                "tenant_id INTEGER NOT NULL, " +
                "PRIMARY KEY (id)" +
                ")"
        );
    }
}
