package com.baomidou.mybatisplus.activerecord;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.activerecord.ex.IllegalFieldNameException;
import com.baomidou.mybatisplus.activerecord.ex.SqlExecuteException;
import com.baomidou.mybatisplus.activerecord.sql.SqlBuilder;
import com.baomidou.mybatisplus.activerecord.sql.TSqlBuilder;

/**
 * 表对象。
 * 
 * @since 1.0
 * @author redraiment
 */
public final class Table {
	final DB dbo;
	final String name;
	final Map<String, Integer> columns;
	final Map<String, Association> relations;
	final Map<String, Lambda> hooks;
	final String primaryKey;

	private String foreignTable;
	private final Map<String, Integer> foreignKeys = new HashMap<String, Integer>();

	Table(DB dbo, String name, Map<String, Integer> columns, Map<String, Association> relations,
			Map<String, Lambda> hooks) {
		this.dbo = dbo;
		this.name = name;
		this.columns = columns;
		this.relations = relations;
		this.hooks = hooks;
		this.primaryKey = name.concat(".id");
	}

	/**
	 * 继承给定的JavaBean，扩展Record对象的get和set方法。
	 * 
	 * @param bean
	 *            希望被继承的JavaBean
	 * @return 返回Table自身
	 * @since 2.3
	 */
	public Table extend(Object bean) {
		Class<?> type = bean.getClass();
		for (Method method : type.getDeclaredMethods()) {
			Class<?> returnType = method.getReturnType();
			Class<?>[] params = method.getParameterTypes();
			String key = method.getName();

			if (params.length == 2 && key.length() > 3 && (key.startsWith("get") || key.startsWith("set"))
					&& params[0].isAssignableFrom(Record.class) && params[1].isAssignableFrom(Object.class)
					&& Object.class.isAssignableFrom(returnType)) {
				key = key.replaceAll("(?=[A-Z])", "_").toLowerCase();
				hooks.put(key, new Lambda(bean, method));
			}
		}

		return this;
	}

	public Map<String, Integer> getColumns() {
		return Collections.unmodifiableMap(columns);
	}

	/* Association */
	private Association assoc(String name, boolean onlyOne, boolean ancestor) {
		name = DB.parseKeyParameter(name);
		Association assoc = new Association(relations, name, onlyOne, ancestor);
		relations.put(name, assoc);
		return assoc;
	}

	public Association belongsTo(String name) {
		return assoc(name, true, false);
	}

	public Association hasOne(String name) {
		return assoc(name, true, true);
	}

	public Association hasMany(String name) {
		return assoc(name, false, true);
	}

	public Association hasAndBelongsToMany(String name) {
		return assoc(name, false, false);
	}

	private String[] getForeignKeys() {
		List<String> conditions = new ArrayList<String>();
		for (Map.Entry<String, Integer> e : foreignKeys.entrySet()) {
			conditions.add(String.format("%s.%s = %d", name, e.getKey(), e.getValue()));
		}
		return conditions.toArray(new String[0]);
	}

	public Table constrain(String key, int id) {
		foreignKeys.put(DB.parseKeyParameter(key), id);
		return this;
	}

	public Table join(String table) {
		this.foreignTable = table;
		return this;
	}

	/* CRUD */
	public Record create(Object... args) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.putAll(foreignKeys);
		for (int i = 0; i < args.length; i += 2) {
			String key = DB.parseKeyParameter(args[i].toString());
			if (!columns.containsKey(key)) {
				throw new IllegalFieldNameException(key);
			}
			Object value = args[i + 1];
			data.put(key, value);
		}

		String[] fields = new String[data.size() + 2];
		int[] types = new int[data.size() + 2];
		Object[] values = new Object[data.size() + 2];
		int index = 0;
		for (Map.Entry<String, Object> e : data.entrySet()) {
			fields[index] = e.getKey();
			types[index] = columns.get(e.getKey());
			values[index] = e.getValue();
			index++;
		}
		Seq.assignAt(fields, Seq.array(-2, -1), "created_at", "updated_at");
		Seq.assignAt(types, Seq.array(-2, -1), Types.TIMESTAMP, Types.TIMESTAMP);
		Seq.assignAt(values, Seq.array(-2, -1), DB.now(), DB.now());

