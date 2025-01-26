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
package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.segments.NormalSegmentList;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.Setter;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Optimistic Lock Light version
 * <p>Intercept on {@link Executor}.update;</p>
 * <p>Support version types: int/Integer, long/Long, java.util.Date, java.sql.Timestamp</p>
 * <p>For extra types, please define a subclass and override {@code getUpdatedVersionVal}() method.</p>
 * <br>
 * <p>How to use?</p>
 * <p>(1) Define an Entity and add {@link Version} annotation on one entity field.</p>
 * <p>(2) Add {@link OptimisticLockerInnerInterceptor} into mybatis plugin.</p>
 * <br>
 * <p>How to work?</p>
 * <p>if update entity with version column=1:</p>
 * <p>(1) no {@link OptimisticLockerInnerInterceptor}:</p>
 * <p>SQL: update tbl_test set name='abc' where id=100001;</p>
 * <p>(2) add {@link OptimisticLockerInnerInterceptor}:</p>
 * <p>SQL: update tbl_test set name='abc',version=2 where id=100001 and version=1;</p>
 *
 * @author yuxiaobin
 * @since 3.4.0
 */
@SuppressWarnings({"unchecked"})
public class OptimisticLockerInnerInterceptor implements InnerInterceptor {

    /**
     * 自定义异常处理
     */
    @Setter
    private RuntimeException exception;

    /**
     * entity类缓存
     */
    private static final Map<String, Class<?>> ENTITY_CLASS_CACHE = new ConcurrentHashMap<>();

    /**
     * 变量占位符正则
     */
    private static final Pattern PARAM_PAIRS_RE = Pattern.compile("#\\{ew\\.paramNameValuePairs\\.(" + Constants.WRAPPER_PARAM + "\\d+)\\}");

    /**
     * paramNameValuePairs存放的version值的key
     */
    private static final String UPDATED_VERSION_VAL_KEY = "#updatedVersionVal#";

    /**
     * Support wrapper mode (update(LambdaUpdateWrapper) or update(UpdateWrapper))
     */
    private final boolean wrapperMode;

    public OptimisticLockerInnerInterceptor() {
        this(false);
    }

    public OptimisticLockerInnerInterceptor(boolean wrapperMode) {
        this.wrapperMode = wrapperMode;
    }

