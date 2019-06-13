/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension.plugins;

import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.Data;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Optimistic Lock Light version
 * <p>Intercept on {@link Executor}.update;</p>
 * <p>Support version types: int/Integer, long/Long, java.util.Date, java.sql.Timestamp</p>
 * <p>For extra types, please define a subclass and override {@code getUpdatedVersionVal}() method.</p>
 * <br>
 * <p>How to use?</p>
 * <p>(1) Define an Entity and add {@link Version} annotation on one entity field.</p>
 * <p>(2) Add {@link OptimisticLockerInterceptor} into mybatis plugin.</p>
 * <br>
 * <p>How to work?</p>
 * <p>if update entity with version column=1:</p>
 * <p>(1) no {@link OptimisticLockerInterceptor}:</p>
 * <p>SQL: update tbl_test set name='abc' where id=100001;</p>
 * <p>(2) add {@link OptimisticLockerInterceptor}:</p>
 * <p>SQL: update tbl_test set name='abc',version=2 where id=100001 and version=1;</p>
 *
 * @author yuxiaobin
 * @since 2017/5/24
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class OptimisticLockerInterceptor implements Interceptor {

    /**
     * 乐观锁常量
     *
     * @deprecated 3.1.1 {@link Constants#MP_OPTLOCK_VERSION_ORIGINAL}
     */
    @Deprecated
    public static final String MP_OPTLOCK_VERSION_ORIGINAL = Constants.MP_OPTLOCK_VERSION_ORIGINAL;
    /**
     * 乐观锁常量
     *
     * @deprecated 3.1.1 {@link Constants#MP_OPTLOCK_VERSION_COLUMN}
     */
    @Deprecated
    public static final String MP_OPTLOCK_VERSION_COLUMN = Constants.MP_OPTLOCK_VERSION_COLUMN;
    /**
     * 乐观锁常量
     *
     * @deprecated 3.1.1 {@link Constants#MP_OPTLOCK_ET_ORIGINAL}
     */
    @Deprecated
    public static final String MP_OPTLOCK_ET_ORIGINAL = Constants.MP_OPTLOCK_ET_ORIGINAL;

    private static final String NAME_ENTITY = Constants.ENTITY;
    private static final String NAME_ENTITY_WRAPPER = Constants.WRAPPER;
    private static final String PARAM_UPDATE_METHOD_NAME = "update";
    private final Map<Class<?>, EntityField> versionFieldCache = new ConcurrentHashMap<>();
    private final Map<Class<?>, List<EntityField>> entityFieldsCache = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        if (SqlCommandType.UPDATE != ms.getSqlCommandType()) {
            return invocation.proceed();
        }
        Object param = args[1];
        if (param instanceof Map) {
            Map map = (Map) param;
            //updateById(et), update(et, wrapper);
            Object et = map.getOrDefault(NAME_ENTITY,null);
            if (et != null) {
                // entity
                String methodId = ms.getId();
                String methodName = methodId.substring(methodId.lastIndexOf(StringPool.DOT) + 1);
                Class<?> entityClass = et.getClass();
                TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
                EntityField versionField = this.getVersionField(entityClass, tableInfo);
                if (versionField == null) {
                    return invocation.proceed();
                }
                Field field = versionField.getField();
                Object originalVersionVal = versionField.getField().get(et);
                if (originalVersionVal == null) {
                    return invocation.proceed();
                }
                Object updatedVersionVal = getUpdatedVersionVal(originalVersionVal);
                if (PARAM_UPDATE_METHOD_NAME.equals(methodName)) {
                    // update(entity, wrapper)
                    // mapper.update(updEntity, QueryWrapper<>(whereEntity);
                    AbstractWrapper<?, ?, ?> ew = (AbstractWrapper<?, ?, ?>) map.getOrDefault(NAME_ENTITY_WRAPPER, null);
                    if (ew == null) {
                        UpdateWrapper<?> uw = new UpdateWrapper<>();
                        uw.eq(versionField.getColumnName(), originalVersionVal);
                        map.put(NAME_ENTITY_WRAPPER, uw);
                        field.set(et, updatedVersionVal);
                    } else {
                        ew.apply(versionField.getColumnName() + " = {0}", originalVersionVal);
                        field.set(et, updatedVersionVal);
                        //TODO: should remove version=oldval condition from aw; 0827 by k神
                    }
                    return invocation.proceed();
                } else {
                    List<EntityField> fields = entityFieldsCache.computeIfAbsent(entityClass, this::getFieldsFromClazz);
                    Map<String, Object> entityMap = new HashMap<>(fields.size());
                    for (EntityField ef : fields) {
                        Field fd = ef.getField();
                        entityMap.put(fd.getName(), fd.get(et));
                    }
                    String versionColumnName = versionField.getColumnName();
                    //update to cache
                    versionField.setColumnName(versionColumnName);
                    entityMap.put(field.getName(), updatedVersionVal);
                    entityMap.put(Constants.MP_OPTLOCK_VERSION_ORIGINAL, originalVersionVal);
                    entityMap.put(Constants.MP_OPTLOCK_VERSION_COLUMN, versionColumnName);
                    entityMap.put(Constants.MP_OPTLOCK_ET_ORIGINAL, et);
                    map.put(NAME_ENTITY, entityMap);
                    Object resultObj = invocation.proceed();
                    if (resultObj instanceof Integer) {
                        Integer effRow = (Integer) resultObj;
                        if (updatedVersionVal != null && effRow != 0) {
                            //updated version value set to entity.
                            field.set(et, updatedVersionVal);
                        }
                    }
                    return resultObj;
                }
            }
        }
        return invocation.proceed();
    }

    /**
     * This method provides the control for version value.<BR>
     * Returned value type must be the same as original one.
     *
     * @param originalVersionVal ignore
     * @return updated version val
     */
    protected Object getUpdatedVersionVal(Object originalVersionVal) {
        Class<?> versionValClass = originalVersionVal.getClass();
        if (long.class.equals(versionValClass) || Long.class.equals(versionValClass)) {
            return ((long) originalVersionVal) + 1;
        } else if (int.class.equals(versionValClass) || Integer.class.equals(versionValClass)) {
            return ((int) originalVersionVal) + 1;
        } else if (Date.class.equals(versionValClass)) {
            return new Date();
        } else if (Timestamp.class.equals(versionValClass)) {
            return new Timestamp(System.currentTimeMillis());
        } else if (LocalDateTime.class.equals(versionValClass)) {
            return LocalDateTime.now();
        }
        //not supported type, return original val.
        return originalVersionVal;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        // to do nothing
    }
    
    private EntityField getVersionField(Class<?> parameterClass, TableInfo tableInfo) {
        return versionFieldCache.computeIfAbsent(parameterClass, mapping -> getVersionFieldRegular(parameterClass, tableInfo));
    }

    /**
     * 反射检查参数类是否启动乐观锁
     *
     * @param parameterClass 实体类
     * @param tableInfo      实体数据库反射信息
     * @return ignore
     */
    private EntityField getVersionFieldRegular(Class<?> parameterClass, TableInfo tableInfo) {
        return Object.class.equals(parameterClass) ? null : ReflectionKit.getFieldList(parameterClass).stream().filter(e -> e.isAnnotationPresent(Version.class)).map(field -> {
            field.setAccessible(true);
            return new EntityField(field, true, tableInfo.getFieldList().stream().filter(e -> field.getName().equals(e.getProperty())).map(TableFieldInfo::getColumn).findFirst().orElse(null));
        }).findFirst().orElseGet(() -> this.getVersionFieldRegular(parameterClass.getSuperclass(), tableInfo));
    }

    private List<EntityField> getFieldsFromClazz(Class<?> parameterClass) {
        return ReflectionKit.getFieldList(parameterClass).stream().map(field -> {
            field.setAccessible(true);
            return new EntityField(field, field.isAnnotationPresent(Version.class));
        }).collect(Collectors.toList());
    }

    @Data
    private class EntityField {

        private Field field;
        private boolean version;
        private String columnName;

        EntityField(Field field, boolean version) {
            this.field = field;
            this.version = version;
        }

        public EntityField(Field field, boolean version, String columnName) {
            this.field = field;
            this.version = version;
            this.columnName = columnName;
        }
    }
}
