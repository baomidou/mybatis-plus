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

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 排序元素载体
 *
 * @author HCL
 * Create at 2019/5/27
 */
@Getter
@Setter
public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 需要进行排序的字段
     */
    private String column;
    /**
     * 是否正序排列，默认 true
     */
    private boolean asc = true;

    public OrderItem() {
    }

    /**
     * @since 3.5.13
     * @param column 字段
     * @param asc 是否升序
     */
    private OrderItem(String column, boolean asc) {
        this.column = column;
        this.asc = asc;
    }

    /**
     * 根据指定字段升序排序
     * @param column 数据库字段
     * @return 排序字段
     */
    public static OrderItem asc(String column) {
        return build(column, true);
    }

    /**
     * 根据指定字段降序排序
     * @param column 数据库字段
     * @return 排序字段
     */
    public static OrderItem desc(String column) {
        return build(column, false);
    }

    /**
     * 根据表达式排序
     * <p>任意表达式语句,自行控制SQL注入</p>
     * <p>不适用与反序列</p>
     * @since 3.5.13
     * @param expression 字段表达式
     * @return 排序字段
     */
    public static OrderItem withExpression(String expression) {
        return withExpression(expression, false);
    }

    /**
     * 根据表达式排序
     * <p>任意表达式语句,自行控制SQL注入</p>
     * <p>不适用与反序列</p>
     * @since 3.5.13
     * @param expression 字段表达式
     * @param asc 是否正序
     * @return 排序字段
     */
    public static OrderItem withExpression(String expression, boolean asc) {
        return new OrderItem(expression, asc);
    }

    /**
     * 根据指定字段列表进行升序排序
     * @param columns 字段列表
     * @return 排序字段
     */
    public static List<OrderItem> ascs(String... columns) {
        return Arrays.stream(columns).map(OrderItem::asc).collect(Collectors.toList());
    }

    /**
     * 根据指定字段列表进行降序排序
     * @param columns 字段列表
     * @return 排序字段
     */
    public static List<OrderItem> descs(String... columns) {
        return Arrays.stream(columns).map(OrderItem::desc).collect(Collectors.toList());
    }

    private static OrderItem build(String column, boolean asc) {
        return new OrderItem().setColumn(column).setAsc(asc);
    }

    public OrderItem setColumn(String column) {
        // TODO 反序列化会到这里被处理,后期重构需要改动
        this.column = StringUtils.replaceAllBlank(column);
        return this;
    }

    public OrderItem setAsc(boolean asc) {
        this.asc = asc;
        return this;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
            "column='" + column + '\'' +
            ", asc=" + asc +
            '}';
    }

}
