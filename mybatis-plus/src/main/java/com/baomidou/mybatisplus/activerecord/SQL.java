package com.baomidou.mybatisplus.activerecord;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.toolkit.CollectionUtil;
import com.baomidou.mybatisplus.toolkit.StringUtils;

public abstract class SQL<T> extends Record<T> {

	@Override
	protected boolean insert() {
		return sqlMapper().insert(delegateSQLReflect().insert());
	}

	@Override
	protected boolean update() {
		return sqlMapper().update(delegateSQLReflect().update());
	}

	@Override
	public List<Map<String, Object>> where(String whereClause, Object... args) {
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT * FROM ").append(table().getTableName());
		if (null != whereClause) {
			for (int i = 0; i < args.length; i++) {
				whereClause = whereClause.replaceFirst("\\?", StringUtils.quotaMark(args[i]));
			}
			selectSql.append(" WHERE ").append(whereClause);
		}
		System.out.println(selectSql.toString());
		List<Map<String, Object>> selectList = sqlMapper().selectList(selectSql.toString());
		if (CollectionUtil.isEmpty(selectList))
			return null;
		return selectList;
	}

	protected SQLReflect delegateSQLReflect() {
		return new SQLReflect(this);
	}

}
