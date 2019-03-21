package com.baomidou.mybatisplus.extension.handlers;

import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * @author miemie
 * @since 2019-03-21
 */
public class MybatisMapWrapperTest {

    private final MybatisMapWrapper mapWrapper = new MybatisMapWrapper(null, Collections.emptyMap());

    @Test
    public void findPropertyOfTrue() {
        boolean useCamelCaseMapping = true;
        assertThat(mapWrapper.findProperty("xxx", useCamelCaseMapping)).
    }
}
