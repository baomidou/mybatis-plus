package com.baomidou.mybatisplus.generator.config.converts;

import com.baomidou.mybatisplus.annotation.DbType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong
 */
public class TypeConvertsTest {

    @Test
    void test() {
        assertThat(TypeConverts.getTypeConvert(DbType.GAUSS)).isInstanceOf(DmTypeConvert.class);
        assertThat(TypeConverts.getTypeConvert(DbType.GAUSS_DB)).isInstanceOf(GaussDBSqlTypeConvert.class);
    }

}
