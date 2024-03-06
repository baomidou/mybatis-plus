/*
 * Copyright (c) 2011-2023, baomidou (jobob@qq.com).
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

    public static OrderItem asc(String column) {
        return build(column, true, true);
    }

    /**
     * 正序排序
     * @param column 列名
     * @param preventSqlInject 是否防止SQL注入
     * @return
     */
    public static OrderItem asc(String column, boolean preventSqlInject) {
        return build(column, true, preventSqlInject);
    }

    public static OrderItem desc(String column) {
        return build(column, false, true);
    }

    /**
     * 倒序排序
     * @param column 列名
     * @param preventSqlInject 是否防止SQL注入
     * @return
     */
    public static OrderItem desc(String column, boolean preventSqlInject) {
        return build(column, false, preventSqlInject);
    }


    public static List<OrderItem> ascs(String... columns) {
        return Arrays.stream(columns).map(OrderItem::asc).collect(Collectors.toList());
    }

    public static List<OrderItem> ascs(boolean preventSqlInject, String... columns) {
        return Arrays.stream(columns).map(column -> OrderItem.asc(column, preventSqlInject)).collect(Collectors.toList());
    }

    public static List<OrderItem> descs(boolean preventSqlInject, String... columns) {
        return Arrays.stream(columns).map(column -> OrderItem.desc(column, preventSqlInject)).collect(Collectors.toList());
    }

    private static OrderItem build(String column, boolean asc) {
        return build(column, asc, true);
    }

    /**
     * 构建函数
     * @param column 列名
     * @param asc 是否正序
     * @param preventSqlInject 是否防止SQL注入
     * @return
     */
    private static OrderItem build(String column, boolean asc, boolean preventSqlInject) {
        return new OrderItem().setColumn(column, preventSqlInject).setAsc(asc);
    }

    public OrderItem setColumn(String column) {
        return this.setColumn(column, true);
    }

    /**
     * 设置列名
     * @param column 列名
     * @param preventSqlInject 是否防止SQL注入
     * @return
     */
    public OrderItem setColumn(String column, boolean preventSqlInject) {
        if (preventSqlInject) {
            // 开启SQL注入后会去除一切空格等特殊符号
            this.column = StringUtils.replaceAllBlank(column);
        } else  {
            this.column = column;
        }
        return this;
    }

    public OrderItem setAsc(boolean asc) {
        this.asc = asc;
        return this;
    }

}
