package com.baomidou.mybatisplus.rmt.sender;

import com.baomidou.mybatisplus.rmt.RmtMeta;

/**
 * <p>
 * 可靠消息发送者
 * </p>
 *
 * @author jobob
 * @since 2019-04-17
 */
public interface IRmtSender {

    /**
     * <p>
     * 发送消息
     * </p>
     *
     * @param rmtMeta         可靠消息元数据
     * @return 消息ID
     */
    String send(RmtMeta rmtMeta);
}
