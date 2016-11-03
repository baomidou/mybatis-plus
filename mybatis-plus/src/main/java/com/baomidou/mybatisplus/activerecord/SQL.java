package com.baomidou.mybatisplus.activerecord;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.mapper.SqlMapper;
import com.baomidou.mybatisplus.toolkit.CollectionUtil;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public abstract class SQL<T extends SQL> extends Record<T> {
	@TableField(exist = false)
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
		selectSql.append("SELECT * FROM ").append(tableName);
		if (null != whereClause) {
			for (int i = 0; i < args.length; i++) {
				whereClause = whereClause.replaceFirst("\\?", StringUtils.quotaMark(args[i]));
			}
			selectSql.append(" WHERE ").append(whereClause);
		}
		System.out.println(selectSql.toString());
		List<Map<String, Object>> selectList = delegateRawSQL().selectList(selectSql.toString());
		if (CollectionUtil.isEmpty(selectList))
			return null;
		return selectList;
	}

	protected abstract SqlMapper delegateRawSQL();

}
