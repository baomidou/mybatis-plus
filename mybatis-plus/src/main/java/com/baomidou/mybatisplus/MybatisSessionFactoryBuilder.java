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

import com.baomidou.mybatisplus.entity.MybatisGlobalCache;
import com.baomidou.mybatisplus.enums.DBType;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.mapper.IMetaObjectHandler;
import com.baomidou.mybatisplus.mapper.ISqlInjector;
import com.baomidou.mybatisplus.toolkit.IOUtils;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

/**
 * <p>
 * replace default SqlSessionFactoryBuilder class
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
public class MybatisSessionFactoryBuilder extends SqlSessionFactoryBuilder {

	private MybatisGlobalCache globalCache = MybatisGlobalCache.defaults();

	@Override
	public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
		try {
			MybatisXMLConfigBuilder parser = new MybatisXMLConfigBuilder(reader, environment, properties);
			// 原生支持全局配置缓存
			Configuration configuration = parser.parse();
			TableInfoHelper.setGlobalCache(configuration, globalCache);
			return build(configuration);
		} catch (Exception e) {
			throw ExceptionFactory.wrapException("Error building SqlSession.", e);
		} finally {
			ErrorContext.instance().reset();
			IOUtils.closeQuietly(reader);
		}
	}

	@Override
	public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
		try {
			MybatisXMLConfigBuilder parser = new MybatisXMLConfigBuilder(inputStream, environment, properties);
			// 原生支持全局配置缓存
			Configuration configuration = parser.parse();
			TableInfoHelper.setGlobalCache(configuration, globalCache);
			return build(configuration);
		} catch (Exception e) {
			throw ExceptionFactory.wrapException("Error building SqlSession.", e);
		} finally {
			ErrorContext.instance().reset();
			IOUtils.closeQuietly(inputStream);
		}
	}

	// TODO 注入数据库类型
	public void setDbType(String dbType) {
		globalCache.setDbType(DBType.getDBType(dbType));
	}

	// TODO 注入主键策略
	public void setIdType(int idType) {
		globalCache.setIdType(IdType.getIdType(idType));
	}

	// TODO 注入表字段使用下划线命名
	public void setDbColumnUnderline(boolean dbColumnUnderline) {
		globalCache.setDbColumnUnderline(dbColumnUnderline);
	}

	// TODO 注入 SQL注入器
	public void setSqlInjector(ISqlInjector sqlInjector) {
		globalCache.setSqlInjector(sqlInjector);
	}

	// TODO 注入 元对象字段填充控制器
	public void setMetaObjectHandler(IMetaObjectHandler metaObjectHandler) {
		globalCache.setMetaObjectHandler(metaObjectHandler);
	}

	// TODO 注入 元对象字段填充控制器
	public void setFieldStrategy(int key) {
		globalCache.setFieldStrategy(FieldStrategy.getFieldStrategy(key));
	}

}
