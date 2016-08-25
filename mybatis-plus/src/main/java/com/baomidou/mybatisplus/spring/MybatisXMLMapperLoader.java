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
package com.baomidou.mybatisplus.spring;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 切莫用于生产环境（后果自负）<br>
 * mybatis映射文件热加载（发生变动后自动重新加载）.<br>
 * 方便开发时使用，不用每次修改xml文件后都要去重启应用.<br>
 * 特性：<br>
 * 1.支持不同的数据源。<br>
 * 2.双线程实时监控，一个用来监控全局，一个用来实时监控热点文件。（100ms）（热点文件2分钟内没续修改自动过期）<br>
 * 3.对于CPU不给力和映射文件庞大的应用，有一定程度的性能问题。<br>
 * </p>
 * 
 * @author hubin
 * @Date 2016-08-25
 */
public class MybatisXMLMapperLoader implements DisposableBean, InitializingBean, ApplicationContextAware {

	private ApplicationContext applicationContext;
	private ScheduledExecutorService pool;

	// 多数据源的场景使用
	private Boolean enableAutoReload = true; // 是否启用热加载.
	private String mapperLocations; // 指定映射配置文件
	private MapperScannerConfigurer config;
	private SqlSessionFactory sqlSessionFactory;

	/**
	 * 是否启用热加载.
	 * 
	 * @param config
	 */
	public void setEnableAutoReload(Boolean enableAutoReload) {
		this.enableAutoReload = enableAutoReload;
	}

	/**
	 * 指定映射配置文件.
	 * 
	 * @param config
	 */
	public void setMapperLocations(String mapperLocations) {
		if (!StringUtils.isEmpty(mapperLocations)) {
			this.mapperLocations = mapperLocations;
		}
	}

	/**
	 * 设置配置对象.
	 *
	 * @param config
	 */
	public void setConfig(MapperScannerConfigurer config) {
		this.config = config;
	}

	/**
	 * 设置数据源.
	 *
	 * @param sqlSessionFactory
	 */
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void afterPropertiesSet() throws Exception {

		// 检查设置
		if (!enableAutoReload) {
			System.out.println("禁用：mybatis自动热加载！");
			return;
		} else {
			System.out.println("启用：mybatis自动热加载");
		}

		checkProperties(); // 检查属性

		// 获取mapperLocations
		String mapperLocations = getMapperLocations();

		// 初始化线程池2个（避免线程来回切换）（一个用来监控全局，一个用来实时监控热点文件.）
		pool = Executors.newScheduledThreadPool(2);

		// 配置扫描器.
		final AutoReloadScanner scaner = new AutoReloadScanner(mapperLocations);
		scaner.start();

		// 扫描全部（2s一次）
		pool.scheduleAtFixedRate(new Runnable() {
			public void run() {
				scaner.scanAllFileChange();
			}
		}, 2, 2, TimeUnit.SECONDS);

		// 扫描热点文件（100ms一次，监控更为频繁）
		pool.scheduleAtFixedRate(new Runnable() {
			public void run() {
				scaner.scanHotspotFileChange();
			}
		}, 2, 100, TimeUnit.MILLISECONDS);

		System.out.println("启动mybatis自动热加载");
	}

	/**
	 * 获取配置文件路径（mapperLocations）.
	 *
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws Exception
	 */
	private String getMapperLocations() throws NoSuchFieldException, IllegalAccessException, Exception {

		// 优先使用mapperLocations
		if (mapperLocations != null) {
			return mapperLocations;
		}

		// 从MapperScannerConfigurer中获取.
		if (config != null) {
			Field field = config.getClass().getDeclaredField("basePackage");
			field.setAccessible(true);
			return (String) field.get(config);
		}

		// 根本就获取不到 org.mybatis.spring.SqlSessionFactoryBean
		// spring的org.springframework.beans.factory.FactoryBean 将其封装成为了一个
		// org.apache.ibatis.session.defaults.DefaultSqlSessionFactory
		// 牛逼的spring
		// if(sqlSessionFactory != null){
		// Field field =
		// sqlSessionFactory.getClass().getDeclaredField("mapperLocations");
		// field.setAccessible(true);
		// Resource[] mapperLocations = (Resource[])
		// field.get(sqlSessionFactory);
		// StringBuilder sb = new StringBuilder();
		// for(Resource r : mapperLocations){
		// String n = r.getURL().toString();
		// sb.append(n).append("\n");
		// }
		// return sb.toString();
		// }
		throw new RuntimeException("获取mapperLocations失败！");
	}

