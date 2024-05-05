package com.baomidou.mybatisplus.test.h2.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * h2address entity.
 *
 * @author yuxiaobin
 * @since 2017/5/25
 */
@Data
@Accessors(chain = true)
@TableName("h2address")
public class H2Addr {

    @TableId("addr_id")
    private Long addrId;

    @TableField("addr_name")
    private String addrName;

    @TableField("test_id")
    private Long testId;

}
