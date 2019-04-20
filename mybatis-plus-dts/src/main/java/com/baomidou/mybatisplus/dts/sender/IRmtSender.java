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
package com.baomidou.mybatisplus.dts.sender;

import com.baomidou.mybatisplus.dts.DtsMeta;

/**
 * <p>
 * 可靠消息发送者
 * </p>
 *
 * @author jobob
 * @since 2019-04-17
 */
public interface IRmtSender {

    /**
     * <p>
     * 发送消息
     * </p>
     *
     * @param dtsMeta 分布式事务元数据
     * @return
     */
    void send(DtsMeta dtsMeta);
}
