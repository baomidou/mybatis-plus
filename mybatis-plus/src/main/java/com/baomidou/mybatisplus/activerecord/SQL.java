package com.baomidou.mybatisplus.activerecord;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.SqlMapper;

@SuppressWarnings("rawtypes")
public abstract class SQL<T extends SQL> extends Record<T> {

	private SQLReflect reflect = new SQLReflect(this);

	@Override
	protected boolean insert() {
		return delegateRawSQL().insert(reflect.insert());
	}

	@Override
	protected boolean update() {
		return delegateRawSQL().update(reflect.update());
	}

	@Override
	public List<Map<String, Object>> where(String whereClause, Object... args) {
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT * FROM ").append(this.classType().getSimpleName());
		if (null != whereClause) {
			for (int i = 0; i < args.length; i++) {
				whereClause = whereClause.replaceFirst("\\?", reflect.castValue(args[i]).toString());
			}
			selectSql.append(" WHERE ").append(whereClause);
		}
		System.out.println(selectSql.toString());
		return delegateRawSQL().selectList(selectSql.toString());
	}

	protected abstract SqlMapper delegateRawSQL();

}
