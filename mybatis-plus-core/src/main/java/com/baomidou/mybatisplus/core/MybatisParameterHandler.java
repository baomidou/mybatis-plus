/*
 * Copyright (c) 2011-2022, baomidou (jobob@qq.com).
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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.SimpleTypeRegistry;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 自定义 ParameterHandler 重装构造函数，填充插入方法主键 ID
 *
 * @author nieqiuqiu 2020/6/5
 * @since 3.4.0
 */
public class MybatisParameterHandler implements ParameterHandler {

    private final TypeHandlerRegistry typeHandlerRegistry;
    private final MappedStatement mappedStatement;
    private final Object parameterObject;
    private final BoundSql boundSql;
    private final Configuration configuration;
    private final SqlCommandType sqlCommandType;

    public MybatisParameterHandler(MappedStatement mappedStatement, Object parameter, BoundSql boundSql) {
        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        this.mappedStatement = mappedStatement;
        this.boundSql = boundSql;
        this.configuration = mappedStatement.getConfiguration();
        this.sqlCommandType = mappedStatement.getSqlCommandType();
        this.parameterObject = processParameter(parameter);
    }

    public Object processParameter(Object parameter) {
        /* 只处理插入或更新操作 */
        if (parameter != null
            && (SqlCommandType.INSERT == this.sqlCommandType || SqlCommandType.UPDATE == this.sqlCommandType)) {
            //检查 parameterObject
            if (SimpleTypeRegistry.isSimpleType(parameter.getClass())) {
                return parameter;
            }
            process(parameter);
        }
        return parameter;
    }

    @Override
    public Object getParameterObject() {
        return this.parameterObject;
    }

    private void process(Object parameter) {
        if (parameter != null) {
            if (parameter instanceof Collection) {
                processCollection(parameter);
            } else if (parameter instanceof Map) {
                // 尝试提取参数进行填充，如果是多参数时，在使用注解时，请注意使用et coll collection list array进行声明
                Map<?, ?> map = (Map<?, ?>) parameter;
                if (map.containsKey(Constants.ENTITY)) {
                    doProcess(map.get(Constants.ENTITY));
                }
                if (map.containsKey("collection")) {
                    processCollection(map.get("collection"));
                }
                if (map.containsKey(Constants.COLL)) {
                    // 兼容逻辑删除对象填充，这里的集合字段后面重构的时候应该和原生保持一致，使用collection
                    processCollection(map.get(Constants.COLL));
                }
                if (map.containsKey(Constants.LIST)) {
                    processCollection(map.get(Constants.LIST));
                }
                if (map.containsKey(Constants.ARRAY)) {
                    processCollection(map.get(Constants.ARRAY));
                }
            } else {
                doProcess(parameter);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processCollection(Object value) {
        if (value == null) {
            return;
        }
        Collection<Object> collection = null;
        // 只处理array和collection
        if (value.getClass().isArray()) {
            collection = Arrays.asList((Object[]) value);
        } else if (Collection.class.isAssignableFrom(value.getClass())) {
            collection = (Collection<Object>) value;
        }
        if (collection != null) {
            collection.forEach(this::doProcess);
        }
    }

    private void doProcess(Object entity) {
        if (entity == null) {
            return;
        }
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entity.getClass());
        if (tableInfo != null) {
            // 到这里就应该转换到实体参数对象了,因为填充和ID处理都是针对实体对象处理的,不用传递原参数对象下去.
            MetaObject metaObject = this.configuration.newMetaObject(entity);
            if (SqlCommandType.INSERT == this.sqlCommandType) {
                populateKeys(tableInfo, metaObject, entity);
                insertFill(metaObject, tableInfo);
            } else {
                updateFill(metaObject, tableInfo);
            }
        }
    }

    protected void populateKeys(TableInfo tableInfo, MetaObject metaObject, Object entity) {
        final IdType idType = tableInfo.getIdType();
        final String keyProperty = tableInfo.getKeyProperty();
        if (StringUtils.isNotBlank(keyProperty) && null != idType && idType.getKey() >= 3) {
            final IdentifierGenerator identifierGenerator = GlobalConfigUtils.getGlobalConfig(this.configuration).getIdentifierGenerator();
            Object idValue = metaObject.getValue(keyProperty);
            if (identifierGenerator.assignId(idValue)) {
                if (idType.getKey() == IdType.ASSIGN_ID.getKey()) {
                    Class<?> keyType = tableInfo.getKeyType();
                    if (Number.class.isAssignableFrom(keyType)) {
                        Number id = identifierGenerator.nextId(entity);
                        if (keyType == id.getClass()) {
                            metaObject.setValue(keyProperty, id);
                        } else if (Integer.class == keyType) {
                            metaObject.setValue(keyProperty, id.intValue());
                        } else if (Long.class == keyType) {
                            metaObject.setValue(keyProperty, id.longValue());
                        } else if (BigDecimal.class.isAssignableFrom(keyType)) {
                            metaObject.setValue(keyProperty, new BigDecimal(id.longValue()));
                        } else if (BigInteger.class.isAssignableFrom(keyType)) {
                            metaObject.setValue(keyProperty, new BigInteger(id.toString()));
                        } else {
                            throw new MybatisPlusException("Key type '" + keyType + "' not supported");
                        }
                    } else if (String.class.isAssignableFrom(keyType)) {
                        metaObject.setValue(keyProperty, identifierGenerator.nextId(entity).toString());
                    } else {
                        metaObject.setValue(keyProperty, identifierGenerator.nextId(entity));
                    }
                } else if (idType.getKey() == IdType.ASSIGN_UUID.getKey()) {
                    metaObject.setValue(keyProperty, identifierGenerator.nextUUID(entity));
                }
            }
        }
    }


    protected void insertFill(MetaObject metaObject, TableInfo tableInfo) {
        GlobalConfigUtils.getMetaObjectHandler(this.configuration).ifPresent(metaObjectHandler -> {
            if (metaObjectHandler.openInsertFill() && tableInfo.isWithInsertFill()) {
                metaObjectHandler.insertFill(metaObject);
            }
        });
    }

    protected void updateFill(MetaObject metaObject, TableInfo tableInfo) {
        GlobalConfigUtils.getMetaObjectHandler(this.configuration).ifPresent(metaObjectHandler -> {
            if (metaObjectHandler.openUpdateFill() && tableInfo.isWithUpdateFill()) {
                metaObjectHandler.updateFill(metaObject);
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setParameters(PreparedStatement ps) {
        ErrorContext.instance().activity("setting parameters").object(this.mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = this.boundSql.getParameterMappings();
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (this.boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
                        value = this.boundSql.getAdditionalParameter(propertyName);
                    } else if (this.parameterObject == null) {
                        value = null;
                    } else if (this.typeHandlerRegistry.hasTypeHandler(this.parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = this.configuration.newMetaObject(this.parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    JdbcType jdbcType = parameterMapping.getJdbcType();
                    if (value == null && jdbcType == null) {
                        jdbcType = this.configuration.getJdbcTypeForNull();
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
