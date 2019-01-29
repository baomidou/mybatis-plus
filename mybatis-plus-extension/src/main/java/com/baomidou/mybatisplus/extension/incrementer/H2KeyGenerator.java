package com.baomidou.mybatisplus.extension.incrementer;

import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;

/**
 * H2 Sequence
 *
 * @author Caratacus
 * @since 2017-06-12
 */
public class H2KeyGenerator implements IKeyGenerator {

    @Override
    public String executeSql(String incrementerName) {
        return "select " + incrementerName + ".nextval";
    }
}
