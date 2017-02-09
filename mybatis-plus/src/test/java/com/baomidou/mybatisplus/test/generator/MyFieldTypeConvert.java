/**
 * Copyright (c) 2011-2016, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.test.generator;

import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;

/**
 * <p>
 * 测试字段类型转换
 * </p>
 *
 * @author hubin
 * @date 2017-01-20
 */
public class MyFieldTypeConvert extends MySqlTypeConvert {

	@Override
	public DbColumnType processTypeConvert(String fieldType) {
		System.out.println("转换类型：" + fieldType);
		return super.processTypeConvert(fieldType);
	}

}
