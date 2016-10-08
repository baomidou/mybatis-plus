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
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.session.Configuration;

import com.baomidou.mybatisplus.MybatisConfiguration;
import com.baomidou.mybatisplus.annotations.FieldStrategy;
import com.baomidou.mybatisplus.annotations.IdType;
import com.baomidou.mybatisplus.toolkit.TableFieldInfo;
import com.baomidou.mybatisplus.toolkit.TableInfo;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;

/**
 * <p>
 * SQL 自动注入器
 * </p>
 * 
 * @author hubin sjy
 * @Date 2016-09-09
 */
public class AutoSqlInjector implements ISqlInjector {
	protected static final Logger logger = Logger.getLogger("AutoSqlInjector");

	protected Configuration configuration;

	protected LanguageDriver languageDriver;

	protected MapperBuilderAssistant builderAssistant;

	protected DBType dbType = DBType.MYSQL;

	/**
	 * CRUD注入后给予标识 注入过后不再注入
	 *
	 * @param configuration
	 * @param builderAssistant
	 * @param mapperClass
	 */
	public void inspectInject(Configuration configuration, MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
		String className = mapperClass.toString();
		Set<String> mapperRegistryCache = MybatisConfiguration.MAPPER_REGISTRY_CACHE;
		if (!mapperRegistryCache.contains(className)) {
			inject(configuration, builderAssistant, mapperClass);
			mapperRegistryCache.add(className);
		}
	}

	/**
	 * 注入单点 crudSql
	 */
	public void inject(Configuration configuration, MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
		this.configuration = configuration;
		this.builderAssistant = builderAssistant;
		this.languageDriver = configuration.getDefaultScriptingLanuageInstance();
		this.dbType = MybatisConfiguration.DB_TYPE;
		if (configuration.isMapUnderscoreToCamelCase()) {
			/* 开启驼峰配置 */
			MybatisConfiguration.DB_COLUMN_UNDERLINE = true;
		}
		Class<?> modelClass = extractModelClass(mapperClass);
		TableInfo table = TableInfoHelper.initTableInfo(modelClass);

		/**
		 * 没有指定主键，默认方法不能使用
		 */
		if (null != table && null != table.getKeyProperty()) {
			/* 插入 */
			this.injectInsertOneSql(false, mapperClass, modelClass, table);
			this.injectInsertOneSql(true, mapperClass, modelClass, table);
			this.injectInsertBatchSql(mapperClass, modelClass, table);

			/* 删除 */
			this.injectDeleteSelectiveSql(mapperClass, modelClass, table);
			this.injectDeleteByMapSql(mapperClass, table);
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
			this.injectSelectByMapSql(mapperClass, modelClass, table);
			this.injectSelectOneSql(mapperClass, modelClass, table);
			this.injectSelectCountSql(mapperClass, modelClass, table);
			this.injectSelectCountByEWSql(SqlMethod.SELECT_COUNT_EW, mapperClass, modelClass, table);
			this.injectSelectListSql(SqlMethod.SELECT_LIST, mapperClass, modelClass, table);
			this.injectSelectListSql(SqlMethod.SELECT_PAGE, mapperClass, modelClass, table);

			/* 自定义方法 */
			this.inject(configuration, builderAssistant, mapperClass, modelClass, table);
		} else {
			/**
			 * 警告
			 */
			logger.warning(String.format("%s ,Not found @TableId annotation, cannot use mybatis-plus curd method.",
					modelClass.toString()));
		}
	}

	/**
	 * 自定义方法，注入点（子类需重写该方法）
	 */
	public void inject(Configuration configuration, MapperBuilderAssistant builderAssistant, Class<?> mapperClass,
			Class<?> modelClass, TableInfo table) {
		// to do nothing
	}

