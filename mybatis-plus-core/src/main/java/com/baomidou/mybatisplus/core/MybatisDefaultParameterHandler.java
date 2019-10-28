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
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IdGenerator;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
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
        //检查 parameterObject
        if (null == parameterObject
            || ReflectionKit.isPrimitiveOrWrapper(parameterObject.getClass())
            || parameterObject.getClass() == String.class) {
            return null;
        }
        /* 只处理插入或更新操作 */
        if (SqlCommandType.INSERT == ms.getSqlCommandType() || SqlCommandType.UPDATE == ms.getSqlCommandType()) {
            Collection<Object> parameters = getParameters(parameterObject);
            if (null != parameters) {
                List<Object> objList = new ArrayList<>();
                for (Object parameter : parameters) {
                    TableInfo tableInfo = TableInfoHelper.getTableInfo(parameter.getClass());
                    if (null != tableInfo) {
                        handlerFill(ms, populateKeys(ms, tableInfo, parameter));
                        objList.add(parameter);
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
                    Map<?, ?> map = (Map<?, ?>) parameterObject;
                    if (map.containsKey(Constants.ENTITY)) {
                        Object et = map.get(Constants.ENTITY);
                        if (et != null) {
                            if (et instanceof Map) {
                                Map<?, ?> realEtMap = (Map<?, ?>) et;
                                if (realEtMap.containsKey(Constants.MP_OPTLOCK_ET_ORIGINAL)) {
                                    tableInfo = TableInfoHelper.getTableInfo(realEtMap.get(Constants.MP_OPTLOCK_ET_ORIGINAL).getClass());
                                }
                            } else {
                                tableInfo = TableInfoHelper.getTableInfo(et.getClass());
                            }
                        }
                    }
                } else {
                    tableInfo = TableInfoHelper.getTableInfo(parameterObject.getClass());
                }
                handlerFill(ms, populateKeys(ms, tableInfo, parameterObject));
                return parameterObject;
            }
        }
        return parameterObject;
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
     * @param ms MappedStatement
     * @param tableInfo         数据库表反射信息
     * @param parameterObject   插入数据库对象
     * @return Object
     */
    protected static MetaObject populateKeys(MappedStatement ms, TableInfo tableInfo, Object parameterObject) {
        MetaObject metaObject = ms.getConfiguration().newMetaObject(parameterObject);
        // 填充主键
        if (tableInfo != null && SqlCommandType.INSERT == ms.getSqlCommandType()) {
            if (!StringUtils.isBlank(tableInfo.getKeyProperty())
                && null != tableInfo.getIdType() && tableInfo.getIdType().getKey() >= 3) {
                IdGenerator idGenerator = GlobalConfigUtils.getGlobalConfig(tableInfo.getConfiguration()).getIdGenerator();
                Object idValue = metaObject.getValue(tableInfo.getKeyProperty());
                /* 自定义 ID */
                if (StringUtils.checkValNull(idValue)) {
                    if (tableInfo.getIdType() == IdType.ID_WORKER) {
                        metaObject.setValue(tableInfo.getKeyProperty(), idGenerator.nextId(parameterObject));
                    } else if (tableInfo.getIdType() == IdType.ID_WORKER_STR) {
                        metaObject.setValue(tableInfo.getKeyProperty(), String.valueOf(idGenerator.nextId(parameterObject)));
                    } else if (tableInfo.getIdType() == IdType.UUID) {
                        metaObject.setValue(tableInfo.getKeyProperty(), IdWorker.get32UUID());
                    }
                }
            }
        }
        return metaObject;
    }

    /**
     * 填充处理
     *
     * @param ms         MappedStatement
     * @param metaObject metaObject {@link #populateKeys(MappedStatement, TableInfo, Object)}
     */
    protected static void handlerFill(MappedStatement ms, MetaObject metaObject) {
        MetaObjectHandler metaObjectHandler = GlobalConfigUtils.getMetaObjectHandler(ms.getConfiguration());
        if (metaObjectHandler != null) {
            if (metaObjectHandler.openInsertFill() && SqlCommandType.INSERT == ms.getSqlCommandType()) {
                // 插入填充
                metaObjectHandler.insertFill(metaObject);
            } else if (metaObjectHandler.openUpdateFill() && SqlCommandType.UPDATE == ms.getSqlCommandType()) {
                // 更新填充
                metaObjectHandler.updateFill(metaObject);
            }
        }
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
