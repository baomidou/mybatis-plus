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

import com.baomidou.mybatisplus.MybatisAbstractSQL;
import com.baomidou.mybatisplus.toolkit.CollectionUtil;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import java.text.MessageFormat;
import java.util.List;

/**
 * <p>
 * 实现AbstractSQL ，实现WHERE条件自定义
 * </p>
 *
 * @author yanghu , Caratacus
 * @Date 2016-08-22
 */
@SuppressWarnings("serial")
public class TSqlPlus extends MybatisAbstractSQL<TSqlPlus> {

	private final String IS_NOT_NULL = " IS NOT NULL";
	private final String IS_NULL = " IS NULL";
	private final String SQL_LIKE = " LIKE CONCAT(CONCAT({0},{1}),{2})";

	@Override
	public TSqlPlus getSelf() {
		return this;
	}

	/**
	 * 将LIKE语句添加到WHERE条件中
	 *
	 * @param column
	 *            字段名
	 * @param value
	 *            like值,无需前后%, MYSQL及ORACEL通用
	 * @return
	 */
	public TSqlPlus LIKE(String column, String value) {
		handerLike(column, value, false);
		return this;
	}

	/**
	 * 将LIKE语句添加到WHERE条件中
	 *
	 * @param column
	 *            字段名
	 * @param value
	 *            like值,无需前后%, MYSQL及ORACEL通用
	 * @return
	 */
	public TSqlPlus NOT_LIKE(String column, String value) {
		handerLike(column, value, true);
		return this;
	}

	/**
	 * IS NOT NULL查询
	 *
	 * @param columns
	 *            以逗号分隔的字段名称
	 * @return this
	 */
	public TSqlPlus IS_NOT_NULL(String columns) {
		handerNull(columns, IS_NOT_NULL);
		return this;
	}

	/**
	 * IS NULL查询
	 *
	 * @param columns
	 *            以逗号分隔的字段名称
	 * @return
	 */
	public TSqlPlus IS_NULL(String columns) {
		handerNull(columns, IS_NULL);
		return this;
	}

	/**
	 * 处理LIKE操作
	 *
	 * @param column
	 *            字段名称
	 * @param value
	 *            like匹配值
	 * @param isNot
	 *            是否为NOT LIKE操作
	 */
	private void handerLike(String column, String value, boolean isNot) {
		if (StringUtils.isNotEmpty(column) && StringUtils.isNotEmpty(value)) {
			StringBuilder inSql = new StringBuilder();
			inSql.append(column);
			if (isNot) {
				inSql.append(" NOT");
			}
			inSql.append(MessageFormat.format(SQL_LIKE, "'%'", StringUtils.quotaMark(value), "'%'"));
			WHERE(inSql.toString());
		}
	}

	/**
	 * 将IN语句添加到WHERE条件中
	 *
	 * @param column 字段名
	 * @param value  List集合
	 * @return
	 */
	public TSqlPlus IN(String column, List<?> value) {
		handerIn(column, value, false);
		return this;
	}

	/**
	 * 将IN语句添加到WHERE条件中
	 *
	 * @param column 字段名
	 * @param value  List集合
	 * @return
	 */
	public TSqlPlus NOT_IN(String column, List<?> value) {
		handerIn(column, value, true);
		return this;
	}

	/**
	 * 将IN语句添加到WHERE条件中
	 *
	 * @param column 字段名
	 * @param value  逗号拼接的字符串
	 * @return
	 */
	public TSqlPlus IN(String column, String value) {
		handerIn(column, value, false);
		return this;
	}

	/**
	 * 将IN语句添加到WHERE条件中
	 *
	 * @param column 字段名
	 * @param value  逗号拼接的字符串
	 * @return
	 */
	public TSqlPlus NOT_IN(String column, String value) {
		handerIn(column, value, true);
		return this;
	}

	/**
	 * 将EXISTS语句添加到WHERE条件中
	 *
	 * @param value
	 * @return
	 */
	public TSqlPlus EXISTS(String value) {
		handerExists(value, false);
		return this;
	}

	/**
	 * 处理EXISTS操作
	 *
	 * @param value
	 * @param isNot 是否为NOT EXISTS操作
	 */
	private void handerExists(String value, boolean isNot) {
		if (StringUtils.isNotEmpty(value)) {
			StringBuilder inSql = new StringBuilder();
			if (isNot) {
				inSql.append(" NOT");
			}
			inSql.append(" EXISTS (").append(value).append(")");
			WHERE(inSql.toString());
		}
	}

	/**
	 * 将NOT_EXISTS语句添加到WHERE条件中
	 *
	 * @param value
	 * @return
	 */
	public TSqlPlus NOT_EXISTS(String value) {
		handerExists(value, true);
		return this;
	}

	/**
	 * 处理IN操作
	 *
	 * @param column 字段名称
	 * @param value  集合List
	 * @param isNot  是否为NOT IN操作
	 */
	private void handerIn(String column, List<?> value, boolean isNot) {
		if (StringUtils.isNotEmpty(column) && CollectionUtil.isNotEmpty(value)) {
			StringBuilder inSql = new StringBuilder();
			inSql.append(column);
			if (isNot) {
				inSql.append(" NOT");
			}
			inSql.append(" IN (");
			int _size = value.size();
			for (int i = 0; i < _size; i++) {
				String tempVal = StringUtils.quotaMark(value.get(i));
				if (i + 1 == _size) {
					inSql.append(tempVal);
				} else {
					inSql.append(tempVal);
					inSql.append(",");
				}
			}
			inSql.append(")");
			WHERE(inSql.toString());
		}
	}

	/**
	 * 处理IN操作
	 *
	 * @param column 字段名称
	 * @param value  逗号拼接的字符串
	 * @param isNot  是否为NOT IN操作
	 */
	private void handerIn(String column, String value, boolean isNot) {
		if (StringUtils.isNotEmpty(column) && StringUtils.isNotEmpty(value)) {
			StringBuilder inSql = new StringBuilder();
			inSql.append(column);
			if (isNot) {
				inSql.append(" NOT");
			}
			inSql.append(" IN (").append(value).append(")");
			WHERE(inSql.toString());
		}
	}

	/**
	 * 以相同的方式处理null和notnull
	 *
	 * @param columns
	 *            以逗号分隔的字段名称
	 * @param sqlPart
	 *            SQL部分
	 */
	private void handerNull(String columns, String sqlPart) {
		if (StringUtils.isNotEmpty(columns)) {
			String[] cols = columns.split(",");
			for (String col : cols) {
				if (StringUtils.isNotEmpty(col.trim())) {
					WHERE(col + sqlPart);
				}
			}
		}
	}

}
