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
package com.baomidou.mybatisplus.extension.plugins.datasource;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * 一致性 hash 分库策略
 * <p>哈希环按数据源列表内容缓存，检测到列表变化（动态加删数据源）时自动重建。
 *
 * @author hubin
 * @since 3.5.18
 */
public class ConsistentHashShardingStrategy extends AbstractShardingStrategy {
    private static final int DEFAULT_VIRTUAL_NODE_COUNT = 128;
    private final int virtualNodeCount;
    private volatile HashRing cache;

    public ConsistentHashShardingStrategy() {
        this(DEFAULT_VIRTUAL_NODE_COUNT, false);
    }

    /**
     * @param virtualNodeCount 虚拟节点数
     * @param fixedNode        兼容保留：哈希环已按数据源列表内容缓存，该参数不再影响行为
     */
    public ConsistentHashShardingStrategy(int virtualNodeCount, boolean fixedNode) {
        if (virtualNodeCount <= 0) throw new IllegalArgumentException("virtualNodeCount must be greater than 0");
        this.virtualNodeCount = virtualNodeCount;
    }

    @Override
    public String chooseKey(List<String> keys, String statementId, Object parameter) {
        if (keys.size() == 1) return keys.get(0);
        String hashKey = buildHashKey(statementId, parameter);
        return hashKey == null ? chooseRandomKey(keys) : chooseConsistentKey(keys, hashKey);
    }

    private String buildHashKey(String statementId, Object parameter) {
        return statementId == null ? null : statementId + '#' + parameter;
    }

    public String chooseConsistentKey(List<String> keys, String hashKey) {
        TreeMap<Long, String> ring = hashRing(keys);
        Long node = ring.ceilingKey(hash(hashKey));
        return ring.get(node == null ? ring.firstKey() : node);
    }

    private TreeMap<Long, String> hashRing(List<String> keys) {
        HashRing cached = this.cache;
        if (cached != null && cached.keys.equals(keys)) {
            return cached.ring;
        }
        HashRing built = new HashRing(new ArrayList<>(keys), buildHashRing(keys));
        this.cache = built;
        return built.ring;
    }

    protected TreeMap<Long, String> buildHashRing(List<String> keys) {
        TreeMap<Long, String> ring = new TreeMap<>();
        for (String key : keys) {
            for (int i = 0; i < virtualNodeCount; i++) {
                ring.put(hash(key + '-' + i), key);
            }
        }
        return ring;
    }

    protected long hash(String value) {
        long result = 0xcbf29ce484222325L;
        for (byte b : value.getBytes(StandardCharsets.UTF_8)) {
            result ^= b & 0xff;
            result *= 0x100000001b3L;
        }
        return result & Long.MAX_VALUE;
    }

    private static final class HashRing {
        private final List<String> keys;
        private final TreeMap<Long, String> ring;

        private HashRing(List<String> keys, TreeMap<Long, String> ring) {
            this.keys = keys;
            this.ring = ring;
        }
    }
}
