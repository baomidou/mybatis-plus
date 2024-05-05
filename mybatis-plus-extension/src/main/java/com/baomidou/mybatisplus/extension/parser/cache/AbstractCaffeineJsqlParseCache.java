/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.parser.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Setter;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * jsqlparser 缓存 Caffeine 缓存实现抽象类
 *
 * @author miemie hubin
 * @since 2023-08-08
 */
public abstract class AbstractCaffeineJsqlParseCache implements JsqlParseCache {
    protected final Log logger = LogFactory.getLog(this.getClass());
    protected final Cache<String, byte[]> cache;
    @Setter
    protected boolean async = false;
    @Setter
    protected Executor executor;

    public AbstractCaffeineJsqlParseCache(Cache<String, byte[]> cache) {
        this.cache = cache;
    }

    public AbstractCaffeineJsqlParseCache(Consumer<Caffeine<Object, Object>> consumer) {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
        consumer.accept(caffeine);
        this.cache = caffeine.build();
    }

    @Override
    public void putStatement(String sql, Statement value) {
        this.put(sql, value);
    }

    @Override
    public void putStatements(String sql, Statements value) {
        this.put(sql, value);
    }

    @Override
    public Statement getStatement(String sql) {
        return this.get(sql);
    }

    @Override
    public Statements getStatements(String sql) {
        return this.get(sql);
    }

    /**
     * 获取解析对象，异常清空缓存逻辑
     *
     * @param sql 执行 SQL
     * @return 返回泛型对象
     */
    protected <T> T get(String sql) {
        byte[] bytes = cache.getIfPresent(sql);
        if (null != bytes) {
            try {
                return (T) deserialize(sql, bytes);
            } catch (Exception e) {
                cache.invalidate(sql);
                logger.error("deserialize error", e);
            }
        }
        return null;
    }

    /**
     * 存储解析对象
     *
     * @param sql   执行 SQL
     * @param value 解析对象
     */
    protected void put(String sql, Object value) {
        if (async) {
            if (executor != null) {
                CompletableFuture.runAsync(() -> cache.put(sql, serialize(value)), executor);
            } else {
                CompletableFuture.runAsync(() -> cache.put(sql, serialize(value)));
            }
        } else {
            cache.put(sql, serialize(value));
        }
    }

    /**
     * 序列化
     */
    public abstract byte[] serialize(Object obj);

    /**
     * 反序列化
     */
    public abstract Object deserialize(String sql, byte[] bytes);

}
