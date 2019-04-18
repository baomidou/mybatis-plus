package com.baomidou.mybatisplus.rmt.listener;

/**
 * 可靠消息事务，消息监听
 *
 * @author jobob
 * @since 2019-04-18
 */
public interface IRmtMessageListener<M> {

    /**
     * 接收消息对象处理
     *
     * @param object 接收到消息对象
     */
    void receive(M object);
}
