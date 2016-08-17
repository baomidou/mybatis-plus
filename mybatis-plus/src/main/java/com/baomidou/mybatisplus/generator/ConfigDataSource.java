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
package com.baomidou.mybatisplus.generator;

/**
 * <p>
 * 数据源配置
 * </p>
 * 
 * @author hubin
 * @Date 2016-04-25
 */
public enum ConfigDataSource {
	MYSQL("mysql", "show tables", "show table status", "show full fields from %s", "NAME", "COMMENT" ,"FIELD","TYPE","COMMENT","KEY"), 
	ORACLE("oracle", "SELECT * FROM USER_TABLES", "SELECT * FROM USER_TAB_COMMENTS",
			"SELECT A.COLUMN_NAME, A.DATA_TYPE, B.COMMENTS  FROM USER_TAB_COLUMNS A, USER_COL_COMMENTS B WHERE A.TABLE_NAME=B.TABLE_NAME AND A.COLUMN_NAME = B.COLUMN_NAME AND A.TABLE_NAME='%s'",
			"TABLE_NAME", "COMMENTS" ,"COLUMN_NAME","DATA_TYPE","COMMENTS","COLUMN_NAME");

	private final String db;
	private final String tablesSql;
	private final String tableCommentsSql;
	private final String tableFieldsSql;
	private final String tableName;
	private final String tableComment;
	private final String fieldName;
	private final String fieldType;
	private final String fieldComment;
	private final String fieldKey;

	ConfigDataSource(final String db, final String tablesSql, final String tableCommentsSql,
			final String tableFieldsSql, final String tableName, final String tableComment, final String fieldName,
			final String fieldType, final String fieldComment, final String fieldKey) {
		this.db = db;
		this.tablesSql = tablesSql;
		this.tableCommentsSql = tableCommentsSql;
		this.tableFieldsSql = tableFieldsSql;
		this.tableName = tableName;
		this.tableComment = tableComment;
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.fieldComment = fieldComment;
		this.fieldKey = fieldKey;
	}

	/**
	 * <p>
	 * 获取数据库类型（默认 MySql）
	 * </p>
	 * 
	 * @param dbType
	 *            数据库类型字符串
	 * @return
	 */
	public static ConfigDataSource getConfigDataSource(String dbType) {
		for (ConfigDataSource dt : ConfigDataSource.values()) {
			if (dt.getDb().equals(dbType)) {
				return dt;
			}
		}
		return MYSQL;
	}

	public String getDb() {
		return db;
	}

	public String getTablesSql() {
		return tablesSql;
	}

	public String getTableCommentsSql() {
		return tableCommentsSql;
	}

	public String getTableFieldsSql() {
		return tableFieldsSql;
	}

	public String getTableName() {
		return tableName;
	}

	public String getTableComment() {
		return tableComment;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public String getFieldComment() {
		return fieldComment;
	}

	public String getFieldKey() {
		return fieldKey;
	}

}
