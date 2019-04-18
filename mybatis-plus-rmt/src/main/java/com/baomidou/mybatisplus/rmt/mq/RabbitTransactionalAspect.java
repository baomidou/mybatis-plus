package com.baomidou.mybatisplus.rmt.mq;

import com.baomidou.mybatisplus.rmt.RmtConstants;
import com.baomidou.mybatisplus.rmt.RmtMeta;
import com.baomidou.mybatisplus.rmt.annotation.RmTransactional;
import com.baomidou.mybatisplus.rmt.coordinator.IRmtCoordinator;
import com.baomidou.mybatisplus.rmt.sender.IRmtSender;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * <p>
 * 可靠消息事务切面
 * </p>
 *
 * @author jobob
 * @since 2019-04-18
 */
@Slf4j
@Aspect
public class RabbitTransactionalAspect {
    @Autowired
    private IRmtCoordinator rmtCoordinator;
    @Autowired
    private IRmtSender rmtSender;

    @Around(value = "@annotation(rmTransactional)")
    public void around(ProceedingJoinPoint joinPoint, RmTransactional rmTransactional) throws Throwable {
        // 消息 ID
        String messageId = rmTransactional.value() + RmtConstants.DB_SPLIT + System.currentTimeMillis();

        /**
         * 发送前暂存消息
         */
        rmtCoordinator.setPrepare(messageId);

        Object returnObj;
        try {
            /**
             * 执行业务函数
             */
            returnObj = joinPoint.proceed();
        } catch (Exception e) {
            log.error("joinPoint proceed error! messageId: {}", messageId);
            throw e;
        }

        if (returnObj == null) {
            returnObj = RmtConstants.BLANK_STR;
        }

        // 生成可靠消息元数据
        RmtMeta rmtMeta = new RmtMeta();
        rmtMeta.setMessageId(messageId);
        rmtMeta.setExchange(rmTransactional.exchange());
        rmtMeta.setRoutingKey(rmTransactional.routingKey());
        rmtMeta.setPayload(returnObj);

        // 将消息设置为ready状态
        rmtCoordinator.setReady(messageId, rmtMeta);

        try {
            rmtSender.send(rmtMeta);
        } catch (Exception e) {
            log.error("The first stage message is sent error. messageId: {} , {}",
                    messageId, e.getMessage());
            throw e;
        }
    }
}
