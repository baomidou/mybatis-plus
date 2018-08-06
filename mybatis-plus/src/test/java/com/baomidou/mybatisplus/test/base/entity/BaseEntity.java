package com.baomidou.mybatisplus.test.base.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 父类
 *
 * @author miemie
 * @since 2018-08-06
 */
@Data
@Accessors(chain = true)
public class BaseEntity {

    private Long id;
}
