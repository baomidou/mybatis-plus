package com.baomidou.mybatisplus.test.base.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author by keray
 * date:2019/8/9 23:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseUserEntity extends BaseEntity {
    private String username;
    private String password;
}
