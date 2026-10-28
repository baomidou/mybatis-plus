/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 用外部已知向量钉住 {@link EncryptUtils#md5Base64(String)} 的算法与 UTF-8 编码。
 *
 * @author TaoYu
 */
@Tag("unit")
class EncryptUtilsTest {

    @Test
    void md5Base64MatchesKnownVectors() {
        assertThat(EncryptUtils.md5Base64("")).isEqualTo("1B2M2Y8AsgTpgAmY7PhCfg==");
        assertThat(EncryptUtils.md5Base64("abc")).isEqualTo("kAFQmDzST7DWlj99KOF/cg==");
        assertThat(EncryptUtils.md5Base64("中文")).isEqualTo("p7rCI5/NyzoGeQPYB3xKBw==");
    }

    @Test
    void md5Base64IsStableAndInputSensitive() {
        assertThat(EncryptUtils.md5Base64("select * from t")).isEqualTo(EncryptUtils.md5Base64("select * from t"));
        assertThat(EncryptUtils.md5Base64("select * from t")).isNotEqualTo(EncryptUtils.md5Base64("select * from t "));
    }

    @Test
    void md5Base64WrapsNullInput() {
        assertThatThrownBy(() -> EncryptUtils.md5Base64(null)).isInstanceOf(MybatisPlusException.class);
    }
}
