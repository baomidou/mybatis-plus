package com.baomidou.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.IdType;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;

/**
 *
 * 用户表
 *
 */
public class User implements Serializable {

	@TableField(exist = false)
	protected static final long serialVersionUID = 1L;

	/** 主键ID */
	@TableId(value = "test_id", type = IdType.AUTO)
	protected Long testId;

	/** 名称 */
	protected String name;

	/** 年龄 */
	protected Integer age;

	/** 测试下划线字段命名类型 */
	@TableField(value = "test_type")
	protected Integer testType;

	public Long getTestId() {
		return this.testId;
	}

	public void setTestId(Long testId) {
		this.testId = testId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return this.age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getTestType() {
		return this.testType;
	}

	public void setTestType(Integer testType) {
		this.testType = testType;
	}

}
