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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
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
import org.apache.ibatis.type.*;

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
            Collection<Object> parameters = getParameters(parameter);
            if (null != parameters) {
                // 感觉这里可以稍微优化一下，理论上都是同一个.
                parameters.forEach(this::process);
            } else {
                process(parameter);
            }
        }
        return parameter;
    }

    @Override
    public Object getParameterObject() {
        return this.parameterObject;
    }

    private void process(Object parameter) {
        if (parameter != null) {
            TableInfo tableInfo = null;
            Object entity = parameter;
            if (parameter instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) parameter;
                if (map.containsKey(Constants.ENTITY)) {
                    Object et = map.get(Constants.ENTITY);
                    if (et != null) {
                        entity = et;
                        tableInfo = TableInfoHelper.getTableInfo(entity.getClass());
                    }
                }
            } else {
                tableInfo = TableInfoHelper.getTableInfo(parameter.getClass());
            }
            if (tableInfo != null) {
                //到这里就应该转换到实体参数对象了,因为填充和ID处理都是针对实体对象处理的,不用传递原参数对象下去.
                MetaObject metaObject = this.configuration.newMetaObject(entity);
                if (SqlCommandType.INSERT == this.sqlCommandType) {
                    populateKeys(tableInfo, metaObject, entity);
                    insertFill(metaObject, tableInfo);
                } else {
                    updateFill(metaObject, tableInfo);
                }
            }
        }
    }


    protected void populateKeys(TableInfo tableInfo, MetaObject metaObject, Object entity) {
        final IdType idType = tableInfo.getIdType();
        final String keyProperty = tableInfo.getKeyProperty();
        if (StringUtils.isNotBlank(keyProperty) && null != idType && idType.getKey() >= 3) {
            final GlobalConfig globalConfig = GlobalConfigUtils.getGlobalConfig(this.configuration);
            final IdentifierGenerator identifierGenerator = globalConfig.getIdentifierGenerator();
            final boolean rewriteZeroId = globalConfig.getDbConfig().isOverrideZeroId();
            Object idValue = metaObject.getValue(keyProperty);
            Class<?> keyType = tableInfo.getKeyType();
            // ASSIGN_ID 需要区分是否为数字类型
            if (idType.getKey() == IdType.ASSIGN_ID.getKey()) {
                //如为数字类型，且 id 值为空，或为零且允许覆盖零值，则赋值
                if (Number.class.isAssignableFrom(keyType)) {
                    if (StringUtils.checkValNull(idValue) || (isZeroValue(idValue, keyType) && rewriteZeroId)) {
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
                    }
                }
                //不为数字类型，但 id 值为空，允许赋值
                else if (StringUtils.checkValNull(idValue)) {
                    metaObject.setValue(keyProperty, identifierGenerator.nextId(entity).toString());
                }
            }
            //ASSIGN_UUID 只在 id 值为空时生效
            else if (idType.getKey() == IdType.ASSIGN_UUID.getKey() && StringUtils.checkValNull(idValue)) {
                metaObject.setValue(keyProperty, identifierGenerator.nextUUID(entity));
            }
        }
    }

    private boolean isZeroValue(Object idValue, Class<?> keyType) {
        Class<?> clz = idValue.getClass();
        if (!Number.class.isAssignableFrom(clz)) {
            return false;
        }
        Number numberId = (Number) idValue;
        return (keyType == Integer.class && numberId.intValue() == 0)
            || (keyType == Long.class && numberId.longValue() == 0L)
            || (keyType == BigInteger.class && BigInteger.ZERO.compareTo(new BigInteger(String.valueOf(numberId.longValue()))) == 0)
            || (keyType == BigDecimal.class && BigDecimal.ZERO.compareTo(new BigDecimal(String.valueOf(numberId.doubleValue()))) == 0);
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

    /**
     * 处理正常批量插入逻辑
     * <p>
     * org.apache.ibatis.session.defaults.DefaultSqlSession$StrictMap 该类方法
     * wrapCollection 实现 StrictMap 封装逻辑
     * </p>
     *
     * @return 集合参数
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Collection<Object> getParameters(Object parameterObject) {
        Collection<Object> parameters = null;
        if (parameterObject instanceof Collection) {
            parameters = (Collection) parameterObject;
        } else if (parameterObject instanceof Map) {
            Map parameterMap = (Map) parameterObject;
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
