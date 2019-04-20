package com.baomidou.mybatisplus.dts;

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
public class DtsMeta {
    /**
     * 业务 KEY
     */
    String key;
    /**
     * 消息内容
     */
    Object payload;

}
