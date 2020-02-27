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
package com.baomidou.mybatisplus.core.enums;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * 自定义枚举接口
 *
 * @author hubin
 * @since 2017-10-11
 */
public interface IEnum<T extends Serializable> {

    /**
     * 枚举数据库存储值
     */
    default T getValue() {
        try {
            Class<?> clazz = getClass();
            // 继承者Enum必须实现
            Field field = clazz.getDeclaredField("value");
            return (T)field.get(this);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static <E extends Enum<E> & IEnum> E toEnum(Class<E> eClass, Integer v) {
        if (v != null) {
            for (final E pc : eClass.getEnumConstants()) {
                if (v == pc.getValue()) {
                    return pc;
                }
            }
        }
        return null;
    }

}
