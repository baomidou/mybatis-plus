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
package com.baomidou.mybatisplus.activerecord;

import java.io.Serializable;

/**
 * <p>
 * ActiveRecord 模式 CRUD
 * </p>
 * 
 * @author hubin
 * @param <T>
 * @Date 2016-11-06
 */
@SuppressWarnings({ "serial", "rawtypes" })
public abstract class Model<T extends Model> implements Serializable {

	protected abstract Serializable getPrimaryKey();

	protected Class<?> currentClass() {
		return getClass();
	}

	public Record<T> mapper() {
		return new Record<T>(this);
	}

}
