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
package com.baomidou.mybatisplus.core;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.ognl.OgnlOps;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.SimpleTypeRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 自定义 ParameterHandler 重装构造函数，填充插入方法主键 ID
 *
 * @author nieqiuqiu 2020/6/5
 * @since 3.4.0
 */
public class MybatisParameterHandler extends DefaultParameterHandler {

    private final Configuration configuration;
    private final SqlCommandType sqlCommandType;
    private final MappedStatement mappedStatement;
    private final Log log;

    public MybatisParameterHandler(MappedStatement mappedStatement, Object parameter, BoundSql boundSql) {
        super(mappedStatement, parameter, boundSql);
        this.mappedStatement = mappedStatement;
        this.log = mappedStatement.getStatementLog();
        this.configuration = mappedStatement.getConfiguration();
        this.sqlCommandType = mappedStatement.getSqlCommandType();
        processParameter(parameter);
    }

    public void processParameter(Object parameter) {
        /* 只处理插入或更新操作 */
        if (parameter != null && !SimpleTypeRegistry.isSimpleType(parameter.getClass())) {
            if (SqlCommandType.INSERT == this.sqlCommandType || SqlCommandType.UPDATE == this.sqlCommandType) {
                extractParameters(parameter).forEach(this::process);
            }
        }
    }

    private void process(Object parameter) {
        if (parameter != null) {
            TableInfo tableInfo = null;
            Object entity = parameter;
            if (parameter instanceof Map) {
                // 处理单参数使用注解标记的时候，尝试提取et来获取实体参数
                Map<?, ?> map = (Map<?, ?>) parameter;
                Object et = null;
                if (map.containsKey(Constants.ENTITY)) {
                    et = map.get(Constants.ENTITY);

                } else if (map.containsKey(Constants.MP_FILL_ET)) {
                    et = map.get(Constants.MP_FILL_ET);
                }
                if (et != null) {
                    entity = et;
                    tableInfo = TableInfoHelper.getTableInfo(entity.getClass());
                }
            } else {
                tableInfo = TableInfoHelper.getTableInfo(parameter.getClass());
            }
            if (tableInfo != null) {
                // 到这里就应该转换到实体参数对象了,因为填充和ID处理都是针对实体对象处理的,不用传递原参数对象下去.
                MetaObject metaObject = this.configuration.newMetaObject(entity);
                if (SqlCommandType.INSERT == this.sqlCommandType) {
                    populateKeys(tableInfo, metaObject, entity);
                    insertFill(metaObject, tableInfo);
                } else {
                    updateFill(metaObject, tableInfo);
                    fillLogicDeleteValue(metaObject, tableInfo);
                }
            }
        }
    }

    protected void fillLogicDeleteValue(MetaObject metaObject, TableInfo tableInfo) {
        if (!tableInfo.isWithLogicDelete()) {
            return;
        }
        TableFieldInfo logicDeleteFieldInfo = tableInfo.getLogicDeleteFieldInfo();
        if (logicDeleteFieldInfo == null || !logicDeleteFieldInfo.isWithUpdateFill()) {
            return;
        }
        String methodName = mappedStatement.getId().substring(mappedStatement.getId().lastIndexOf('.') + 1);
        if (!SqlMethod.DELETE_BY_ID.getMethod().equals(methodName)
                && !SqlMethod.DELETE_BY_IDS.getMethod().equals(methodName)) {
            return;
        }
        String property = logicDeleteFieldInfo.getProperty();
        Object currentValue = metaObject.getValue(property);
        if (currentValue == null
                || String.valueOf(currentValue).equals(logicDeleteFieldInfo.getLogicNotDeleteValue())) {
            metaObject.setValue(
                    property, OgnlOps.convertValue(
                            logicDeleteFieldInfo.getLogicDeleteValue(), logicDeleteFieldInfo.getPropertyType()));
        }
    }

    protected void populateKeys(TableInfo tableInfo, MetaObject metaObject, Object entity) {
        final IdType idType = tableInfo.getIdType();
        final String keyProperty = tableInfo.getKeyProperty();
        if (StringUtils.isNotBlank(keyProperty) && null != idType && idType.getKey() >= 3) {
            final IdentifierGenerator identifierGenerator = GlobalConfigUtils.getGlobalConfig(this.configuration)
                    .getIdentifierGenerator();
            Object idValue = metaObject.getValue(keyProperty);
            if (identifierGenerator.assignId(idValue)) {
                if (idType.getKey() == IdType.ASSIGN_ID.getKey()) {
                    Number id = identifierGenerator.nextId(entity);
                    metaObject.setValue(keyProperty, OgnlOps.convertValue(id, tableInfo.getKeyType()));
                } else if (idType.getKey() == IdType.ASSIGN_UUID.getKey()) {
                    if (String.class.equals(tableInfo.getKeyType())) {
                        metaObject.setValue(keyProperty, identifierGenerator.nextUUID(entity));
                    } else {
                        log.warn("The current ID generation strategy does not support: " + tableInfo.getKeyType());
                    }
                }
            }
        }
    }

    protected void insertFill(MetaObject metaObject, TableInfo tableInfo) {
        GlobalConfigUtils.getMetaObjectHandler(this.configuration).ifPresent(metaObjectHandler -> {
            if (metaObjectHandler.openInsertFill(mappedStatement) && tableInfo.isWithInsertFill()) {
                metaObjectHandler.insertFill(metaObject);
            }
        });
    }

    protected void updateFill(MetaObject metaObject, TableInfo tableInfo) {
        GlobalConfigUtils.getMetaObjectHandler(this.configuration).ifPresent(metaObjectHandler -> {
            if (metaObjectHandler.openUpdateFill(mappedStatement) && tableInfo.isWithUpdateFill()) {
                metaObjectHandler.updateFill(metaObject);
            }
        });
    }

    /**
     * 提取特殊key值 (只支持外层参数,嵌套参数不考虑) List<Map>虽然这种写法目前可以进去提取et,但不考虑再提取list等其他类型,只做简单参数提取
     *
     * @param parameterObject 参数
     * @return 预期可能为填充参数值
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private Collection<Object> extractParameters(Object parameterObject) {
        if (parameterObject instanceof Collection) {
            return (Collection) parameterObject;
        } else if (ArrayUtils.isArray(parameterObject)) {
            return toCollection(parameterObject);
        } else if (parameterObject instanceof Map) {
            Collection<Object> parameters = new ArrayList<>();
            Map<String, Object> parameterMap = (Map) parameterObject;
            // @Param 名称和 paramN 可能引用同一个参数，值相等的不同实体仍需分别填充。
            Set<Object> objectSet = Collections.newSetFromMap(new IdentityHashMap<>());
            parameterMap.forEach((k, v) -> {
                if (objectSet.add(v)) {
                    Collection<Object> collection = toCollection(v);
                    parameters.addAll(collection);
                }
            });
            return parameters;
        } else {
            return Collections.singleton(parameterObject);
        }
    }

    @SuppressWarnings("unchecked")
    protected Collection<Object> toCollection(Object value) {
        if (value == null) {
            return Collections.emptyList();
        }
        if (ArrayUtils.isArray(value) && !value.getClass().getComponentType().isPrimitive()) {
            return Arrays.asList((Object[]) value);
        } else if (Collection.class.isAssignableFrom(value.getClass())) {
            return (Collection<Object>) value;
        } else {
            return Collections.singletonList(value);
        }
    }
}
