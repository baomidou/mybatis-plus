
package com.baomidou.mybatisplus.dts.rabbit.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

/**
 * RabbitMQ 抽象消息监听，所有消息消费者必须继承此类
 *
 * @author jobob
 * @since 2019-04-18
 */
@Slf4j
public abstract class RmtMessageListener implements IRmtListener<Message> {

}
