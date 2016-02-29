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
package com.baomidou.mybatisplus.plugins.pagination;

import org.apache.ibatis.session.RowBounds;

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;

/**
 * <p>
 * 简单分页模型
 * </p>
 * 用户可以通过继承 org.apache.ibatis.session.RowBounds实现自己的分页模型<br>
 * 注意：插件仅支持RowBounds及其子类作为分页参数
 * 
 * @author hubin
 * @Date 2016-01-23
 */
public class Pagination extends RowBounds {
	/* 总数 */
	private int total;

	/* 每页显示条数 */
	private int size;

	/* 总页数 */
	private int pages;

	/* 当前页 */
	private int current;

	public Pagination() {
		super();
	}

	/**
	 * <p>
	 * 分页构造函数
	 * </p>
	 * 
	 * @param current
	 *            当前页
	 * @param size
	 *            每页显示条数
	 */
	public Pagination(int current, int size) {
		super((current - 1) * size, size);
		if ( current <= 0 ) {
			throw new MybatisPlusException("current must be greater than zero.");
		}
		this.current = current;
		this.size = size;
	}

	public boolean hasPrevious() {
		return this.current > 1;
	}

	public boolean hasNext() {
		return this.current < this.pages;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
		this.pages = this.total / this.size;
		if (this.total % this.size != 0) {
			this.pages++;
		}
	}

	public int getSize() {
		return size;
	}

	public int getPages() {
		return pages;
	}

	public int getCurrent() {
		return current;
	}

	@Override
	public String toString() {
		return "Pagination { total=" + total + " ,size=" + size + " ,pages=" + pages + " ,current=" + current + " }";
	}
}
