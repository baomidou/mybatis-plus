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
package com.baomidou.mybatisplus.plugins.pagination.dialects;

import com.baomidou.mybatisplus.plugins.pagination.IDialect;

/**
 * <p>
 * ORACLE 数据库分页语句组装实现
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
public class OracleDialect implements IDialect {

	public String buildPaginationSql(String originalSql, int offset, int limit) {
		StringBuilder sql = new StringBuilder(originalSql);
		/*
		 * ORACLE 分页是通过 ROWNUMBER 进行的，ROWNUMBER 是从 1 开始的
		 */
		offset++;
		sql.insert(0, "SELECT U.*, ROWNUM R FROM (").append(") U WHERE ROWNUM < ").append(offset + limit);
		sql.insert(0, "SELECT * FROM (").append(") TEMP WHERE R >= ").append(offset);
		return sql.toString();
	}

}
