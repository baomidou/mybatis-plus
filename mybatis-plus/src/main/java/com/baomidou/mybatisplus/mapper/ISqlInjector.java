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
package com.baomidou.mybatisplus.mapper;

import org.apache.ibatis.builder.MapperBuilderAssistant;

import com.baomidou.mybatisplus.MybatisConfiguration;

/**
 * <p>
 * SQL 自动注入器接口
 * </p>
 * 
 * @author hubin
 * @Date 2016-07-24
 */
public interface ISqlInjector {

	/**
	 * <p>
	 * 注入 SQL
	 * </p>
	 */
	void inject(MybatisConfiguration configuration, MapperBuilderAssistant builderAssistant, Class<?> mapperClass);

	/**
	 * <p>
	 * 检查SQL是否已经注入
	 * </p>
	 * <p>
	 * ps:注入基本SQL后给予标识 注入过不再注入
	 * </p>
	 */
	void inspectInject(MybatisConfiguration configuration, MapperBuilderAssistant builderAssistant, Class<?> mapperClass);

}
