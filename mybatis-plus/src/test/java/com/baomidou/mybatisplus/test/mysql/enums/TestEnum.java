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
package com.baomidou.mybatisplus.test.mysql.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;

/**
 * @author miemie
 * @since 2018-08-19
 */
@Getter
public enum TestEnum implements IEnum<Integer> {
    ONE(1, "一"),
    TWO(2, "二");

    private final int code;
    private final String val;

    TestEnum(int code, String val) {
        this.code = code;
        this.val = val;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
