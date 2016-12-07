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

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import com.baomidou.mybatisplus.enums.DBType;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.mapper.AutoSqlInjector;
import com.baomidou.mybatisplus.mapper.IMetaObjectHandler;
import com.baomidou.mybatisplus.mapper.ISqlInjector;
import com.baomidou.mybatisplus.toolkit.JdbcUtils;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;

/**
 * <p>
 * Mybatis全局缓存
 * </p>
 *
 * @author Caratacus
 * @Date 2016-12-06
 */
@SuppressWarnings("serial")
public class MybatisGlobalCache implements Cloneable, Serializable {

	// 日志
	private static final Log logger = LogFactory.getLog(MybatisGlobalCache.class);

	/**
	 * 默认参数
	 */
	public static final MybatisGlobalCache DEFAULT = new MybatisGlobalCache(new AutoSqlInjector());

	// 数据库类型（默认 MySql）
	private DBType dbType = DBType.MYSQL;
	// 主键类型（默认 ID_WORKER）
	private IdType idType = IdType.ID_WORKER;
	// 表字段使用下划线命名（默认 false）
	private boolean dbColumnUnderline = false;
	// SQL注入器
	private ISqlInjector sqlInjector;
	// 元对象字段填充控制器
	private IMetaObjectHandler metaObjectHandler = null;
	// 元对象字段填充控制器
	private FieldStrategy fieldStrategy = FieldStrategy.NOT_NULL;
	// 是否刷新mapper
	private boolean isRefresh = false;
	// 是否自动获取DBType
	private boolean isAutoSetDbType = true;
	// 缓存当前Configuration的SqlSessionFactory
	private SqlSessionFactory sqlSessionFactory;

	private Set<String> mapperRegistryCache = new ConcurrentSkipListSet<String>();

	public MybatisGlobalCache() {
		// TODO
	}

	public MybatisGlobalCache(ISqlInjector sqlInjector) {
		this.sqlInjector = sqlInjector;
	}

	public DBType getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = DBType.getDBType(dbType);
		this.isAutoSetDbType = false;
	}

	public void setDbTypeByJdbcUrl(String jdbcUrl) {
		this.dbType = JdbcUtils.getDbType(jdbcUrl);
	}

	public IdType getIdType() {
		return idType;
	}

	public void setIdType(int idType) {
		this.idType = IdType.getIdType(idType);
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

	public void setFieldStrategy(int fieldStrategy) {
		this.fieldStrategy = FieldStrategy.getFieldStrategy(fieldStrategy);
	}

	public boolean isRefresh() {
		return isRefresh;
	}

	public void setRefresh(boolean refresh) {
		this.isRefresh = refresh;
	}

	public boolean isAutoSetDbType() {
		return isAutoSetDbType;
	}

	public void setAutoSetDbType(boolean autoSetDbType) {
		this.isAutoSetDbType = autoSetDbType;
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
	public static SqlSessionFactory currentSessionFactory(Class<?> clazz) {
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
	public void setGlobalCache(Configuration configuration) {
		TableInfoHelper.setGlobalCache(configuration, this);
	}

	/**
	 * 获取MybatisGlobalCache (统一所有入口)
	 * 
	 * @param configuration
	 * @return
	 */
	public static MybatisGlobalCache globalCache(Configuration configuration) {
		if (configuration == null) {
			throw new MybatisPlusException("Error: You need Initialize MybatisConfiguration !");
		}
		return globalCache(configuration.toString());
	}

	/**
	 * 获取MybatisGlobalCache (统一所有入口)
	 * 
	 * @param configMark
	 * @return
	 */
	public static MybatisGlobalCache globalCache(String configMark) {
		MybatisGlobalCache globalCache = TableInfoHelper.getGlobalCache(configMark);
		if (globalCache == null) {
			// 没有获取全局配置初始全局配置
			logger.warn("Warn: Not getting global configuration ! global configuration Initializing !");
			return DEFAULT;
		}
		return globalCache;
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

}
