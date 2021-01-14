package com.baomidou.mybatisplus.test.sharding;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author miemie
 * @since 2020-06-24
 */
@Data
@Accessors(chain = true)
public class ShardingOrder {

    @TableId
    private Long orderId;

    private String subject;

    private LocalDateTime createTime;
}
