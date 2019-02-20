package com.baomidou.mybatisplus.test.generator.entity;

import java.util.Date;

import lombok.Data;

/**
 * <p>
 * 测试的基础父类
 * </p>
 *
 * @author hubin
 * @since 2019-02-20
 */
@Data
public class BaseEntity extends SuperEntity {

    private Boolean deleted;
    private Date createTime;

}
