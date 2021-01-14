/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
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

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.executor.MybatisBatchExecutor;
import com.baomidou.mybatisplus.core.executor.MybatisCachingExecutor;
import com.baomidou.mybatisplus.core.executor.MybatisReuseExecutor;
import com.baomidou.mybatisplus.core.executor.MybatisSimpleExecutor;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

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

    /**
     * @deprecated 该属性将会随着 com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor 插件的移除而移除
     */
    @Deprecated
    @Setter
    @Getter
    private boolean useDeprecatedExecutor = true;

    public MybatisConfiguration(Environment environment) {
        this();
        this.environment = environment;
    }

    /**
     * @return GlobalConfig
     * @deprecated 3.4.0 please use {@link GlobalConfigUtils#getGlobalConfig(Configuration)}
     */
    @Deprecated
    public GlobalConfig getGlobalConfig() {
        return GlobalConfigUtils.getGlobalConfig(this);
    }

    /**
     * @param globalConfig GlobalConfig
     * @deprecated 3.4.0 please use {@link GlobalConfigUtils#setGlobalConfig(Configuration, GlobalConfig)}
     */
    @Deprecated
    public void setGlobalConfig(GlobalConfig globalConfig) {
        GlobalConfigUtils.setGlobalConfig(this, globalConfig);
    }

    /**
     * 初始化调用
     */
    public MybatisConfiguration() {
        super();
        this.mapUnderscoreToCamelCase = true;
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
        logger.debug("addMappedStatement: " + ms.getId());
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
            //todo 替换动态SQL生成的默认语言为自己的。
            driver = MybatisXMLLanguageDriver.class;
        }
        getLanguageRegistry().setDefaultDriverClass(driver);
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
        if (useDeprecatedExecutor) {
            executorType = executorType == null ? defaultExecutorType : executorType;
            executorType = executorType == null ? ExecutorType.SIMPLE : executorType;
            Executor executor;
            if (ExecutorType.BATCH == executorType) {
                executor = new MybatisBatchExecutor(this, transaction);
            } else if (ExecutorType.REUSE == executorType) {
                executor = new MybatisReuseExecutor(this, transaction);
            } else {
                executor = new MybatisSimpleExecutor(this, transaction);
            }
            if (cacheEnabled) {
                executor = new MybatisCachingExecutor(executor);
            }
            executor = (Executor) interceptorChain.pluginAll(executor);
            return executor;
        }
        return super.newExecutor(transaction, executorType);
    }

    // Slow but a one time cost. A better solution is welcome.
    @Override
    protected void checkGloballyForDiscriminatedNestedResultMaps(ResultMap rm) {
        if (rm.hasNestedResultMaps()) {
            for (Map.Entry<String, ResultMap> entry : resultMaps.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof ResultMap) {
                    ResultMap entryResultMap = (ResultMap) value;
                    if (!entryResultMap.hasNestedResultMaps() && entryResultMap.getDiscriminator() != null) {
                        Collection<String> discriminatedResultMapNames = entryResultMap.getDiscriminator().getDiscriminatorMap().values();
                        if (discriminatedResultMapNames.contains(rm.getId())) {
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
            for (Map.Entry<String, String> entry : rm.getDiscriminator().getDiscriminatorMap().entrySet()) {
                String discriminatedResultMapName = entry.getValue();
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

    protected class StrictMap<V> extends HashMap<String, V> {

        private static final long serialVersionUID = -4950446264854982944L;
        private final String name;
        private BiFunction<V, V, String> conflictMessageProducer;

        public StrictMap(String name) {
            super();
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
                    + (conflictMessageProducer == null ? "" : conflictMessageProducer.apply(super.get(key), value)));
            }
            if (useGeneratedShortKey) {
                if (key.contains(".")) {
                    final String shortKey = getShortName(key);
                    if (super.get(shortKey) == null) {
                        super.put(shortKey, value);
                    } else {
                        super.put(shortKey, (V) new StrictMap.Ambiguity(shortKey));
                    }
                }
            }
            return super.put(key, value);
        }

        @Override
        public V get(Object key) {
            V value = super.get(key);
            if (value == null) {
                throw new IllegalArgumentException(name + " does not contain value for " + key);
            }
            if (useGeneratedShortKey && value instanceof StrictMap.Ambiguity) {
                throw new IllegalArgumentException(((StrictMap.Ambiguity) value).getSubject() + " is ambiguous in " + name
                    + " (try using the full name including the namespace, or rename one of the entries)");
            }
            return value;
        }

        protected class Ambiguity {
            private final String subject;

            public Ambiguity(String subject) {
                this.subject = subject;
            }

            public String getSubject() {
                return subject;
            }
        }

        private String getShortName(String key) {
            final String[] keyParts = key.split("\\.");
            return keyParts[keyParts.length - 1];
        }
    }
}
