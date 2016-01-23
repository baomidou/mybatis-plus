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
package com.baomidou.mybatisplus.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.annotation.Id;
import com.baomidou.mybatisplus.annotation.Table;
import com.baomidou.mybatisplus.toolkit.FieldReflectionHelper;

/**
 * <p>
 * SQL 自动注入器
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
public class AutoSqlInjector {

	private transient Logger logger = LoggerFactory.getLogger(getClass());

	private static final String DEFAULT_ID = "id";

	private static final String SQL_DELETE = "DELETE FROM %s WHERE %s = #{ID}";
	private static final String SQL_SELECTONE = "SELECT * FROM %s WHERE %s = #{ID}";
	private static final String SQL_SELECTALL = "SELECT * FROM %s";

	private static final String METHOD_INSERTONE = "insert";
	private static final String METHOD_UPDATEONE = "updateById";
	private static final String METHOD_DELETEONE = "deleteById";
	private static final String METHOD_SELECTONE = "selectById";
	private static final String METHOD_SELECTALL = "selectAll";

	private Configuration configuration;
	private MapperBuilderAssistant assistant;

	/**
	 * 数据库主键
	 */
	private TID tableID;

	public AutoSqlInjector(Configuration configuration) {
		super();
		this.configuration = configuration;
	}

	public void inject(Class<?> mapperClass) {
		assistant = new MapperBuilderAssistant(configuration, mapperClass.getName().replaceAll("\\.", "/"));
		assistant.setCurrentNamespace(mapperClass.getName());

		Class<?> modelClass = extractModelClass(mapperClass);
		String table = this.extractTableName(modelClass);
		tableID = this.extractTableID(modelClass);

		/* 新增 */
		this.injectInsertSql(mapperClass, modelClass, table);

		/* 没有指定主键，默认忽略按主键修改、删除、查询方法 */
		if (tableID != null) {
			/* 根据主键修改，主键名默认为id */
			this.injectUpdateSql(mapperClass, modelClass, table);

			/* 根据主键删除，主键名默认为id */
			SqlSource sqlSource = new RawSqlSource(configuration, String.format(SQL_DELETE, table, tableID.name),
					Object.class);
			this.addMappedStatement(mapperClass, METHOD_DELETEONE, sqlSource, SqlCommandType.DELETE, null);

			/* 根据主键查找，主键名默认为id */
			sqlSource = new RawSqlSource(configuration, String.format(SQL_SELECTONE, table, tableID.name),
					Object.class);
			this.addMappedStatement(mapperClass, METHOD_SELECTONE, sqlSource, SqlCommandType.SELECT, modelClass);
		}

		/* 查询全部 */
		SqlSource sqlSource = new RawSqlSource(configuration, String.format(SQL_SELECTALL, table), null);
		this.addMappedStatement(mapperClass, METHOD_SELECTALL, sqlSource, SqlCommandType.SELECT, modelClass);
	}

	private Class<?> extractModelClass(Class<?> mapperClass) {
		Type[] types = mapperClass.getGenericInterfaces();
		ParameterizedType target = null;
		for (Type type : types) {
			if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType().equals(AutoMapper.class)) {
				target = (ParameterizedType) type;
				break;
			}
		}
		Type[] parameters = target.getActualTypeArguments();
		Class<?> modelClass = (Class<?>) parameters[0];
		return modelClass;
	}

	/** 提取数据库表名 **/
	private String extractTableName(Class<?> modelClass) {
		Table table = modelClass.getAnnotation(Table.class);
		if (table != null && table.name() != null && table.name().trim().length() > 0) {
			return table.name();
		}
		return this.camelToUnderline(modelClass.getSimpleName());
	}

	/** 提取主键 */
	private TID extractTableID(Class<?> modelClass) {
		List<Field> fields = FieldReflectionHelper.getAllFieldsExcludeTransient(modelClass);
		TID TID = null;
		for (Field field : fields) {
			Id id = field.getAnnotation(Id.class);
			if (id != null) {
				TID = new TID();
				TID.name = field.getName();
				TID.auto = id.auto();
				break;
			}
		}

		if (TID != null) {
			return TID;
		}

		/* 检测是否采用了默认id，作为主键 */
		for (Field field : fields) {
			if (this.isDefaultAutoID(field)) {
				TID = new TID();
				TID.name = field.getName();
				TID.auto = true;
				return TID;
			}
		}
		return null;
	}

	/**
	 * <p>
	 * 注入插入 SQL 语句
	 * </p>
	 */
	private void injectInsertSql(Class<?> mapperClass, Class<?> modelClass, String table) {
		KeyGenerator keyGenerator = new NoKeyGenerator();
		String keyProperty = null;
		String keyColumn = null;

		List<Field> fields = FieldReflectionHelper.getAllFieldsExcludeTransient(modelClass);
		StringBuilder fieldBuilder = new StringBuilder();
		StringBuilder placeholderBuilder = new StringBuilder();

		int fieldSize = fields.size();
		for (int i = 0; i < fieldSize; i++) {
			String fieldName = fields.get(i).getName();
			if (tableID != null && tableID.name.equals(fieldName) && tableID.auto) {
				keyGenerator = new Jdbc3KeyGenerator();
				keyProperty = keyColumn = tableID.name;
				continue;
			}

			fieldBuilder.append(fieldName);
			placeholderBuilder.append("#{" + fieldName + "}");
			if (i < fieldSize - 1) {
				fieldBuilder.append(",");
				placeholderBuilder.append(",");
			}
		}
		String sql = String.format("INSERT INTO %s(%s) VALUES(%s)", table, fieldBuilder.toString(), placeholderBuilder.toString());
		SqlSource sqlSource = new RawSqlSource(configuration, sql, modelClass);
		this.addInsertMappedStatement(mapperClass, modelClass, METHOD_INSERTONE, sqlSource, keyGenerator, keyProperty, keyColumn);
	}

	private void injectUpdateSql(Class<?> mapperClass, Class<?> modelClass, String table) {
		List<Field> fields = FieldReflectionHelper.getAllFieldsExcludeTransient(modelClass);
		StringBuilder sqlBuilder = new StringBuilder("UPDATE ").append(table).append(" SET ");

		int fieldSize = fields.size();
		for (int i = 0; i < fieldSize; i++) {
			String fieldName = fields.get(i).getName();
			if (tableID.name.equals(fieldName)) {
				continue;
			}

			sqlBuilder.append(fieldName).append("=#{").append(fieldName).append("}");
			if (i < fieldSize - 1) {
				sqlBuilder.append(", ");
			}
		}
		sqlBuilder.append(" WHERE ").append(tableID.name).append("= #{").append(tableID.name).append("}");
		SqlSource sqlSource = new RawSqlSource(configuration, sqlBuilder.toString(), modelClass);
		this.addUpdateMappedStatement(mapperClass, modelClass, METHOD_UPDATEONE, sqlSource);
	}

	private void addMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource, SqlCommandType sqlCommandType,
			Class<?> resultType) {
		this.addMappedStatement(mapperClass, id, sqlSource, sqlCommandType, null, resultType, new NoKeyGenerator(),
				null, null);
	}

	private void addInsertMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id, SqlSource sqlSource,
			KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
		this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.INSERT, modelClass, null, keyGenerator,
				keyProperty, keyColumn);
	}

	private void addUpdateMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id, SqlSource sqlSource) {
		this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.UPDATE, modelClass, null,
				new NoKeyGenerator(), null, null);
	}

	private void addMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource, SqlCommandType sqlCommandType,
			Class<?> parameterClass, Class<?> resultType, KeyGenerator keyGenerator, String keyProperty,
			String keyColumn) {
		String statementName = mapperClass.getName() + "." + id;
		if (configuration.hasStatement(statementName)) {
			logger.warn("{},已通过xml或SqlProvider加载了，忽略该sql的注入", statementName);
			return;
		}
		assistant.addMappedStatement(id, sqlSource, StatementType.PREPARED, sqlCommandType, null, null, null,
				parameterClass, null, resultType, null, false, true, false, keyGenerator, keyProperty, keyColumn,
				configuration.getDatabaseId(), new XMLLanguageDriver(), null);
	}

	/**
	 * 
	 * <p>
	 * 判断是否为id主键自增
	 * </p>
	 * 
	 * @param field
	 * @return
	 */
	private boolean isDefaultAutoID(Field field) {
		String fieldName = field.getName();
		if (DEFAULT_ID.equals(fieldName)) {
			Class<?> fieldClass = field.getType();
			if (fieldClass.equals(long.class) || fieldClass.equals(Long.class) || fieldClass.equals(int.class)
					|| fieldClass.equals(Integer.class)) {
				return true;
			}
		}
		return false;
	}

	private String camelToUnderline(String param) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (Character.isUpperCase(c) && i > 0) {
				sb.append("_");
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(Character.toLowerCase(c));
			}
		}
		return sb.toString();
	}

	/**
	 * Table ID
	 */
	class TID {
		String name;
		boolean auto;
	}
}
