package com.baomidou.mybatisplus.core.toolkit;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 空值判定按"长度/元素数"算，标量一律算非空
 */
class ObjectUtilsTest {

    @Test
    void nullIsEmptyButScalarsAreNot() {
        assertThat(ObjectUtils.isEmpty(null)).isTrue();
        assertThat(ObjectUtils.isEmpty("")).isTrue();
        assertThat(ObjectUtils.isEmpty(1)).isFalse();
        assertThat(ObjectUtils.isEmpty(0)).isFalse();
        assertThat(ObjectUtils.isEmpty(Boolean.FALSE)).isFalse();
        assertThat(ObjectUtils.isEmpty(new Object())).isFalse();
        assertThat(ObjectUtils.isNotEmpty(0)).isTrue();
    }

    @Test
    void charSequencesAreJudgedByLength() {
        assertThat(ObjectUtils.isEmpty(new StringBuilder())).isTrue();
        assertThat(ObjectUtils.isEmpty(" ")).isFalse();
        assertThat(ObjectUtils.isEmpty(new StringBuilder("x"))).isFalse();
    }

    @Test
    void arraysAreJudgedByLength() {
        assertThat(ObjectUtils.isEmpty(new int[0])).isTrue();
        assertThat(ObjectUtils.isEmpty(new Object[0])).isTrue();
        assertThat(ObjectUtils.isEmpty(new int[]{0})).isFalse();
        assertThat(ObjectUtils.isEmpty(new String[]{"a"})).isFalse();
    }

    @Test
    void collectionsAndMapsFollowTheirSize() {
        assertThat(ObjectUtils.isEmpty(List.of())).isTrue();
        assertThat(ObjectUtils.isEmpty(Set.of())).isTrue();
        assertThat(ObjectUtils.isEmpty(Map.of())).isTrue();
        assertThat(ObjectUtils.isEmpty(List.of(1))).isFalse();
        assertThat(ObjectUtils.isEmpty(Set.of(1))).isFalse();
        assertThat(ObjectUtils.isEmpty(Map.of("k", "v"))).isFalse();
        assertThat(ObjectUtils.isEmpty(Collections.singletonList(null))).isFalse();
    }

    @Test
    void iterablesAndIteratorsAreProbedWithoutCopying() {
        Iterable<String> emptyIterable = Collections::emptyIterator;
        Iterable<String> oneElementIterable = () -> List.of("a").iterator();
        assertThat(ObjectUtils.isEmpty(emptyIterable)).isTrue();
        assertThat(ObjectUtils.isEmpty(oneElementIterable)).isFalse();

        Iterator<String> emptyIterator = Collections.emptyIterator();
        Iterator<String> oneElementIterator = List.of("a").iterator();
        assertThat(ObjectUtils.isEmpty(emptyIterator)).isTrue();
        assertThat(ObjectUtils.isEmpty(oneElementIterator)).isFalse();
    }

    @Test
    void varargsIsNullFailsWhenAnyArgumentIsEmpty() {
        assertThat(ObjectUtils.isNull(null, "x")).isTrue();
        assertThat(ObjectUtils.isNull("x", "")).isTrue();
        assertThat(ObjectUtils.isNull("x", "y")).isFalse();
        assertThat(ObjectUtils.isNull()).isFalse();
        assertThat(ObjectUtils.isNotNull("x", "")).isFalse();
        assertThat(ObjectUtils.isNotNull("x", "y")).isTrue();
    }
}
