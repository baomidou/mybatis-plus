package com.baomidou.mybatisplus.extension.incrementer;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;

/**
 * DM Sequence
 *
 * @author cdtjj
 * @since 2022-04-22
 */
public class DmKeyGenerator implements IKeyGenerator {

	@Override
	public String executeSql(String incrementerName) {
		return "SELECT " + incrementerName + ".NEXTVAL FROM DUAL";
	}

	@Override
	public DbType dbType() {
		return DbType.DM;
	}
}
