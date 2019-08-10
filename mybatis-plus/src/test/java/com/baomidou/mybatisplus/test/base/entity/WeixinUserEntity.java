package com.baomidou.mybatisplus.test.base.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author by keray
 * date:2019/8/9 23:29
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class WeixinUserEntity extends BaseUserEntity{
    private String openId;
    private String unionid;
}
