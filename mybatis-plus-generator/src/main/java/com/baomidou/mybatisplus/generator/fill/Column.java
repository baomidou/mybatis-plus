/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.generator.fill;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.IFill;
import org.jetbrains.annotations.NotNull;

/**
 * 字段填充
 *
 * @author nieqiurong
 * @since 3.5.0 2020/12/1.
 */
public class Column implements IFill {

    private final String columnName;

    private final FieldFill fieldFill;

    public Column(@NotNull String columnName, @NotNull FieldFill fieldFill) {
        this.columnName = columnName;
        this.fieldFill = fieldFill;
    }

    public Column(String columnName) {
        this.columnName = columnName;
        this.fieldFill = FieldFill.DEFAULT;
    }

    @Override
    public @NotNull String getName() {
        return this.columnName;
    }

    @Override
    public @NotNull FieldFill getFieldFill() {
        return this.fieldFill;
    }
}
