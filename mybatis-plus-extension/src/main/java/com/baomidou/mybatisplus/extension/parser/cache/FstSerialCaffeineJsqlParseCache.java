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
 * @author miemie
 * @since 2023-08-05
 */
public class FstSerialCaffeineJsqlParseCache implements JsqlParseCache {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final Cache<String, byte[]> cache;
    @Setter
    private boolean async = false;
    @Setter
    private Executor executor;

    public FstSerialCaffeineJsqlParseCache(Cache<String, byte[]> cache) {
        this.cache = cache;
    }

    public FstSerialCaffeineJsqlParseCache(Consumer<Caffeine<Object, Object>> consumer) {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
        consumer.accept(caffeine);
        this.cache = caffeine.build();
    }

    @Override
    public void putStatement(String sql, Statement value) {
        put(sql, value);
    }

    @Override
    public void putStatements(String sql, Statements value) {
        put(sql, value);
    }

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

    @Override
    public Statement getStatement(String sql) {
        byte[] bytes = cache.getIfPresent(sql);
        if (bytes != null)
            return (Statement) deserialize(sql, bytes);
        return null;
    }

    @Override
    public Statements getStatements(String sql) {
        byte[] bytes = cache.getIfPresent(sql);
        if (bytes != null)
            return (Statements) deserialize(sql, bytes);
        return null;
    }

    protected byte[] serialize(Object obj) {
        return FstFactory.getDefaultFactory().asByteArray(obj);
    }

    protected Object deserialize(String sql, byte[] bytes) {
        try {
            return FstFactory.getDefaultFactory().asObject(bytes);
        } catch (Exception e) {
            cache.invalidate(sql);
            logger.error("deserialize error", e);
        }
        return null;
    }
}
