package com.baomidou.mybatisplus.test.generator.entity;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 测试的基础父类
 * </p>
 *
 * @author hubin
 * @since 2019-02-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseEntity extends SuperEntity {

    private Boolean deleted;
    private Date createTime;

}
