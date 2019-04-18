package com.baomidou.mybatisplus.rmt.coordinator;

import com.baomidou.mybatisplus.rmt.RmtConstants;
import com.baomidou.mybatisplus.rmt.RmtMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Redis 协调器
 * </p>
 *
 * @author jobob
 * @since 2019-04-18
 */
@Slf4j
public class RedisRmtCoordinator implements IRmtCoordinator {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void setPrepare(String messageId) {
        redisTemplate.opsForSet().add(RmtConstants.MQ_MSG_PREPARE, messageId);
    }

    @Override
    public void setReady(String messageId, RmtMeta rmtMeta) {
        redisTemplate.opsForHash().put(RmtConstants.MQ_MSG_READY, messageId, rmtMeta);
        redisTemplate.opsForSet().remove(RmtConstants.MQ_MSG_PREPARE, messageId);
    }

    @Override
    public void setSuccess(String messageId) {
        redisTemplate.opsForHash().delete(RmtConstants.MQ_MSG_READY, messageId);
    }

    @Override
    public RmtMeta getRmtMeta(String messageId) {
        return (RmtMeta) redisTemplate.opsForHash().get(RmtConstants.MQ_MSG_READY, messageId);
    }

    @Override
    public List<String> getPrepare() throws Exception {
        SetOperations setOperations = redisTemplate.opsForSet();
        Set<String> messageIds = setOperations.members(RmtConstants.MQ_MSG_PREPARE);
        List<String> messageAlert = new ArrayList();
        for (String messageId : messageIds) {
            // 如果超时加入、超时消息队列
            if (messageTimeOut(messageId)) {
                messageAlert.add(messageId);
            }
        }
        // 删除已超时的消息
        setOperations.remove(RmtConstants.MQ_MSG_READY, messageAlert);
        return messageAlert;
    }

    @Override
    public List<RmtMeta> getReady() throws Exception {
        HashOperations hashOperations = redisTemplate.opsForHash();
        List<RmtMeta> messages = hashOperations.values(RmtConstants.MQ_MSG_READY);
        List<RmtMeta> messageAlert = new ArrayList();
        List<String> messageIds = new ArrayList<>();
        for (RmtMeta message : messages) {
            // 如果超时加入、超时消息队列
            if (messageTimeOut(message.getMessageId())) {
                messageIds.add(message.getMessageId());
                messageAlert.add(message);
            }
        }
        // 删除已超时的消息
        hashOperations.delete(RmtConstants.MQ_MSG_READY, messageIds);
        return messageAlert;
    }

    @Override
    public Long incrResendKey(String key, String hashKey) {
        return redisTemplate.opsForHash().increment(key, hashKey, 1);
    }

    @Override
    public Long getResendValue(String key, String hashKey) {
        return (Long) redisTemplate.opsForHash().get(key, hashKey);
    }

    protected boolean messageTimeOut(String messageId) {
        return (System.currentTimeMillis() - Long.parseLong((messageId
                .split(RmtConstants.DB_SPLIT))[1])) > RmtConstants.TIME_GAP;
    }
}
