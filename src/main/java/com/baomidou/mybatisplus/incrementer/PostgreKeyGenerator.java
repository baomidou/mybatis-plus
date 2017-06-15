package com.baomidou.mybatisplus.incrementer;

/**
 * <p>
 * Postgre Sequence
 * </p>
 *
 * @author Caratacus
 * @Date 2017-06-12
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
