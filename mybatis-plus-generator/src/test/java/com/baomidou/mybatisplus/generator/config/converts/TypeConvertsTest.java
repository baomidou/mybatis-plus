package com.baomidou.mybatisplus.generator.config.converts;

import com.baomidou.mybatisplus.annotation.DbType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author nieqiurong
 */
public class TypeConvertsTest {

    @Test
    void test() {
        Assertions.assertInstanceOf(DmTypeConvert.class, TypeConverts.getTypeConvert(DbType.GAUSS));
        Assertions.assertInstanceOf(GaussDBSqlTypeConvert.class, TypeConverts.getTypeConvert(DbType.GAUSS_DB));
    }

}
