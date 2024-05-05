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
package com.baomidou.mybatisplus.core;

import com.baomidou.mybatisplus.core.handlers.IJsonTypeHandler;
import com.baomidou.mybatisplus.core.toolkit.MybatisUtils;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * 重写了原生方法.
 *
 * @author nieqiurong
 * @see MapperBuilderAssistant
 * @since 3.5.6
 */
public class MybatisMapperBuilderAssistant extends MapperBuilderAssistant {

    public MybatisMapperBuilderAssistant(Configuration configuration, String resource) {
        super(configuration, resource);
    }

    @Override
    public ResultMapping buildResultMapping(Class<?> resultType, String property, String column, Class<?> javaType,
                                            JdbcType jdbcType, String nestedSelect, String nestedResultMap, String notNullColumn, String columnPrefix,
                                            Class<? extends TypeHandler<?>> typeHandler, List<ResultFlag> flags, String resultSet, String foreignColumn,
                                            boolean lazy) {
        Class<?> javaTypeClass = resolveResultJavaType(resultType, property, javaType);
        TypeHandler<?> typeHandlerInstance = null;
        if (typeHandler != null) {
            if (IJsonTypeHandler.class.isAssignableFrom(typeHandler)) {
                try {
                    Field field = resultType.getDeclaredField(property);
                    typeHandlerInstance = MybatisUtils.newJsonTypeHandler(typeHandler, javaTypeClass, field);
                } catch (NoSuchFieldException e) {
                    //ignore 降级兼容处理
                    typeHandlerInstance = resolveTypeHandler(javaTypeClass, typeHandler);
                }
            } else {
                typeHandlerInstance = resolveTypeHandler(javaTypeClass, typeHandler);
            }
        }
        List<ResultMapping> composites;
        if ((nestedSelect == null || nestedSelect.isEmpty()) && (foreignColumn == null || foreignColumn.isEmpty())) {
            composites = Collections.emptyList();
        } else {
            composites = parseCompositeColumnName(column);
        }
        return new ResultMapping.Builder(configuration, property, column, javaTypeClass).jdbcType(jdbcType)
            .nestedQueryId(applyCurrentNamespace(nestedSelect, true))
            .nestedResultMapId(applyCurrentNamespace(nestedResultMap, true)).resultSet(resultSet)
            .typeHandler(typeHandlerInstance).flags(flags == null ? new ArrayList<>() : flags).composites(composites)
            .notNullColumns(parseMultipleColumnNames(notNullColumn)).columnPrefix(columnPrefix).foreignColumn(foreignColumn)
            .lazy(lazy).build();
    }


    private Set<String> parseMultipleColumnNames(String columnName) {
        Set<String> columns = new HashSet<>();
        if (columnName != null) {
            if (columnName.indexOf(',') > -1) {
                StringTokenizer parser = new StringTokenizer(columnName, "{}, ", false);
                while (parser.hasMoreTokens()) {
                    String column = parser.nextToken();
                    columns.add(column);
                }
            } else {
                columns.add(columnName);
            }
        }
        return columns;
    }


    private List<ResultMapping> parseCompositeColumnName(String columnName) {
        List<ResultMapping> composites = new ArrayList<>();
        if (columnName != null && (columnName.indexOf('=') > -1 || columnName.indexOf(',') > -1)) {
            StringTokenizer parser = new StringTokenizer(columnName, "{}=, ", false);
            while (parser.hasMoreTokens()) {
                String property = parser.nextToken();
                String column = parser.nextToken();
                ResultMapping complexResultMapping = new ResultMapping.Builder(configuration, property, column,
                    configuration.getTypeHandlerRegistry().getUnknownTypeHandler()).build();
                composites.add(complexResultMapping);
            }
        }
        return composites;
    }

    private Class<?> resolveResultJavaType(Class<?> resultType, String property, Class<?> javaType) {
        if (javaType == null && property != null) {
            try {
                MetaClass metaResultType = MetaClass.forClass(resultType, configuration.getReflectorFactory());
                javaType = metaResultType.getSetterType(property);
            } catch (Exception e) {
                // ignore, following null check statement will deal with the situation
            }
        }
        if (javaType == null) {
            javaType = Object.class;
        }
        return javaType;
    }

}
