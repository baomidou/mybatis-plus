package com.baomidou.mybatisplus.extension.incrementer;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;

public class FirebirdKeyGenerator implements IKeyGenerator {

    @Override
    public String executeSql(String incrementerName) {
        return "SELECT next value for " + incrementerName + " from rdb$database";
    }

    @Override
    public DbType dbType() {
        return DbType.FIREBIRD;
    }
}
