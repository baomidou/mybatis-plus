/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.mapper;

import java.text.MessageFormat;

import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * 查询条件 filter
 * </p>
 *
 * @author hubin
 * @Date 2016-08-21
 */
public class QueryFilter {

	/**
	 * 查询条件
	 */
	protected StringBuffer queryFilter = new StringBuffer();

	/**
	 * <p>
	 * 添加查询条件
	 * </p>
	 * <p>
	 * 例如：ew.addFilter("name={0}", "'123'") <br>
	 * 输出：name='123'
	 * </p>
	 *
	 * @param keyWord
	 *            SQL关键字
	 * @param filter
	 *            SQL 片段内容
	 * @param params
	 *            格式参数
	 * @return
	 */
	protected QueryFilter addFilter(String keyWord, String filter, Object... params) {
		if (StringUtils.isEmpty(filter)) {
			return this;
		}
		if (StringUtils.isNotEmpty(keyWord)) {
			queryFilter.append(keyWord);
		}
		if (null != params && params.length >= 1) {
			queryFilter.append(MessageFormat.format(filter, params));
		} else {
			queryFilter.append(filter);
		}
		return this;
	}

	/**
	 * <p>
	 * 添加查询条件
	 * </p>
	 * <p>
	 * 例如：ew.addFilter("name={0}", "'123'") <br>
	 * 输出：name='123'
	 * </p>
	 *
	 * @param filter
	 *            SQL 片段内容
	 * @param params
	 *            格式参数
	 * @return
	 */
	public QueryFilter addFilter(String filter, Object... params) {
		return addFilter(null, filter, params);
	}

	/**
	 * <p>
	 * 添加查询条件
	 * </p>
	 * <p>
	 * 例如：ew.addFilter("name={0}", "'123'").addFilterIfNeed(false, " ORDER BY id") <br>
	 * 输出：name='123'
	 * </p>
	 *
	 * @param keyWord
	 *            SQL关键字
	 * @param willAppend
	 *            判断条件 true 输出 SQL 片段，false 不输出
	 * @param filter
	 *            SQL 片段
	 * @param params
	 *            格式参数
	 * @return
	 */
	protected QueryFilter addFilterIfNeed(boolean willAppend, String keyWord, String filter, Object... params) {
		if (willAppend) {
			addFilter(keyWord, filter, params);
		}
		return this;
	}

	/**
	 * <p>
	 * 添加查询条件
	 * </p>
	 * <p>
	 * 例如：ew.addFilter("name={0}", "'123'").addFilterIfNeed(false, " ORDER BY id") <br>
	 * 输出：name='123'
	 * </p>
	 *
	 * @param willAppend
	 *            判断条件 true 输出 SQL 片段，false 不输出
	 * @param filter
	 *            SQL 片段
	 * @param params
	 *            格式参数
	 * @return
	 */
	public QueryFilter addFilterIfNeed(boolean willAppend, String filter, Object... params) {
		return addFilterIfNeed(willAppend, null, filter, params);
	}

}
