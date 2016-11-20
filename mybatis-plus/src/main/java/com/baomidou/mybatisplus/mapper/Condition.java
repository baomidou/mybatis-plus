/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR EntityWrapperS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.mapper;

import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import java.util.Map;

/**
 * <p>
 * 条件查询构造器
 * </p>
 *
 * @author hubin Caratacus
 * @date 2016-11-7
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class Condition extends Wrapper {

	private Map<String, Object> params;

	/**
	 * 获取实例
	 */
	public static Condition instance() {
		return new Condition();
	}

	/**
	 * SQL 片段
	 */
	@Override
	public String getSqlSegment() {
		Map<String, Object> params = getParams();
		if (MapUtils.isNotEmpty(params)) {
			allEq(params);
		}
		/*
		 * 无条件
		 */
		String sqlWhere = sql.toString();
		if (StringUtils.isEmpty(sqlWhere)) {
			return null;
		}

		return sqlWhere;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public Condition setParams(Map<String, Object> params) {
		this.params = params;
		return this;
	}
}
