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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;

/**
 * <p>
 * 实体类反射表辅助类
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
public class TableInfoHelper {

	/**
	 * 缓存反射类表信息
	 */
	private static Map<String, TableInfo> tableInfoCache = new ConcurrentHashMap<String, TableInfo>();

	/**
	 * <p>
	 * 根据实体类反射获取表信息
	 * <p>
	 * 
	 * @param clazz
	 *            反射实体类
	 * @return
	 */
	public synchronized static TableInfo getTableInfo(Class<?> clazz) {
		TableInfo ti = tableInfoCache.get(clazz.getName());
		if (ti != null) {
			return ti;
		}
		List<Field> list = getAllFields(clazz);
		TableInfo tableInfo = new TableInfo();

		/* 表名 */
		TableName table = clazz.getAnnotation(TableName.class);
		if (table != null && table.value() != null && table.value().trim().length() > 0) {
			tableInfo.setTableName(table.value());
		} else {
			tableInfo.setTableName(camelToUnderline(clazz.getSimpleName()));
		}

		List<TableFieldInfo> fieldList = new ArrayList<TableFieldInfo>();
		for (Field field : list) {
			/* 主键ID */
			TableId tableId = field.getAnnotation(TableId.class);
			if (tableId != null) {
				if (tableInfo.getKeyColumn() == null) {
					tableInfo.setIdType(tableId.type());
					if(tableId.value() != null && !"".equals(tableId.value())) {
						/* 自定义字段 */
						tableInfo.setKeyColumn(tableId.value());
						tableInfo.setKeyRelated(true);
					} else {
						tableInfo.setKeyColumn(field.getName());
					}
					tableInfo.setKeyProperty(field.getName());
					continue;
				} else {
					/* 发现设置多个主键注解抛出异常 */
					throw new MybatisPlusException("There must be only one, Discover multiple @TableId annotation in " + clazz);
				}
			}

			/* 获取注解属性，自定义字段 */
			TableField tableField = field.getAnnotation(TableField.class);
			if (tableField != null && tableField.value() != null && !"".equals(tableField.value())) {
				fieldList.add(new TableFieldInfo(true, tableField.value(), field.getName()));
				continue;
			}
			
			/* 字段 */
			fieldList.add(new TableFieldInfo(field.getName()));
		}

		/* 字段列表 */
		tableInfo.setFieldList(fieldList);

		/* 未发现主键注解抛出异常 */
		if (tableInfo.getKeyColumn() == null) {
			throw new MybatisPlusException("Not found @TableId annotation in " + clazz);
		}

		tableInfoCache.put(clazz.getName(), tableInfo);
		return tableInfo;
	}

	/**
	 * 去掉下划线转换为大写
	 */
	private static String camelToUnderline(String param) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (Character.isUpperCase(c) && i > 0) {
				sb.append("_");
			}
			sb.append(Character.toUpperCase(c));
		}
		return sb.toString();
	}

	/**
	 * 获取该类的所有属性列表
	 * 
	 * @param clazz
	 *            反射类
	 * @return
	 */
	private static List<Field> getAllFields(Class<?> clazz) {
		List<Field> result = new LinkedList<Field>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {

			/* 过滤 transient关键字修饰的属性 */
			if (Modifier.isTransient(field.getModifiers())) {
				continue;
			}

			/* 过滤注解非表字段属性 */
			TableField tableField = field.getAnnotation(TableField.class);
			if (tableField == null || tableField.exist()) {
				result.add(field);
			}
		}

		/* 处理父类字段 */
		Class<?> superClass = clazz.getSuperclass();
		if (superClass.equals(Object.class)) {
			return result;
		}
		result.addAll(getAllFields(superClass));
		return result;
	}

}
