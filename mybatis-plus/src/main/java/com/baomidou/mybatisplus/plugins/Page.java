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
package com.baomidou.mybatisplus.plugins;

import java.util.Collections;
import java.util.List;

import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * 实现分页辅助类
 * </p>
 * 
 * @author hubin
 * @Date 2016-03-01
 */
public class Page<T> extends Pagination {

	private static final long serialVersionUID = 1L;

	/**
	 * 查询数据列表
	 */
	private List<T> records = Collections.emptyList();

	/**
	 * <p>
	 * SQL 排序 ORDER BY 字段，例如： id DESC（根据id倒序查询）
	 * </p>
	 * <p>
	 * DESC 表示按倒序排序(即：从大到小排序)<br>
	 * ASC 表示按正序排序(即：从小到大排序)
	 * </p>
	 */
	private String orderByField;

	/**
	 * 是否为升序 ASC（ 默认： true ）
	 */
	private boolean isAsc = true;

	public Page() {
		/* 注意，传入翻页参数 */
	}

	public Page(int current, int size) {
		super(current, size);
	}

	public Page(int current, int size, String orderByField) {
		super(current, size);
		this.setOrderByField(orderByField);
	}

	public List<T> getRecords() {
		return records;
	}

	public void setRecords(List<T> records) {
		this.records = records;
	}

	public String getOrderByField() {
		return orderByField;
	}

	public void setOrderByField(String orderByField) {
		if (StringUtils.isNotEmpty(orderByField)) {
			this.orderByField = orderByField;
		}
	}

	public boolean isAsc() {
		return isAsc;
	}

	public void setAsc(boolean isAsc) {
		this.isAsc = isAsc;
	}

	@Override
	public String toString() {
		StringBuffer pg = new StringBuffer();
		pg.append(" Page:{ [").append(super.toString()).append("], ");
		if (records != null) {
			pg.append("records-size:").append(records.size());
		} else {
			pg.append("records is null");
		}
		return pg.append(" }").toString();
	}
}
