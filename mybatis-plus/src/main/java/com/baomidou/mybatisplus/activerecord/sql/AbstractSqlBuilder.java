package com.baomidou.mybatisplus.activerecord.sql;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

import com.baomidou.mybatisplus.activerecord.Seq;

/**
 * 实现生成insert、update、delete和select的语句的方法。 由子类自行实现如何填充数据。
 * 
 * @since 1.0
 * @author redraiment
 */
public abstract class AbstractSqlBuilder implements SqlBuilder {
	protected static enum Mode {
		Select, Insert, Update, Delete
	}

	protected Mode mode;
	protected Deque<String> fields;
	protected Deque<String> tables;
	protected Deque<String> conditions;
	protected Deque<String> groups;
	protected Deque<String> having;
	protected Deque<String> orders;
	protected int limit;
	protected int offset;

	protected SqlBuilder start(Mode mode) {
		this.mode = mode;
		fields = new LinkedList<String>();
		tables = new LinkedList<String>();
		conditions = new LinkedList<String>();
		groups = new LinkedList<String>();
		having = new LinkedList<String>();
		orders = new LinkedList<String>();
		limit = offset = -1;
		return this;
	}

	public SqlBuilder addField(String field) {
		fields.addLast(field);
		return this;
	}

	public SqlBuilder addTable(String table) {
		tables.addLast(table);
		return this;
	}

	public SqlBuilder addCondition(String condition) {
		conditions.addLast(condition);
		return this;
	}

	public SqlBuilder addGroup(String group) {
		groups.addLast(group);
		return this;
	}

	public SqlBuilder addHaving(String having) {
		this.having.addLast(having);
		return this;
	}

	public SqlBuilder addOrder(String order) {
		orders.addLast(order);
		return this;
	}

	public SqlBuilder setFields(String... fields) {
		this.fields.clear();
		this.fields.addAll(Arrays.asList(fields));
		return this;
	}

	public SqlBuilder setTables(String... tables) {
		this.tables.clear();
		this.tables.addAll(Arrays.asList(tables));
		return this;
	}

	public SqlBuilder setConditions(String... conditions) {
		this.conditions.clear();
		this.conditions.addAll(Arrays.asList(conditions));
		return this;
	}

	public SqlBuilder setGroups(String... groups) {
		this.groups.clear();
		this.groups.addAll(Arrays.asList(groups));
		return this;
	}

	public SqlBuilder setHaving(String... having) {
		this.having.clear();
		this.having.addAll(Arrays.asList(having));
		return this;
	}

	public SqlBuilder setOrders(String... orders) {
		this.orders.clear();
		this.orders.addAll(Arrays.asList(orders));
		return this;
	}

	public SqlBuilder setLimit(int limit) {
		this.limit = limit;
		return this;
	}

	public SqlBuilder setOffset(int offset) {
		this.offset = offset;
		return this;
	}

	// toString

	private String selectToString() {
		StringBuilder sql = new StringBuilder("select ");
		sql.append(Seq.join(fields, ", ")).append(" from ").append(Seq.join(tables, " join "));
		if (!conditions.isEmpty()) {
			sql.append(" where ").append(Seq.join(conditions, " and "));
		}
		if (!groups.isEmpty()) {
			sql.append(" group by ").append(Seq.join(groups, ", "));
			if (!having.isEmpty()) {
				sql.append(" having ").append(Seq.join(having, " and "));
			}
		}
		if (!orders.isEmpty()) {
			sql.append(" order by ").append(Seq.join(orders, ", "));
		}
		if (limit > 0) {
			sql.append(" limit ").append(Integer.toString(limit));
		}
		if (offset > -1) {
			sql.append(" offset ").append(Integer.toString(offset));
		}

		return sql.toString();
	}

	private String insertToString() {
		StringBuilder sql = new StringBuilder("insert into ");
		sql.append(tables.getFirst()).append(" (").append(Seq.join(fields, ", ")).append(") values (")
				.append(Seq.join(Seq.map(fields, "?"), ", ")).append(")");
		return sql.toString();
	}

	private String updateToString() {
		StringBuilder sql = new StringBuilder("update ");
		sql.append(tables.getFirst()).append(" set ").append(Seq.join(Seq.map(fields, "%s = ?"), ", "));

		if (!conditions.isEmpty()) {
			sql.append(" where ").append(Seq.join(conditions, " and "));
		}
		return sql.toString();
	}

	private String deleteToString() {
		StringBuilder sql = new StringBuilder("delete from ");
		sql.append(tables.getFirst());
		if (!conditions.isEmpty()) {
			sql.append(" where ").append(Seq.join(conditions, " and "));
		}
		return sql.toString();
	}

	@Override
	public String toString() {
		switch (mode) {
		case Select:
			return selectToString();
		case Insert:
			return insertToString();
		case Update:
			return updateToString();
		case Delete:
			return deleteToString();
		default:
			return "";
		}
	}
}
