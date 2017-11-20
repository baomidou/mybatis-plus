package com.baomidou.mybatisplus.incrementer;

/**
 * <p>
 * H2 Sequence
 * </p>
 *
 * @author Caratacus
 * @Date 2017-06-12
 */
public class H2KeyGenerator implements IKeyGenerator {

    @Override
    public String executeSql(String incrementerName) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append(incrementerName);
        sql.append(".nextval");
        return sql.toString();
    }
}
