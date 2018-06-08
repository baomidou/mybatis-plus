package com.baomidou.mybatisplus.extension.incrementer;

import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;

/**
 * <p>
 * Postgre Sequence
 * </p>
 *
 * @author Caratacus
 * @since 2017-06-12
 */
public class PostgreKeyGenerator implements IKeyGenerator {

    @Override
    public String executeSql(String incrementerName) {
        StringBuilder sql = new StringBuilder();
        sql.append("select nextval('");
        sql.append(incrementerName);
        sql.append("')");
        return sql.toString();
    }
}
