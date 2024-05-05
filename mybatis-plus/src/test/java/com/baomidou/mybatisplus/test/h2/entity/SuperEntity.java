package com.baomidou.mybatisplus.test.h2.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 测试父类情况
 *
 * @author hubin
 * @since 2016-06-26
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class SuperEntity extends SuSuperEntity implements Serializable {

    /**
	 *  serialVersionUID
	 */
	private static final long serialVersionUID = -3111558058262086115L;

	/* 主键ID 注解，value 字段名，type 用户输入ID */
    @TableId
    private Long testId;

}

