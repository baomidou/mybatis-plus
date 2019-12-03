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
package com.baomidou.mybatisplus.core;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * 自定义 ParameterHandler 重装构造函数，填充插入方法主键 ID
 *
 * @author hubin
 * @since 2016-03-11
 */
public class MybatisDefaultParameterHandler extends DefaultParameterHandler {

    private final TypeHandlerRegistry typeHandlerRegistry;
    private final MappedStatement mappedStatement;
    private final Object parameterObject;
    private final BoundSql boundSql;
    private final Configuration configuration;

    public MybatisDefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        super(mappedStatement, processBatch(mappedStatement, parameterObject), boundSql);
        this.mappedStatement = mappedStatement;
        this.configuration = mappedStatement.getConfiguration();
        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        this.parameterObject = parameterObject;
        this.boundSql = boundSql;
    }

    /**
     * 批量（填充主键 ID）
     *
     * @param ms              MappedStatement
     * @param parameterObject 插入数据库对象
     * @return ignore
     */
    protected static Object processBatch(MappedStatement ms, Object parameterObject) {
        /* 只处理插入或更新操作 */
        if (parameterObject != null
            && (SqlCommandType.INSERT == ms.getSqlCommandType() || SqlCommandType.UPDATE == ms.getSqlCommandType())) {
            //检查 parameterObject
            if (ReflectionKit.isPrimitiveOrWrapper(parameterObject.getClass())
                || parameterObject.getClass() == String.class) {
                return parameterObject;
            }
            Collection<Object> parameters = getParameters(parameterObject);
            if (null != parameters) {
                // 感觉这里可以稍微优化一下，理论上都是同一个.
                parameters.stream().filter(Objects::nonNull).forEach(obj -> getTableInfo(obj).ifPresent(tableInfo -> process(ms, obj, tableInfo)));
            } else {
                getTableInfo(parameterObject).ifPresent(tableInfo -> process(ms, parameterObject, tableInfo));
            }
        }
        return parameterObject;
    }

    private static Optional<TableInfo> getTableInfo(Object parameterObject) {
        TableInfo tableInfo = null;
        if (parameterObject instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) parameterObject;
            String entityKey = Constants.ENTITY;
            if (map.containsKey(entityKey)) {
                Object et = map.get(entityKey);
                if (et != null) {
                    if (et instanceof Map) {
                        Map<?, ?> realEtMap = (Map<?, ?>) et;
                        String optLockKey = Constants.MP_OPTLOCK_ET_ORIGINAL;
                        if (realEtMap.containsKey(optLockKey)) {
                            tableInfo = TableInfoHelper.getTableInfo(realEtMap.get(optLockKey).getClass());
                        }
                    } else {
                        tableInfo = TableInfoHelper.getTableInfo(et.getClass());
                    }
                }
            }
        } else {
            tableInfo = TableInfoHelper.getTableInfo(parameterObject.getClass());
        }
        return Optional.ofNullable(tableInfo);
    }

    private static void process(MappedStatement ms, Object parameterObject, TableInfo tableInfo) {
        MetaObject metaObject = ms.getConfiguration().newMetaObject(parameterObject);
        if (SqlCommandType.INSERT == ms.getSqlCommandType()) {
            insertFill(metaObject, tableInfo);
            populateKeys(tableInfo, metaObject, parameterObject);
        } else {
            updateFill(metaObject, tableInfo);
        }
    }


    /**
     * 处理正常批量插入逻辑
     * <p>
     * org.apache.ibatis.session.defaults.DefaultSqlSession$StrictMap 该类方法
     * wrapCollection 实现 StrictMap 封装逻辑
     * </p>
     *
     * @param parameter 插入数据库对象
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected static Collection<Object> getParameters(Object parameter) {
        Collection<Object> parameters = null;
        if (parameter instanceof Collection) {
            parameters = (Collection) parameter;
        } else if (parameter instanceof Map) {
            Map parameterMap = (Map) parameter;
            if (parameterMap.containsKey("collection")) {
                parameters = (Collection) parameterMap.get("collection");
            } else if (parameterMap.containsKey("list")) {
                parameters = (List) parameterMap.get("list");
            } else if (parameterMap.containsKey("array")) {
                parameters = Arrays.asList((Object[]) parameterMap.get("array"));
            }
        }
        return parameters;
    }

    /**
     * 自定义元对象填充控制器
     *
     * @param tableInfo       数据库表反射信息
     * @param parameterObject 插入数据库对象
     * @return Object
     */
    protected static void populateKeys(TableInfo tableInfo, MetaObject metaObject, Object parameterObject) {
        // 填充主键
        if (StringUtils.isNotBlank(tableInfo.getKeyProperty()) && null != tableInfo.getIdType() && tableInfo.getIdType().getKey() >= 3) {
            GlobalConfigUtils.getGlobalConfig(tableInfo.getConfiguration()).getIdGenerator().ifPresent(idGenerator -> {
                Object idValue = metaObject.getValue(tableInfo.getKeyProperty());
                /* 自定义 ID */
                if (StringUtils.checkValNull(idValue)) {
                    if (IdType.ASSIGN_ID.getKey() == tableInfo.getIdType().getKey()) {
                        // 应该只有数值型和字符串的区别了.
                        if (Number.class.isAssignableFrom(tableInfo.getKeyType())) {
                            metaObject.setValue(tableInfo.getKeyProperty(), idGenerator.generateId(parameterObject));
                        } else {
                            metaObject.setValue(tableInfo.getKeyProperty(), idGenerator.generateId(parameterObject).toString());
                        }
                    } else if (IdType.UUID.getKey() == tableInfo.getIdType().getKey()) {
                        metaObject.setValue(tableInfo.getKeyProperty(), idGenerator.generateUUID(parameterObject));
                    }
                }
            });
        }
    }

    protected static void insertFill(MetaObject metaObject, TableInfo tableInfo) {
        GlobalConfigUtils.getMetaObjectHandler(tableInfo.getConfiguration()).ifPresent(metaObjectHandler -> {
            if (metaObjectHandler.openInsertFill()) {
                if (tableInfo.isWithInsertFill()) {
                    metaObjectHandler.insertFill(metaObject);
                } else {
                    // 兼容旧操作 id类型为input或none的要用填充器处理一下
                    if (metaObjectHandler.compatibleFillId()) {
                        String keyProperty = tableInfo.getKeyProperty();
                        if (StringUtils.isNotBlank(keyProperty)) {
                            Object value = metaObject.getValue(keyProperty);
                            if (value == null && (IdType.NONE == tableInfo.getIdType() || IdType.INPUT == tableInfo.getIdType())) {
                                metaObjectHandler.insertFill(metaObject);
                            }
                        }
                    }
                }
            }
        });
    }

    protected static void updateFill(MetaObject metaObject, TableInfo tableInfo) {
        GlobalConfigUtils.getMetaObjectHandler(tableInfo.getConfiguration()).ifPresent(metaObjectHandler -> {
            if (metaObjectHandler.openUpdateFill() && tableInfo.isWithUpdateFill()) {
                metaObjectHandler.updateFill(metaObject);
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setParameters(PreparedStatement ps) {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    JdbcType jdbcType = parameterMapping.getJdbcType();
                    if (value == null && jdbcType == null) {
                        jdbcType = configuration.getJdbcTypeForNull();
                    }
                    try {
                        typeHandler.setParameter(ps, i + 1, value, jdbcType);
                    } catch (TypeException | SQLException e) {
                        throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
                    }
                }
            }
        }
    }
}
