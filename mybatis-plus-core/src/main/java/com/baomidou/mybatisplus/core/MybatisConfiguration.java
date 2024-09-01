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

import com.baomidou.mybatisplus.core.handlers.CompositeEnumTypeHandler;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.type.TypeHandler;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * replace default Configuration class
 * <p>Caratacus 2016/9/25 replace mapperRegistry</p>
 *
 * @author hubin
 * @since 2016-01-23
 */
public class MybatisConfiguration extends Configuration {
    private static final Log logger = LogFactory.getLog(MybatisConfiguration.class);
    /**
     * Mapper 注册
     */
    protected final MybatisMapperRegistry mybatisMapperRegistry = new MybatisMapperRegistry(this);

    protected final Map<String, Cache> caches = new StrictMap<>("Caches collection");
    protected final Map<String, ResultMap> resultMaps = new StrictMap<>("Result Maps collection");
    protected final Map<String, ParameterMap> parameterMaps = new StrictMap<>("Parameter Maps collection");
    protected final Map<String, KeyGenerator> keyGenerators = new StrictMap<>("Key Generators collection");
    protected final Map<String, XNode> sqlFragments = new StrictMap<>("XML fragments parsed from previous mappers");
    protected final Map<String, MappedStatement> mappedStatements = new StrictMap<MappedStatement>("Mapped Statements collection")
        .conflictMessageProducer((savedValue, targetValue) ->
            ". please check " + savedValue.getResource() + " and " + targetValue.getResource());
    /**
     * 是否生成短key缓存
     *
     * @since 3.4.0
     */
    @Setter
    @Getter
    private boolean useGeneratedShortKey = true;

    public MybatisConfiguration(Environment environment) {
        this();
        this.environment = environment;
    }

    /**
     * 初始化调用
     */
    public MybatisConfiguration() {
        super();
        this.mapUnderscoreToCamelCase = true;
        typeHandlerRegistry.setDefaultEnumTypeHandler(CompositeEnumTypeHandler.class);
        languageRegistry.setDefaultDriverClass(MybatisXMLLanguageDriver.class);
    }

    /**
     * MybatisPlus 加载 SQL 顺序：
     * <p> 1、加载 XML中的 SQL </p>
     * <p> 2、加载 SqlProvider 中的 SQL </p>
     * <p> 3、XmlSql 与 SqlProvider不能包含相同的 SQL </p>
     * <p>调整后的 SQL优先级：XmlSql > sqlProvider > CurdSql </p>
     */
    @Override
    public void addMappedStatement(MappedStatement ms) {
        if (mappedStatements.containsKey(ms.getId())) {
            /*
             * 说明已加载了xml中的节点； 忽略mapper中的 SqlProvider 数据
             */
            logger.error("mapper[" + ms.getId() + "] is ignored, because it exists, maybe from xml file");
            return;
        }
        mappedStatements.put(ms.getId(), ms);
    }

    /**
     * 使用自己的 MybatisMapperRegistry
     */
    @Override
    public MapperRegistry getMapperRegistry() {
        return mybatisMapperRegistry;
    }

    /**
     * 使用自己的 MybatisMapperRegistry
     */
    @Override
    public <T> void addMapper(Class<T> type) {
        mybatisMapperRegistry.addMapper(type);
    }

    /**
     * 新增注入新的 Mapper 信息，新增前会清理之前的缓存信息
     *
     * @param type Mapper Type
     * @deprecated 3.5.8 不建议在实际生产环境中使用.
     */
    @Deprecated
    public <T> void addNewMapper(Class<T> type) {
        this.removeMapper(type);
        this.addMapper(type);
    }

    /**
     * 移除 Mapper 相关缓存，支持 GroovyClassLoader 动态注入 Mapper
     *
     * @param type Mapper Type
     * @deprecated 3.5.8 不建议在实际生产环境中使用.
     */
    @Deprecated
    public <T> void removeMapper(Class<T> type) {
        Set<String> mapperRegistryCache = GlobalConfigUtils.getGlobalConfig(this).getMapperRegistryCache();
        final String mapperType = type.toString();
        if (mapperRegistryCache.contains(mapperType)) {
            // 清空实体表信息映射信息
            TableInfoHelper.remove(ReflectionKit.getSuperClassGenericType(type, Mapper.class, 0));

            // 清空 Mapper 缓存信息
            this.mybatisMapperRegistry.removeMapper(type);
            this.loadedResources.remove(type.toString());
            this.loadedResources.remove(type.getName().replace(StringPool.DOT, StringPool.SLASH) + ".xml");
            mapperRegistryCache.remove(mapperType);

            // 清空 Mapper 方法 mappedStatement 缓存信息
            String typeKey = type.getName() + StringPool.DOT;
            String simpleName = type.getSimpleName();
            mappedStatements.keySet().stream().filter(ms -> ms.startsWith(typeKey) || ms.equals(simpleName)).collect(Collectors.toSet()).forEach(mappedStatements::remove);
            resultMaps.keySet().stream().filter(r -> r.startsWith(typeKey)).collect(Collectors.toSet()).forEach(resultMaps::remove);
            parameterMaps.keySet().stream().filter(p -> p.startsWith(typeKey)).collect(Collectors.toSet()).forEach(parameterMaps::remove);
            keyGenerators.keySet().stream().filter(k -> k.startsWith(typeKey)).collect(Collectors.toSet()).forEach(keyGenerators::remove);
            sqlFragments.keySet().stream().filter(s -> s.startsWith(typeKey)).collect(Collectors.toSet()).forEach(sqlFragments::remove);
            caches.keySet().stream().filter(p -> p.equals(type.getName()) || p.equals(simpleName)).collect(Collectors.toSet()).forEach(caches::remove);
        }
    }

