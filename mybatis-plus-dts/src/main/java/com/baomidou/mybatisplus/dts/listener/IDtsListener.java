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
package com.baomidou.mybatisplus.dts.listener;

import com.baomidou.mybatisplus.dts.DtsMeta;

/**
 * 分布式事务监听
 *
 * @author jobob
 * @since 2019-04-18
 */
public interface IDtsListener {

    /**
     * 监听处理
     *
     * @param dtsMeta 分布式元数据对象
     */
    void process(DtsMeta dtsMeta);
}
