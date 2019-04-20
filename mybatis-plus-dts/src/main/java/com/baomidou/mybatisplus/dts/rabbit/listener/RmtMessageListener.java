
package com.baomidou.mybatisplus.dts.rabbit.listener;

import com.baomidou.mybatisplus.dts.rabbit.RmtConstants;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * RabbitMQ 抽象消息监听，所有消息消费者必须继承此类
 *
 * @author jobob
 * @since 2019-04-18
 */
@Slf4j
public abstract class RmtMessageListener implements IRmtListener<Message>, ChannelAwareMessageListener {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        MessageProperties messageProperties = message.getMessageProperties();
        Long deliveryTag = messageProperties.getDeliveryTag();
        Long consumerCount = redisTemplate.opsForHash().increment(RmtConstants.MQ_CONSUMER_RETRY_COUNT_KEY,
                messageProperties.getMessageId(), 1);
        log.debug("收到消息,当前消息ID:{} 消费次数：{}", messageProperties.getMessageId(), consumerCount);
        try {
            // 处理接收消息对象
            this.receive(message);
            // 成功的回执
            channel.basicAck(deliveryTag, false);
            // 如果消费成功，将Redis中统计消息消费次数的缓存删除
            redisTemplate.opsForHash().delete(RmtConstants.MQ_CONSUMER_RETRY_COUNT_KEY,
                    messageProperties.getMessageId());
        } catch (Exception e) {
            log.error("RabbitMQ 消息消费失败，" + e.getMessage(), e);
            if (consumerCount >= RmtConstants.MAX_CONSUMER_COUNT) {
                // 入死信队列
                channel.basicReject(deliveryTag, false);
            } else {
                // 重回到队列，重新消费, 按照2的指数级递增
                Thread.sleep((long) (Math.pow(RmtConstants.BASE_NUM, consumerCount) * 1000));
                redisTemplate.opsForHash().increment(RmtConstants.MQ_CONSUMER_RETRY_COUNT_KEY,
                        messageProperties.getMessageId(), 1);
                channel.basicNack(deliveryTag, false, true);
            }
        }
    }
}