    /**
     * 使用自己的 MybatisMapperRegistry
     */
    @Override
    public void addMappers(String packageName, Class<?> superType) {
        mybatisMapperRegistry.addMappers(packageName, superType);
    }

    /**
     * 使用自己的 MybatisMapperRegistry
     */
    @Override
    public void addMappers(String packageName) {
        mybatisMapperRegistry.addMappers(packageName);
    }

    /**
     * 使用自己的 MybatisMapperRegistry
     */
    @Override
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mybatisMapperRegistry.getMapper(type, sqlSession);
    }

    /**
     * 使用自己的 MybatisMapperRegistry
     */
    @Override
    public boolean hasMapper(Class<?> type) {
        return mybatisMapperRegistry.hasMapper(type);
    }

    /**
     * 指定动态SQL生成的默认语言
     *
     * @param driver LanguageDriver
     */
    @Override
    public void setDefaultScriptingLanguage(Class<? extends LanguageDriver> driver) {
        if (driver == null) {
            driver = MybatisXMLLanguageDriver.class;
        }
        getLanguageRegistry().setDefaultDriverClass(driver);
    }

    @Override
    public void setDefaultEnumTypeHandler(Class<? extends TypeHandler> typeHandler) {
        if (typeHandler != null) {
            CompositeEnumTypeHandler.setDefaultEnumTypeHandler(typeHandler);
        }
    }

    @Override
    public void addKeyGenerator(String id, KeyGenerator keyGenerator) {
        keyGenerators.put(id, keyGenerator);
    }

    @Override
    public Collection<String> getKeyGeneratorNames() {
        return keyGenerators.keySet();
    }

    @Override
    public Collection<KeyGenerator> getKeyGenerators() {
        return keyGenerators.values();
    }

    @Override
    public KeyGenerator getKeyGenerator(String id) {
        return keyGenerators.get(id);
    }

    @Override
    public boolean hasKeyGenerator(String id) {
        return keyGenerators.containsKey(id);
    }

    @Override
    public void addCache(Cache cache) {
        caches.put(cache.getId(), cache);
    }

    @Override
    public Collection<String> getCacheNames() {
        return caches.keySet();
    }

    @Override
    public Collection<Cache> getCaches() {
        return caches.values();
    }

    @Override
    public Cache getCache(String id) {
        return caches.get(id);
    }

    @Override
    public boolean hasCache(String id) {
        return caches.containsKey(id);
    }

    @Override
    public void addResultMap(ResultMap rm) {
        resultMaps.put(rm.getId(), rm);
        checkLocallyForDiscriminatedNestedResultMaps(rm);
        checkGloballyForDiscriminatedNestedResultMaps(rm);
    }

    @Override
    public Collection<String> getResultMapNames() {
        return resultMaps.keySet();
    }

    @Override
    public Collection<ResultMap> getResultMaps() {
        return resultMaps.values();
    }

    @Override
    public ResultMap getResultMap(String id) {
        return resultMaps.get(id);
    }

    @Override
    public boolean hasResultMap(String id) {
        return resultMaps.containsKey(id);
    }

    @Override
    public void addParameterMap(ParameterMap pm) {
        parameterMaps.put(pm.getId(), pm);
    }

    @Override
    public Collection<String> getParameterMapNames() {
        return parameterMaps.keySet();
    }

    @Override
    public Collection<ParameterMap> getParameterMaps() {
        return parameterMaps.values();
    }

    @Override
    public ParameterMap getParameterMap(String id) {
        return parameterMaps.get(id);
    }

    @Override
    public boolean hasParameterMap(String id) {
        return parameterMaps.containsKey(id);
    }

    @Override
    public Map<String, XNode> getSqlFragments() {
        return sqlFragments;
    }

    @Override
    public Collection<String> getMappedStatementNames() {
        buildAllStatements();
        return mappedStatements.keySet();
    }

    @Override
    public Collection<MappedStatement> getMappedStatements() {
        buildAllStatements();
        return mappedStatements.values();
    }

    @Override
    public MappedStatement getMappedStatement(String id) {
        return this.getMappedStatement(id, true);
    }

    @Override
    public MappedStatement getMappedStatement(String id, boolean validateIncompleteStatements) {
        if (validateIncompleteStatements) {
            buildAllStatements();
        }
        return mappedStatements.get(id);
    }

    @Override
    public boolean hasStatement(String statementName, boolean validateIncompleteStatements) {
        if (validateIncompleteStatements) {
            buildAllStatements();
        }
        return mappedStatements.containsKey(statementName);
    }

    @Override
    public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
        return super.newExecutor(transaction, executorType);
    }

    // Slow but a one time cost. A better solution is welcome.
    @Override
    public void checkGloballyForDiscriminatedNestedResultMaps(ResultMap rm) {
        if (rm.hasNestedResultMaps()) {
            final String resultMapId = rm.getId();
            for (Object resultMapObject : resultMaps.values()) {
                if (resultMapObject instanceof ResultMap) {
                    ResultMap entryResultMap = (ResultMap) resultMapObject;
                    if (!entryResultMap.hasNestedResultMaps() && entryResultMap.getDiscriminator() != null) {
                        Collection<String> discriminatedResultMapNames = entryResultMap.getDiscriminator().getDiscriminatorMap()
                            .values();
                        if (discriminatedResultMapNames.contains(resultMapId)) {
                            entryResultMap.forceNestedResultMaps();
                        }
                    }
                }
            }
        }
    }

    // Slow but a one time cost. A better solution is welcome.
    @Override
    protected void checkLocallyForDiscriminatedNestedResultMaps(ResultMap rm) {
        if (!rm.hasNestedResultMaps() && rm.getDiscriminator() != null) {
            for (String discriminatedResultMapName : rm.getDiscriminator().getDiscriminatorMap().values()) {
                if (hasResultMap(discriminatedResultMapName)) {
                    ResultMap discriminatedResultMap = resultMaps.get(discriminatedResultMapName);
                    if (discriminatedResultMap.hasNestedResultMaps()) {
                        rm.forceNestedResultMaps();
                        break;
                    }
                }
            }
        }
    }

    protected class StrictMap<V> extends ConcurrentHashMap<String, V> {

        private static final long serialVersionUID = -4950446264854982944L;
        private final String name;
        private BiFunction<V, V, String> conflictMessageProducer;
        private final Object AMBIGUITY_INSTANCE = new Object();

        public StrictMap(String name, int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
            this.name = name;
        }

        public StrictMap(String name, int initialCapacity) {
            super(initialCapacity);
            this.name = name;
        }

        public StrictMap(String name) {
            super();
            this.name = name;
        }

        public StrictMap(String name, Map<String, ? extends V> m) {
            super(m);
            this.name = name;
        }

        /**
         * Assign a function for producing a conflict error message when contains value with the same key.
         * <p>
         * function arguments are 1st is saved value and 2nd is target value.
         *
         * @param conflictMessageProducer A function for producing a conflict error message
         * @return a conflict error message
         * @since 3.5.0
         */
        public StrictMap<V> conflictMessageProducer(BiFunction<V, V, String> conflictMessageProducer) {
            this.conflictMessageProducer = conflictMessageProducer;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public V put(String key, V value) {
            if (containsKey(key)) {
                throw new IllegalArgumentException(name + " already contains value for " + key
                    + (conflictMessageProducer == null ? StringPool.EMPTY : conflictMessageProducer.apply(super.get(key), value)));
            }
            if (useGeneratedShortKey) {
                if (key.contains(StringPool.DOT)) {
                    final String shortKey = getShortName(key);
                    if (super.get(shortKey) == null) {
                        super.put(shortKey, value);
                    } else {
                        super.put(shortKey, (V) AMBIGUITY_INSTANCE);
                    }
                }
            }
            return super.put(key, value);
        }

        @Override
        public boolean containsKey(Object key) {
            if (key == null) {
                return false;
            }
            return super.get(key) != null;
        }

        @Override
        public V get(Object key) {
            V value = super.get(key);
            if (value == null) {
                throw new IllegalArgumentException(name + " does not contain value for " + key);
            }
            if (useGeneratedShortKey && AMBIGUITY_INSTANCE == value) {
                throw new IllegalArgumentException(key + " is ambiguous in " + name
                    + " (try using the full name including the namespace, or rename one of the entries)");
            }
            return value;
        }

        private String getShortName(String key) {
            final String[] keyParts = key.split("\\.");
            return keyParts[keyParts.length - 1];
        }
    }
}
