package com.baomidou.mybatisplus.generator.entity;

import java.io.Serializable;

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
public class SuperEntity implements Serializable {

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4801865210961587582L;

	private Long id;
    private Boolean deleted;

}
