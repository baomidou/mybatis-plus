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
package com.baomidou.mybatisplus.core.injector.methods;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;

/**
 * <p>
 * 抽象的注入方法类
 * </p>
 *
 * @author hubin
 * @since 2018-04-06
 */
public abstract class AbstractMethod {

    protected Configuration configuration;
    protected LanguageDriver languageDriver;
    protected MapperBuilderAssistant builderAssistant;


    public void AbstractMethod(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
        this.configuration = builderAssistant.getConfiguration();
        this.builderAssistant = builderAssistant;
        this.languageDriver = configuration.getDefaultScriptingLanguageInstance();
        Class<?> modelClass = extractModelClass(mapperClass);
        if (null != modelClass) {
            // 注入自定义方法
            TableInfo tableInfo = TableInfoHelper.initTableInfo(builderAssistant, modelClass);
            this.injectMappedStatement(mapperClass, modelClass, tableInfo);
        }
    }


    /**
     * 提取泛型模型,多泛型的时候请将泛型T放在第一位
     *
     * @param mapperClass
     * @return
     */
    protected Class<?> extractModelClass(Class<?> mapperClass) {
        Type[] types = mapperClass.getGenericInterfaces();
        ParameterizedType target = null;
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                Type[] typeArray = ((ParameterizedType) type).getActualTypeArguments();
                if (ArrayUtils.isNotEmpty(typeArray)) {
                    for (Type t : typeArray) {
                        if (t instanceof TypeVariable || t instanceof WildcardType) {
                            target = null;
                            break;
                        } else {
                            target = (ParameterizedType) type;
                            break;
                        }
                    }
                }
                break;
            }
        }
        return target == null ? null : (Class<?>) target.getActualTypeArguments()[0];
    }


    /**
     * 是否已经存在MappedStatement
     *
     * @param mappedStatement
     * @return
     */
    private boolean hasMappedStatement(String mappedStatement) {
        return configuration.hasStatement(mappedStatement, false);
    }


    /**
     * 查询
     */
    public MappedStatement addSelectMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource, Class<?> resultType,
                                                    TableInfo table) {
        if (null != table) {
            String resultMap = table.getResultMap();
            if (null != resultMap) {
                /** 返回 resultMap 映射结果集 */
                return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.SELECT, null, resultMap, null,
                    new NoKeyGenerator(), null, null);
            }
        }

        /** 普通查询 */
        return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.SELECT, null, null, resultType,
            new NoKeyGenerator(), null, null);
    }


    /**
     * 插入
     */
    public MappedStatement addInsertMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id, SqlSource sqlSource,
                                                    KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
        return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.INSERT, modelClass, null, Integer.class,
            keyGenerator, keyProperty, keyColumn);
    }


    /**
     * 删除
     */
    public MappedStatement addDeleteMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource) {
        return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.DELETE, null, null, Integer.class,
            new NoKeyGenerator(), null, null);
    }

    /**
     * 添加 MappedStatement 到 Mybatis 容器
     */
    public MappedStatement addMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource,
                                              SqlCommandType sqlCommandType, Class<?> parameterClass, String resultMap, Class<?> resultType,
                                              KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
        String statementName = mapperClass.getName() + "." + id;
        if (hasMappedStatement(statementName)) {
            System.err.println("{" + statementName + "} Has been loaded by XML or SqlProvider, ignoring the injection of the SQL.");
            return null;
        }
        /** 缓存逻辑处理 */
        boolean isSelect = false;
        if (sqlCommandType == SqlCommandType.SELECT) {
            isSelect = true;
        }
        return builderAssistant.addMappedStatement(id, sqlSource, StatementType.PREPARED, sqlCommandType, null, null, null,
            parameterClass, resultMap, resultType, null, !isSelect, isSelect, false, keyGenerator, keyProperty, keyColumn,
            configuration.getDatabaseId(), languageDriver, null);
    }


    /**
     * 注入自定义 MappedStatement
     */
    abstract MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo);

}