	/**
	 * 检查属性，如果没有设置，直接初始化成默认的方式.
	 */
	private void checkProperties() {
		// 如果没有指定数据源，直接使用默认的方式获取数据源
		if (sqlSessionFactory == null) {
			try {
				sqlSessionFactory = applicationContext.getBean(SqlSessionFactory.class);
			} catch (BeansException e) {
				throw new RuntimeException("获取数据源失败！", e);
			}
		}

		// 如果没有指定配置文件，使用默认的方式获取配置文件
		if (config == null && mapperLocations == null) {
			try {
				config = applicationContext.getBean(MapperScannerConfigurer.class);
			} catch (BeansException e) {
				System.err.println("获取配置文件失败！");
			}
		}

		if (config == null && mapperLocations == null) {
			throw new RuntimeException("设置配置mapperLocations失败！，请设置好配置属性，否则自动热加载无法起作用!");
		}
	}

	public void destroy() throws Exception {
		if (pool == null) {
			return;
		}
		pool.shutdown(); // 是否线程池资源
	}

	/**
	 * 自动重载扫描器的具体实现
	 *
	 *
	 * @author thomas
	 * @date Mar 31, 2016 6:59:34 PM
	 *
	 */
	class AutoReloadScanner {

		static final String XML_RESOURCE_PATTERN = "**/*.xml";
		static final String CLASSPATH_ALL_URL_PREFIX = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;

		static final int expireTimes = 600 * 2; // 2分钟内没有继续修改，变成非热点文件.不进行实时监控.

		// 需要扫描的包
		String[] basePackages;

		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

		// 所有文件
		Map<String, String> files = new ConcurrentHashMap<String, String>();

		// 热点文件.
		Map<String, AtomicInteger> hotspot = new ConcurrentHashMap<String, AtomicInteger>();

