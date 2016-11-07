/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
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

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;

/**
 * <p>
 * 纯 SQL Provider
 * </p>
 * 
 * @author hubin
 * @Date 2016-11-06
 */
public class PureSqlProvider {

	/**
	 * <p>
	 * 执行 SQL 语句
	 * </p>
	 * 
	 * @param sql
	 *            SQL语句
	 * @return
	 */
	public String sql(String sql) {
		if (null == sql) {
			throw new MybatisPlusException("sql is null.");
		}
		return sql;
	}

}
