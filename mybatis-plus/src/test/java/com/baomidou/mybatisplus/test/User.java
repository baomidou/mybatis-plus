/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.Id;
import com.baomidou.mybatisplus.annotation.Table;

/**
 * <p>
 * 测试用户类
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
/* 表名 注解 */
@Table(name = "user")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	/* 主键ID 注解，auto 属性 true 自增，false 非自增 */
	@Id
	private long id;

	private String name;

	private int age;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
