/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension.handlers;

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
