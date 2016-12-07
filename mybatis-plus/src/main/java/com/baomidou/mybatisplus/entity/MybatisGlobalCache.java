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
package com.baomidou.mybatisplus.entity;

import com.baomidou.mybatisplus.enums.DBType;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.mapper.AutoSqlInjector;
import com.baomidou.mybatisplus.mapper.IMetaObjectHandler;
import com.baomidou.mybatisplus.mapper.ISqlInjector;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * <p>
 * Mybatis全局缓存
 * </p>
 *
 * @author Caratacus
 * @Date 2016-12-06
 */
public class MybatisGlobalCache implements Cloneable, Serializable {
	/**
	 * 默认参数
	 */
	public static final MybatisGlobalCache DEFAULT;

	// 数据库类型
	private DBType dbType;
	// 主键类型
	private IdType idType;
	// 表字段使用下划线命名
	private boolean dbColumnUnderline;
	// SQL注入器
	private ISqlInjector sqlInjector;
	// 元对象字段填充控制器
	private IMetaObjectHandler metaObjectHandler;
	// 元对象字段填充控制器
	private FieldStrategy fieldStrategy;
	// 是否刷新mapper
	private boolean isRefresh = false;
	// 是否自动获取DBType
	private boolean isAutoSetDbType = true;
	// 缓存当前Configuration的SqlSessionFactory
	private SqlSessionFactory sqlSessionFactory;

	private Set<String> mapperRegistryCache = new ConcurrentSkipListSet<String>();

	public DBType getDbType() {
		return dbType;
	}

	public void setDbType(DBType dbType) {
		this.dbType = dbType;
	}

	public IdType getIdType() {
		return idType;
	}

	public void setIdType(IdType idType) {
		this.idType = idType;
	}

	public boolean isDbColumnUnderline() {
		return dbColumnUnderline;
	}

	public void setDbColumnUnderline(boolean dbColumnUnderline) {
		this.dbColumnUnderline = dbColumnUnderline;
	}

	public ISqlInjector getSqlInjector() {
		return sqlInjector;
	}

	public void setSqlInjector(ISqlInjector sqlInjector) {
		this.sqlInjector = sqlInjector;
	}

	public IMetaObjectHandler getMetaObjectHandler() {
		return metaObjectHandler;
	}

	public void setMetaObjectHandler(IMetaObjectHandler metaObjectHandler) {
		this.metaObjectHandler = metaObjectHandler;
	}

	public FieldStrategy getFieldStrategy() {
		return fieldStrategy;
	}

	public void setFieldStrategy(FieldStrategy fieldStrategy) {
		this.fieldStrategy = fieldStrategy;
	}

	public boolean isRefresh() {
		return isRefresh;
	}

	public void setRefresh(boolean refresh) {
		isRefresh = refresh;
	}

	public boolean isAutoSetDbType() {
		return isAutoSetDbType;
	}

	public void setAutoSetDbType(boolean autoSetDbType) {
		isAutoSetDbType = autoSetDbType;
	}

	public Set<String> getMapperRegistryCache() {
		return mapperRegistryCache;
	}

	public void setMapperRegistryCache(Set<String> mapperRegistryCache) {
		this.mapperRegistryCache = mapperRegistryCache;
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	@Override
	protected MybatisGlobalCache clone() throws CloneNotSupportedException {
		return (MybatisGlobalCache) super.clone();
	}

	/**
	 * 获取当前的SqlSessionFactory
	 * 
	 * @param clazz
	 * @return
	 */
	public static SqlSessionFactory currentSessionFactory(Class clazz) {
		String configMark = TableInfoHelper.getTableInfo(clazz).getConfigMark();
		MybatisGlobalCache mybatisGlobalCache = MybatisGlobalCache.globalCache(configMark);
		return mybatisGlobalCache.getSqlSessionFactory();
	}

	/**
	 * 获取默认MybatisGlobalCache
	 * 
	 * @return
	 */
	public static MybatisGlobalCache defaults() {
		try {
			return DEFAULT.clone();
		} catch (CloneNotSupportedException e) {
			throw new MybatisPlusException("ERROR: CLONE MybatisGlobalCache DEFAULT FAIL !  Cause:" + e);
		}
	}

	/**
	 * <p>
	 * 设置全局设置 (统一所有入口)
	 * <p/>
	 *
	 * @param configuration
	 * @param mybatisGlobalCache
	 * @return
	 */
	public static void setGlobalCache(Configuration configuration, MybatisGlobalCache mybatisGlobalCache) {
		TableInfoHelper.setGlobalCache(configuration, mybatisGlobalCache);
	}

	/**
	 * 获取MybatisGlobalCache (统一所有入口)
	 * 
	 * @param configuration
	 * @return
	 */
	public static MybatisGlobalCache globalCache(Configuration configuration) {
		if (configuration != null) {
			return globalCache(configuration.toString());
		}
		return null;
	}

	/**
	 * 获取MybatisGlobalCache (统一所有入口)
	 * 
	 * @param configMark
	 * @return
	 */
	public static MybatisGlobalCache globalCache(String configMark) {
		return TableInfoHelper.getGlobalCache(configMark);
	}

	public static DBType getDbType(Configuration configuration) {
		return globalCache(configuration).getDbType();
	}

	public static IdType getIdType(Configuration configuration) {
		return globalCache(configuration).getIdType();
	}

	public static boolean isDbColumnUnderline(Configuration configuration) {
		return globalCache(configuration).isDbColumnUnderline();
	}

	public static ISqlInjector getSqlInjector(Configuration configuration) {
		return globalCache(configuration).getSqlInjector();
	}

	public static IMetaObjectHandler getMetaObjectHandler(Configuration configuration) {
		return globalCache(configuration).getMetaObjectHandler();
	}

	public static FieldStrategy getFieldStrategy(Configuration configuration) {
		return globalCache(configuration).getFieldStrategy();
	}

	public static boolean isRefresh(Configuration configuration) {
		return globalCache(configuration).isRefresh();
	}

	public static boolean isAutoSetDbType(Configuration configuration) {
		return globalCache(configuration).isAutoSetDbType();
	}

	public static Set<String> getMapperRegistryCache(Configuration configuration) {
		return globalCache(configuration).getMapperRegistryCache();
	}

	// init 初始化默认值
	static {
		DEFAULT = new MybatisGlobalCache();
		/*
		 * 数据库类型（默认 MySql）
		 */
		DEFAULT.setDbType(DBType.MYSQL);

		/*
		 * 主键策略 （默认 ID_WORKER）
		 */
		DEFAULT.setIdType(IdType.ID_WORKER);

		/*
		 * 数据库字段使用下划线命名（默认 false）
		 */
		DEFAULT.setDbColumnUnderline(false);

		/*
		 * SQL 注入器，实现 ISqlInjector 或继承 AutoSqlInjector 自定义方法
		 */
		DEFAULT.setSqlInjector(new AutoSqlInjector());

		/*
		 * 元对象字段填充控制器
		 */
		DEFAULT.setMetaObjectHandler(null);
		/*
		 * 字段验证策略
		 */
		DEFAULT.setFieldStrategy(FieldStrategy.NOT_NULL);

		/*
		 * 是否刷新mapper
		 */
		DEFAULT.setRefresh(false);
	}
}