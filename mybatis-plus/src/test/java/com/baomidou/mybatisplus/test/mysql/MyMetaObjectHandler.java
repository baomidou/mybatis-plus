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
package com.baomidou.mybatisplus.test.mysql;

import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.mapper.IMetaObjectHandler;

/**
 * <p>
 * 测试，自定义元对象字段填充控制器，实现公共字段自动写入
 * </p>
 * 
 * @author hubin
 * @Date 2016-08-28
 */
public class MyMetaObjectHandler implements IMetaObjectHandler {

	/**
	 * 测试 user 表 name 字段为空自动填充
	 */
	public void insertFill(MetaObject metaObject) {
//		Object name = metaObject.getValue("name");
//		if (null == name) {
//			metaObject.setValue("name", "instert-fill");
//		}

		// 测试下划线
		Object testType = metaObject.getValue("testType");
		System.err.println("testType==" + testType);
		if (null == testType) {
			metaObject.setValue("testType", 3);
		}
	}

}
