package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * @author nieqiuqiu 2020/7/2
 */
@Tag("unit")
class CollectionUtilsTest {

    /**
     * 按预期大小建 map 的容量档位，读 HashMap 内部字段验证预分配算法。
     * <p>
     * 这里钉的是 HashMap 的实现细节，JDK 8 与 17/21 的 tableSizeFor、resize 结果一致； 提高 JDK 基线时必须复核这几组数字，不能想当然沿用。
     */
    @ParameterizedTest
    @CsvSource({"4, 8, 6", "11, 16, 12", "16, 32, 24"})
    void newHashMapWithExpectedSizePreallocatesTable(int expectedSize, int tableSize, int threshold)
            throws ReflectiveOperationException {
        Map<String, String> hashMap = newHashMapWithExpectedSize(expectedSize);
        assertThat(getTableSize(hashMap)).isEqualTo(tableSize);
        assertThat(getThresholdValue(hashMap)).isEqualTo(threshold);
    }

    /**
     * 直接用 JDK 构造器时观察到的同一组数字，用来对照上面预分配公式选出来的档位是否确实更省。
     */
    @ParameterizedTest
    @CsvSource({"4, 4, 3", "11, 16, 12", "16, 16, 12"})
    void plainHashMapKeepsItsOwnSizing(int initialCapacity, int tableSize, int threshold)
            throws ReflectiveOperationException {
        Map<String, String> map = newHashMap(initialCapacity);
        assertThat(getTableSize(map)).isEqualTo(tableSize);
        assertThat(getThresholdValue(map)).isEqualTo(threshold);
    }

    @Test
    void shouldSplitListIntoChunksOfGivenSize() {
        List<Integer> list = asList(1, 2, 3, 4, 5);
        List<List<Integer>> lists = CollectionUtils.split(list, 2);
        assertThat(lists).isEqualTo(asList(asList(1, 2), asList(3, 4), singletonList(5)));
    }

    @ParameterizedTest
    @ValueSource(ints = {10_000, 1_000_000})
    void shouldStopSplittingWhenIteratorIsExhausted(int batchSize) {
        LinkedHashSet<Integer> collection = spy(new LinkedHashSet<>(singletonList(42)));
        Iterator<Integer> iterator = spy(collection.iterator());
        doReturn(iterator).when(collection).iterator();

        assertThat(CollectionUtils.split(collection, batchSize)).isEqualTo(singletonList(singletonList(42)));
        verify(iterator, atMost(5)).hasNext();
    }

    @Test
    void shouldSplitSetInIterationOrder() {
        LinkedHashSet<Integer> collection = new LinkedHashSet<>(asList(3, 1, 2, 4));

        assertThat(CollectionUtils.split(collection, 2)).isEqualTo(asList(asList(3, 1), asList(2, 4)));
    }

    @Test
    void shouldKeepSplitResultsMutableAndPreserveNullElements() {
        List<Integer> source = asList(1, null, 3);
        List<List<Integer>> batches = CollectionUtils.split(source, 2);

        assertThat(batches).isEqualTo(asList(asList(1, null), singletonList(3)));
        batches.get(0).add(4);
        batches.add(singletonList(5));
        assertThat(batches).isEqualTo(asList(asList(1, null, 4), singletonList(3), singletonList(5)));
        assertThat(source).containsExactly(1, null, 3);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void shouldRejectNonPositiveBatchSizeForNonEmptyCollection(int batchSize) {
        assertThatThrownBy(() -> CollectionUtils.split(singletonList(1), batchSize))
                .isInstanceOf(MybatisPlusException.class).hasMessage("batchSize must not be less than one");
    }

    @Test
    void shouldReturnEmptyResultBeforeValidatingBatchSize() {
        assertThat(CollectionUtils.split(null, 0)).isEmpty();
        assertThat(CollectionUtils.split(emptyList(), 0)).isEmpty();
    }

    /**
     * computeIfAbsent 的公共契约：缺失时写入并返回，命中时不再调用 mappingFunction。
     * <p>
     * 不再断言"在 mappingFunction 里更新同一个 map"，那是 JDK 8 分支才允许的行为，JDK 9+ 会抛 IllegalStateException("Recursive update")，与工具类里
     * isJdk8 的兼容性分支一一对应。
     */
    @Test
    void computeIfAbsentWritesValueAndSkipsFunctionWhenKeyPresent() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        assertThat(CollectionUtils.computeIfAbsent(map, "AaAa", key -> key + "-value")).isEqualTo("AaAa-value");
        assertThat(map).containsEntry("AaAa", "AaAa-value");

        AtomicInteger invoked = new AtomicInteger();
        assertThat(CollectionUtils.computeIfAbsent(map, "AaAa", key -> {
            invoked.incrementAndGet();
            return "ignored";
        })).isEqualTo("AaAa-value");
        assertThat(invoked).hasValue(0);
    }

    private Map<String, String> newHashMapWithExpectedSize(int size) {
        Map<String, String> hashMap = CollectionUtils.newHashMapWithExpectedSize(size);
        hashMap.put("1", "1");
        return hashMap;
    }

    private Map<String, String> newHashMap(int size) {
        Map<String, String> map = new HashMap<>(size);
        map.put("1", "1");
        return map;
    }

    private int getTableSize(Map<String, String> map) throws ReflectiveOperationException {
        Field field = map.getClass().getDeclaredField("table");
        field.setAccessible(true);
        Object[] o = (Object[]) field.get(map);
        return o.length;
    }

    private int getThresholdValue(Map<String, String> map) throws ReflectiveOperationException {
        Field field = map.getClass().getDeclaredField("threshold");
        field.setAccessible(true);
        return (int) field.get(map);
    }

}
