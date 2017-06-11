/**
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
package com.baomidou.mybatisplus.enums;

/**
 * <p>
 * 生成ID类型枚举类
 * </p>
 *
 * @author hubin
 * @Date 2015-11-10
 */
public enum IdType {
    AUTO(0, "数据库ID自增"), INPUT(1, "用户输入ID"),

    /* 以下2种类型、只有当插入对象ID 为空，才自动填充。 */
    ID_WORKER(2, "全局唯一ID"), UUID(3, "全局唯一ID"), NONE(4, "该类型为未设置主键类型");

    /** 主键 */
    private final int key;

    /** 描述 */
    private final String desc;

    IdType(final int key, final String desc) {
        this.key = key;
        this.desc = desc;
    }

    /**
     * <p>
     * 主键策略 （默认 ID_WORKER）
     * </p>
     *
     * @param idType
     *            ID 策略类型
     * @return
     */
    public static IdType getIdType(int idType) {
        IdType[] its = IdType.values();
        for (IdType it : its) {
            if (it.getKey() == idType) {
                return it;
            }
        }
        return ID_WORKER;
    }

    public int getKey() {
        return this.key;
    }

    public String getDesc() {
        return this.desc;
    }

}
