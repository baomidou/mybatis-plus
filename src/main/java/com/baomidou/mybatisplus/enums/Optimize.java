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
 * Count优化枚举
 * </p>
 *
 * @author Caratacus
 * @Date 2016-11-30
 */
public enum Optimize {
    /**
     * 默认支持方式
     */
    DEFAULT("default", "默认方式"),
    /**
     * aliDruid,需添加相关依赖jar包
     */
    ALI_DRUID("aliDruid", "依赖aliDruid模式"),
    /**
     * jsqlparser方式,需添加相关依赖jar包
     */
    JSQLPARSER("jsqlparser", "jsqlparser方式");

    private final String optimize;

    private final String desc;

    Optimize(final String optimize, final String desc) {
        this.optimize = optimize;
        this.desc = desc;
    }

    /**
     * <p>
     * 获取优化类型.如果没有找到默认DEFAULT
     * </p>
     *
     * @param optimizeType
     *            优化方式
     * @return
     */
    public static Optimize getOptimizeType(String optimizeType) {
        for (Optimize optimize : Optimize.values()) {
            if (optimize.getOptimize().equalsIgnoreCase(optimizeType)) {
                return optimize;
            }
        }
        return DEFAULT;
    }

    public String getOptimize() {
        return this.optimize;
    }

    public String getDesc() {
        return this.desc;
    }

}