		SqlBuilder sql = new TSqlBuilder();
		sql.insert().into(name).values(fields);
		PreparedStatement call = dbo.prepare(sql.toString(), values, types);
		try {
			int id = 0;
			if (call.executeUpdate() > 0) {
				ResultSet rs = call.getGeneratedKeys();
				if (rs != null && rs.next()) {
					id = rs.getInt(1);
					rs.close();
				}
			}
			return id > 0 ? find(id) : null;
		} catch (SQLException e) {
			throw new SqlExecuteException(sql.toString(), e);
		} finally {
			dbo.close(call);
		}
	}

	/**
	 * 根据现有的Record创建新的Record. 为跨数据库之间导数据提供便捷接口；同时也方便根据模板创建多条相似的纪录。
	 * 
	 * @param o
	 *            Record对象
	 * @return 根据参数创建的新的Record对象
	 */
	public Record create(Record o) {
		List<Object> params = new LinkedList<Object>();
		for (String key : columns.keySet()) {
			if (!foreignKeys.containsKey(key)) {
				params.add(key);
				params.add(o.get(key));
			}
		}
		return create(params.toArray());
	}

	public void update(Record record) {
		String[] fields = new String[columns.size() + 1];
		int[] types = new int[columns.size() + 1];
		Object[] values = new Object[columns.size() + 1];
		int index = 0;
		for (String column : columns.keySet()) {
			fields[index] = column;
			types[index] = columns.get(column);
			values[index] = record.get(column);
			index++;
		}

		fields[columns.size()] = "updated_at";
		types[columns.size()] = Types.TIMESTAMP;
		values[columns.size()] = DB.now();

		SqlBuilder sql = new TSqlBuilder();
		sql.update(name).set(fields).where(String.format("%s = %d", primaryKey, record.getInt("id")));
		dbo.execute(sql.toString(), values, types);
	}

	public void delete(Record record) {
		int id = record.get("id");
		SqlBuilder sql = new TSqlBuilder();
		sql.delete().from(name).where(String.format("%s = %d", primaryKey, id));
		dbo.execute(sql.toString());
	}

	public void purge() {
		// TODO: need enhancement
		for (Record record : all()) {
			delete(record);
		}
	}

	List<Record> query(SqlBuilder sql, Object... args) {
		List<Record> records = new LinkedList<Record>();
		ResultSet rs = dbo.query(sql.toString(), args);
		try {
			ResultSetMetaData meta = rs.getMetaData();
			while (rs.next()) {
				Map<String, Object> values = new LinkedHashMap<String, Object>();
				for (int i = 1; i <= meta.getColumnCount(); i++) {
					String label = DB.parseKeyParameter(meta.getColumnLabel(i));
					values.put(label, rs.getObject(label));
				}
				records.add(new Record(this, values));
			}
		} catch (SQLException e) {
			throw new SqlExecuteException(sql.toString(), e);
		} finally {
			dbo.close(rs);
		}
		return records;
	}

	public Query select(String... columns) {
		Query sql = new Query(this);
		if (columns == null || columns.length == 0) {
			sql.select(String.format("%s.*", name));
		} else {
			sql.select(columns);
		}
		sql.from(name);
		if (foreignTable != null && !foreignTable.isEmpty()) {
			sql.join(foreignTable);
		}
		if (!foreignKeys.isEmpty()) {
			for (String condition : getForeignKeys()) {
				sql.where(condition);
			}
		}
		return sql.orderBy(primaryKey);
	}

	public Record first() {
		return select().limit(1).one();
	}

	public Record first(String condition, Object... args) {
		return select().where(condition).limit(1).one(args);
	}

	public Record last() {
		return select().orderBy(primaryKey.concat(" desc")).limit(1).one();
	}

	public Record last(String condition, Object... args) {
		return select().where(condition).orderBy(primaryKey.concat(" desc")).limit(1).one(args);
	}

	public Record find(int id) {
		return first(primaryKey.concat(" = ?"), id);
	}

	/**
	 * 根据指定列，返回符合条件的第一条记录.
	 * 
	 * @param key
	 *            要匹配的列名
	 * @param value
	 *            要匹配的值
	 * @return 返回符合条件的第一条记录
	 */
	public Record findA(String key, Object value) {
		key = DB.parseKeyParameter(key);
		if (value != null) {
			return first(key.concat(" = ?"), value);
		} else {
			return first(key.concat(" is null"));
		}
	}

	public List<Record> findBy(String key, Object value) {
		key = DB.parseKeyParameter(key);
		if (value != null) {
			return where(key.concat(" = ?"), value);
		} else {
			return where(key.concat(" is null"));
		}
	}

	public List<Record> all() {
		return select().all();
	}

	public List<Record> where(String condition, Object... args) {
		return select().where(condition).all(args);
	}

	public List<Record> paging(int page, int size) {
		return select().limit(size).offset(page * size).all();
	}
}
