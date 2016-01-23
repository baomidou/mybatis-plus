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
package com.baomidou.mybatisplus.mapper;

import java.util.List;

/**
 * <p>
 * Mapper 继承该接口后，无需编写 mapper.xml 文件，即可获得CRUD功能
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
public interface AutoMapper<T> {

	/*
	 * 新增
	 */
	public int insert(T entity);

	/*
	 * 根据主键删除，主键名默认为id
	 */
	public int deleteById(Object id);

	/*
	 * 根据主键修改，主键名默认为id
	 */
	public int updateById(T entity);

	/*
	 * 根据主键查找，主键名默认为id
	 */
	public T selectById(Object id);

	/*
	 * 查询全部
	 */
	public List<T> selectAll();

}
