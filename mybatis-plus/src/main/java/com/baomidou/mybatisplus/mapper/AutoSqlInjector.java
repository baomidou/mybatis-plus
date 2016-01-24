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

import com.baomidou.mybatisplus.toolkit.TableInfo;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;

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

	public AutoSqlInjector(Configuration configuration) {
		super();
		this.configuration = configuration;
	}

	public void inject(Class<?> mapperClass) {
		assistant = new MapperBuilderAssistant(configuration, mapperClass.getName().replaceAll("\\.", "/"));
		assistant.setCurrentNamespace(mapperClass.getName());

		Class<?> modelClass = extractModelClass(mapperClass);
		TableInfo table = TableInfoHelper.getTableInfo(modelClass);

		/* 新增 */
		this.injectInsertSql(mapperClass, modelClass, table);

		/* 没有指定主键，默认忽略按主键修改、删除、查询方法 */
		if (table.getTableId() != null) {
			/* 根据主键修改，主键名默认为id */
			this.injectUpdateSql(mapperClass, modelClass, table);

			/* 根据主键删除，主键名默认为id */
			SqlSource sqlSource = new RawSqlSource(configuration,
					String.format(SQL_DELETE, table.getTableName(), table.getTableId()), Object.class);
			this.addMappedStatement(mapperClass, METHOD_DELETEONE, sqlSource, SqlCommandType.DELETE, null);

			/* 根据主键查找，主键名默认为id */
			sqlSource = new RawSqlSource(configuration,
					String.format(SQL_SELECTONE, table.getTableName(), table.getTableId()), Object.class);
			this.addMappedStatement(mapperClass, METHOD_SELECTONE, sqlSource, SqlCommandType.SELECT, modelClass);
		}

		/* 查询全部 */
		SqlSource sqlSource = new RawSqlSource(configuration, String.format(SQL_SELECTALL, table.getTableName()), null);
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

	/**
	 * <p>
	 * 注入插入 SQL 语句
	 * </p>
	 */
	private void injectInsertSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		KeyGenerator keyGenerator = new NoKeyGenerator();
		StringBuilder fieldBuilder = new StringBuilder();
		StringBuilder placeholderBuilder = new StringBuilder();
		String keyParam = null;
		if (table.getTableId() != null){
			if(table.isAutoIncrement()) {
				/* 自增主键 */
				keyGenerator = new Jdbc3KeyGenerator();
				keyParam = table.getTableId();
			} else {
				/* 非自增，用户生成 */
				fieldBuilder.append(table.getTableId()).append(",");
				placeholderBuilder.append("#{" + table.getTableId() + "}").append(",");
			}
		}
		
		List<String> fieldList = table.getFieldList();
		int size = fieldList.size();
		for (int i = 0; i < size; i++) {
			String fielName = fieldList.get(i);
			fieldBuilder.append(fielName);
			placeholderBuilder.append("#{" + fielName + "}");
			if (i < size - 1) {
				fieldBuilder.append(",");
				placeholderBuilder.append(",");
			}
		}
		String sql = String.format("INSERT INTO %s(%s) VALUES(%s)", table.getTableName(), fieldBuilder.toString(),
				placeholderBuilder.toString());
		SqlSource sqlSource = new RawSqlSource(configuration, sql, modelClass);
		this.addInsertMappedStatement(mapperClass, modelClass, METHOD_INSERTONE, sqlSource, keyGenerator, keyParam,
				keyParam);
	}

	private void injectUpdateSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		StringBuilder sqlBuilder = new StringBuilder("UPDATE ").append(table.getTableName()).append(" SET ");
		List<String> fieldList = table.getFieldList();
		int size = fieldList.size();
		for (int i = 0; i < size; i++) {
			String fieldName = fieldList.get(i);
			sqlBuilder.append(fieldName).append("=#{").append(fieldName).append("}");
			if (i < size - 1) {
				sqlBuilder.append(", ");
			}
		}
		sqlBuilder.append(" WHERE ").append(table.getTableId()).append("= #{").append(table.getTableId()).append("}");
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

}
