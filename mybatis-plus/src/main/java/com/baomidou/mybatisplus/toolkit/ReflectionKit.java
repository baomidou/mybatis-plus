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
 * <p>
 * 反射工具类
 * </p>
 *
 * @author Caratacus
 * @Date 2016-09-22
 */
public class ReflectionKit {

	private static Logger logger = LoggerFactory.getLogger(ReflectionKit.class);

	/**
	 * <p>
	 * 反射 method 方法名，例如 getId
	 * </p>
	 *
	 * @param str
	 *            属性字符串内容
	 * @return
	 */
	public static String getMethodCapitalize(final String str) {
		return StringUtils.concatCapitalize("get", str);
	}

	/**
	 * 调用对象的get方法检查对象所有属性是否为null
	 * 
	 * @param bean
	 *            检查对象
	 * @return boolean true对象所有属性不为null,false对象所有属性为null
	 */
	public static boolean checkFieldValueNotNull(Object bean) {
		if (null == bean) {
			return false;
		}

		Class<?> cls = bean.getClass();
		Method[] methods = cls.getDeclaredMethods();
		TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
		if (null == tableInfo) {
			logger.warn("Warn: Could not find @TableId.");
			return false;
		}

		boolean result = false;
		List<TableFieldInfo> fieldList = tableInfo.getFieldList();
		for (TableFieldInfo tableFieldInfo : fieldList) {
			String fieldGetName = getMethodCapitalize(tableFieldInfo.getProperty());
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
				logger.warn("Warn: Unexpected exception on checkFieldValueNull.  Cause:" + e);
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