/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.toolkit;

import com.baomidou.mybatisplus.MybatisConfiguration;
import com.baomidou.mybatisplus.MybatisPlusHolder;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.entity.TableFieldInfo;
import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.baomidou.mybatisplus.enums.IdType;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 实体类反射表辅助类
 * </p>
 *
 * @author hubin sjy
 * @Date 2016-09-09
 */
public class TableInfoHelper {

	private static final Log logger = LogFactory.getLog(TableInfoHelper.class);

	/**
	 * 缓存反射类表信息
	 */
	private static final Map<String, TableInfo> tableInfoCache = new ConcurrentHashMap<String, TableInfo>();
	/**
	 * 默认表主键
	 */
	private static final String DEFAULT_ID_NAME = "id";

	/**
	 * <p>
	 * 获取实体映射表信息
	 * <p>
	 *
	 * @param clazz
	 *            反射实体类
	 * @return
	 */
	public static TableInfo getTableInfo(Class<?> clazz) {
		return tableInfoCache.get(clazz.getName());
	}

	/**
	 * <p>
	 * 实体类反射获取表信息【初始化】
	 * <p>
	 *
	 * @param clazz
	 *            反射实体类
	 * @return
	 */
	public synchronized static TableInfo initTableInfo(MapperBuilderAssistant builderAssistant, Class<?> clazz) {
		TableInfo ti = tableInfoCache.get(clazz.getName());
		if (ti != null) {
			return ti;
		}
		TableInfo tableInfo = new TableInfo();

		if (null != builderAssistant) {
			tableInfo.setCurrentNamespace(builderAssistant.getCurrentNamespace());
		}
		/* 表名 */
		TableName table = clazz.getAnnotation(TableName.class);
		String tableName = clazz.getSimpleName();
		if (table != null && StringUtils.isNotEmpty(table.value())) {
			tableName = table.value();
		} else if (MybatisConfiguration.DB_COLUMN_UNDERLINE) {
			/* 开启字段下划线申明 */
			tableName = StringUtils.camelToUnderline(tableName);
		} else {
			tableName = tableName.toLowerCase();
		}
		tableInfo.setTableName(tableName);
		/* 表结果集映射 */
		if (table != null && StringUtils.isNotEmpty(table.resultMap())) {
			tableInfo.setResultMap(table.resultMap());
		}
		List<TableFieldInfo> fieldList = new ArrayList<TableFieldInfo>();
		List<Field> list = getAllFields(clazz);
		boolean existTableId = existTableId(list);
		for (Field field : list) {

			/**
			 * 主键ID 初始化
			 */
			if (existTableId) {
				if (initTableId(tableInfo, field, clazz)) {
					continue;
				}
			} else if (initFieldId(tableInfo, field, clazz)) {
				continue;
			}

			/**
			 * 字段初始化
			 */
			if (initTableField(fieldList, field, clazz)) {
				continue;
			}

			/**
			 * 字段, 使用 camelToUnderline 转换驼峰写法为下划线分割法, 如果已指定 TableField , 便不会执行这里
			 */
			TableFieldInfo tfi = new TableFieldInfo(field.getName());
			fieldList.add(tfi);
		}

		/* 字段列表 */
		tableInfo.setFieldList(fieldList);
		/**
		 * SqlSessionFactory
		 */
		if (null != MybatisPlusHolder.getSqlSessionFactory()) {
			tableInfo.setSqlSessionFactory(MybatisPlusHolder.getSqlSessionFactory());
		}
		/*
		 * 未发现主键注解，跳过注入
		 */
		if (null == tableInfo.getKeyColumn()) {
			logger.warn(String.format("Warn: Could not find @TableId in Class: %s, initTableInfo Method Fail.",
					clazz.getName()));
			return null;
		}
		/*
		 * 注入
		 */
		tableInfoCache.put(clazz.getName(), tableInfo);
		return tableInfo;
	}

	/**
	 * <p>
	 * 判断主键注解是否存在
	 * </p>
	 *
	 * @param list
	 *            字段列表
	 * @return
	 */
	public static boolean existTableId(List<Field> list) {
		boolean exist = false;
		for (Field field : list) {
			TableId tableId = field.getAnnotation(TableId.class);
			if (tableId != null) {
				exist = true;
				break;
			}
		}
		return exist;
	}

