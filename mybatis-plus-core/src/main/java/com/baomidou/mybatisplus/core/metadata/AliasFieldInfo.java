/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
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

import java.io.Serializable;
import java.lang.reflect.Field;

import com.baomidou.mybatisplus.annotation.TableField;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 别名查询扩展字段信息
 *
 * @author huguirong
 * @since 2021-06-21
 */
@Getter
@ToString
@EqualsAndHashCode
public class AliasFieldInfo implements Serializable {

    /**
     * 自定义列名
     */
    private final String column;

    /**
     * 属性名
     */
    private final String property;

    /**
     * 全新的 存在 TableField 注解时使用的构造函数
     */
    public AliasFieldInfo(Field field, TableField tableField) {
        this.property = field.getName();
        this.column = tableField.value();
    }
}
