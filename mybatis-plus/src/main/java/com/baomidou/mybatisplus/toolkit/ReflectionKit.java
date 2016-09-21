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
package com.baomidou.mybatisplus.toolkit;

import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 反射工具类.
 */
public class ReflectionKit {

	private static Logger logger = LoggerFactory.getLogger(ReflectionKit.class);

	/**
	 * 调用对象的get方法检查对象所以属性是否为null
	 * 
	 * @param bean
	 * @return boolean true对象所有属性不为null,false对象所有属性为null
	 */
	public static boolean checkFieldValueNull(Object bean) {
		boolean result = false;
		if (bean == null) {
			return true;
		}
		Class<?> cls = bean.getClass();
		Method[] methods = cls.getDeclaredMethods();
		TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);

		if (tableInfo == null) {
			logger.warn("Error: Could not find @TableId.");
			return false;
		}
		List<TableFieldInfo> fieldList = tableInfo.getFieldList();
		for (TableFieldInfo tableFieldInfo : fieldList) {
			String fieldGetName = StringUtils.concatCapitalize("get", tableFieldInfo.getProperty());
			if (!checkMethod(methods, fieldGetName)) {
				continue;
			}
			try {
				Method method = cls.getMethod(fieldGetName);
				Object obj = method.invoke(bean);
				if (null != obj) {
					result = true;
					break;
				}
			} catch (Exception e) {
				logger.warn("Unexpected exception on checkFieldValueNull.  Cause:" + e);
			}

		}
		return result;
	}

	/**
	 * 判断是否存在某属性的 get方法
	 *
	 * @param methods
	 *            对象所有方法
	 * @param method
	 *            当前检查的方法
	 * @return boolean true存在,false不存在
	 */
	public static boolean checkMethod(Method[] methods, String method) {
		for (Method met : methods) {
			if (method.equals(met.getName())) {
				return true;
			}
		}
		return false;
	}

}