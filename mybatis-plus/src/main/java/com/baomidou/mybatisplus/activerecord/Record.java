package com.baomidou.mybatisplus.activerecord;

import java.util.Map;
import java.util.Set;

/**
 * 行记录对象。
 * 
 * @since 1.0
 * @author redraiment
 */
public final class Record {
	private final Table table;
	private final Map<String, Object> values;

	Record(Table table, Map<String, Object> values) {
		this.table = table;
		this.values = values;
	}

	public Set<String> columnNames() {
		return values.keySet();
	}

	@SuppressWarnings("unchecked")
	public <E> E get(String name) {
		name = DB.parseKeyParameter(name);
		Object value = null;

		if (values.containsKey(name)) {
			value = values.get(name);
		} else if (table.relations.containsKey(name)) {
			Association relation = table.relations.get(name);
			Table active = table.dbo.active(relation.target);
			active.join(relation.assoc(table.name, getInt("id")));
			if (relation.isAncestor() && !relation.isCross()) {
				active.constrain(relation.key, getInt("id"));
			}
			value = (relation.isOnlyOneResult() ? active.first() : active);
		}

		String key = "get_".concat(name);
		if (table.hooks.containsKey(key)) {
			value = table.hooks.get(key).call(this, value);
		}
		return (E) value;
	}

	/* For primitive types */
	public boolean getBool(String name) {
		return get(name);
	}

	public byte getByte(String name) {
		return get(name);
	}

	public char getChar(String name) {
		return get(name);
	}

	public short getShort(String name) {
		return get(name);
	}

	public int getInt(String name) {
		return get(name);
	}

	public long getLong(String name) {
		return get(name);
	}

	public float getFloat(String name) {
		return get(name);
	}

	public double getDouble(String name) {
		return get(name);
	}

	/* For any other types */

	public String getStr(String name) {
		return get(name);
	}

	public <E> E get(String name, Class<E> type) {
		return type.cast(get(name));
	}

	public Record set(String name, Object value) {
		name = DB.parseKeyParameter(name);
		String key = "set_".concat(name);
		if (table.hooks.containsKey(key)) {
			value = table.hooks.get(key).call(this, value);
		}
		values.put(name, value);
		return this;
	}

	public Record save() {
		table.update(this);
		return this;
	}

	public Record update(Object... args) {
		for (int i = 0; i < args.length; i += 2) {
			set(args[i].toString(), args[i + 1]);
		}
		return save();
	}

	public void destroy() {
		table.delete(this);
	}

	@Override
	public String toString() {
		StringBuilder line = new StringBuilder();
		for (Map.Entry<String, Object> e : values.entrySet()) {
			line.append(String.format("%s = %s\n", e.getKey(), e.getValue()));
		}
		return line.toString();
	}
}
