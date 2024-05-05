package com.baomidou.mybatisplus.generator.entity;

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

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 199327361052220940L;
	private Boolean deleted;
    private Date createTime;

}
