package com.baomidou.mybatisplus.activerecord.sql;

import java.util.Arrays;

import com.baomidou.mybatisplus.activerecord.Seq;

/**
 * TSQL构造器。
 * 
 * @since 1.0
 * @author redraiment
 */
public class TSqlBuilder extends AbstractSqlBuilder {
	public SqlBuilder insert() {
		return start(Mode.Insert);
	}

	public SqlBuilder into(String table) {
		return setTables(table);
	}

	public SqlBuilder values(String... columns) {
		return setFields(columns);
	}

	public SqlBuilder update(String table) {
		start(Mode.Update);
		return setTables(table);
	}

	public SqlBuilder set(String... columns) {
		return setFields(columns);
	}

	public SqlBuilder select(String... columns) {
		start(Mode.Select);
		if (columns != null && columns.length > 0) {
			return setFields(columns);
		} else {
			return setFields("*");
		}
	}

	public SqlBuilder delete() {
		return start(Mode.Delete);
	}

	public SqlBuilder from(String table) {
		return setTables(table);
	}

	public SqlBuilder join(String table) {
		return addTable(table);
	}

	public SqlBuilder on(String... conditions) {
		String table = tables.removeLast();
		return addTable(table.concat(" on ").concat(Seq.join(Arrays.asList(conditions), "and")));
	}

	public SqlBuilder where(String... conditions) {
		return setConditions(conditions);
	}

	public SqlBuilder groupBy(String... columns) {
		return setGroups(columns);
	}

	public SqlBuilder having(String... conditions) {
		return setHaving(conditions);
	}

	public SqlBuilder orderBy(String... columns) {
		return setOrders(columns);
	}

	public SqlBuilder limit(int limit) {
		return setLimit(limit);
	}

	public SqlBuilder offset(int offset) {
		return setOffset(offset);
	}
}
