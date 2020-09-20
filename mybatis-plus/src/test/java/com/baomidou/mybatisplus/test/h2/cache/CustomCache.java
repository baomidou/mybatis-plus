package com.baomidou.mybatisplus.test.h2.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.Cache;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CustomCache implements Cache {

    private final String id;

    private Map<Object, Object> cache = new HashMap<>();

    public CustomCache(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getSize() {
        return cache.size();
    }

    @Override
    public void putObject(Object key, Object value) {
        log.info("添加命名空间:{},缓存:{}", id, key);
        cache.put(key, value);
    }

    @Override
    public Object getObject(Object key) {
        log.info("获取命名空间:{},缓存:{}", id, key);
        return cache.get(key);
    }

    @Override
    public Object removeObject(Object key) {
        log.info("清除命名空间:{},缓存:{}", id, key);
        return cache.remove(key);
    }

    @Override
    public void clear() {
        log.info("清除命名空间:{}缓存", id);
        cache.clear();
    }

}
