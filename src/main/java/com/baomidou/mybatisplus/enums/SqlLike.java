/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.enums;

/**
 * <p>
 * SQL like 枚举
 * </p>
 *
 * @author Caratacus
 * @Date 2016-12-4
 */
public enum SqlLike {
    /**
     * LEFT
     */
    LEFT("left", "左边%"),
    /**
     * RIGHT
     */
    RIGHT("right", "右边%"),
    /**
     * CUSTOM
     */
    CUSTOM("custom", "定制"),
    /**
     * DEFAULT
     */
    DEFAULT("default", "两边%");

    /** 主键 */
    private final String type;

    /** 描述 */
    private final String desc;

    SqlLike(final String type, final String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

}
