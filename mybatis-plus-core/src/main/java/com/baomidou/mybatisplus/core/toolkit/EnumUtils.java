/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.toolkit;


import java.lang.reflect.Field;
import java.util.Objects;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * <p>
 * 枚举处理工具类
 * </p>
 *
 * @author hubin
 * @since 2017-10-11
 */
public class EnumUtils {

    /**
     * <p>
     * 值映射为枚举
     * </p>
     *
     * @param enumClass 枚举类
     * @param value     枚举值
     * @param <E>       对应枚举
     * @return
     */
    public static <E extends Enum<?> & IEnum> E valueOf(Class<E> enumClass, Object value) {
        E[] es = enumClass.getEnumConstants();
        for (E e : es) {
            if (Objects.equals(e.getValue(), value)) {
                return e;
            }
        }
        return null;
    }

    public static <E extends Enum<?>> E valueOf(Class<E> enumClass, Object value, Field enumField) {
        E[] es = enumClass.getEnumConstants();
        for (E e : es) {
            try {
                if (Objects.equals(enumField.get(e), value)) {
                    return e;
                }
            } catch (IllegalAccessException e1) {

            }
        }
        return null;
    }

}
