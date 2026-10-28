package com.baomidou.mybatisplus.generator.config.converts;

import com.baomidou.mybatisplus.annotation.DbType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong
 */
@Tag("unit")
public class TypeConvertsTest {

    @Test
    void shouldGetTypeConvertForGaussDbTypes() {
        assertThat(TypeConverts.getTypeConvert(DbType.GAUSS)).isInstanceOf(DmTypeConvert.class);
        assertThat(TypeConverts.getTypeConvert(DbType.GAUSS_DB)).isInstanceOf(GaussDBSqlTypeConvert.class);
    }

}
