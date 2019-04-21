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

import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.dts.DtsConstants;
import com.baomidou.mybatisplus.dts.DtsMeta;
import com.baomidou.mybatisplus.dts.parser.IDtsParser;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Rabbit 可靠消息事务监听
 *
 * @author jobob
 * @since 2019-04-18
 */
@Component
@RabbitListener(queues = {DtsConstants.RABBIT_QUEUE})
public class RabbitRmtListener {
    @Autowired
    private IDtsParser dtsParser;
    @Autowired
    private List<IDtsListener> dtsListenerList;

    /**
     * 解析处理，接收消息对象
     *
     * @param event rabbit 消息
     */
    @RabbitHandler
    @Transactional(rollbackFor = Exception.class)
    public void receive(String event) {
        try {
            DtsMeta dtsMeta = dtsParser.readValue(event, DtsMeta.class);
            dtsListenerList.forEach(d -> d.process(dtsMeta));
        } catch (Exception e) {
            ExceptionUtils.mpe("rmt parser error, event: %s", e, event);
        }
    }
}
