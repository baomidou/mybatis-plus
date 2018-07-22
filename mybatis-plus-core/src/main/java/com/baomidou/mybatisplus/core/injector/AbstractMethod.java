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
package com.baomidou.mybatisplus.core.injector;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.parser.SqlParserHelper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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

    /**
     * 注入自定义方法
     */
    public void inject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
        configuration = builderAssistant.getConfiguration();
        this.builderAssistant = builderAssistant;
        languageDriver = configuration.getDefaultScriptingLanguageInstance();
        Class<?> modelClass = extractModelClass(mapperClass);
        if (null != modelClass) {
            /**
             * 初始化 SQL 解析
             */
            if (this.getGlobalConfig().isSqlParserCache()) {
                SqlParserHelper.initSqlParserInfoCache(mapperClass);
            }
            /**
             * 注入自定义方法
             */
            TableInfo tableInfo = TableInfoHelper.initTableInfo(builderAssistant, modelClass);
            injectMappedStatement(mapperClass, modelClass, tableInfo);
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
     * <p>
     * SQL 更新 set 语句
     * </p>
     *
     * @param logic  是否逻辑删除注入器
     * @param ew     是否存在 UpdateWrapper 条件
     * @param table  表信息
     * @param prefix 前缀
     * @return
     */
    protected String sqlSet(boolean logic, boolean ew, TableInfo table, String prefix) {
        StringBuilder set = new StringBuilder();
        set.append("<trim prefix=\"SET\" suffixOverrides=\",\">");

        // 是否 IF 标签判断
        boolean ifTag;
        List<TableFieldInfo> fieldList = table.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            // 逻辑删除逻辑
            if (logic && fieldInfo.isLogicDelete()) {
                if (null != prefix) {
                    // 更新排除逻辑删除字段
                    continue;
                }
                set.append(fieldInfo.getColumn()).append("=");
                if (StringUtils.isCharSequence(fieldInfo.getPropertyType())) {
                    set.append("'").append(fieldInfo.getLogicDeleteValue()).append("',");
                } else {
                    set.append(fieldInfo.getLogicDeleteValue()).append(",");
                }
                continue;
            }
            // 判断是否更新忽略,在FieldIgnore,UPDATE,INSERT_UPDATE设置为false
            ifTag = !(FieldFill.UPDATE == fieldInfo.getFieldFill()
                || FieldFill.INSERT_UPDATE == fieldInfo.getFieldFill());
            if (ifTag) {
                if (StringUtils.isNotEmpty(fieldInfo.getUpdate())) {
                    set.append(fieldInfo.getColumn()).append("=")
                        .append(String.format(fieldInfo.getUpdate(), fieldInfo.getColumn())).append(",");
                } else {
                    set.append(convertIfTag(true, fieldInfo, prefix, false))
                        .append(fieldInfo.getColumn()).append("=#{");
                    if (null != prefix) {
                        set.append(prefix);
                    }
                    set.append(fieldInfo.getEl()).append("},")
                        .append(convertIfTag(true, fieldInfo, null, true));
                }
            }else {
                set.append(fieldInfo.getColumn()).append("=#{");
                if (null != prefix) {
                    set.append(prefix);
                }
                set.append(fieldInfo.getEl()).append("},");
            }
        }
        // UpdateWrapper SqlSet 部分
        if (ew) {
            set.append("<if test=\"ew != null and ew.sqlSet != null\">${ew.sqlSet}</if>");
        }
        set.append("</trim>");
        return set.toString();
    }

    /**
     * <p>
     * 获取需要转义的SQL字段
     * </p>
     *
     * @param column 字段
     * @return
     */
    protected String sqlWordConvert(String column) {
        return getGlobalConfig().getDbConfig().getReservedWordsHandler()
            .convert(getGlobalConfig().getDbConfig().getDbType(), column);
    }

    /**
     * <p>
     * SQL 查询所有表字段
     * </p>
     *
     * @param table
     * @param entityWrapper 是否为包装类型查询
     * @return
     */
    protected String sqlSelectColumns(TableInfo table, boolean entityWrapper) {
        StringBuilder columns = new StringBuilder();
        if (null != table.getResultMap()) {
            /*
             * 存在 resultMap 映射返回
             */
            if (entityWrapper) {
                columns.append("<choose><when test=\"ew != null and ew.sqlSelect != null\">${ew.sqlSelect}</when><otherwise>");
            }
            columns.append("*");
            if (entityWrapper) {
                columns.append("</otherwise></choose>");
            }
        } else {
            /*
             * 普通查询
             */
            if (entityWrapper) {
                columns.append("<choose><when test=\"ew != null and ew.sqlSelect != null\">${ew.sqlSelect}</when><otherwise>");
            }
            List<TableFieldInfo> fieldList = table.getFieldList();
            int size = 0;
            if (null != fieldList) {
                size = fieldList.size();
            }

            // 主键处理
            if (StringUtils.isNotEmpty(table.getKeyProperty())) {
                if (table.isKeyRelated()) {
                    columns.append(table.getKeyColumn()).append(" AS ").append(sqlWordConvert(table.getKeyProperty()));
                } else {
                    columns.append(sqlWordConvert(table.getKeyColumn()));
                }
                if (size >= 1) {
                    // 判断其余字段是否存在
                    columns.append(",");
                }
            }

            if (size >= 1) {
                // 字段处理
                columns.append(fieldList.stream().map(i -> {
                    String v = sqlWordConvert(i.getColumn());
                    if (i.isRelated()) {
                        v += (" AS " + i.getProperty());
                    }
                    return v;
                }).collect(Collectors.joining(",")));
            }
            if (entityWrapper) {
                columns.append("</otherwise></choose>");
            }
        }

        /*
         * 返回所有查询字段内容
         */
        return columns.toString();
    }

    /**
     * <p>
     * SQL 设置selectObj sqlselect
     * </p>
     *
     * @param table 是否为包装类型查询
     * @return
     */
    protected String sqlSelectObjsColumns(TableInfo table) {
        StringBuilder columns = new StringBuilder();
        /*
         * 普通查询
         */
        columns.append("<choose><when test=\"ew != null and ew.sqlSelect != null\">${ew.sqlSelect}</when><otherwise>");
        // 主键处理
        if (StringUtils.isNotEmpty(table.getKeyProperty())) {
            if (table.isKeyRelated()) {
                columns.append(table.getKeyColumn()).append(" AS ").append(sqlWordConvert(table.getKeyProperty()));
            } else {
                columns.append(sqlWordConvert(table.getKeyProperty()));
            }
        } else {
            // 表字段处理
            List<TableFieldInfo> fieldList = table.getFieldList();
            if (CollectionUtils.isNotEmpty(fieldList)) {
                TableFieldInfo fieldInfo = fieldList.get(0);
                // 匹配转换内容
                String wordConvert = sqlWordConvert(fieldInfo.getProperty());
                if (fieldInfo.getColumn().equals(wordConvert)) {
                    columns.append(wordConvert);
                } else {
                    // 字段属性不一致
                    columns.append(fieldInfo.getColumn()).append(" AS ").append(wordConvert);
                }
            }
        }
        columns.append("</otherwise></choose>");
        return columns.toString();
    }

    /**
     * <p>
     * SQL map 查询条件
     * </p>
     */
    protected String sqlWhereByMap(TableInfo table) {
        return new StringBuilder()
            .append("<if test=\"cm!=null and !cm.isEmpty\">")
            .append("<where>")
            .append("<foreach collection=\"cm\" index=\"k\" item=\"v\" separator=\"AND\">")
            .append("<choose><when test=\"v==null\"> ${k} IS NULL </when>")
            .append("<otherwise> ${k}=#{v} </otherwise></choose>")
            .append("</foreach>")
            .append("</where>")
            .append("</if>")
            .toString();
    }

    /**
     * <p>
     * IF 条件转换方法
     * </p>
     *
     * @param ignored   允许忽略
     * @param fieldInfo 字段信息
     * @param prefix    条件前缀
     * @param close     是否闭合标签
     * @return
     */
    protected String convertIfTag(boolean ignored, TableFieldInfo fieldInfo, String prefix, boolean close) {
        /** 忽略策略 */
        FieldStrategy fieldStrategy = fieldInfo.getFieldStrategy();
        if (fieldStrategy == FieldStrategy.IGNORED) {
            if (ignored) {
                return "";
            }
            // 查询策略，使用全局策略
            fieldStrategy = getGlobalConfig().getDbConfig().getFieldStrategy();
        }

        // 关闭标签
        if (close) {
            return "</if>";
        }

        /** 前缀处理 */
        String property = fieldInfo.getProperty();
        Class propertyType = fieldInfo.getPropertyType();
        property = StringUtils.removeIsPrefixIfBoolean(property, propertyType);
        if (null != prefix) {
            property = prefix + property;
        }
        // 验证逻辑
        if (fieldStrategy == FieldStrategy.NOT_EMPTY) {
            if (StringUtils.isCharSequence(propertyType)) {
                return String.format("<if test=\"%s!=null and %s!=''\">", property, property);
            } else {
                return String.format("<if test=\"%s!=null \">", property);
            }
        } else {
            // FieldStrategy.NOT_NULL
            return String.format("<if test=\"%s!=null\">", property);
        }
    }

    protected String convertIfTagIgnored(TableFieldInfo fieldInfo, boolean close) {
        return convertIfTag(true, fieldInfo, null, close);
    }

    protected String convertIfTag(TableFieldInfo fieldInfo, String prefix, boolean close) {
        return convertIfTag(false, fieldInfo, prefix, close);
    }

    protected String convertIfTag(TableFieldInfo fieldInfo, boolean close) {
        return convertIfTag(fieldInfo, null, close);
    }

    /**
     * <p>
     * Sql 运算条件
     * </p>
     */
    protected String sqlCondition(String condition, String column, String property) {
        return String.format(condition, column, property);
    }

    /**
     * <p>
     * EntityWrapper方式获取select where
     * </p>
     *
     * @param table 表信息
     * @return String
     */
    protected String sqlWhereEntityWrapper(TableInfo table) {
        StringBuilder where = new StringBuilder(128);
        where.append("<if test=\"ew!=null and !ew.emptyOfWhere\">");
        where.append("<trim prefix=\"WHERE\" prefixOverrides=\"AND|OR\">");
        where.append("<if test=\"ew.entity!=null\">");
        if (StringUtils.isNotEmpty(table.getKeyProperty())) {
            where.append("<if test=\"ew.entity.").append(table.getKeyProperty()).append("!=null\">");
            where.append(table.getKeyColumn()).append("=#{ew.entity.").append(table.getKeyProperty()).append("}");
            where.append("</if>");
        }
        List<TableFieldInfo> fieldList = table.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            where.append(convertIfTag(fieldInfo, "ew.entity.", false));
            where.append(" AND ").append(sqlCondition(fieldInfo.getCondition(), fieldInfo.getColumn(),
                "ew.entity." + fieldInfo.getEl()));
            where.append(convertIfTag(fieldInfo, true));
        }
        where.append("</if>");
        where.append("<if test=\"ew.sqlSegment!=null and ew.sqlSegment!=''\"> AND ${ew.sqlSegment}</if>");
        where.append("</trim>");
        where.append("</if>");
        return where.toString();
    }

    /**
     * 查询
     */
    protected MappedStatement addSelectMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource, Class<?> resultType,
                                                       TableInfo table) {
        if (null != table) {
            String resultMap = table.getResultMap();
            if (null != resultMap) {
                /** 返回 resultMap 映射结果集 */
                return addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.SELECT, null, resultMap, null,
                    new NoKeyGenerator(), null, null);
            }
        }

        /** 普通查询 */
        return addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.SELECT, null, null, resultType,
            new NoKeyGenerator(), null, null);
    }

    /**
     * 插入
     */
    protected MappedStatement addInsertMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id, SqlSource sqlSource,
                                                       KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
        return addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.INSERT, modelClass, null, Integer.class,
            keyGenerator, keyProperty, keyColumn);
    }

    /**
     * 删除
     */
    protected MappedStatement addDeleteMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource) {
        return addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.DELETE, null, null, Integer.class,
            new NoKeyGenerator(), null, null);
    }

    /**
     * 更新
     */
    public MappedStatement addUpdateMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id, SqlSource sqlSource) {
        return addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.UPDATE, modelClass, null, Integer.class,
            new NoKeyGenerator(), null, null);
    }

    /**
     * 添加 MappedStatement 到 Mybatis 容器
     */
    protected MappedStatement addMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource,
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
     * <p>
     * 全局配置
     * </p>
     */
    protected GlobalConfig getGlobalConfig() {
        return GlobalConfigUtils.getGlobalConfig(configuration);
    }

    /**
     * 注入自定义 MappedStatement
     */
    public abstract MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo);
}
