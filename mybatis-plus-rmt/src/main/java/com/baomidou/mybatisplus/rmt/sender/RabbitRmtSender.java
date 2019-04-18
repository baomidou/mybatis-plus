package com.baomidou.mybatisplus.rmt.sender;

import com.baomidou.mybatisplus.rmt.RmtMeta;
import com.baomidou.mybatisplus.rmt.parser.IRmtParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * <p>
 * RabbitMQ 消息发送者
 * </p>
 *
 * @author hubin
 * @since 2019-04-17
 */
@Slf4j
public class RabbitRmtSender implements IRmtSender {
    @Autowired
    private IRmtParser rmtParser;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送MQ消息
     *
     * @param rmtMeta Rabbit元信息对象，用于存储交换器、队列名、消息体
     * @return 消息ID
     * @throws JsonProcessingException
     */
    @Override
    public String send(RmtMeta rmtMeta) {
        final String messageId = UUID.randomUUID().toString();
        MessagePostProcessor messagePostProcessor = message -> {
            message.getMessageProperties().setMessageId(messageId);
            // 设置消息持久化
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        };
        try {
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType("application/json");
            Message message = new Message(rmtParser.toJSONString(rmtMeta.getPayload()).getBytes(), messageProperties);
            rabbitTemplate.convertAndSend(rmtMeta.getExchange(), rmtMeta.getRoutingKey(),
                    message, messagePostProcessor, new CorrelationData(messageId));
            log.info("发送消息，消息ID:{}", messageId);
            return messageId;
        } catch (AmqpException e) {
            throw new RuntimeException("发送RabbitMQ消息失败！", e);
        } catch (Exception e) {
            throw new RuntimeException("发送RabbitMQ消息失败！", e);
        }
    }
}