		public AutoReloadScanner(String basePackage) {
			basePackages = StringUtils.tokenizeToStringArray(basePackage,
					ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
		}

		/**
		 * 只扫描热点文件改变.（热点文件失效：连续600个扫描周期内（1分钟）没有改变）
		 */
		public void scanHotspotFileChange() {

			// 如果热点文件为空，立即返回.
			if (hotspot.isEmpty()) {
				return;
			}

			List<String> list = new ArrayList<String>();
			for (Map.Entry<String, AtomicInteger> e : hotspot.entrySet()) {
				String url = e.getKey();
				AtomicInteger counter = e.getValue();
				if (counter.incrementAndGet() >= expireTimes) {
					// 计数器自增，判断是否超过指定的过期次数
					list.add(url);
				}
				if (hasChange(url, files.get(url))) {
					reload(url); // 变化，调用重新加载方法
					counter.set(0); // 计数器清零
				}
			}

			// 移除过期的热点文件
			if (!list.isEmpty()) {
				// System.out.println("移除过期的热点文件：list=" + list);
				for (String s : list) {
					hotspot.remove(s);
				}
			}
		}

		/**
		 * 重新加载文件.
		 *
		 * @param url
		 */
		private void reload(String url) {
			reloadAll(); // 必须加载所有文件，否则其它文件由于没有加载会导致找不到对应的语句异常（暂时先这样吧）
		}

		/**
		 * 重新加载所有文件.
		 */
		private void reloadAll() {
			StopWatch sw = new StopWatch("mybatis mapper auto reload");
			sw.start();
			Configuration configuration = getConfiguration();
			for (Map.Entry<String, String> entry : files.entrySet()) {
				String location = entry.getKey();
				Resource r = resourcePatternResolver.getResource(location);
				try {
					XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(r.getInputStream(), configuration,
							r.toString(), configuration.getSqlFragments());
					xmlMapperBuilder.parse();
				} catch (Exception e) {
					throw new RuntimeException("Failed to parse mapping resource: '" + r + "'", e);
				} finally {
					ErrorContext.instance().reset();
				}
			}
			sw.stop();
			// System.out.println("重新加载mybatis映射文件完成.");
			System.out.println(sw.shortSummary());
		}

		/**
		 * 扫描所有文件改变.
		 */
		public void scanAllFileChange() {
			for (Map.Entry<String, String> entry : files.entrySet()) {
				String url = entry.getKey();
				if (hasChange(url, entry.getValue())) {
					// 变化，判断是否在热点文件中，如果存在，直接忽略，如果不存在，触发重新加载
					if (!hotspot.containsKey(url)) {
						// 添加到热点文件，并且触发重新加载
						hotspot.put(url, new AtomicInteger(0));
						reload(url);
					}
				}
			}
		}

		/**
		 * 判断文件是否变化.
		 *
		 * @param url
		 * @param tag
		 * @return
		 */
		private boolean hasChange(String url, String tag) {
			Resource r = resourcePatternResolver.getResource(url);
			String newTag = getTag(r);
			// 之前的标记和最新的标记不一致，说明文件修改了！
			if (!tag.equals(newTag)) {
				files.put(url, newTag); // 更新标记
				return true;
			}
			return false;
		}

		/**
		 * 获得文件的标记.
		 * 
		 * @param r
		 * @return
		 */
		private String getTag(Resource r) {
			try {
				StringBuilder sb = new StringBuilder();
				sb.append(r.contentLength());
				sb.append(r.lastModified());
				return sb.toString();
			} catch (IOException e) {
				throw new RuntimeException("获取文件标记信息失败！r=" + r, e);
			}
		}

		/**
		 * 开启扫描服务
		 */
		public void start() {
			try {
				for (String basePackage : basePackages) {
					Resource[] resources = getResource(basePackage);
					if (resources != null) {
						for (Resource r : resources) {
							String tag = getTag(r);
							files.put(r.getURL().toString(), tag);
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("初始化扫描服务失败！", e);
			}
		}

		/**
		 * 获取xml文件资源.
		 *
		 * @param basePackage
		 * @param pattern
		 * @return
		 * @throws IOException
		 */
		public Resource[] getResource(String basePackage) {
			try {
				if (!basePackage.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
					basePackage = CLASSPATH_ALL_URL_PREFIX
							+ ClassUtils.convertClassNameToResourcePath(
									applicationContext.getEnvironment().resolveRequiredPlaceholders(basePackage))
							+ "/" + XML_RESOURCE_PATTERN;
				}
				Resource[] resources = resourcePatternResolver.getResources(basePackage);
				return resources;
			} catch (Exception e) {
				throw new RuntimeException("获取xml文件资源失败！basePackage=" + basePackage, e);
			}
		}

		/**
		 * 获取配置信息，必须每次都重新获取，否则重新加载xml不起作用.
		 * 
		 * @return
		 */
		private Configuration getConfiguration() {
			Configuration configuration = sqlSessionFactory.getConfiguration();
			removeConfig(configuration);
			return configuration;
		}

		/**
		 * 删除不必要的配置项.
		 * 
		 * @param configuration
		 * @throws Exception
		 */
		private void removeConfig(Configuration configuration) {
			try {
				Class<?> classConfig = configuration.getClass();
				clearMap(classConfig, configuration, "mappedStatements");
				clearMap(classConfig, configuration, "caches");
				clearMap(classConfig, configuration, "resultMaps");
				clearMap(classConfig, configuration, "parameterMaps");
				clearMap(classConfig, configuration, "keyGenerators");
				clearMap(classConfig, configuration, "sqlFragments");
				clearSet(classConfig, configuration, "loadedResources");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@SuppressWarnings("rawtypes")
		private void clearMap(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
			Field field = classConfig.getDeclaredField(fieldName);
			field.setAccessible(true);
			Map mapConfig = (Map) field.get(configuration);
			mapConfig.clear();
		}

		@SuppressWarnings("rawtypes")
		private void clearSet(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
			Field field = classConfig.getDeclaredField(fieldName);
			field.setAccessible(true);
			Set setConfig = (Set) field.get(configuration);
			setConfig.clear();
		}

	}

}
