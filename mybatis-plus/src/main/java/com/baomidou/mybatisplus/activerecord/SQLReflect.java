package com.baomidou.mybatisplus.activerecord;

import java.util.List;

import com.baomidou.mybatisplus.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.baomidou.mybatisplus.toolkit.TableFieldInfo;
import com.baomidou.mybatisplus.toolkit.TableInfo;

public class SQLReflect {

	private Record<?> object;
	private TableInfo table;

	public SQLReflect(Record<?> object) {
		this.object = object;
		this.table = object.table();
	}

	public String insert() {
		String sqlInsert = "INSERT INTO " + table.getTableName() + " ";
		sqlInsert += "(";
		List<TableFieldInfo> fields = table.getFieldList();
		String values = "VALUES (";
		for (int i = 0; i < fields.size(); i++) {
			if (i > 0) {
				sqlInsert += ", ";
				values += ", ";
			}
			TableFieldInfo f = fields.get(i);
			String property = f.getProperty();
			String column = f.getColumn();
			Object value = ReflectionKit.getMethodValue(object, property);
			sqlInsert += column;
			values += StringUtils.quotaMark(value);
		}
		sqlInsert += ") " + values;
		sqlInsert += ")";
		System.err.println(sqlInsert);
		return sqlInsert;
	}

	public String update() {
		String sqlUpdate = "UPDATE " + table.getTableName() + " ";
		sqlUpdate += "SET ";
        List<TableFieldInfo> fields = table.getFieldList();
		for (int i = 0; i < fields.size(); i++) {
			if (i > 0) {
				sqlUpdate += ", ";
			}
            TableFieldInfo f = fields.get(i);
            String property = f.getProperty();
            String column = f.getColumn();
            Object value = ReflectionKit.getMethodValue(object, property);
            sqlUpdate += column + "=" + StringUtils.quotaMark(value);
		}
		sqlUpdate += " WHERE " + table.getKeyColumn() + " = " + object.getPrimaryKey();
		System.err.println(sqlUpdate);
		return sqlUpdate;
	}

	public Object castValue(Object value) {
		if (value == null) {
			return null;
		}

		// System.out.println("castvalue: "+value+" class "+value.getClass());
		if (value.getClass() == Boolean.class) {
			if ((Boolean) value) {
				return 1;
			} else {
				return 0;
			}
		}
		return value;
	}
}