	/**
	 * <p>
	 * 主键属性初始化
	 * </p>
	 *
	 * @param tableInfo
	 * @param field
	 * @param clazz
	 * @return true 继续下一个属性判断，返回 continue;
	 */
	private static boolean initTableId(TableInfo tableInfo, Field field, Class<?> clazz) {
		TableId tableId = field.getAnnotation(TableId.class);
		if (tableId != null) {
			if (tableInfo.getKeyColumn() == null) {
				tableInfo.setIdType(tableId.type());
				if (StringUtils.isNotEmpty(tableId.value())) {
					/* 自定义字段 */
					tableInfo.setKeyColumn(tableId.value());
					tableInfo.setKeyRelated(true);
				} else if (MybatisConfiguration.DB_COLUMN_UNDERLINE) {
					/* 开启字段下划线申明 */
					tableInfo.setKeyColumn(StringUtils.camelToUnderline(field.getName()));
				} else {
					tableInfo.setKeyColumn(field.getName());
				}
				tableInfo.setKeyProperty(field.getName());
				return true;
			} else {
				throwExceptionId(clazz);
			}
		}
		return false;
	}

	/**
	 * <p>
	 * 主键属性初始化
	 * </p>
	 *
	 * @param tableInfo
	 * @param field
	 * @param clazz
	 * @return true 继续下一个属性判断，返回 continue;
	 */
	private static boolean initFieldId(TableInfo tableInfo, Field field, Class<?> clazz) {
		if (DEFAULT_ID_NAME.equals(field.getName())) {
			if (tableInfo.getKeyColumn() == null) {
				tableInfo.setIdType(IdType.ID_WORKER);
				tableInfo.setKeyColumn(field.getName());
				tableInfo.setKeyProperty(field.getName());
				return true;
			} else {
				throwExceptionId(clazz);
			}
		}
		return false;
	}

	/**
	 * <p>
	 * 发现设置多个主键注解抛出异常
	 * </p>
	 */
	private static void throwExceptionId(Class<?> clazz) {
		StringBuffer errorMsg = new StringBuffer();
		errorMsg.append("There must be only one, Discover multiple @TableId annotation in ");
		errorMsg.append(clazz.getName());
		throw new MybatisPlusException(errorMsg.toString());
	}

	/**
	 * <p>
	 * 字段属性初始化
	 * </p>
	 *
	 * @param fieldList
	 * @param clazz
	 * @return true 继续下一个属性判断，返回 continue;
	 */
	private static boolean initTableField(List<TableFieldInfo> fieldList, Field field, Class<?> clazz) {
		/* 获取注解属性，自定义字段 */
		TableField tableField = field.getAnnotation(TableField.class);
		if (tableField != null) {
			String columnName = field.getName();
			if (StringUtils.isNotEmpty(tableField.value())) {
				columnName = tableField.value();
			}

			Class<?> fieldType = field.getType();
			FieldStrategy validate = tableField.validate();
			/* 字符串类型默认 FieldStrategy.NOT_EMPTY */
			if (String.class.isAssignableFrom(fieldType) && FieldStrategy.NOT_NULL.equals(validate)) {
				validate = FieldStrategy.NOT_EMPTY;
			}

			/*
			 * el 语法支持，可以传入多个参数以逗号分开
			 */
			String el = field.getName();
			if (StringUtils.isNotEmpty(tableField.el())) {
				el = tableField.el();
			}
			String[] columns = columnName.split(";");
			String[] els = el.split(";");
			if (null != columns && null != els && columns.length == els.length) {
				for (int i = 0; i < columns.length; i++) {
					fieldList.add(new TableFieldInfo(columns[i], field.getName(), els[i], validate));
				}
			} else {
				String errorMsg = "Class: %s, Field: %s, 'value' 'el' Length must be consistent.";
				throw new MybatisPlusException(String.format(errorMsg, clazz.getName(), field.getName()));
			}

			return true;
		}

		return false;
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

			/* 过滤静态属性 */
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

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

	/**
	 * 初始化sqlMapper (供Mybatis原生调用)
	 *
	 * @param sqlSessionFactory
	 * @return
	 */
	public static void initSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		for (TableInfo tableInfo : tableInfoCache.values()) {
			if (null == tableInfo.getSqlSessionFactory()) {
				tableInfo.setSqlSessionFactory(sqlSessionFactory);
			}
		}
	}

}
