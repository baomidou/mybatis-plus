package com.baomidou.mybatisplus.activerecord;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.toolkit.TableInfo;

@SuppressWarnings("rawtypes")
public abstract class Record<T> {

	/**
	 * 查询所有
	 * 
	 * @return
	 */
	public List<Map<String, Object>> all() {
		return where(null);
	}

	/**
	 * 根据id查找
	 * 
	 * @param id
	 * @return
	 */
	public Map<String, Object> find(Serializable id) {
		return where(table().getKeyColumn() + " = ?", id).get(0);
	}

	public boolean exists(Serializable id) {
		return where(table().getKeyColumn() + " = ?", id).size() > 0;
	}

	public boolean save() {
		if (isNew()) {
			return insert();
		}
		return update();
	}

	public void eagerLoad() {
		List<Field> assoc = getAssociations();
		for (int i = 0; i < assoc.size(); i++) {
			try {
				// TODO
				load(assoc.get(0).getName().replaceAll("_id$", ""));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isNew() {
		return null == table().getKeyColumn();
	}

	protected abstract Class<T> classType();

	protected abstract Serializable getPrimaryKey();

	protected abstract TableInfo table();

	protected abstract boolean insert();

	protected abstract boolean update();

	public abstract List<Map<String, Object>> where(String whereClause, Object... args);

	public List<Field> getAssociations() {
		List<Field> associationsFields = new ArrayList<Field>();
		Field[] fields = classType().getFields();
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			// TODO
			if (f.getName().matches(".*_id$"))
				associationsFields.add(f);
		}
		return associationsFields;
	}

	@Override
	public String toString() {
		return table().toString();
	}

	public boolean isValidType(Class<?> t) {
		return (t == String.class || t == boolean.class || t == int.class || t == float.class || t == double.class
				|| t == long.class);
	}

	private void load(String name) {
		try {
			// TODO
			Field idField = this.classType().getField(name + "_id");
			Field field = this.classType().getDeclaredField(name);
			if (idField.getInt(this) == 0) {
				return;
			}
			Object obj = ((Record) field.getType().newInstance()).find(idField.getInt(this));
			field.setAccessible(true);
			field.set(this, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
