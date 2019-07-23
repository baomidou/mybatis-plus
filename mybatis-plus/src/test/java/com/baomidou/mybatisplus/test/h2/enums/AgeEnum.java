/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.h2.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * 通用枚举注入演示，注意需要实现 IEnums 也需要扫描枚举包
 *
 * @author hubin
 * @since 2018-08-15
 */
public enum AgeEnum implements IEnum<Integer> {
    ONE(1, "一岁"),
    TWO(2, "二岁"),
    THREE(3, "三岁");

    private final int value;
    @SuppressWarnings("unused")
    private final String desc;

    AgeEnum(final int value, final String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static AgeEnum parseValue(Integer v) {
        if (v == null) {
            return null;
        }
        for (AgeEnum e : AgeEnum.values()) {
            if (e.getValue().equals(v)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
