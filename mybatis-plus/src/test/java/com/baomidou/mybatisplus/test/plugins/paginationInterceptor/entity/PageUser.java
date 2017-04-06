package com.baomidou.mybatisplus.test.plugins.paginationInterceptor.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableName;

@TableName("page_user")
public class PageUser implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private Short age;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Short getAge() {
		return age;
	}

	public void setAge(Short age) {
		this.age = age;
	}

}
