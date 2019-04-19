package com.baomidou.mybatisplus.rmt.listener;

/**
 * 可靠消息事务监听
 *
 * @author jobob
 * @since 2019-04-18
 */
public interface IRmtListener<M> {

    /**
     * 接收对象处理
     *
     * @param object 接收到对象
     */
    void receive(M object);
}
