/*
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.annotation;

import lombok.Getter;

/**
 * <p>
 * 字段填充策略枚举类
 * </p>
 *
 * @author hubin
 * @since 2017-06-27
 */
@Getter
public enum FieldFill {
    DEFAULT(0, "默认不处理"),
    INSERT(1, "插入填充字段"),
    UPDATE(2, "更新填充字段"),
    INSERT_UPDATE(3, "插入和更新填充字段");

    /**
     * 主键
     */
    private final int key;

    /**
     * 描述
     */
    private final String desc;

    FieldFill(final int key, final String desc) {
        this.key = key;
        this.desc = desc;
    }

    public static FieldFill getIgnore(int key) {
        FieldFill[] fis = FieldFill.values();
        for (FieldFill fi : fis) {
            if (fi.getKey() == key) {
                return fi;
            }
        }
        return FieldFill.DEFAULT;
    }

}
