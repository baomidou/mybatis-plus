package com.baomidou.mybatisplus.activerecord;

import java.io.Serializable;

import com.baomidou.mybatisplus.mapper.SqlMapper;
import com.baomidou.mybatisplus.toolkit.TableInfo;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;

@SuppressWarnings({ "serial", "rawtypes" })
public abstract class Model<T extends Model> extends SQL<T> implements Serializable {

	@Override
	protected TableInfo table() {
		return TableInfoHelper.getTableInfo(this.classType());
	}

	@Override
	protected SqlMapper delegateRawSQL() {
		return mapper(this.classType());
	}

	public static SqlMapper mapper(Class<?> clazz) {
		return TableInfoHelper.getSqlMapper(clazz);
	}

}
