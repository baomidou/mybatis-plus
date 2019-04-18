package com.baomidou.mybatisplus.rmt.coordinator;

import com.baomidou.mybatisplus.rmt.RmtMeta;

import java.util.Collection;

/**
 * <p>
 * RMT 协调器
 * </p>
 *
 * @author jobob
 * @since 2019-04-19
 */
public interface IRmtCoordinator {

    /**
     * 设置消息为prepare状态
     *
     * @param messageId 消息 ID
     */
    void setPrepare(String messageId);

    /**
     * 设置消息为ready状态，删除prepare状态
     *
     * @param messageId 消息 ID
     * @param rmtMeta   可靠消息元数据
     */
    void setReady(String messageId, RmtMeta rmtMeta);

    /**
     * 消息发送成功，删除ready状态消息
     *
     * @param messageId 消息 ID
     */
    void setSuccess(String messageId);

    /**
     * 获取消息实体
     *
     * @param messageId 消息 ID
     * @return
     */
    RmtMeta getRmtMeta(String messageId);

    /**
     * 获取prepare状态消息
     *
     * @param <T>
     * @return
     * @throws Exception
     */
    <T extends Collection> T getPrepare() throws Exception;

    /**
     * 获取ready状态消息T
     *
     * @param <T>
     * @return
     * @throws Exception
     */
    <T extends Collection> T getReady() throws Exception;

    /**
     * 消息重发次数 +1
     *
     * @param key
     * @param hashKey
     * @return
     */
    Long incrResendKey(String key, String hashKey);

    /**
     * 获取重发次数值
     *
     * @param key
     * @param hashKey
     * @return
     */
    Long getResendValue(String key, String hashKey);

}
