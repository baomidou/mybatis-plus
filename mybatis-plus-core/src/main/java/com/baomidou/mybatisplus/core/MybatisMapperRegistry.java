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
package com.baomidou.mybatisplus.core;

import com.baomidou.mybatisplus.core.override.MybatisMapperProxyFactory;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 继承至MapperRegistry
 *
 * @author Caratacus hubin
 * @since 2017-04-19
 */
public class MybatisMapperRegistry extends MapperRegistry {

    private final Configuration config;

    private final Map<Class<?>, MybatisMapperProxyFactory<?>> knownMappers = new ConcurrentHashMap<>();

    public MybatisMapperRegistry(Configuration config) {
        super(config);
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        // fix https://github.com/baomidou/mybatis-plus/issues/4247
        MybatisMapperProxyFactory<T> mapperProxyFactory = (MybatisMapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null) {
            mapperProxyFactory = (MybatisMapperProxyFactory<T>) knownMappers.entrySet().stream()
                .filter(t -> t.getKey().getName().equals(type.getName())).findFirst().map(Map.Entry::getValue)
                .orElseThrow(() -> new BindingException("Type " + type + " is not known to the MybatisPlusMapperRegistry."));
        }
        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new BindingException("Error getting mapper instance. Cause: " + e, e);
        }
    }

    @Override
    public <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }

    /**
     * 清空 Mapper 缓存信息
     */
    protected <T> void removeMapper(Class<T> type) {
        knownMappers.entrySet().stream().filter(t -> t.getKey().getName().equals(type.getName()))
            .findFirst().ifPresent(t -> knownMappers.remove(t.getKey()));
    }

    @Override
    public <T> void addMapper(Class<T> type) {
        if (type.isInterface()) {
            if (hasMapper(type)) {
                return;
//                throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
            }
            boolean loadCompleted = false;
            try {
                knownMappers.put(type, new MybatisMapperProxyFactory<>(type));
                // It's important that the type is added before the parser is run
                // otherwise the binding may automatically be attempted by the
                // mapper parser. If the type is already known, it won't try.
                MybatisMapperAnnotationBuilder parser = new MybatisMapperAnnotationBuilder(config, type);
                parser.parse();
                loadCompleted = true;
            } finally {
                if (!loadCompleted) {
                    knownMappers.remove(type);
                }
            }
        }
    }

    /**
     * 使用自己的 knownMappers
     */
    @Override
    public Collection<Class<?>> getMappers() {
        return Collections.unmodifiableCollection(knownMappers.keySet());
    }

}
