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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * <p>
 * 数据加载拦截，Controller 层方法调用后页面渲染前加载数据
 * </p>
 * <p>
 * 	//1、页面实现控制拦截器接口
 * 	public class BaseController extends SuperController implements HandlerInterceptor { ... }
 *  <br>
 *  //2、配置数据加载拦截
 * 	<mvc:interceptors>
 *   	<bean class="com.baomidou.framework.spring.DataLoadingInterceptor" />
 *  </mvc:interceptors>
 * </p>
 * 
 * @author hubin
 * @Date 2016-03-15
 */
public class DataLoadingInterceptor implements HandlerInterceptor {


	@Override
	public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler )
		throws Exception {
		if ( handler instanceof HandlerMethod ) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			if ( handlerMethod.getBean() instanceof HandlerInterceptor ) {
				return ((HandlerInterceptor) handlerMethod.getBean()).preHandle(request, response, handler);
			}
		}
		return true;
	}


	@Override
	public void postHandle( HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView ) throws Exception {
		if ( handler instanceof HandlerMethod ) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			if ( handlerMethod.getBean() instanceof HandlerInterceptor ) {
				((HandlerInterceptor) handlerMethod.getBean()).postHandle(request, response, handler, modelAndView);
			}
		}
	}


	@Override
	public void afterCompletion( HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex ) throws Exception {
		if ( handler instanceof HandlerMethod ) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			if ( handlerMethod.getBean() instanceof HandlerInterceptor ) {
				((HandlerInterceptor) handlerMethod.getBean()).afterCompletion(request, response, handler, ex);
			}
		}
	}

}
