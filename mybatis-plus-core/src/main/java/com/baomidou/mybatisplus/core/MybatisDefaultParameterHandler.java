/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.*;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * <p>
 * 自定义 ParameterHandler 重装构造函数，填充插入方法主键 ID
 * </p>
 * TODO: 3.0 优化注解填充 ?   怎么优化?
 *
 * @author hubin
 * @since 2016-03-11
 */
public class MybatisDefaultParameterHandler extends DefaultParameterHandler {

    /**
     * @see BoundSql
     */
    private static final Field ADDITIONAL_PARAMETERS_FIELD = getAdditionalParametersField();
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
     * 反射获取BoundSql中additionalParameters参数字段
     *
     * @return additionalParameters 字段
     * @see BoundSql
     */
    private static Field getAdditionalParametersField() {
        try {
            Field additionalParametersField = BoundSql.class.getDeclaredField("additionalParameters");
            additionalParametersField.setAccessible(true);
            return additionalParametersField;
        } catch (NoSuchFieldException e) {
            // ignored, Because it will never happen.
        }
        return null;
    }

    /**
     * <p>
     * 批量（填充主键 ID）
     * </p>
     *
     * @param ms MappedStatement
     * @param parameterObject 插入数据库对象
     * @return
     */
    protected static Object processBatch(MappedStatement ms, Object parameterObject) {
        //检查 parameterObject
        if (null == parameterObject) {
            return null;
        }
        // 全局配置是否配置填充器
        MetaObjectHandler metaObjectHandler = GlobalConfigUtils.getMetaObjectHandler(ms.getConfiguration());
        boolean isFill = false;
        boolean isInsert = false;
        /* 只处理插入或更新操作 */
        if (ms.getSqlCommandType() == SqlCommandType.INSERT) {
            isFill = true;
            isInsert = true;
        } else if (ms.getSqlCommandType() == SqlCommandType.UPDATE &&
            metaObjectHandler != null && metaObjectHandler.openUpdateFill()) {
            isFill = true;
        }
        if (isFill) {
            Collection<Object> parameters = getParameters(parameterObject);
            if (null != parameters) {
                List<Object> objList = new ArrayList<>();
                for (Object parameter : parameters) {
                    TableInfo tableInfo = TableInfoHelper.getTableInfo(parameter.getClass());
                    if (null != tableInfo) {
                        objList.add(populateKeys(metaObjectHandler, tableInfo, ms, parameter, isInsert));
                    } else {
                        /*
                         * 非表映射类不处理
                         */
                        objList.add(parameter);
                    }
                }
                return objList;
            } else {
                TableInfo tableInfo = null;
                if (parameterObject instanceof Map) {
                    Map map = (Map) parameterObject;
                    if (map.containsKey(Constants.ENTITY)) {
                        Object et = map.get(Constants.ENTITY);
                        if (et != null) {
                            if (et instanceof Map) {
                                Map realEtMap = (Map) et;
                                if (realEtMap.containsKey("MP_OPTLOCK_ET_ORIGINAL")) {
                                    //refer to OptimisticLockerInterceptor.MP_OPTLOCK_ET_ORIGINAL
                                    tableInfo = TableInfoHelper.getTableInfo(realEtMap.get("MP_OPTLOCK_ET_ORIGINAL").getClass());
                                }
                            } else {
                                tableInfo = TableInfoHelper.getTableInfo(et.getClass());
                            }
                        }
                    }
                } else {
                    tableInfo = TableInfoHelper.getTableInfo(parameterObject.getClass());
                }
                return populateKeys(metaObjectHandler, tableInfo, ms, parameterObject, isInsert);
            }
        }
        return parameterObject;
    }

    /**
     * <p>
     * 处理正常批量插入逻辑
     * </p>
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
     * <p>
     * 自定义元对象填充控制器
     * </p>
     *
     * @param metaObjectHandler 元数据填充处理器
     * @param tableInfo         数据库表反射信息
     * @param ms                MappedStatement
     * @param parameterObject   插入数据库对象
     * @return Object
     */
    protected static Object populateKeys(MetaObjectHandler metaObjectHandler, TableInfo tableInfo,
                                         MappedStatement ms, Object parameterObject, boolean isInsert) {
        if (null == tableInfo) {
            /* 不处理 */
            return parameterObject;
        }
        /* 自定义元对象填充控制器 */
        MetaObject metaObject = ms.getConfiguration().newMetaObject(parameterObject);
        // 填充主键
        if (isInsert && !StringUtils.isEmpty(tableInfo.getKeyProperty())
            && null != tableInfo.getIdType() && tableInfo.getIdType().getKey() >= 3) {
            Object idValue = metaObject.getValue(tableInfo.getKeyProperty());
            /* 自定义 ID */
            if (StringUtils.checkValNull(idValue)) {
                if (tableInfo.getIdType() == IdType.ID_WORKER) {
                    metaObject.setValue(tableInfo.getKeyProperty(), IdWorker.getId());
                } else if (tableInfo.getIdType() == IdType.ID_WORKER_STR) {
                    metaObject.setValue(tableInfo.getKeyProperty(), IdWorker.getIdStr());
                } else if (tableInfo.getIdType() == IdType.UUID) {
                    metaObject.setValue(tableInfo.getKeyProperty(), IdWorker.get32UUID());
                }
            }
        }
        if (metaObjectHandler != null) {
            if (isInsert && metaObjectHandler.openInsertFill()) {
                // 插入填充
                metaObjectHandler.insertFill(metaObject);
            } else if (!isInsert) {
                // 更新填充
                metaObjectHandler.updateFill(metaObject);
            }
        }
        return metaObject.getOriginalObject();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setParameters(PreparedStatement ps) {
        // 反射获取动态参数
        Map<String, Object> additionalParameters = Collections.emptyMap();
        try {
            additionalParameters = (Map<String, Object>) ADDITIONAL_PARAMETERS_FIELD.get(boundSql);
        } catch (IllegalAccessException e) {
            // ignored, Because it will never happen.
        }
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = additionalParameters.get(propertyName);
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
