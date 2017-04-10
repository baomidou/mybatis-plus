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

import java.util.List;
import java.util.Map;

import org.apache.ibatis.mapping.SqlSource;

import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.entity.TableFieldInfo;
import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.enums.SqlMethod;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * SQL 自动注入逻辑处理器<br>
 * 1、支持逻辑删除
 * </p>
 *
 * @author hubin
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
			String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlLogicSet(table), sqlWhereByMap(table));
			SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
			this.addUpdateMappedStatement(mapperClass, Map.class, sqlMethod.getMethod(), sqlSource);
		} else {
			// 正常删除
			super.injectDeleteByMapSql(mapperClass, table);
		}
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
				set.append(fieldInfo.getColumn()).append("=").append(fieldInfo.getLogicDeleteValue());
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
			int i=0;
			for (TableFieldInfo fieldInfo : fieldList) {
				if (fieldInfo.isLogicDelete()) {
					if (++i > 1) {
						where.append(" AND ");
					}
					where.append(fieldInfo.getColumn()).append("!=").append(fieldInfo.getLogicDeleteValue());
				}
			}
			// EW 逻辑
	        if (StringUtils.isNotEmpty(table.getKeyProperty())) {
	            where.append("\n<if test=\"ew.").append(table.getKeyProperty()).append("!=null\">");
	            where.append(" AND ").append(table.getKeyColumn()).append("=#{ew.").append(table.getKeyProperty()).append("}");
	            where.append("</if>");
	        }
	        for (TableFieldInfo fieldInfo : fieldList) {
	            where.append(convertIfTag(fieldInfo, "ew.", false));
	            where.append(" AND ").append(fieldInfo.getColumn()).append("=#{ew.").append(fieldInfo.getEl()).append("}");
	            where.append(convertIfTag(fieldInfo, true));
	        }
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
			// 过滤逻辑
			List<TableFieldInfo> fieldList = table.getFieldList();
			int i=0;
			for (TableFieldInfo fieldInfo : fieldList) {
				if (fieldInfo.isLogicDelete()) {
					if (++i > 1) {
						where.append(" AND ");
					}
					where.append(fieldInfo.getColumn()).append("!=").append(fieldInfo.getLogicDeleteValue());
				}
			}
			// EW 逻辑
	        where.append("\n<if test=\"ew!=null\">\n<if test=\"ew.entity!=null\">");
	        if (StringUtils.isNotEmpty(table.getKeyProperty())) {
	            where.append("\n<if test=\"ew.entity.").append(table.getKeyProperty()).append("!=null\">");
	            where.append(" AND ").append(table.getKeyColumn()).append("=#{ew.entity.").append(table.getKeyProperty()).append("}");
	            where.append("</if>");
	        }
 			for (TableFieldInfo fieldInfo : fieldList) {
	            where.append(convertIfTag(fieldInfo, "ew.entity.", false));
	            where.append(" AND ").append(fieldInfo.getColumn()).append("=#{ew.entity.").append(fieldInfo.getEl()).append("}");
	            where.append(convertIfTag(fieldInfo, true));
	        }
	        where.append("\n</if>\n<if test=\"ew.sqlSegment!=null\">${ew.sqlSegment}</if>");
	        where.append("\n</if>");
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
			// 过滤逻辑
			List<TableFieldInfo> fieldList = table.getFieldList();
			int i=0;
			for (TableFieldInfo fieldInfo : fieldList) {
				if (fieldInfo.isLogicDelete()) {
					if (++i > 1) {
						where.append("AND ");
					}
					where.append(fieldInfo.getColumn()).append("!=").append(fieldInfo.getLogicDeleteValue());
				}
			}
			// MAP 逻辑
			where.append("\n<if test=\"cm!=null and !cm.isEmpty\">");
			where.append("\n<foreach collection=\"cm.keys\" item=\"k\" separator=\"AND\">");
			where.append("\n<if test=\"cm[k] != null\">");
			String quote = GlobalConfiguration.getIdentifierQuote(configuration);
			if (StringUtils.isNotEmpty(quote)) {
				where.append(quote).append("${k}").append(quote).append("=#{cm[${k}]}");
			} else {
				where.append("\n${k} = #{cm[${k}]}");
			}
			where.append("</if>");
			where.append("\n</foreach>");
			where.append("\n</if>");
			where.append("\n</where>");
			return where.toString();
		}
		// 正常逻辑
		return super.sqlWhereByMap(table);
	}

}
