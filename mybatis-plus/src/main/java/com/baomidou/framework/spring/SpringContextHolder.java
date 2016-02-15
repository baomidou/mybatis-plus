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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <p>
 * Spring 工具类 ，获取Spring容器中的上下文信息
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-15
 */
public class SpringContextHolder implements ApplicationContextAware {

	private static ApplicationContext context;


	public static ApplicationContext getApplicationContext() {
		checkApplicationContext();
		return context;
	}


	public static <T> T getBean( Class<T> clazz ) {
		checkApplicationContext();
		return context.getBean(clazz);
	}


	public void setApplicationContext( ApplicationContext ac ) throws BeansException {
		context = ac;
	}


	private static void checkApplicationContext() {
		if ( context == null ) {
			throw new IllegalStateException("applicaitonContext未注入,请在applicationContext.xml中定义SpringContextHolder");
		}
	}

}
