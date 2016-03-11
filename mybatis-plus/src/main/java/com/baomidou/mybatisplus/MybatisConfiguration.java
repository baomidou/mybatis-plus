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
package com.baomidou.mybatisplus;

import java.util.Set;

import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.mapper.AutoMapper;
import com.baomidou.mybatisplus.mapper.AutoSqlInjector;

/**
 * <p>
 * replace default Configuration class
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
public class MybatisConfiguration extends Configuration {

	private Logger logger = LoggerFactory.getLogger(MybatisConfiguration.class);

	/**
	 * 初始化调用
	 */
	public MybatisConfiguration() {
		/*
		 * 设置自定义 XMLLanguageDriver
		 */
		this.languageRegistry.setDefaultDriverClass(MybatisXMLLanguageDriver.class);
		System.err.println("mybatis-plus init success.");
	}
	
	/**
	 * <p>
	 * MybatisPlus 加载 SQL 顺序：
	 * </p>
	 * 1、加载XML中的SQL<br>
	 * 2、加载sqlProvider中的SQL<br>
	 * 3、xmlSql 与 sqlProvider不能包含相同的SQL<br>
	 * <br>
	 * 调整后的SQL优先级：xmlSql > sqlProvider > crudSql
	 * <br>
	 */
	@Override
	public void addMappedStatement(MappedStatement ms) {
		System.err.println(ms.getLang());
		if (this.mappedStatements.containsKey(ms.getId())) {
			/*
			 * 说明已加载了xml中的节点；
			 * 忽略mapper中的SqlProvider数据
			 */
			logger.warn("mapper[{}] is ignored, because it's exists, maybe from xml file", ms.getId());
			return;
		}
		super.addMappedStatement(ms);
	}
	
	@Override
	public LanguageDriver getDefaultScriptingLanuageInstance() {
		return languageRegistry.getDriver(MybatisXMLLanguageDriver.class);
	}

	@Override
	public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
		return super.getMapper(type, sqlSession);
	}

	/**
	 * 重新 addMapper 方法
	 */
	@Override
	public <T> void addMapper(Class<T> type) {
		super.addMapper(type);

		if (!AutoMapper.class.isAssignableFrom(type)) {
			return;
		}
		
		/* 自动注入 SQL */
		new AutoSqlInjector(this).inject(type);
	}

	@Override
	public void addMappers(String packageName) {
		this.addMappers(packageName, Object.class);
	}

	@Override
	public void addMappers(String packageName, Class<?> superType) {
		ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<Class<?>>();
		resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
		Set<Class<? extends Class<?>>> mapperSet = resolverUtil.getClasses();
		for (Class<?> mapperClass : mapperSet) {
			this.addMapper(mapperClass);
		}
	}
}
