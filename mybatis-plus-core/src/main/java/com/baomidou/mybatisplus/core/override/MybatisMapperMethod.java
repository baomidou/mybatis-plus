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
package com.baomidou.mybatisplus.core.override;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 从  {@link MapperMethod} copy 过来 </br>
 * <p> 不要内部类 ParamMap </p>
 * <p> 不要内部类 SqlCommand </p>
 * <p> 不要内部类 MethodSignature </p>
 *
 * @author miemie
 * @since 2018-06-09
 */
public class MybatisMapperMethod {
    private final MapperMethod.SqlCommand command;
    private final MapperMethod.MethodSignature method;
    private final Map<Integer, String> wrapperParamsAliasNameMap;

    /**
     * execute 级共享数据持有者，生命周期覆盖 MyBatis 拦截器全部生命周期。
     * 由 {@code MybatisPlusInterceptor} 通过 {@link #getExecuteSharedData()} 读取并传入拦截器链。
     *
     * @since 3.5.18
     */
    private static final ThreadLocal<Map<String, Object>> EXECUTE_SHARED_DATA = new ThreadLocal<>();

    /**
     * 获取当前 execute 周期内的共享数据 Map。
     * 供 {@code MybatisPlusInterceptor} 调用。
     */
    public static Map<String, Object> getExecuteSharedData() {
        return EXECUTE_SHARED_DATA.get();
    }

    public MybatisMapperMethod(Class<?> mapperInterface, Method method, Configuration config) {
        wrapperParamsAliasNameMap = this.getWrapperParamsAliasNameMap(method);
        this.command = new MapperMethod.SqlCommand(config, mapperInterface, method);
        this.method = new MapperMethod.MethodSignature(config, mapperInterface, method);
    }

