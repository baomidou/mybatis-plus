package com.baomidou.mybatisplus.rmt;

import lombok.Data;

/**
 * <p>
 * 可靠消息元数据
 * </p>
 *
 * @author jobob
 * @since 2019-04-17
 */
@Data
public class RmtMeta {
    /**
     * 消息 ID
     */
    String messageId;
    /**
     * 交换器
     */
    String exchange;
    /**
     * 路由 KEY
     */
    String routingKey;
    /**
     * 消息内容
     */
    Object payload;

}
