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

/**
 * <p>
 * 实现分页辅助类
 * </p>
 * 
 * @author hubin
 * @Date 2016-03-01
 */
public class Page<T> extends Pagination {

	/**
	 * 查询数据列表
	 */
	private List<T> records = Collections.emptyList();


	protected Page() {
		/* 保护 */
	}


	public Page( int current, int size ) {
		super(current, size);
	}


	public List<T> getRecords() {
		return records;
	}


	public void setRecords( List<T> records ) {
		this.records = records;
	}
	

	@Override
	public String toString() {
		StringBuffer pg = new StringBuffer();
		pg.append(" Page:{ [").append(super.toString()).append("], ");
		if ( records != null ) {
			pg.append("records-size:").append(records.size());
		} else {
			pg.append("records is null");
		}
		return pg.append(" }").toString();
	}
}
