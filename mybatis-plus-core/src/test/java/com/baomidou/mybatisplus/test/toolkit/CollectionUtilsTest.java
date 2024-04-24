package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * @author nieqiuqiu 2020/7/2
 */
class CollectionUtilsTest {

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void testCreateHashMap() throws ReflectiveOperationException {
        Map<String, String> hashMap = newHashMapWithExpectedSize(4);
        Assertions.assertEquals(8, getTableSize(hashMap));
        Assertions.assertEquals(6, getThresholdValue(hashMap));

        hashMap = newHashMapWithExpectedSize(11);
        Assertions.assertEquals(16, getTableSize(hashMap));
        Assertions.assertEquals(12, getThresholdValue(hashMap));

        hashMap = newHashMapWithExpectedSize(16);
        Assertions.assertEquals(32, getTableSize(hashMap));
        Assertions.assertEquals(24, getThresholdValue(hashMap));

        Map<String, String> map = newHashMap(4);
        Assertions.assertEquals(4, getTableSize(map));
        Assertions.assertEquals(3, getThresholdValue(map));

        map = newHashMap(11);
        Assertions.assertEquals(16, getTableSize(map));
        Assertions.assertEquals(12, getThresholdValue(map));

        map = newHashMap(16);
        Assertions.assertEquals(16, getTableSize(map));
        Assertions.assertEquals(12, getThresholdValue(map));

        computeIfAbsent();
    }

    @Test
    void testSplit() {
        List<Integer> list = asList(1, 2, 3, 4, 5);
        List<List<Integer>> lists = CollectionUtils.split(list, 2);
        Assertions.assertEquals(asList(asList(1, 2), asList(3, 4), singletonList(5)), lists);
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

    private void computeIfAbsent() {

        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        CollectionUtils.computeIfAbsent(map, "AaAa", key -> map.computeIfAbsent("BBBB", key2 -> "noah"));
        System.out.println("i can return");
    }

}