	protected Class<?> extractModelClass(Class<?> mapperClass) {
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
	 *            是否选择插入
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	protected void injectInsertOneSql(boolean selective, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
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
				fieldBuilder.append(convertIfTagInsert(fieldInfo, false));
				placeholderBuilder.append(convertIfTagInsert(fieldInfo, false));
			}
			fieldBuilder.append(fieldInfo.getColumn()).append(",");
			placeholderBuilder.append("#{").append(fieldInfo.getEl()).append("},");
			if (selective) {
				fieldBuilder.append(convertIfTagInsert(fieldInfo, true));
				placeholderBuilder.append(convertIfTagInsert(fieldInfo, true));
			}
		}
		fieldBuilder.append("\n</trim>");
		placeholderBuilder.append("\n</trim>");
		String sql = String.format(sqlMethod.getSql(), table.getTableName(), fieldBuilder.toString(),
				placeholderBuilder.toString());
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
		this.addInsertMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource, keyGenerator, keyProperty,
				keyColumn);
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
	protected void injectInsertBatchSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
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
			placeholderBuilder.append("#{item.").append(fieldInfo.getEl()).append("},");
		}
		fieldBuilder.append("\n</trim>");
		placeholderBuilder.append("\n</trim>");
		String sql = String.format(sqlMethod.getSql(), table.getTableName(), fieldBuilder.toString(),
				placeholderBuilder.toString());
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
		this.addInsertMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource, keyGenerator, keyProperty,
				keyColumn);
	}

	/**
	 * <p>
	 * 注入 entity 条件删除 SQL 语句
	 * </p>
	 * 
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	protected void injectDeleteSelectiveSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		SqlMethod sqlMethod = SqlMethod.DELETE_SELECTIVE;
		String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlWhere(table, false));
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
		this.addDeleteMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource);
	}

	/**
	 * <p>
	 * 注入 map 条件删除 SQL 语句
	 * </p>
	 * 
	 * @param mapperClass
	 * @param table
	 */
	protected void injectDeleteByMapSql(Class<?> mapperClass, TableInfo table) {
		SqlMethod sqlMethod = SqlMethod.DELETE_BY_MAP;
		String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlWhereByMap());
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
		this.addDeleteMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource);
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
	protected void injectDeleteSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
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
		this.addDeleteMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource);
	}

	/**
	 * <p>
	 * 注入更新 SQL 语句
	 * </p>
	 * 
	 * @param selective
	 *            是否选择更新
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	protected void injectUpdateByIdSql(boolean selective, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		SqlMethod sqlMethod = SqlMethod.UPDATE_BY_ID;
		if (selective) {
			sqlMethod = SqlMethod.UPDATE_SELECTIVE_BY_ID;
		}
		String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlSet(selective, table), table.getKeyColumn(),
				table.getKeyProperty());
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
	protected void injectUpdateBatchById(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		StringBuilder set = new StringBuilder();
		set.append("<trim prefix=\"SET\" suffixOverrides=\",\">\n");
		SqlMethod sqlMethod = SqlMethod.UPDATE_BATCH_BY_ID_MYSQL;
		if (DBType.ORACLE == dbType) {
			sqlMethod = SqlMethod.UPDATE_BATCH_BY_ID_ORACLE;
			List<TableFieldInfo> fieldList = table.getFieldList();
			for (TableFieldInfo fieldInfo : fieldList) {
				set.append(fieldInfo.getColumn()).append("=#{item.").append(fieldInfo.getEl()).append("},");
			}
		} else if (DBType.MYSQL == dbType) {
			List<TableFieldInfo> fieldList = table.getFieldList();
			for (TableFieldInfo fieldInfo : fieldList) {
				set.append("\n<trim prefix=\"").append(fieldInfo.getColumn()).append("=CASE ");
				set.append(table.getKeyColumn()).append("\" suffix=\"END,\">");
				set.append("\n<foreach collection=\"list\" item=\"i\" index=\"index\">");
				set.append(convertIfTag(fieldInfo, "i.", false));
				set.append("\nWHEN ").append("#{i.").append(table.getKeyProperty());
				set.append("} THEN #{i.").append(fieldInfo.getEl()).append("}");
				set.append(convertIfTag(fieldInfo, true));
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
	 *            是否选择更新
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	protected void injectUpdateSql(boolean selective, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		SqlMethod sqlMethod = SqlMethod.UPDATE;
		if (selective) {
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
	protected void injectSelectSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		SqlMethod sqlMethod = SqlMethod.SELECT_BY_ID;
		SqlSource sqlSource = null;
		if (batch) {
			sqlMethod = SqlMethod.SELECT_BATCH;
			StringBuilder ids = new StringBuilder();
			ids.append("\n<foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\">");
			ids.append("#{item}");
			ids.append("\n</foreach>");
			sqlSource = languageDriver.createSqlSource(configuration, String.format(sqlMethod.getSql(),
					sqlSelectColumns(table, false), table.getTableName(), table.getKeyColumn(), ids.toString()), modelClass);
		} else {
			sqlSource = new RawSqlSource(configuration, String.format(sqlMethod.getSql(), sqlSelectColumns(table, false),
					table.getTableName(), table.getKeyColumn(), table.getKeyProperty()), Object.class);
		}
		this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, table);
	}

	/**
	 * <p>
	 * 注入 map 查询 SQL 语句
	 * </p>
	 * 
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	protected void injectSelectByMapSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		SqlMethod sqlMethod = SqlMethod.SELECT_BY_MAP;
		String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table, false), table.getTableName(), sqlWhereByMap());
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
		this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, table);
	}

	/**
	 * <p>
	 * 注入实体查询一条记录 SQL 语句
	 * </p>
	 * 
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	protected void injectSelectOneSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		SqlMethod sqlMethod = SqlMethod.SELECT_ONE;
		String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table, false), table.getTableName(),
				sqlWhere(table, false));
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
		this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, table);
	}

	/**
	 * <p>
	 * 注入实体查询总记录数 SQL 语句
	 * </p>
	 * 
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	protected void injectSelectCountSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		SqlMethod sqlMethod = SqlMethod.SELECT_COUNT;
		String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlWhere(table, true));
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
		this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, Integer.class, null);
	}

	/**
	 * <p>
	 * 注入EntityWrapper方式查询记录列表 SQL 语句
	 * </p>
	 *
	 * @param sqlMethod
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	protected void injectSelectListSql(SqlMethod sqlMethod, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table, true), table.getTableName(), sqlWhereEntityWrapper(table));
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
		this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, table);
	}
	/**
	 * <p>
	 * 注入EntityWrapper查询总记录数 SQL 语句
	 * </p>
	 *
	 * @param sqlMethod
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	protected void injectSelectCountByEWSql(SqlMethod sqlMethod, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlWhereEntityWrapper(table));
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
		this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, Integer.class, null);
	}

	/**
	 * <p>
	 * EntityWrapper方式获取select where
	 * </p>
	 *
	 * @param table
	 * @return String
	 */
	protected String sqlWhereEntityWrapper(TableInfo table) {
		StringBuilder where = new StringBuilder("\n<if test=\"ew!=null\">");
		where.append("\n<if test=\"ew.entity!=null\">\n<where>");
		where.append("\n<if test=\"ew.entity.").append(table.getKeyProperty()).append("!=null\">\n");
		where.append(table.getKeyColumn()).append("=#{ew.entity.").append(table.getKeyProperty()).append("}");
		where.append("\n</if>");
		List<TableFieldInfo> fieldList = table.getFieldList();
		for (TableFieldInfo fieldInfo : fieldList) {
			where.append(convertIfTag(fieldInfo, "ew.entity.", false));
			where.append(" AND ").append(fieldInfo.getColumn()).append("=#{ew.entity.").append(fieldInfo.getEl()).append("}");
			where.append(convertIfTag(fieldInfo, true));
		}
		where.append("\n</where>\n</if>");
		where.append("\n<if test=\"ew.sqlSegment!=null\">\n${ew.sqlSegment}\n</if>");
		where.append("\n</if>");
		return where.toString();
	}

	/**
	 * <p>
	 * SQL 更新 set 语句
	 * </p>
	 * 
	 * @param selective
	 *            是否选择更新
	 * @param table
	 * @return
	 */
	protected String sqlSet(boolean selective, TableInfo table) {
		StringBuilder set = new StringBuilder();
		set.append("<trim prefix=\"SET\" suffixOverrides=\",\">");
		List<TableFieldInfo> fieldList = table.getFieldList();
		for (TableFieldInfo fieldInfo : fieldList) {
			if (selective) {
				set.append(convertIfTag(fieldInfo, "et.", false));
			}
			set.append(fieldInfo.getColumn()).append("=#{et.").append(fieldInfo.getEl()).append("},");
			if (selective) {
				set.append(convertIfTag(fieldInfo, true));
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
	 * @param entityWrapper
	 *            是否为包装类型查询
	 * @return
	 */
	protected String sqlSelectColumns(TableInfo table, boolean entityWrapper) {
		StringBuilder columns = new StringBuilder();
		if (null != table.getResultMap()) {
			/*
			 * 存在 resultMap 映射返回
			 */
			if (entityWrapper) {
				columns.append("<choose><when test=\"ew != null and ew.sqlSelect != null\">${ew.sqlSelect}</when><otherwise>");
			}
			columns.append("*");
			if (entityWrapper) {
				columns.append("</otherwise></choose>");
			}
		} else {
			/*
			 * 普通查询
			 */
			if (entityWrapper) {
				columns.append("<choose><when test=\"ew != null and ew.sqlSelect != null\">${ew.sqlSelect}</when><otherwise>");
			}
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
			if (entityWrapper) {
				columns.append("</otherwise></choose>");
			}
		}

		/*
		 * 返回所有查询字段内容
		 */
		return columns.toString();
	}

	/**
	 * <p>
	 * SQL 查询条件
	 * </p>
	 * 
	 * @param table
	 * @param space
	 *            是否为空判断
	 * @return
	 */
	protected String sqlWhere(TableInfo table, boolean space) {
		StringBuilder where = new StringBuilder();
		if (space) {
			where.append("\n<if test=\"ew!=null\">");
		}
		where.append("\n<where>");
		where.append("\n<if test=\"ew.").append(table.getKeyProperty()).append("!=null\">\n");
		where.append(table.getKeyColumn()).append("=#{ew.").append(table.getKeyProperty()).append("}");
		where.append("\n</if>");
		List<TableFieldInfo> fieldList = table.getFieldList();
		for (TableFieldInfo fieldInfo : fieldList) {
			where.append(convertIfTag(fieldInfo, "ew.", false));
			where.append(" AND ").append(fieldInfo.getColumn()).append("=#{ew.").append(fieldInfo.getEl()).append("}");
			where.append(convertIfTag(fieldInfo, true));
		}
		where.append("\n</where>");
		if (space) {
			where.append("\n</if>");
		}
		return where.toString();
	}

	/**
	 * <p>
	 * SQL map 查询条件
	 * </p>
	 */
	protected String sqlWhereByMap() {
		StringBuilder where = new StringBuilder();
		where.append("\n<foreach collection=\"cm.keys\" item=\"k\" separator=\"AND\"> ");
		where.append("\n${k}=#{cm[${k}]}");
		where.append("\n</foreach>");
		return where.toString();
	}

	/**
	 * <p>
	 * IF 条件转换方法
	 * </p>
	 * 
	 * @param sqlCommandType
	 *            SQL 操作类型
	 * @param fieldInfo
	 *            字段信息
	 * @param prefix
	 *            条件前缀
	 * @param colse
	 *            是否闭合标签
	 * @return
	 */
	protected String convertIfTag(SqlCommandType sqlCommandType, TableFieldInfo fieldInfo, String prefix,
			boolean colse) {
		/* 前缀处理 */
		String property = fieldInfo.getProperty();
		if (null != prefix) {
			property = prefix + property;
		}

		/* 判断策略 */
		if (sqlCommandType == SqlCommandType.INSERT && fieldInfo.getFieldStrategy() == FieldStrategy.FILL) {
			return "";
		}
		if (fieldInfo.getFieldStrategy() == FieldStrategy.NOT_NULL) {
			if (colse) {
				return "</if>";
			} else {
				return String.format("\n\t<if test=\"%s!=null\">", property);
			}
		} else if (fieldInfo.getFieldStrategy() == FieldStrategy.NOT_EMPTY) {
			if (colse) {
				return "</if>";
			} else {
				return String.format("\n\t<if test=\"%s!=null and %s!=''\">", property, property);
			}
		}
		return "";
	}
	
	protected String convertIfTagInsert(TableFieldInfo fieldInfo, boolean colse) {
		return convertIfTag(SqlCommandType.INSERT, fieldInfo, null, colse);
	}

	protected String convertIfTag(TableFieldInfo fieldInfo, String prefix, boolean colse) {
		return convertIfTag(SqlCommandType.UNKNOWN, fieldInfo, prefix, colse);
	}

	protected String convertIfTag(TableFieldInfo fieldInfo, boolean colse) {
		return convertIfTag(fieldInfo, null, colse);
	}

	/*
	 * 查询
	 */
	public MappedStatement addSelectMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource, Class<?> resultType,
			TableInfo table) {
		if (null != table) {
			String resultMap = table.getResultMap();
			if (null != resultMap) {
				/* 返回 resultMap 映射结果集 */
				return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.SELECT, null, resultMap, null,
						new NoKeyGenerator(), null, null);
			}
		}

		/* 普通查询 */
		return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.SELECT, null, null, resultType,
				new NoKeyGenerator(), null, null);
	}

	/*
	 * 插入
	 */
	public MappedStatement addInsertMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id, SqlSource sqlSource,
			KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
		return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.INSERT, modelClass, null, Integer.class,
				keyGenerator, keyProperty, keyColumn);
	}

	/*
	 * 删除
	 */
	public MappedStatement addDeleteMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource) {
		return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.DELETE, null, null, Integer.class,
				new NoKeyGenerator(), null, null);
	}

	/*
	 * 更新
	 */
	public MappedStatement addUpdateMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id, SqlSource sqlSource) {
		return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.UPDATE, modelClass, null, Integer.class,
				new NoKeyGenerator(), null, null);
	}

	public MappedStatement addMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource,
			SqlCommandType sqlCommandType, Class<?> parameterClass, String resultMap, Class<?> resultType,
			KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
		String statementName = mapperClass.getName() + "." + id;
		if (configuration.hasStatement(statementName)) {
			System.err.println("{" + statementName
					+ "} Has been loaded by XML or SqlProvider, ignoring the injection of the SQL.");
			return null;
		}
		/* 缓存逻辑处理 */
		boolean isSelect = false;
		if (sqlCommandType == SqlCommandType.SELECT) {
			isSelect = true;
		}
		return builderAssistant.addMappedStatement(id, sqlSource, StatementType.PREPARED, sqlCommandType, null, null, null,
				parameterClass, resultMap, resultType, null, !isSelect, isSelect, false, keyGenerator, keyProperty, keyColumn,
				configuration.getDatabaseId(), languageDriver, null);
	}

}
