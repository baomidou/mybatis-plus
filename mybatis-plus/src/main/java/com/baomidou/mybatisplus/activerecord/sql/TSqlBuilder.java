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
	@Override
	public SqlBuilder insert() {
		return start(Mode.Insert);
	}

	@Override
	public SqlBuilder into(String table) {
		return setTables(table);
	}

	@Override
	public SqlBuilder values(String... columns) {
		return setFields(columns);
	}

	@Override
	public SqlBuilder update(String table) {
		start(Mode.Update);
		return setTables(table);
	}

	@Override
	public SqlBuilder set(String... columns) {
		return setFields(columns);
	}

	@Override
	public SqlBuilder select(String... columns) {
		start(Mode.Select);
		if (columns != null && columns.length > 0) {
			return setFields(columns);
		} else {
			return setFields("*");
		}
	}

	@Override
	public SqlBuilder delete() {
		return start(Mode.Delete);
	}

	@Override
	public SqlBuilder from(String table) {
		return setTables(table);
	}

	@Override
	public SqlBuilder join(String table) {
		return addTable(table);
	}

	@Override
	public SqlBuilder on(String... conditions) {
		String table = tables.removeLast();
		return addTable(table.concat(" on ").concat(Seq.join(Arrays.asList(conditions), "and")));
	}

	@Override
	public SqlBuilder where(String... conditions) {
		return setConditions(conditions);
	}

	@Override
	public SqlBuilder groupBy(String... columns) {
		return setGroups(columns);
	}

	@Override
	public SqlBuilder having(String... conditions) {
		return setHaving(conditions);
	}

	@Override
	public SqlBuilder orderBy(String... columns) {
		return setOrders(columns);
	}

	@Override
	public SqlBuilder limit(int limit) {
		return setLimit(limit);
	}

	@Override
	public SqlBuilder offset(int offset) {
		return setOffset(offset);
	}
}
