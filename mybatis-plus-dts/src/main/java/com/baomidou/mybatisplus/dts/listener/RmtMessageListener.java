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

import com.baomidou.mybatisplus.dts.DtsConstants;
import com.baomidou.mybatisplus.dts.DtsMeta;
import com.baomidou.mybatisplus.dts.parser.IDtsParser;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * RabbitMQ 抽象消息监听
 *
 * @author jobob
 * @since 2019-04-18
 */
@RabbitListener(queues = {DtsConstants.RABBIT_QUEUE})
public abstract class RmtMessageListener implements IDtsListener<String> {
    @Autowired
    private IDtsParser dtsParser;

    @RabbitHandler
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void process(String event) {
        try {
            receive(dtsParser.readValue(event, DtsMeta.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void receive(DtsMeta dtsMeta);
}
