package com.baomidou.mybatisplus.activerecord;

import com.baomidou.mybatisplus.toolkit.SystemClock;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public abstract class Record<T extends Record> {
	public int id;
	public long createdAt;
	public long updatedAt;
	public boolean active = true;

	public List<Map<String, Object>> all() {
		return where(null);
	}

	public Map<String, Object> find(int id) {
		return where("id = ?", id).get(0);
	}

	public boolean exists(int id) {
		return where("id = ?", id).size() > 0;
	}

	public boolean save() {
		updatedAt = SystemClock.now();
		if (isNew()) {
			createdAt = SystemClock.now();
			return insert();
		}
		return update();
	}

	public void eagerLoad() {
		List<Field> assoc = getAssociations();
		for (int i = 0; i < assoc.size(); i++) {
			try {
				load(assoc.get(0).getName().replaceAll("_id$", ""));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isNew() {
		return id == 0;
	}

	protected abstract Class<T> classType();

	protected abstract boolean insert();

	protected abstract boolean update();

	public abstract List<Map<String, Object>> where(String whereClause, Object... args);

	public List<Field> getAssociations() {
		List<Field> associationsFields = new ArrayList<Field>();
		Field[] fields = classType().getFields();
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			if (f.getName().matches(".*_id$"))
				associationsFields.add(f);
		}
		return associationsFields;
	}

	@Override
	public String toString() {
		String obj = "{ id: " + id;
		List<Field> fields = getFieldsWithoutId();
		for (int i = 0; i < fields.size(); i++) {
			obj += ", ";
			Field f = fields.get(i);
			try {
				obj += f.getName() + ": " + f.get(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		obj += " }";
		return obj;
	}

	public List<Field> getFieldsWithoutId() {
		List<Field> fieldsWithoutId = new ArrayList<Field>();
		Field[] fields = classType().getFields();
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			Class<?> t = f.getType();
			if (isValidType(t) && !f.getName().equals("id")) {
				fieldsWithoutId.add(f);
			}
		}
		return fieldsWithoutId;
	}

	public boolean isValidType(Class<?> t) {
		return (t == String.class || t == boolean.class || t == int.class || t == float.class || t == double.class || t == long.class);
	}

	private void load(String name) {
		try {
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
