package com.baomidou.mybatisplus.test.handlers;

import com.baomidou.mybatisplus.extension.handlers.MybatisMapWrapper;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2019-03-21
 */
class MybatisMapWrapperTest {

    private final MybatisMapWrapper mapWrapper = new MybatisMapWrapper(null, Collections.emptyMap());

    @Test
    void findProperty() {
        assertThat(mapWrapper.findProperty("xxx", true)).isEqualTo("xxx");
        assertThat(mapWrapper.findProperty("xxx_sss", true)).isEqualTo("xxxSss");
        assertThat(mapWrapper.findProperty("xxx_sss_eee", true)).isEqualTo("xxxSssEee");
        assertThat(mapWrapper.findProperty("XXX_SSS_EEE", true)).isEqualTo("xxxSssEee");
        assertThat(mapWrapper.findProperty("xxxSss", true)).isEqualTo("xxxSss");
        /* 注意一下情况不支持 */

        // 1.不包含下划线,并且首字母大写,会被全部转成小写
        assertThat(mapWrapper.findProperty("SxxSss", true)).isEqualTo("sxxsss");
    }
}
