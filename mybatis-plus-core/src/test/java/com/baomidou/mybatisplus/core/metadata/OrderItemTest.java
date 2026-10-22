/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.metadata;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 钉住 {@link OrderItem} 的排序方向、空白处理与表达式透传语义。
 *
 * @author TaoYu
 */
class OrderItemTest {

    @Test
    void ascAndDescSetDirectionAndStripWhitespaceFromColumn() {
        OrderItem asc = OrderItem.asc(" user \tname\n");
        assertThat(asc.getColumn()).isEqualTo("username");
        assertThat(asc.isAsc()).isTrue();

        OrderItem desc = OrderItem.desc(" create_time ");
        assertThat(desc.getColumn()).isEqualTo("create_time");
        assertThat(desc.isAsc()).isFalse();
    }

    @Test
    void ascBatchBuildersKeepOrderAndDirection() {
        List<OrderItem> ascItems = OrderItem.ascs("id", "name");
        assertThat(ascItems).extracting(OrderItem::getColumn).containsExactly("id", "name");
        assertThat(ascItems).allMatch(OrderItem::isAsc);

        List<OrderItem> descItems = OrderItem.descs("id", "name");
        assertThat(descItems).extracting(OrderItem::getColumn).containsExactly("id", "name");
        assertThat(descItems).noneMatch(OrderItem::isAsc);
    }

    @Test
    void withExpressionKeepsTheExpressionVerbatimAndDefaultsToDescending() {
        OrderItem defaultDirection = OrderItem.withExpression("DATE( create_time ) DESC");
        assertThat(defaultDirection.getColumn()).isEqualTo("DATE( create_time ) DESC");
        assertThat(defaultDirection.isAsc()).isFalse();

        OrderItem ascending = OrderItem.withExpression("DATE( create_time )", true);
        assertThat(ascending.getColumn()).isEqualTo("DATE( create_time )");
        assertThat(ascending.isAsc()).isTrue();
    }

    @Test
    void setColumnAndSetAscAreFluent() {
        OrderItem item = new OrderItem();
        assertThat(item.isAsc()).isTrue();
        assertThat(item.setColumn(" a b ")).isSameAs(item);
        assertThat(item.setAsc(false)).isSameAs(item);
        assertThat(item.getColumn()).isEqualTo("ab");
        assertThat(item.isAsc()).isFalse();
    }

    @Test
    void toStringExposesColumnAndDirection() {
        assertThat(OrderItem.asc("id")).hasToString("OrderItem{column='id', asc=true}");
        assertThat(OrderItem.desc("id")).hasToString("OrderItem{column='id', asc=false}");
    }
}
