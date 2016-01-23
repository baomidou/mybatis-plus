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
package com.baomidou.mybatisplus.toolkit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 字段反射辅助类
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
public class FieldReflectionHelper {

	/**
	 * 获取该类的所有字符列表
	 * 
	 * @param clazz
	 *            反射类
	 * @return
	 */
	public static List<Field> getAllFields(Class<?> clazz) {
		List<Field> result = new LinkedList<Field>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			result.add(field);
		}

		Class<?> superClass = clazz.getSuperclass();
		if (superClass.equals(Object.class)) {
			return result;
		}
		result.addAll(getAllFields(superClass));
		return result;
	}

	/**
	 * 获取该类的所有字符列表，排查 Transient 类型的字段
	 * 
	 * @param clazz
	 *            反射类
	 * @return
	 */
	public static List<Field> getAllFieldsExcludeTransient(Class<?> clazz) {
		List<Field> result = new LinkedList<Field>();
		List<Field> list = getAllFields(clazz);
		for (Field field : list) {
			if (Modifier.isTransient(field.getModifiers())) {
				continue;
			}
			result.add(field);
		}
		return result;
	}
}