    public Map<Integer, String> getWrapperParamsAliasNameMap(Method method) {
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        Class<?>[] parameterTypes = method.getParameterTypes();
        int paramCount = method.getParameterCount();
        final Map<Integer, String> map = new HashMap<>();
        // get names from @Param annotations
        for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
            for (Annotation annotation : paramAnnotations[paramIndex]) {
                Class<?> parameterType = parameterTypes[paramIndex];
                if (annotation instanceof Param && Wrapper.class.isAssignableFrom(parameterType)) {
                    map.put(paramIndex, ((Param) annotation).value());
                    break;
                }
            }
        }
        return map.isEmpty() ? null : Collections.unmodifiableMap(map);
    }

    public Object execute(SqlSession sqlSession, Object[] args) {
        Object result;
        EXECUTE_SHARED_DATA.set(new HashMap<>());
        try {
            switch (command.getType()) {
                case INSERT: {
                    Object param = this.convertArgsToSqlCommandParam(args);
                    result = rowCountResult(sqlSession.insert(command.getName(), param));
                    break;
                }
                case UPDATE: {
                    Object param = this.convertArgsToSqlCommandParam(args);
                    result = rowCountResult(sqlSession.update(command.getName(), param));
                    break;
                }
                case DELETE: {
                    Object param = this.convertArgsToSqlCommandParam(args);
                    result = rowCountResult(sqlSession.delete(command.getName(), param));
                    break;
                }
                case SELECT:
                    if (method.returnsVoid() && method.hasResultHandler()) {
                        executeWithResultHandler(sqlSession, args);
                        result = null;
                    } else if (method.returnsMany()) {
                        result = executeForMany(sqlSession, args);
                    } else if (method.returnsMap()) {
                        result = executeForMap(sqlSession, args);
                    } else if (method.returnsCursor()) {
                        result = executeForCursor(sqlSession, args);
                    } else {
                        Object param = this.convertArgsToSqlCommandParam(args);
                        SelectReturnTypeHandler handler = SelectReturnTypeHandlerRegistry.findHandler(method.getReturnType());
                        if (handler != null && handler.selectMethod() == SelectReturnTypeHandler.SelectMethod.SELECT_LIST) {
                            result = sqlSession.selectList(command.getName(), param);
                        } else {
                            result = sqlSession.selectOne(command.getName(), param);
                            if (method.returnsOptional()
                                && (result == null || !method.getReturnType().equals(result.getClass()))) {
                                result = Optional.ofNullable(result);
                            }
                        }
                        // transform：在拦截器链全部执行完毕后进行类型转换
                        if (handler != null) {
                            result = handler.transform(result, args, method, EXECUTE_SHARED_DATA.get());
                        }
                    }
                    break;
                case FLUSH:
                    result = sqlSession.flushStatements();
                    break;
                default:
                    throw new BindingException("Unknown execution method for: " + command.getName());
            }
        } finally {
            EXECUTE_SHARED_DATA.remove();
        }
        if (result == null && method.getReturnType().isPrimitive() && !method.returnsVoid()) {
            throw new BindingException("Mapper method '" + command.getName()
                + " attempted to return null from a method with a primitive return type (" + method.getReturnType() + ").");
        }
        return result;
    }

    private Object rowCountResult(int rowCount) {
        final Object result;
        if (method.returnsVoid()) {
            result = null;
        } else if (Integer.class.equals(method.getReturnType()) || Integer.TYPE.equals(method.getReturnType())) {
            result = rowCount;
        } else if (Long.class.equals(method.getReturnType()) || Long.TYPE.equals(method.getReturnType())) {
            result = (long) rowCount;
        } else if (Boolean.class.equals(method.getReturnType()) || Boolean.TYPE.equals(method.getReturnType())) {
            result = rowCount > 0;
        } else {
            throw new BindingException("Mapper method '" + command.getName() + "' has an unsupported return type: " + method.getReturnType());
        }
        return result;
    }

    private void executeWithResultHandler(SqlSession sqlSession, Object[] args) {
        MappedStatement ms = sqlSession.getConfiguration().getMappedStatement(command.getName());
        if (!StatementType.CALLABLE.equals(ms.getStatementType())
            && void.class.equals(ms.getResultMaps().get(0).getType())) {
            throw new BindingException("method " + command.getName()
                + " needs either a @ResultMap annotation, a @ResultType annotation,"
                + " or a resultType attribute in XML so a ResultHandler can be used as a parameter.");
        }
        Object param = this.convertArgsToSqlCommandParam(args);
        if (method.hasRowBounds()) {
            RowBounds rowBounds = method.extractRowBounds(args);
            sqlSession.select(command.getName(), param, rowBounds, method.extractResultHandler(args));
        } else {
            sqlSession.select(command.getName(), param, method.extractResultHandler(args));
        }
    }

    private <E> Object executeForMany(SqlSession sqlSession, Object[] args) {
        List<E> result;
        Object param = this.convertArgsToSqlCommandParam(args);
        if (method.hasRowBounds()) {
            RowBounds rowBounds = method.extractRowBounds(args);
            result = sqlSession.selectList(command.getName(), param, rowBounds);
        } else {
            result = sqlSession.selectList(command.getName(), param);
        }
        // issue #510 Collections & arrays support
        if (!method.getReturnType().isAssignableFrom(result.getClass())) {
            if (method.getReturnType().isArray()) {
                return convertToArray(result);
            } else {
                return convertToDeclaredCollection(sqlSession.getConfiguration(), result);
            }
        }
        return result;
    }

    private <T> Cursor<T> executeForCursor(SqlSession sqlSession, Object[] args) {
        Cursor<T> result;
        Object param = this.convertArgsToSqlCommandParam(args);
        if (method.hasRowBounds()) {
            RowBounds rowBounds = method.extractRowBounds(args);
            result = sqlSession.selectCursor(command.getName(), param, rowBounds);
        } else {
            result = sqlSession.selectCursor(command.getName(), param);
        }
        return result;
    }

    private <E> Object convertToDeclaredCollection(Configuration config, List<E> list) {
        Object collection = config.getObjectFactory().create(method.getReturnType());
        MetaObject metaObject = config.newMetaObject(collection);
        metaObject.addAll(list);
        return collection;
    }

    @SuppressWarnings("unchecked")
    private <E> Object convertToArray(List<E> list) {
        Class<?> arrayComponentType = method.getReturnType().getComponentType();
        Object array = Array.newInstance(arrayComponentType, list.size());
        if (!arrayComponentType.isPrimitive()) {
            return list.toArray((E[]) array);
        }
        for (int i = 0; i < list.size(); i++) {
            Array.set(array, i, list.get(i));
        }
        return array;
    }

    private <K, V> Map<K, V> executeForMap(SqlSession sqlSession, Object[] args) {
        Map<K, V> result;
        Object param = this.convertArgsToSqlCommandParam(args);
        if (method.hasRowBounds()) {
            RowBounds rowBounds = method.extractRowBounds(args);
            result = sqlSession.selectMap(command.getName(), param, method.getMapKey(), rowBounds);
        } else {
            result = sqlSession.selectMap(command.getName(), param, method.getMapKey());
        }
        return result;
    }

    private Object convertArgsToSqlCommandParam(Object[] args) {
        if (args == null) {
            return null;
        }
        if (null != wrapperParamsAliasNameMap) {
            for (Map.Entry<Integer, String> entry : wrapperParamsAliasNameMap.entrySet()) {
                Object arg = args[entry.getKey()];
                if (arg instanceof AbstractWrapper) {
                    AbstractWrapper<?, ?, ?> wrapper = (AbstractWrapper<?, ?, ?>) arg;
                    String paramAlias = entry.getValue();
                    if (!paramAlias.equals(wrapper.getParamAlias())) {
                        wrapper.setParamAlias(paramAlias);
                    }
                }
            }
        }
        return method.convertArgsToSqlCommandParam(args);
    }
}
