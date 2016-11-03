package com.baomidou.mybatisplus.activerecord;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.toolkit.TableInfo;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public abstract class Record<T extends Record> {
	/**
	 * 表信息
	 */
	@TableField(exist = false)
	public TableInfo tableInfo;
	/**
	 * 表名
	 */
	@TableField(exist = false)
	public String tableName;
	/**
	 * 表主键ID 属性名
	 */
	@TableField(exist = false)
	public String primaryKey;
	/**
	 * 表主键ID值
	 */
	@TableField(exist = false)
	public Serializable pkVal;
	/**
	 * 表主键ID 属性名
	 */
	@TableField(exist = false)
	public String keyProperty;

	/**
	 * init
	 */
	{
		tableInfo = TableInfoHelper.getTableInfo(getClass());
		tableName = tableInfo.getTableName();
		primaryKey = tableInfo.getKeyColumn();
		keyProperty = tableInfo.getKeyProperty();
		pkVal = (Serializable) ReflectionKit.getMethodValue(this, keyProperty);
	}

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
		return where(primaryKey + " = ?", id).get(0);
	}

	public boolean exists(Serializable id) {
		return where(primaryKey + " = ?", id).size() > 0;
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
		return null == pkVal;
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
			// TODO
			if (f.getName().matches(".*_id$"))
				associationsFields.add(f);
		}
		return associationsFields;
	}

	@Override
	public String toString() {
		String obj = "{ " + primaryKey + ": " + pkVal;
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
			if (isValidType(t) && !f.getName().equals(primaryKey)) {
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
