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
import org.apache.ibatis.mapping.MappedStatement;
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

	private static final XMLLanguageDriver languageDriver = new XMLLanguageDriver();
	
	private Configuration configuration;

	private MapperBuilderAssistant assistant;


	public AutoSqlInjector( Configuration configuration ) {
		super();
		this.configuration = configuration;
	}


	public void inject( Class<?> mapperClass ) {
		assistant = new MapperBuilderAssistant(configuration, mapperClass.getName().replaceAll("\\.", "/"));
		assistant.setCurrentNamespace(mapperClass.getName());

		Class<?> modelClass = extractModelClass(mapperClass);
		TableInfo table = TableInfoHelper.getTableInfo(modelClass);

		/* 插入 */
		this.injectInsertSql(false, mapperClass, modelClass, table);
		this.injectInsertSql(true, mapperClass, modelClass, table);

		/* 没有指定主键，默认忽略按主键修改、删除、查询方法 */
		if ( table.getTableId() != null ) {
			/* 删除 */
			this.injectDeleteSql(false, mapperClass, modelClass, table);
			this.injectDeleteSql(true, mapperClass, modelClass, table);
			
			/* 修改 */
			this.injectUpdateSql(false, mapperClass, modelClass, table);
			this.injectUpdateSql(true, mapperClass, modelClass, table);

			/* 查询 */
			this.injectSelectSql(false, mapperClass, modelClass, table);
		}

		/* 查询全部 */
		SqlSource sqlSource = new RawSqlSource(configuration, String.format(SqlMethod.SELECT_ALL.getSql(), table.getTableName()), null);
		this.addMappedStatement(mapperClass, SqlMethod.SELECT_ALL, sqlSource, SqlCommandType.SELECT, modelClass);
	}


	private Class<?> extractModelClass( Class<?> mapperClass ) {
		Type[] types = mapperClass.getGenericInterfaces();
		ParameterizedType target = null;
		for ( Type type : types ) {
			if ( type instanceof ParameterizedType
					&& ((ParameterizedType) type).getRawType().equals(AutoMapper.class) ) {
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
	 * @param batch
	 * 				是否为批量插入
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	private void injectInsertSql( boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table ) {
		KeyGenerator keyGenerator = new NoKeyGenerator();
		StringBuilder fieldBuilder = new StringBuilder();
		StringBuilder placeholderBuilder = new StringBuilder();
		SqlMethod sqlMethod = SqlMethod.INSERT_ONE;
		if ( batch ) {
			sqlMethod = SqlMethod.INSERT_BATCH;
			placeholderBuilder.append("\n<foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\">");
		}

		String keyParam = null;
		if ( table.getTableId() != null ) {
			if ( table.isAutoIncrement() ) {
				/* 自增主键 */
				keyGenerator = new Jdbc3KeyGenerator();
				keyParam = table.getTableId();
			} else {
				/* 非自增，用户生成 */
				fieldBuilder.append(table.getTableId()).append(",");
				if ( batch ) {
					placeholderBuilder.append("(#{item.");
				} else {
					placeholderBuilder.append("#{");
				}
				placeholderBuilder.append(table.getTableId()).append("},");
			}
		}

		List<String> fieldList = table.getFieldList();
		int size = fieldList.size();
		for ( int i = 0 ; i < size ; i++ ) {
			String fielName = fieldList.get(i);
			fieldBuilder.append(fielName);
			placeholderBuilder.append("#{");
			if ( batch ) {
				placeholderBuilder.append("item.");
			}
			placeholderBuilder.append(fielName).append("}");
			if ( i < size - 1 ) {
				fieldBuilder.append(",");
				placeholderBuilder.append(",");
			}
		}

		if ( batch ) {
			placeholderBuilder.append(")\n</foreach>");
		}

		String sql = String.format(sqlMethod.getSql(), table.getTableName(), fieldBuilder.toString(),
			placeholderBuilder.toString());
		SqlSource sqlSource = null;
		if ( batch ) {
			sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
		} else {
			sqlSource = new RawSqlSource(configuration, sql, modelClass);
		}
		this.addInsertMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource,
			keyGenerator, keyParam, keyParam);
	}

	/**
	 * <p>
	 * 注入删除 SQL 语句
	 * </p>
	 * @param batch
	 * 				是否为批量插入
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	private void injectDeleteSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		SqlMethod sqlMethod = SqlMethod.DELETE_ONE;
		SqlSource sqlSource = null;
		if (batch) {
			sqlMethod = SqlMethod.DELETE_BATCH;
			StringBuilder ids = new StringBuilder();
			ids.append("\n<foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\">");
			ids.append("#{item}");
			ids.append("\n</foreach>");
			String sql = String.format(sqlMethod.getSql(), table.getTableName(), table.getTableId(), ids.toString());
			sqlSource = languageDriver.createSqlSource(configuration, sql.toString(), modelClass);
		} else {
			String sql = String.format(sqlMethod.getSql(), table.getTableName(), table.getTableId(), table.getTableId());
			sqlSource = new RawSqlSource(configuration, sql, Object.class);
		}
		this.addMappedStatement(mapperClass, sqlMethod, sqlSource, SqlCommandType.DELETE, null);
	}
	

	/**
	 * <p>
	 * 注入更新 SQL 语句
	 * </p>
	 * @param batch
	 * 				是否为批量插入
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	private void injectUpdateSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table ) {
		SqlMethod sqlMethod = SqlMethod.UPDATE_ONE;
		StringBuilder set = new StringBuilder();
		List<String> fieldList = table.getFieldList();
		int size = fieldList.size();
		for ( int i = 0 ; i < size ; i++ ) {
			String fieldName = fieldList.get(i);
			set.append(fieldName).append("=#{");
			if (batch) {
				set.append("item.");
			}
			set.append(fieldName).append("}");
			if ( i < size - 1 ) {
				set.append(", ");
			}
		}
		StringBuilder id = new StringBuilder();
		id.append(table.getTableId()).append("=#{");
		if (batch) {
			id.append("item.");
		}
		id.append(table.getTableId()).append("}");
		String sql = String.format(sqlMethod.getSql(), table.getTableName(), set.toString(), id.toString());
		SqlSource sqlSource = null;
		if(batch){
			sqlSource = new RawSqlSource(configuration, sql, modelClass);
		} else {
			sqlMethod = SqlMethod.UPDATE_BATCH;
			sqlSource = new RawSqlSource(configuration, String.format(sqlMethod.getSql(), sql), modelClass);
		}
		this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
	}


	/**
	 * <p>
	 * 注入查询 SQL 语句
	 * </p>
	 * @param batch
	 * 				是否为批量插入
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	private void injectSelectSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		/* 根据主键查找，主键名默认为id */
		SqlSource sqlSource = new RawSqlSource(configuration,
				String.format(SqlMethod.SELECT_ONE.getSql(), table.getTableName(), table.getTableId(), table.getTableId()), Object.class);
		this.addMappedStatement(mapperClass, SqlMethod.SELECT_ONE, sqlSource, SqlCommandType.SELECT, modelClass);
	}
	
	
	private MappedStatement addMappedStatement( Class<?> mapperClass, SqlMethod sm, SqlSource sqlSource,
			SqlCommandType sqlCommandType, Class<?> resultType ) {
		return this.addMappedStatement(mapperClass, sm.getMethod(), sqlSource, sqlCommandType, null, resultType,
			new NoKeyGenerator(), null, null);
	}


	private MappedStatement addInsertMappedStatement( Class<?> mapperClass, Class<?> modelClass, String id,
			SqlSource sqlSource, KeyGenerator keyGenerator, String keyProperty, String keyColumn ) {
		return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.INSERT, modelClass, null,
			keyGenerator, keyProperty, keyColumn);
	}


	private MappedStatement addUpdateMappedStatement( Class<?> mapperClass, Class<?> modelClass, String id,
			SqlSource sqlSource ) {
		return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.UPDATE, modelClass, null,
			new NoKeyGenerator(), null, null);
	}


	private MappedStatement addMappedStatement( Class<?> mapperClass, String id, SqlSource sqlSource,
			SqlCommandType sqlCommandType, Class<?> parameterClass, Class<?> resultType, KeyGenerator keyGenerator,
			String keyProperty, String keyColumn ) {
		String statementName = mapperClass.getName() + "." + id;
		if ( configuration.hasStatement(statementName) ) {
			logger.warn("{} Has been loaded by XML or SqlProvider, ignoring the injection of the SQL.", statementName);
			return null;
		}
		return assistant.addMappedStatement(id, sqlSource, StatementType.PREPARED, sqlCommandType, null, null, null,
			parameterClass, null, resultType, null, false, true, false, keyGenerator, keyProperty, keyColumn,
			configuration.getDatabaseId(), new XMLLanguageDriver(), null);
	}

}
