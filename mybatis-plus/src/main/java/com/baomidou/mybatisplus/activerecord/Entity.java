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
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.activerecord;

import java.io.Serializable;

import com.baomidou.mybatisplus.test.mysql.entity.Test;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;

/**
 * <p>
 * ActiveRecord 模式基础类
 * </p>
 *
 * @author hubin
 * @Date 2016-10-12
 */
@SuppressWarnings({ "serial", "rawtypes" })
public abstract class Entity<M extends Entity> implements Serializable {

	/**
	 * 插入
	 */
	public static Record insert(Record record) {
		return db().create(record);
	}

	/**
	 * 删除
	 */
	public static void delete(Record record) {
		db().delete(record);
	}

	/**
	 * 更新
	 */
	public static void update(Record record) {
		db().update(record);
	}

	/**
	 * 查询
	 */
	public static Query select(String... columns) {
		return db().select(columns);
	}

	/**
	 * 连接
	 */
	public static Table db() {
		return TableInfoHelper.getTable(Test.class);
	}

}
