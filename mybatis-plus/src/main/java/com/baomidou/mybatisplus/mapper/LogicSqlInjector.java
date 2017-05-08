/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.mapper;

import com.baomidou.mybatisplus.entity.TableFieldInfo;
import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.enums.SqlMethod;
import com.baomidou.mybatisplus.toolkit.SqlReservedWords;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * SQL 自动注入逻辑处理器<br>
 * 1、支持逻辑删除
 * </p>
 *
 * @author hubin willenfoo
 * @Date 2017-09-09
 */
public class LogicSqlInjector extends AutoSqlInjector {

	/**
	 * 根据 ID 删除
	 */
	@Override
	protected void injectDeleteByIdSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		if (table.isLogicDelete()) {
			// 逻辑删除注入
			SqlMethod sqlMethod = SqlMethod.LOGIC_DELETE_BY_ID;
			SqlSource sqlSource;
			String idStr = table.getKeyColumn();
			if (batch) {
				sqlMethod = SqlMethod.LOGIC_DELETE_BATCH_BY_IDS;
				StringBuilder ids = new StringBuilder();
				ids.append("\n<foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\">");
				ids.append("#{item}");
				ids.append("\n</foreach>");
				idStr = ids.toString();
			}
			String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlLogicSet(table),
					table.getKeyColumn(), idStr);
			sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
			this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
		} else {
			// 正常删除
			super.injectDeleteByIdSql(batch, mapperClass, modelClass, table);
		}
	}

	/**
	 * 根据 SQL 删除
	 */
	@Override
	protected void injectDeleteSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		if (table.isLogicDelete()) {
			// 逻辑删除注入
			SqlMethod sqlMethod = SqlMethod.LOGIC_DELETE;
			String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlLogicSet(table),
					sqlWhereEntityWrapper(table));
			SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
			this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
		} else {
			// 正常删除
			super.injectDeleteSql(mapperClass, modelClass, table);
		}
	}

	/**
	 * 根据 MAP 删除
	 */
	@Override
	protected void injectDeleteByMapSql(Class<?> mapperClass, TableInfo table) {
		if (table.isLogicDelete()) {
			// 逻辑删除注入
			SqlMethod sqlMethod = SqlMethod.LOGIC_DELETE_BY_MAP;
			String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlLogicSet(table),
					sqlWhereByMap(table));
			SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
			this.addUpdateMappedStatement(mapperClass, Map.class, sqlMethod.getMethod(), sqlSource);
		} else {
			// 正常删除
			super.injectDeleteByMapSql(mapperClass, table);
		}
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
	protected void injectSelectByIdSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		if (table.isLogicDelete()) {
			SqlMethod sqlMethod = SqlMethod.LOGIC_SELECT_BY_ID;
			SqlSource sqlSource;
			if (batch) {
				sqlMethod = SqlMethod.LOGIC_SELECT_BATCH_BY_IDS;
				StringBuilder ids = new StringBuilder();
				ids.append("\n<foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\">");
				ids.append("#{item}");
				ids.append("\n</foreach>");
				sqlSource = languageDriver.createSqlSource(configuration, String.format(sqlMethod.getSql(), sqlSelectColumns(table, false),
								table.getTableName(), table.getKeyColumn(), ids.toString(), getLogicDeleteSql(table)), modelClass);
			} else {
				sqlSource = new RawSqlSource(configuration, String.format(sqlMethod.getSql(), sqlSelectColumns(table, false), table.getTableName(),
								table.getKeyColumn(), table.getKeyProperty(), getLogicDeleteSql(table)), Object.class);
			}
			this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, table);
		} else {
			// 正常查询
			super.injectSelectByIdSql(batch, mapperClass, modelClass, table);
		}
	}

	/**
	 * <p>
	 * 注入更新 SQL 语句
	 * </p>
	 *
	 * @param mapperClass
	 * @param modelClass
	 * @param table
	 */
	protected void injectUpdateByIdSql(boolean selective, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		if (table.isLogicDelete()) {
			SqlMethod sqlMethod = selective ? SqlMethod.LOGIC_UPDATE_BY_ID : SqlMethod.LOGIC_UPDATE_ALL_COLUMN_BY_ID;
			String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlSet(selective, table, null),
					table.getKeyColumn(), table.getKeyProperty(), getLogicDeleteSql(table));
			SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
			this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
		} else {
			super.injectUpdateByIdSql(selective, mapperClass, modelClass, table);
		}
	}

	/**
	 * <p>
	 * SQL 更新 set 语句
	 * </p>
	 *
	 * @param table
	 *            表信息
	 * @return sql and 片段
	 */
	public String getLogicDeleteSql(TableInfo table) {
		StringBuilder sql = new StringBuilder();
		List<TableFieldInfo> fieldList = table.getFieldList();
		for (TableFieldInfo fieldInfo : fieldList) {
			if (fieldInfo.isLogicDelete()) {
				sql.append(" AND ").append(fieldInfo.getColumn());
				if ("java.lang.String".equals(fieldInfo.getPropertyType())) {
					sql.append("='").append(fieldInfo.getLogicNotDeleteValue()).append("'");
				} else {
					sql.append("=").append(fieldInfo.getLogicNotDeleteValue());
				}
			}
		}
		return sql.toString();
	}

	/**
	 * <p>
	 * SQL 更新 set 语句
	 * </p>
	 *
	 * @param table
	 *            表信息
	 * @return sql set 片段
	 */
	protected String sqlLogicSet(TableInfo table) {
		List<TableFieldInfo> fieldList = table.getFieldList();
		StringBuilder set = new StringBuilder("SET ");
		int i = 0;
		for (TableFieldInfo fieldInfo : fieldList) {
			if (fieldInfo.isLogicDelete()) {
				if (++i > 1) {
					set.append(",");
				}
				set.append(fieldInfo.getColumn()).append("=");
				if ("java.lang.String".equals(fieldInfo.getPropertyType())) {
					set.append("'").append(fieldInfo.getLogicDeleteValue()).append("'");
				} else {
					set.append(fieldInfo.getLogicDeleteValue());
				}
			}
		}
		return set.toString();
	}

	// ------------ 处理逻辑删除条件过滤 ------------

	@Override
	protected String sqlWhere(TableInfo table) {
		if (table.isLogicDelete()) {
			StringBuilder where = new StringBuilder("\n<where>");
			// 过滤逻辑
			List<TableFieldInfo> fieldList = table.getFieldList();
			// EW 逻辑
			if (StringUtils.isNotEmpty(table.getKeyProperty())) {
				where.append("\n<if test=\"ew.").append(table.getKeyProperty()).append("!=null\">");
				where.append(" AND ").append(table.getKeyColumn()).append("=#{ew.");
				where.append(table.getKeyProperty()).append("}");
				where.append("</if>");
			}
			for (TableFieldInfo fieldInfo : fieldList) {
				where.append(convertIfTag(fieldInfo, "ew.", false));
				where.append(" AND ").append(fieldInfo.getColumn()).append("=#{ew.");
				where.append(fieldInfo.getEl()).append("}");
				where.append(convertIfTag(fieldInfo, true));
			}
			// 过滤逻辑
			where.append("\n").append(getLogicDeleteSql(table));
			where.append("\n</where>");
			return where.toString();
		}
		// 正常逻辑
		return super.sqlWhere(table);
	}

	@Override
	protected String sqlWhereEntityWrapper(TableInfo table) {
		if (table.isLogicDelete()) {
			StringBuilder where = new StringBuilder("\n<where>");
			// EW 逻辑
			where.append("\n<if test=\"ew!=null\">\n<if test=\"ew.entity!=null\">");
			if (StringUtils.isNotEmpty(table.getKeyProperty())) {
				where.append("\n<if test=\"ew.entity.").append(table.getKeyProperty()).append("!=null\">");
				where.append(" AND ").append(table.getKeyColumn()).append("=#{ew.entity.");
				where.append(table.getKeyProperty()).append("}");
				where.append("</if>");
			}
			List<TableFieldInfo> fieldList = table.getFieldList();
			for (TableFieldInfo fieldInfo : fieldList) {
				where.append(convertIfTag(fieldInfo, "ew.entity.", false));
				where.append(" AND ").append(fieldInfo.getColumn()).append("=#{ew.entity.");
				where.append(fieldInfo.getEl()).append("}");
				where.append(convertIfTag(fieldInfo, true));
			}
			where.append("\n</if>");
			where.append("\n</if>");

			// 过滤逻辑, 这段代码放在这里的原因，第一：不把 逻辑的过滤 放在where条件 第一位， 能够方便利用索引
			where.append("\n").append(getLogicDeleteSql(table));
			where.append("\n<if test=\"ew!=null\">\n<if test=\"ew.sqlSegment!=null\">${ew.sqlSegment}</if>\n</if>");
			where.append("\n</where>");
			return where.toString();
		}
		// 正常逻辑
		return super.sqlWhereEntityWrapper(table);
	}

	@Override
	protected String sqlWhereByMap(TableInfo table) {
		if (table.isLogicDelete()) {
			StringBuilder where = new StringBuilder();
			where.append("\n<where>");
			// MAP 逻辑
			where.append("\n<if test=\"cm!=null and !cm.isEmpty\">");
			where.append("\n<foreach collection=\"cm.keys\" item=\"k\" separator=\"AND\">");
			where.append("\n<if test=\"cm[k] != null\">");
			where.append(SqlReservedWords.convert(getGlobalConfig(), "\n${k}")).append(" = #{cm[${k}]}");
			where.append("</if>");
			where.append("\n</foreach>");
			where.append("\n</if>");
			// 过滤逻辑
			where.append("\n").append(getLogicDeleteSql(table));
			where.append("\n</where>");
			return where.toString();
		}
		// 正常逻辑
		return super.sqlWhereByMap(table);
	}

}
