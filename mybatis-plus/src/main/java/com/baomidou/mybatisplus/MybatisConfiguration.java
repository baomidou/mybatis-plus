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
package com.baomidou.mybatisplus;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import com.baomidou.mybatisplus.enums.DBType;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.mapper.AutoSqlInjector;
import com.baomidou.mybatisplus.mapper.IMetaObjectHandler;
import com.baomidou.mybatisplus.mapper.ISqlInjector;

/**
 * <p>
 * replace default Configuration class
 * </p>
 * <p>
 * Caratacus 2016/9/25 replace mapperRegistry
 * </p>
 *
 * @author hubin
 * @Date 2016-01-23
 */
public class MybatisConfiguration extends Configuration {

	private static final Log logger = LogFactory.getLog(MybatisConfiguration.class);

	/*
	 * 数据库类型（默认 MySql）
	 */
	public static DBType DB_TYPE = DBType.MYSQL;
	
	/*
	 * 主键策略 （默认 ID_WORKER）
	 */
	public static IdType ID_TYPE = IdType.ID_WORKER;

	/*
	 * 数据库字段使用下划线命名（默认 false）
	 */
	public static boolean DB_COLUMN_UNDERLINE = false;

	/*
	 * SQL 注入器，实现 ISqlInjector 或继承 AutoSqlInjector 自定义方法
	 */
	public static ISqlInjector SQL_INJECTOR = new AutoSqlInjector();

	/*
	 * Mapper 注册
	 */
	public final MybatisMapperRegistry mybatisMapperRegistry = new MybatisMapperRegistry(this);

	/**
	 * 缓存注册标识
	 */
	public static Set<String> MAPPER_REGISTRY_CACHE = new ConcurrentSkipListSet<String>();

	/*
	 * 元对象字段填充控制器
	 */
	public static IMetaObjectHandler META_OBJECT_HANDLER = null;

	/*
	 * 字段验证策略
	 */
	public static FieldStrategy FIELD_STRATEGY = FieldStrategy.NOT_NULL;

	/*
	 * 是否刷新mapper
	 */
	public static boolean IS_REFRESH = false;

	/**
	 * 初始化调用
	 */
	public MybatisConfiguration() {
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
	 * 调整后的SQL优先级：xmlSql > sqlProvider > curdSql <br>
	 */
	@Override
	public void addMappedStatement(MappedStatement ms) {
		logger.debug(" addMappedStatement: " + ms.getId());
		if (IS_REFRESH) {
			/*
			 * 支持是否自动刷新 XML 变更内容，开发环境使用【 注：生产环境勿用！】
			 */
			this.mappedStatements.remove(ms.getId());
		} else {
			if (this.mappedStatements.containsKey(ms.getId())) {
				/*
				 * 说明已加载了xml中的节点； 忽略mapper中的SqlProvider数据
				 */
				logger.error("mapper[" + ms.getId() + "] is ignored, because it's exists, maybe from xml file");
				return;
			}
		}
		super.addMappedStatement(ms);
	}

	@Override
	public void setDefaultScriptingLanguage(Class<?> driver) {
		if (driver == null) {
			/* 设置自定义 driver */
			driver = MybatisXMLLanguageDriver.class;
		}
		super.setDefaultScriptingLanguage(driver);
	}

	@Override
	public MapperRegistry getMapperRegistry() {
		return mybatisMapperRegistry;
	}

	@Override
	public <T> void addMapper(Class<T> type) {
		mybatisMapperRegistry.addMapper(type);
	}

	@Override
	public void addMappers(String packageName, Class<?> superType) {
		mybatisMapperRegistry.addMappers(packageName, superType);
	}

	@Override
	public void addMappers(String packageName) {
		mybatisMapperRegistry.addMappers(packageName);
	}

	@Override
	public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
		return mybatisMapperRegistry.getMapper(type, sqlSession);
	}

	@Override
	public boolean hasMapper(Class<?> type) {
		return mybatisMapperRegistry.hasMapper(type);
	}

}
