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

import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.baomidou.mybatisplus.entity.MybatisGlobalCache;
import com.baomidou.mybatisplus.toolkit.IOUtils;

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
			globalCache.setGlobalCache(configuration);
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
			globalCache.setGlobalCache(configuration);
			return build(configuration);
		} catch (Exception e) {
			throw ExceptionFactory.wrapException("Error building SqlSession.", e);
		} finally {
			ErrorContext.instance().reset();
			IOUtils.closeQuietly(inputStream);
		}
	}

	//TODO 注入全局配置
	public void setMybatisGlobalCache(MybatisGlobalCache mybatisGlobalCache) {
		this.globalCache = mybatisGlobalCache;
	}

}
