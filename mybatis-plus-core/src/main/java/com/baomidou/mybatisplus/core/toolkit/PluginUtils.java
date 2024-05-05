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
package com.baomidou.mybatisplus.core.toolkit;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 插件工具类
 *
 * @author TaoYu , hubin
 * @since 2017-06-20
 */
public abstract class PluginUtils {

    /**
     * 缓存内置的插件对象反射信息
     *
     * @since 3.5.3.2
     */
    public static final DefaultReflectorFactory DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();

    public static final String DELEGATE_BOUNDSQL_SQL = "delegate.boundSql.sql";

    /**
     * 获得真正的处理对象,可能多层代理.
     */
    @SuppressWarnings("unchecked")
    public static <T> T realTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            Plugin plugin = (Plugin) Proxy.getInvocationHandler(target);
            MetaObject metaObject = getMetaObject(plugin);
            return realTarget(metaObject.getValue("target"));
        }
        return (T) target;
    }

    /**
     * 获取对象元数据信息
     *
     * @param object 参数
     * @return 元数据信息
     * @since 3.5.3
     */
    public static MetaObject getMetaObject(Object object) {
        return MetaObject.forObject(object, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
    }

    /**
     * 给 BoundSql 设置 additionalParameters
     *
     * @param boundSql             BoundSql
     * @param additionalParameters additionalParameters
     */
    public static void setAdditionalParameter(BoundSql boundSql, Map<String, Object> additionalParameters) {
        additionalParameters.forEach(boundSql::setAdditionalParameter);
    }

    public static MPBoundSql mpBoundSql(BoundSql boundSql) {
        return new MPBoundSql(boundSql);
    }

    public static MPStatementHandler mpStatementHandler(StatementHandler statementHandler) {
        statementHandler = realTarget(statementHandler);
        MetaObject object = getMetaObject(statementHandler);
        return new MPStatementHandler(getMetaObject(object.getValue("delegate")));
    }

    /**
     * {@link org.apache.ibatis.executor.statement.BaseStatementHandler}
     */
    public static class MPStatementHandler {
        private final MetaObject statementHandler;

        MPStatementHandler(MetaObject statementHandler) {
            this.statementHandler = statementHandler;
        }

        public ParameterHandler parameterHandler() {
            return get("parameterHandler");
        }

        public MappedStatement mappedStatement() {
            return get("mappedStatement");
        }

        public Executor executor() {
            return get("executor");
        }

        public MPBoundSql mPBoundSql() {
            return new MPBoundSql(boundSql());
        }

        public BoundSql boundSql() {
            return get("boundSql");
        }

        public Configuration configuration() {
            return get("configuration");
        }

        @SuppressWarnings("unchecked")
        private <T> T get(String property) {
            return (T) statementHandler.getValue(property);
        }
    }

    /**
     * {@link BoundSql}
     */
    public static class MPBoundSql {

        private final MetaObject boundSql;
        private final BoundSql delegate;

        MPBoundSql(BoundSql boundSql) {
            this.delegate = boundSql;
            this.boundSql = getMetaObject(boundSql);
        }

        public String sql() {
            return delegate.getSql();
        }

        public void sql(String sql) {
            boundSql.setValue("sql", sql);
        }

        public List<ParameterMapping> parameterMappings() {
            List<ParameterMapping> parameterMappings = delegate.getParameterMappings();
            return new ArrayList<>(parameterMappings);
        }

        public void parameterMappings(List<ParameterMapping> parameterMappings) {
            boundSql.setValue("parameterMappings", Collections.unmodifiableList(parameterMappings));
        }

        public Object parameterObject() {
            return get("parameterObject");
        }

        public Map<String, Object> additionalParameters() {
            return get("additionalParameters");
        }

        @SuppressWarnings("unchecked")
        private <T> T get(String property) {
            return (T) boundSql.getValue(property);
        }
    }
}
