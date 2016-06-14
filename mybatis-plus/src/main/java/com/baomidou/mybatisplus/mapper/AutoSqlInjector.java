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
import org.apache.ibatis.session.Configuration;

import com.baomidou.mybatisplus.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.annotations.IdType;
import com.baomidou.mybatisplus.toolkit.TableFieldInfo;
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

	private static final MybatisXMLLanguageDriver languageDriver = new MybatisXMLLanguageDriver();

	private Configuration configuration;

	private MapperBuilderAssistant builderAssistant;
	
	private DBType dbType = DBType.MYSQL;
	
	protected AutoSqlInjector() {
		/* 保护 */
	}

	public AutoSqlInjector(Configuration configuration, DBType dbType) {
		this.configuration = configuration;
		this.dbType = dbType;
	}

	/**
	 * 注入单点 crudSql
	 */
	public void inject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
		this.builderAssistant = builderAssistant;
		Class<?> modelClass = extractModelClass(mapperClass);
		TableInfo table = TableInfoHelper.getTableInfo(modelClass);

		/**
		 * 没有指定主键，默认方法不能使用
		 */
		if (table.getKeyProperty() != null) {
			/* 插入 */
			this.injectInsertOneSql(false, mapperClass, modelClass, table);
			this.injectInsertOneSql(true, mapperClass, modelClass, table);
			this.injectInsertBatchSql(mapperClass, modelClass, table);

			/* 删除 */
			this.injectDeleteSelectiveSql(mapperClass, modelClass, table);
			this.injectDeleteSql(false, mapperClass, modelClass, table);
			this.injectDeleteSql(true, mapperClass, modelClass, table);

			/* 修改 */
			this.injectUpdateByIdSql(false, mapperClass, modelClass, table);
			this.injectUpdateByIdSql(true, mapperClass, modelClass, table);
			this.injectUpdateSql(false, mapperClass, modelClass, table);
			this.injectUpdateSql(true, mapperClass, modelClass, table);
			this.injectUpdateBatchById(mapperClass, modelClass, table);

			/* 查询 */
			this.injectSelectSql(false, mapperClass, modelClass, table);
			this.injectSelectSql(true, mapperClass, modelClass, table);
			this.injectSelectOneSql(mapperClass, modelClass, table);
			this.injectSelectCountSql(mapperClass, modelClass, table);
			this.injectSelectListSql(SqlMethod.SELECT_LIST, mapperClass, modelClass, table);
			this.injectSelectListSql(SqlMethod.SELECT_PAGE, mapperClass, modelClass, table);
		} else {
			/**
			 * 提示
			 */
			System.err.println(String.format("%s ,The unknown primary key, cannot use the generic method", modelClass.toString()));
		}
	}

	private Class<?> extractModelClass(Class<?> mapperClass) {
		Type[] types = mapperClass.getGenericInterfaces();
		ParameterizedType target = null;
		for (Type type : types) {
			if (type instanceof ParameterizedType && BaseMapper.class.isAssignableFrom(mapperClass)) {
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
	 * 
	 * @param selective
	 * 				是否选择插入
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	private void injectInsertOneSql(boolean selective, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		/*
		 * INSERT INTO table <trim prefix="(" suffix=")" suffixOverrides=",">
		 * <if test="xx != null">xx,</if> </trim> <trim prefix="values ("
		 * suffix=")" suffixOverrides=","> <if test="xx != null">#{xx},</if>
		 * </trim>
		 */
		KeyGenerator keyGenerator = new NoKeyGenerator();
		StringBuilder fieldBuilder = new StringBuilder();
		StringBuilder placeholderBuilder = new StringBuilder();
		SqlMethod sqlMethod = SqlMethod.INSERT_ONE;
		if (selective) {
			sqlMethod = SqlMethod.INSERT_ONE_SELECTIVE;
		}
		fieldBuilder.append("\n<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
		placeholderBuilder.append("\n<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
		String keyProperty = null;
		String keyColumn = null;
		if (table.getIdType() == IdType.AUTO) {
			/* 自增主键 */
			keyGenerator = new Jdbc3KeyGenerator();
			keyProperty = table.getKeyProperty();
			keyColumn = table.getKeyColumn();
		} else {
			/* 用户输入自定义ID */
			fieldBuilder.append(table.getKeyColumn()).append(",");
			placeholderBuilder.append("#{").append(table.getKeyProperty()).append("},");
		}
		List<TableFieldInfo> fieldList = table.getFieldList();
		for (TableFieldInfo fieldInfo : fieldList) {
			if (selective) {
				fieldBuilder.append("\n\t<if test=\"").append(fieldInfo.getProperty()).append("!=null\">");
				placeholderBuilder.append("\n\t<if test=\"").append(fieldInfo.getProperty()).append("!=null\">");
			}
			fieldBuilder.append(fieldInfo.getColumn()).append(",");
			placeholderBuilder.append("#{").append(fieldInfo.getProperty()).append("},");
			if (selective) {
				fieldBuilder.append("</if>");
				placeholderBuilder.append("</if>");
			}
		}
		fieldBuilder.append("\n</trim>");
		placeholderBuilder.append("\n</trim>");
		String sql = String.format(sqlMethod.getSql(), table.getTableName(), fieldBuilder.toString(), placeholderBuilder.toString());
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
		this.addInsertMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource, keyGenerator,
			keyProperty, keyColumn);
	}

	/**
	 * <p>
	 * 注入批量插入 SQL 语句
	 * </p>
	 * 
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	private void injectInsertBatchSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		KeyGenerator keyGenerator = new NoKeyGenerator();
		StringBuilder fieldBuilder = new StringBuilder();
		StringBuilder placeholderBuilder = new StringBuilder();
		SqlMethod sqlMethod = SqlMethod.INSERT_BATCH_MYSQL;
		if (DBType.ORACLE == dbType) {
			sqlMethod = SqlMethod.INSERT_BATCH_ORACLE;
			placeholderBuilder.append("\n<trim prefix=\"(SELECT \" suffix=\" FROM DUAL)\" suffixOverrides=\",\">\n");
		} else {
			placeholderBuilder.append("\n<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
		}
		fieldBuilder.append("\n<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
		String keyProperty = null;
		String keyColumn = null;
		if (table.getIdType() == IdType.AUTO) {
			/* 自增主键 */
			keyGenerator = new Jdbc3KeyGenerator();
			keyProperty = table.getKeyProperty();
			keyColumn = table.getKeyColumn();
		} else {
			/* 用户输入自定义ID */
			fieldBuilder.append(table.getKeyColumn()).append(",");
			placeholderBuilder.append("#{item.").append(table.getKeyProperty()).append("},");
		}
		List<TableFieldInfo> fieldList = table.getFieldList();
		for (TableFieldInfo fieldInfo : fieldList) {
			fieldBuilder.append(fieldInfo.getColumn()).append(",");
			placeholderBuilder.append("#{item.").append(fieldInfo.getProperty()).append("},");
		}
		fieldBuilder.append("\n</trim>");
		placeholderBuilder.append("\n</trim>");
		String sql = String.format(sqlMethod.getSql(), table.getTableName(), fieldBuilder.toString(), placeholderBuilder.toString());
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
		this.addInsertMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource, keyGenerator,
				keyProperty, keyColumn);
	}

	/**
	 * <p>
	 * 注入条件删除 SQL 语句
	 * </p>
	 * 
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	private void injectDeleteSelectiveSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		SqlMethod sqlMethod = SqlMethod.DELETE_SELECTIVE;
		String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlWhere(table, false));
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
		this.addMappedStatement(mapperClass, sqlMethod, sqlSource, SqlCommandType.DELETE, Integer.class);
	}

	/**
	 * <p>
	 * 注入删除 SQL 语句
	 * </p>
	 * 
	 * @param batch
	 *            是否为批量插入
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	private void injectDeleteSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		SqlMethod sqlMethod = SqlMethod.DELETE_BY_ID;
		SqlSource sqlSource = null;
		if (batch) {
			sqlMethod = SqlMethod.DELETE_BATCH;
			StringBuilder ids = new StringBuilder();
			ids.append("\n<foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\">");
			ids.append("#{item}");
			ids.append("\n</foreach>");
			String sql = String.format(sqlMethod.getSql(), table.getTableName(), table.getKeyColumn(), ids.toString());
			sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
		} else {
			String sql = String.format(sqlMethod.getSql(), table.getTableName(), table.getKeyColumn(), table.getKeyColumn());
			sqlSource = new RawSqlSource(configuration, sql, Object.class);
		}
		this.addMappedStatement(mapperClass, sqlMethod, sqlSource, SqlCommandType.DELETE, Integer.class);
	}

	/**
	 * <p>
	 * 注入更新 SQL 语句
	 * </p>
	 * 
	 * @param selective
	 * 				是否选择更新
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	private void injectUpdateByIdSql( boolean selective, Class<?> mapperClass, Class<?> modelClass, TableInfo table ) {
		SqlMethod sqlMethod = SqlMethod.UPDATE_BY_ID;
		if ( selective ) {
			sqlMethod = SqlMethod.UPDATE_SELECTIVE_BY_ID;
		}
		String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlSet(selective, table),
			table.getKeyColumn(), table.getKeyProperty());
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
		this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
	}
	
	/**
	 * <p>
	 * 注入批量更新 SQL 语句
	 * </p>
	 * 
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	private void injectUpdateBatchById(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		StringBuilder set = new StringBuilder();
		set.append("<trim prefix=\"SET\" suffixOverrides=\",\">\n");
		SqlMethod sqlMethod = SqlMethod.UPDATE_BATCH_BY_ID_MYSQL;
		if (DBType.ORACLE == dbType) {
			sqlMethod = SqlMethod.UPDATE_BATCH_BY_ID_ORACLE;
			List<TableFieldInfo> fieldList = table.getFieldList();
			for (TableFieldInfo fieldInfo : fieldList) {
				set.append(fieldInfo.getColumn()).append("=#{item.").append(fieldInfo.getProperty()).append("},");
			}
		} else if (DBType.MYSQL == dbType) {
			List<TableFieldInfo> fieldList = table.getFieldList();
			for (TableFieldInfo fieldInfo : fieldList) {
				set.append("\n<trim prefix=\"").append(fieldInfo.getColumn()).append("=CASE ");
				set.append(table.getKeyColumn()).append("\" suffix=\"END,\">");
				set.append("\n<foreach collection=\"list\" item=\"i\" index=\"index\">");
				set.append("\n<if test=\"i.").append(fieldInfo.getProperty()).append("!=null\">");
				set.append("\nWHEN ").append("#{i.").append(table.getKeyProperty());
				set.append("} THEN #{i.").append(fieldInfo.getProperty()).append("}");
				set.append("\n</if>");
				set.append("\n</foreach>");
				set.append("\n</trim>");			
			}
		}
		set.append("\n</trim>");
		String sql = String.format(sqlMethod.getSql(), table.getTableName(), set.toString(), table.getKeyColumn(),
				table.getKeyProperty());
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
		this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
	}
	
	/**
	 * <p>
	 * 注入批量更新 SQL 语句
	 * </p>
	 * 
	 * @param selective
	 * 				是否选择更新
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	private void injectUpdateSql( boolean selective, Class<?> mapperClass, Class<?> modelClass, TableInfo table ) {
		SqlMethod sqlMethod = SqlMethod.UPDATE;
		if ( selective ) {
			sqlMethod = SqlMethod.UPDATE_SELECTIVE;
		}
		String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlSet(selective, table), sqlWhere(table, true));
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
		this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
	}
	

	/**
	 * <p>
	 * 注入查询 SQL 语句
	 * </p>
	 * 
	 * @param batch
	 *            是否为批量插入
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	private void injectSelectSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		SqlMethod sqlMethod = SqlMethod.SELECT_BY_ID;
		SqlSource sqlSource = null;
		if (batch) {
			sqlMethod = SqlMethod.SELECT_BATCH;
			StringBuilder ids = new StringBuilder();
			ids.append("\n<foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\">");
			ids.append("#{item}");
			ids.append("\n</foreach>");
			sqlSource = languageDriver.createSqlSource(configuration, String.format(sqlMethod.getSql(),
					sqlSelectColumns(table), table.getTableName(), table.getKeyColumn(), ids.toString()), modelClass);
		} else {
			sqlSource = new RawSqlSource(configuration, String.format(sqlMethod.getSql(), sqlSelectColumns(table),
					table.getTableName(), table.getKeyColumn(), table.getKeyProperty()), Object.class);
		}
		this.addMappedStatement(mapperClass, sqlMethod, sqlSource, SqlCommandType.SELECT, modelClass);
	}

	/**
	 * <p>
	 * 注入实体查询一条记录 SQL 语句
	 * </p>
	 * 
	 * @param sqlMethod
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	private void injectSelectOneSql( Class<?> mapperClass, Class<?> modelClass, TableInfo table ) {
		SqlMethod sqlMethod = SqlMethod.SELECT_ONE;
		String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table), table.getTableName(), sqlWhere(table, false));
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
		this.addMappedStatement(mapperClass, sqlMethod, sqlSource, SqlCommandType.SELECT, modelClass);
	}
	
	/**
	 * <p>
	 * 注入实体查询总记录数 SQL 语句
	 * </p>
	 * 
	 * @param sqlMethod
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	private void injectSelectCountSql( Class<?> mapperClass, Class<?> modelClass, TableInfo table ) {
		SqlMethod sqlMethod = SqlMethod.SELECT_COUNT;
		String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlWhere(table, true));
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
		this.addMappedStatement(mapperClass, sqlMethod, sqlSource, SqlCommandType.SELECT, Integer.class);
	}
	
	/**
	 * <p>
	 * 注入实体查询记录列表 SQL 语句
	 * </p>
	 * 
	 * @param sqlMethod
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	private void injectSelectListSql(SqlMethod sqlMethod, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		StringBuilder where = new StringBuilder("\n<if test=\"ew!=null\">");
		where.append("\n<if test=\"ew.entity!=null\">\n<where>");
		where.append("\n<if test=\"ew.entity.").append(table.getKeyProperty()).append("!=null\">\n");
		where.append(table.getKeyColumn()).append("=#{ew.entity.").append(table.getKeyProperty()).append("}");
		where.append("\n</if>");
		List<TableFieldInfo> fieldList = table.getFieldList();
		for (TableFieldInfo fieldInfo : fieldList) {
			where.append("\n<if test=\"ew.entity.").append(fieldInfo.getProperty()).append("!=null\">\n");
			where.append(" AND ").append(fieldInfo.getColumn()).append("=#{ew.entity.").append(fieldInfo.getProperty()).append("}");
			where.append("\n</if>");
		}
		where.append("\n</where>\n</if>");
		where.append("\n<if test=\"ew.orderByField!=null\">\n${ew.orderByField}\n</if>");
		where.append("\n</if>");
		String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table), table.getTableName(), where.toString());
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
		this.addMappedStatement(mapperClass, sqlMethod, sqlSource, SqlCommandType.SELECT, modelClass);
	}

	/**
	 * <p>
	 * SQL 更新 set 语句
	 * </p>
	 * 
	 * @param selective
	 * 				是否选择更新
	 * @param table
	 * @return
	 */
	private String sqlSet( boolean selective, TableInfo table ) {
		StringBuilder set = new StringBuilder();
		set.append("<trim prefix=\"SET\" suffixOverrides=\",\">");
		List<TableFieldInfo> fieldList = table.getFieldList();
		for ( TableFieldInfo fieldInfo : fieldList ) {
			if ( selective ) {
				set.append("\n<if test=\"et.").append(fieldInfo.getProperty()).append("!=null\">\n");
			}
			set.append(fieldInfo.getColumn()).append("=#{et.").append(fieldInfo.getProperty()).append("},");
			if ( selective ) {
				set.append("\n</if>");
			}
		}
		set.append("\n</trim>");
		return set.toString();
	}
	
	/**
	 * <p>
	 * SQL 查询所有表字段
	 * </p>
	 * 
	 * @param table
	 * @return
	 */
	private String sqlSelectColumns(TableInfo table) {
		StringBuilder columns = new StringBuilder();
		if (table.isKeyRelated()) {
			columns.append(table.getKeyColumn()).append(" AS ").append(table.getKeyProperty());
		} else {
			columns.append(table.getKeyProperty());
		}
		List<TableFieldInfo> fieldList = table.getFieldList();
		for (TableFieldInfo fieldInfo : fieldList) {
			columns.append(",").append(fieldInfo.getColumn());
			if (fieldInfo.isRelated()) {
				columns.append(" AS ").append(fieldInfo.getProperty());
			}
		}
		return columns.toString();
	}

	/**
	 * <p>
	 * SQL 查询条件
	 * </p>
	 * 
	 * @param table
	 * @param space
	 * 				是否为空判断
	 * @return
	 */
	private String sqlWhere(TableInfo table, boolean space) {
		StringBuilder where = new StringBuilder();
		if ( space ) {
			where.append("\n<if test=\"ew!=null\">");
		}
		where.append("\n<where>");
		where.append("\n<if test=\"ew.").append(table.getKeyProperty()).append("!=null\">\n");
		where.append(table.getKeyColumn()).append("=#{ew.").append(table.getKeyProperty()).append("}");
		where.append("\n</if>");
		List<TableFieldInfo> fieldList = table.getFieldList();
		for (TableFieldInfo fieldInfo : fieldList) {
			where.append("\n<if test=\"ew.").append(fieldInfo.getProperty()).append("!=null\">\n");
			where.append(" AND ").append(fieldInfo.getColumn()).append("=#{ew.").append(fieldInfo.getProperty()).append("}");
			where.append("\n</if>");
		}
		where.append("\n</where>");
		if ( space ) {
			where.append("\n</if>");
		}
		return where.toString();
	}

	private MappedStatement addMappedStatement(Class<?> mapperClass, SqlMethod sm, SqlSource sqlSource,
			SqlCommandType sqlCommandType, Class<?> resultType) {
		return this.addMappedStatement(mapperClass, sm.getMethod(), sqlSource, sqlCommandType, null, resultType,
				new NoKeyGenerator(), null, null);
	}

	private MappedStatement addInsertMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id,
			SqlSource sqlSource, KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
		return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.INSERT, modelClass, Integer.class,
				keyGenerator, keyProperty, keyColumn);
	}

	private MappedStatement addUpdateMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id,
			SqlSource sqlSource) {
		return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.UPDATE, modelClass, Integer.class,
				new NoKeyGenerator(), null, null);
	}

	private MappedStatement addMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource,
			SqlCommandType sqlCommandType, Class<?> parameterClass, Class<?> resultType, KeyGenerator keyGenerator,
			String keyProperty, String keyColumn) {
		String statementName = mapperClass.getName() + "." + id;
		if (configuration.hasStatement(statementName)) {
			System.err.println("{" + statementName + "} Has been loaded by XML or SqlProvider, ignoring the injection of the SQL.");
			return null;
		}
		/* 缓存逻辑处理 */
		boolean isSelect = false;
		if (sqlCommandType == SqlCommandType.SELECT) {
			isSelect = true;
		}
		return builderAssistant.addMappedStatement(id, sqlSource, StatementType.PREPARED, sqlCommandType, null, null, null,
				parameterClass, null, resultType, null, !isSelect, isSelect, false, keyGenerator, keyProperty, keyColumn,
				configuration.getDatabaseId(), new MybatisXMLLanguageDriver(), null);
	}

}
