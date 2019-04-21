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

import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.dts.DtsConstants;
import com.baomidou.mybatisplus.dts.DtsMeta;
import com.baomidou.mybatisplus.dts.parser.IDtsParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * RabbitMQ 消息发送者
 * </p>
 *
 * @author hubin
 * @since 2019-04-17
 */
@Slf4j
@Component
public class RabbitRmtSender implements IRmtSender {
    @Autowired
    private IDtsParser rmtParser;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送MQ消息
     *
     * @param dtsMeta Rabbit元信息对象，用于存储交换器、队列名、消息体
     */
    @Override
    public void send(DtsMeta dtsMeta) {
        String object = null;
        try {
            object = rmtParser.toJSONString(dtsMeta);
            rabbitTemplate.convertAndSend(DtsConstants.RABBIT_EXCHANGE,
                DtsConstants.RABBIT_ROUTINGKEY, object);
        } catch (AmqpException e) {
            ExceptionUtils.mpe("rabbit send error, dtsMeta: %s", e, object);
        } catch (Exception e) {
            ExceptionUtils.mpe("rmt parser error, dtsMeta.event: %s", e, dtsMeta.getEvent());
        }
    }
}