    @Override
    public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) {
        if (SqlCommandType.UPDATE != ms.getSqlCommandType()) {
            return;
        }
        if (parameter instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) parameter;
            doOptimisticLocker(map, ms.getId());
        }
    }

    protected void doOptimisticLocker(Map<String, Object> map, String msId) {
        // updateById(et), update(et, wrapper);
        Object et = map.getOrDefault(Constants.ENTITY, null);
        if (Objects.nonNull(et)) {

            // version field
            TableFieldInfo fieldInfo = this.getVersionFieldInfo(et.getClass());
            if (null == fieldInfo) {
                return;
            }

            try {
                Field versionField = fieldInfo.getField();
                // 旧的 version 值
                Object originalVersionVal = versionField.get(et);
                if (originalVersionVal == null) {
                    if (null != exception) {
                        throw exception;
                    }
                    return;
                }
                String versionColumn = fieldInfo.getColumn();
                // 新的 version 值
                Object updatedVersionVal = this.getUpdatedVersionVal(fieldInfo.getPropertyType(), originalVersionVal);
                String methodName = msId.substring(msId.lastIndexOf(StringPool.DOT) + 1);
                if ("update".equals(methodName)) {
                    AbstractWrapper<?, ?, ?> aw = (AbstractWrapper<?, ?, ?>) map.getOrDefault(Constants.WRAPPER, null);
                    if (aw == null) {
                        UpdateWrapper<?> uw = new UpdateWrapper<>();
                        uw.eq(versionColumn, originalVersionVal);
                        map.put(Constants.WRAPPER, uw);
                    } else {
                        aw.apply(versionColumn + " = {0}", originalVersionVal);
                    }
                } else {
                    map.put(Constants.MP_OPTLOCK_VERSION_ORIGINAL, originalVersionVal);
                }
                versionField.set(et, updatedVersionVal);
            } catch (IllegalAccessException e) {
                throw ExceptionUtils.mpe(e);
            }
        } else if (wrapperMode && map.containsKey(Constants.WRAPPER)) {
            setVersionByWrapper(map, msId);
        }
    }

    protected TableFieldInfo getVersionFieldInfo(Class<?> entityClazz) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClazz);
        return (null != tableInfo && tableInfo.isWithVersion()) ? tableInfo.getVersionFieldInfo() : null;
    }

    private void setVersionByWrapper(Map<String, Object> map, String msId) {
        Object ew = map.get(Constants.WRAPPER);
        if (ew instanceof AbstractWrapper && ew instanceof Update) {
            Class<?> entityClass = ENTITY_CLASS_CACHE.get(msId);
            if (null == entityClass) {
                try {
                    final String className = msId.substring(0, msId.lastIndexOf('.'));
                    entityClass = ReflectionKit.getSuperClassGenericType(Class.forName(className), Mapper.class, 0);
                    ENTITY_CLASS_CACHE.put(msId, entityClass);
                } catch (ClassNotFoundException e) {
                    throw ExceptionUtils.mpe(e);
                }
            }

            final TableFieldInfo versionField = getVersionFieldInfo(entityClass);
            if (null == versionField) {
                return;
            }

            final String versionColumn = versionField.getColumn();
            final FieldEqFinder fieldEqFinder = new FieldEqFinder(versionColumn, (Wrapper<?>) ew);
            if (!fieldEqFinder.isPresent()) {
                return;
            }
            final Map<String, Object> paramNameValuePairs = ((AbstractWrapper<?, ?, ?>) ew).getParamNameValuePairs();
            final Object originalVersionValue = paramNameValuePairs.get(fieldEqFinder.valueKey);
            if (originalVersionValue == null) {
                return;
            }
            final Object updatedVersionVal = getUpdatedVersionVal(originalVersionValue.getClass(), originalVersionValue);
            if (originalVersionValue == updatedVersionVal) {
                return;
            }
            // 拼接新的version值
            paramNameValuePairs.put(UPDATED_VERSION_VAL_KEY, updatedVersionVal);
            ((Update<?, ?>) ew).setSql(String.format("%s = #{%s.%s}", versionColumn, "ew.paramNameValuePairs", UPDATED_VERSION_VAL_KEY));
        }
    }

    /**
     * EQ字段查找器
     */
    private static class FieldEqFinder {

        /**
         * 状态机
         */
        enum State {
            INIT,
            FIELD_FOUND,
            EQ_FOUND,
            VERSION_VALUE_PRESENT

        }

        /**
         * 字段值的key
         */
        private String valueKey;
        /**
         * 当前状态
         */
        private State state;
        /**
         * 字段名
         */
        private final String fieldName;

        public FieldEqFinder(String fieldName, Wrapper<?> wrapper) {
            this.fieldName = fieldName;
            state = State.INIT;
            find(wrapper);
        }

        /**
         * 是否已存在
         */
        public boolean isPresent() {
            return state == State.VERSION_VALUE_PRESENT;
        }

        private boolean find(Wrapper<?> wrapper) {
            Matcher matcher;
            final NormalSegmentList segments = wrapper.getExpression().getNormal();
            for (ISqlSegment segment : segments) {
                // 如果字段已找到并且当前segment为EQ
                if (state == State.FIELD_FOUND && segment == SqlKeyword.EQ) {
                    this.state = State.EQ_FOUND;
                    // 如果EQ找到并且value已找到
                } else if (state == State.EQ_FOUND
                    && (matcher = PARAM_PAIRS_RE.matcher(segment.getSqlSegment())).matches()) {
                    this.valueKey = matcher.group(1);
                    this.state = State.VERSION_VALUE_PRESENT;
                    return true;
                    // 处理嵌套
                } else if (segment instanceof Wrapper) {
                    if (find((Wrapper<?>) segment)) {
                        return true;
                    }
                    // 判断字段是否是要查找字段
                } else if (segment.getSqlSegment().equals(this.fieldName)) {
                    this.state = State.FIELD_FOUND;
                }
            }
            return false;
        }
    }

    private static class VersionFactory {

        /**
         * 存放版本号类型与获取更新后版本号的map
         */
        private static final Map<Class<?>, Function<Object, Object>> VERSION_FUNCTION_MAP = new HashMap<>();

        static {
            VERSION_FUNCTION_MAP.put(long.class, version -> (long) version + 1);
            VERSION_FUNCTION_MAP.put(Long.class, version -> (long) version + 1);
            VERSION_FUNCTION_MAP.put(int.class, version -> (int) version + 1);
            VERSION_FUNCTION_MAP.put(Integer.class, version -> (int) version + 1);
            VERSION_FUNCTION_MAP.put(Date.class, version -> new Date());
            VERSION_FUNCTION_MAP.put(Timestamp.class, version -> new Timestamp(System.currentTimeMillis()));
            VERSION_FUNCTION_MAP.put(LocalDateTime.class, version -> LocalDateTime.now());
            VERSION_FUNCTION_MAP.put(Instant.class, version -> Instant.now());
        }

        public static Object getUpdatedVersionVal(Class<?> clazz, Object originalVersionVal) {
            Function<Object, Object> versionFunction = VERSION_FUNCTION_MAP.get(clazz);
            if (versionFunction == null) {
                // not supported type, return original val.
                return originalVersionVal;
            }
            return versionFunction.apply(originalVersionVal);
        }
    }

    /**
     * This method provides the control for version value.<BR>
     * Returned value type must be the same as original one.
     *
     * @param originalVersionVal ignore
     * @return updated version val
     */
    protected Object getUpdatedVersionVal(Class<?> clazz, Object originalVersionVal) {
        return VersionFactory.getUpdatedVersionVal(clazz, originalVersionVal);
    }
}
