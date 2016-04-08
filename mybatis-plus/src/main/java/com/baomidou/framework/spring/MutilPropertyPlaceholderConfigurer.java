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
package com.baomidou.framework.spring;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;


/**
 * <p>
 * spring 根据不同配置运行模式，加载对应配置内容。
 * </p>
 * <p>
 * 运行模式参数 key 配置 configEnv 默认 sysRunmode<br>
 * online 线上 ， dev 开发 ， test 测试<br>
 * 首先环境变量中获取，变量名：sysRunmode 变量值：dev <br>
 * 如果不存在 JVM -D选项 参数中获取，例如：-DsysRunmode=dev <br>
 * </p>
 * 
 * 例如：设置不同环境的数据库密码配置：
 * <p>
 * jdbc.password_dev_mode=1230600<br>
 * jdbc.password_test_mode=2001006<br>
 * jdbc.password_online_mode=#!Esd30210<br>
 * </p>
 * 
 * <p>
 * --------------------   使用配置       -------------------------
 * <bean id="placeholder" class="com.baomidou.mybatisplus.spring.MutilPropertyPlaceholderConfigurer">
 * 	  <property name="locations">
 * 		 <list>
 * 			<value>classpath:jdbc.properties</value>
 * 			<value>classpath*:*-placeholder.properties</value>
 * 		 </list>
 * 	  </property>
 * </bean>
 * -------------------------------------------------------
 * </p>
 * <p>
 * --------------------   使用方法       -------------------------
 *  //1、 Controller 层使用可以直接注入，通过 prop.getProperty("xxx") 获取 properties 加载配置内容
 * 	//	@Autowired
 *	//	protected MutilPropertyPlaceholderConfigurer prop;
 *	<br>
 *  //2、 Service 层使用 Value 注解自动获取  properties 加载配置内容
 *  //	@Value(${'xxxxx'})
 *  //	private String xxx;
 * -------------------------------------------------------
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-27
 */
public class MutilPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
	
	/**
	 * <p>
	 * 运行环境<br>
	 * online 线上 ， dev 开发 ， test 测试
	 * </p>
	 */
	private static final String ONLINE = "online";

	private static final String DEV = "dev";

	private static final String TEST = "test";

	/**
	 * 运行环境配置变量名
	 */
	private String configEnv = "sysRunmode";

	private Properties properties;


	/**
	 * 获取当前运行模式，默认 DEV 开发模式。
	 * <p>
	 * 首先环境变量中获取，变量名：sysRunmode 变量值：dev <br>
	 * 如果不存在 JVM -D选项 参数中获取，例如：-DsysRunmode=dev <br>
	 * </p>
	 */
	public String getRunMode() {
		String mode = System.getenv(getConfigEnv());
		if ( mode == null || "".equals(mode) ) {
			mode = System.getProperty(getConfigEnv());
		}
		if ( mode != null ) {
			if ( ONLINE.equals(mode) ) {
				mode = ONLINE;
			} else if ( DEV.equals(mode) ) {
				mode = DEV;
			} else if ( TEST.equals(mode) ) {
				mode = TEST;
			}
			/**
			 * 其他使用自定义 mode 类型，使用 IP 例如 mode = 30
			 * 配置为  jdb.url_30_mode = xxxxx 
			 */
		} else {
			/**
			 * Windows 认为是开发环境
			 */
			if (isLinux()) {
				mode = ONLINE;
			} else {
				mode = DEV;
			}
		}
		System.err.println("-DsysRunmode=" + mode + "_mode");
		return mode;
	}

	
	/**
	 * 判断是否为 Linux 环境
	 */
	protected boolean isLinux(  ) {
		String OS = System.getProperty("os.name").toLowerCase();
		logger.info("os.name: " + OS);
		if (OS != null && OS.contains("windows")) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * <p>
	 * spring 加载完配置文件，依靠父类  PropertiesLoaderSupport 方法 mergeProperties() 合并<br>
	 * 重载该方法实现不同环境配置选择。
	 * </p>
	 * 
	 */
	@Override
	protected Properties mergeProperties() throws IOException {
		this.properties = convertMergeProperties(super.mergeProperties());
		return this.properties;
	}

	/**
	 * <p>
	 * 转换 prop 加载内容
	 * </p>
	 * @param mergeProperties
	 * 					spring 容器加载 Properties 文件
	 * @return
	 */
	protected Properties convertMergeProperties(Properties mergeProperties) {
		Properties prop = new Properties();
		String runMode = "_" + getRunMode() + "_mode";
		Set<Entry<Object, Object>> es = mergeProperties.entrySet();
		for ( Entry<Object, Object> entry : es ) {
			String key = (String) entry.getKey();
			String realKey = key;
			int idx = key.lastIndexOf("_mode");
			if ( idx > 0 ) {
				if ( key.contains(runMode) ) {
					realKey = key.substring(0, key.lastIndexOf(runMode));
				} else {
					/** 排除其他运行模式 */
					realKey = null;
				}
			}
			/**
			 * 抽取合法属性<br>
			 * 如果某个属性为空抛出运行时异常
			 */
			if ( realKey != null && !prop.containsKey(realKey) ) {
				Object value = null;
				if ( idx > 0 ) {
					value = mergeProperties.get(realKey + runMode);
				} else {
					value = mergeProperties.get(realKey);
				}
				if ( value != null ) {
					prop.put(realKey, value);
				} else {
					throw new RuntimeException("impossible empty property for " + realKey);
				}
			}
		}
		return prop;
	}

	/** 
	 * 
	 * <p>
	 * 开放此方法给需要的业务 
	 * </p>
	 *  
	 * @param key
	 * 				查询属性
	 * @return 
	 */
	public String getProperty( String key ) {
		return this.resolvePlaceholder(key, this.properties);
	}


	public String getConfigEnv() {
		return configEnv;
	}


	public void setConfigEnv( String configEnv ) {
		if ( configEnv != null && !"".equals(configEnv) ) {
			this.configEnv = configEnv;
		}
	}

}
