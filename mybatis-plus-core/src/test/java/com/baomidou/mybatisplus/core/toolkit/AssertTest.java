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

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 钉住 {@link Assert} 的失败类型、消息格式化与各类空值判定。
 *
 * @author TaoYu
 */
@Tag("unit")
class AssertTest {

    @Test
    void isTrueRejectsFalseAndFormatsTheMessage() {
        assertThatCode(() -> Assert.isTrue(true, "unreachable")).doesNotThrowAnyException();
        assertThatThrownBy(() -> Assert.isTrue(false, "value=%s, flag=%s", 1, true))
                .isInstanceOf(MybatisPlusException.class).hasMessage("value=1, flag=true");
    }

    @Test
    void isFalseMirrorsIsTrue() {
        assertThatCode(() -> Assert.isFalse(false, "unreachable")).doesNotThrowAnyException();
        assertThatThrownBy(() -> Assert.isFalse(true, "still true")).isInstanceOf(MybatisPlusException.class)
                .hasMessage("still true");
    }

    @Test
    void nullChecksFollowTheExpectedDirection() {
        assertThatCode(() -> {
            Assert.isNull(null, "must be null");
            Assert.notNull(new Object(), "must not be null");
        }).doesNotThrowAnyException();
        assertThatThrownBy(() -> Assert.isNull(new Object(), "was not null")).isInstanceOf(MybatisPlusException.class)
                .hasMessage("was not null");
        assertThatThrownBy(() -> Assert.notNull(null, "was null")).isInstanceOf(MybatisPlusException.class)
                .hasMessage("was null");
    }

    @Test
    void notEmptyStringRejectsNullEmptyAndBlank() {
        assertThatCode(() -> Assert.notEmpty("x", "must not be empty")).doesNotThrowAnyException();
        assertThatThrownBy(() -> Assert.notEmpty("", "blank")).isInstanceOf(MybatisPlusException.class)
                .hasMessage("blank");
        assertThatThrownBy(() -> Assert.notEmpty(" \t\n", "blank")).isInstanceOf(MybatisPlusException.class)
                .hasMessage("blank");
        assertThatThrownBy(() -> Assert.notEmpty((String) null, "blank")).isInstanceOf(MybatisPlusException.class)
                .hasMessage("blank");
    }

    @Test
    void notEmptyCollectionAndMapRejectNullAndEmpty() {
        assertThatCode(() -> {
            Assert.notEmpty(List.of(1), "must not be empty");
            Assert.notEmpty(Map.of("k", "v"), "must not be empty");
        }).doesNotThrowAnyException();
        assertThatThrownBy(() -> Assert.notEmpty(List.of(), "empty list")).isInstanceOf(MybatisPlusException.class)
                .hasMessage("empty list");
        assertThatThrownBy(() -> Assert.notEmpty((List<?>) null, "null list")).isInstanceOf(MybatisPlusException.class)
                .hasMessage("null list");
        assertThatThrownBy(() -> Assert.notEmpty(Map.of(), "empty map")).isInstanceOf(MybatisPlusException.class)
                .hasMessage("empty map");
        assertThatThrownBy(() -> Assert.notEmpty((Map<?, ?>) null, "null map")).isInstanceOf(MybatisPlusException.class)
                .hasMessage("null map");
    }

    @Test
    void isEmptyMapIsTheInverseOfNotEmpty() {
        assertThatCode(() -> {
            Assert.isEmpty(Map.of(), "must be empty");
            Assert.isEmpty(null, "must be empty");
        }).doesNotThrowAnyException();
        assertThatThrownBy(() -> Assert.isEmpty(Map.of("k", "v"), "not empty")).isInstanceOf(MybatisPlusException.class)
                .hasMessage("not empty");
    }

    @Test
    void notEmptyArrayRejectsNullAndEmpty() {
        assertThatCode(() -> Assert.notEmpty(new Object[]{1}, "must not be empty")).doesNotThrowAnyException();
        assertThatThrownBy(() -> Assert.notEmpty(new Object[0], "empty array")).isInstanceOf(MybatisPlusException.class)
                .hasMessage("empty array");
        assertThatThrownBy(() -> Assert.notEmpty((Object[]) null, "null array"))
                .isInstanceOf(MybatisPlusException.class).hasMessage("null array");
    }
}
