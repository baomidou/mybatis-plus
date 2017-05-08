/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.mapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.session.Configuration;

import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.entity.TableFieldInfo;
import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.enums.SqlMethod;
import com.baomidou.mybatisplus.toolkit.SqlReservedWords;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;

/**
 * <p>
 * SQL 自动注入器
 * </p>
 *
 * @author hubin sjy
 * @Date 2016-09-09
 */
public class AutoSqlInjector implements ISqlInjector {

    private static final Log logger = LogFactory.getLog(AutoSqlInjector.class);

    protected Configuration configuration;
    protected LanguageDriver languageDriver;
    protected MapperBuilderAssistant builderAssistant;

    /**
     * CRUD注入后给予标识 注入过后不再注入
     *
     * @param builderAssistant
     * @param mapperClass
     */
    public void inspectInject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
        String className = mapperClass.toString();
        Set<String> mapperRegistryCache = GlobalConfiguration.getMapperRegistryCache(builderAssistant.getConfiguration());
        if (!mapperRegistryCache.contains(className)) {
            inject(builderAssistant, mapperClass);
            mapperRegistryCache.add(className);
        }
    }

    /**
     * 注入单点 crudSql
     */
    public void inject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
        this.configuration = builderAssistant.getConfiguration();
        this.builderAssistant = builderAssistant;
        this.languageDriver = configuration.getDefaultScriptingLanguageInstance();
        /*
         * 驼峰设置 PLUS 配置 > 原始配置
		 */
        GlobalConfiguration globalCache = this.getGlobalConfig();
        if (!globalCache.isDbColumnUnderline()) {
            globalCache.setDbColumnUnderline(configuration.isMapUnderscoreToCamelCase());
        }
        Class<?> modelClass = extractModelClass(mapperClass);
        if (modelClass != null) {
            TableInfo table = TableInfoHelper.initTableInfo(builderAssistant, modelClass);
            injectSql(builderAssistant, mapperClass, modelClass, table);
        }
    }

    /**
     * <p>
     * 注入SQL
     * </p>
     *
     * @param builderAssistant
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectSql(MapperBuilderAssistant builderAssistant, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        /**
         * #148 表信息包含主键，注入主键相关方法
         */
        if (StringUtils.isNotEmpty(table.getKeyProperty())) {
            /* 删除 */
            this.injectDeleteByIdSql(false, mapperClass, modelClass, table);
            this.injectDeleteByIdSql(true, mapperClass, modelClass, table);
            /* 修改 */
            this.injectUpdateByIdSql(true, mapperClass, modelClass, table);
            this.injectUpdateByIdSql(false, mapperClass, modelClass, table);
            /* 查询 */
            this.injectSelectByIdSql(false, mapperClass, modelClass, table);
            this.injectSelectByIdSql(true, mapperClass, modelClass, table);
        } else {
            // 表不包含主键时 给予警告
            logger.warn(String.format("%s ,Not found @TableId annotation, Cannot use Mybatis-Plus 'xxById' Method.",
                    modelClass.toString()));
        }
        /**
         * 正常注入无需主键方法
         */
        /* 插入 */
        this.injectInsertOneSql(true, mapperClass, modelClass, table);
        this.injectInsertOneSql(false, mapperClass, modelClass, table);
        /* 删除 */
        this.injectDeleteSql(mapperClass, modelClass, table);
        this.injectDeleteByMapSql(mapperClass, table);
		/* 修改 */
        this.injectUpdateSql(mapperClass, modelClass, table);
		/* 查询 */
        this.injectSelectByMapSql(mapperClass, modelClass, table);
        this.injectSelectOneSql(mapperClass, modelClass, table);
        this.injectSelectCountSql(mapperClass, modelClass, table);
        this.injectSelectListSql(SqlMethod.SELECT_LIST, mapperClass, modelClass, table);
        this.injectSelectListSql(SqlMethod.SELECT_PAGE, mapperClass, modelClass, table);
        this.injectSelectMapsSql(SqlMethod.SELECT_MAPS, mapperClass, modelClass, table);
        this.injectSelectMapsSql(SqlMethod.SELECT_MAPS_PAGE, mapperClass, modelClass, table);
        this.injectSelectObjsSql(SqlMethod.SELECT_OBJS, mapperClass, modelClass, table);
		/* 自定义方法 */
        this.inject(configuration, builderAssistant, mapperClass, modelClass, table);
    }

    /**
     * 自定义方法，注入点（子类需重写该方法）
     */
    public void inject(Configuration configuration, MapperBuilderAssistant builderAssistant, Class<?> mapperClass,
                       Class<?> modelClass, TableInfo table) {
        // to do nothing
    }

    /**
     * 避免扫描到BaseMapper
     *
     * @param mapperClass
     * @return
     */
    protected Class<?> extractModelClass(Class<?> mapperClass) {
        if (mapperClass == BaseMapper.class) {
            logger.warn(" Current Class is BaseMapper ");
            return null;
        } else {
            Type[] types = mapperClass.getGenericInterfaces();
            ParameterizedType target = null;
            for (Type type : types) {
                if (type instanceof ParameterizedType && BaseMapper.class.isAssignableFrom(mapperClass)) {
                    target = (ParameterizedType) type;
                    break;
                }
            }
            return target == null ? null : (Class<?>) target.getActualTypeArguments()[0];
        }
    }

    /**
     * <p>
     * 注入插入 SQL 语句
     * </p>
     *
     * @param selective   是否选择插入
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectInsertOneSql(boolean selective, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
		/*
		 * INSERT INTO table <trim prefix="(" suffix=")" suffixOverrides=",">
		 * <if test="xx != null">xx,</if> </trim> <trim prefix="values ("
		 * suffix=")" suffixOverrides=","> <if test="xx != null">#{xx},</if>
		 * </trim>
		 */
        KeyGenerator keyGenerator = new NoKeyGenerator();
        StringBuilder fieldBuilder = new StringBuilder();
        StringBuilder placeholderBuilder = new StringBuilder();
        SqlMethod sqlMethod = selective ? SqlMethod.INSERT_ONE : SqlMethod.INSERT_ONE_ALL_COLUMN;

        fieldBuilder.append("\n<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        placeholderBuilder.append("\n<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        String keyProperty = null;
        String keyColumn = null;

        // 表包含主键处理逻辑,如果不包含主键当普通字段处理
        if (StringUtils.isNotEmpty(table.getKeyProperty())) {
            if (table.getIdType() == IdType.AUTO) {
				/* 自增主键 */
                keyGenerator = new Jdbc3KeyGenerator();
                keyProperty = table.getKeyProperty();
                keyColumn = table.getKeyColumn();
            } else {
                if (null != table.getKeySequence()) {
                    keyGenerator = TableInfoHelper.genKeyGenerator(table, builderAssistant, sqlMethod.getMethod(), languageDriver);
                    keyProperty = table.getKeyProperty();
                    keyColumn = table.getKeyColumn();
                    fieldBuilder.append(table.getKeyColumn()).append(",");
                    placeholderBuilder.append("#{").append(table.getKeyProperty()).append("},");
                } else {
            		/* 用户输入自定义ID */
                    fieldBuilder.append(table.getKeyColumn()).append(",");
                    // 正常自定义主键策略
                    placeholderBuilder.append("#{").append(table.getKeyProperty()).append("},");
                }
            }
        }

        List<TableFieldInfo> fieldList = table.getFieldList();

        for (TableFieldInfo fieldInfo : fieldList) {
            if (selective) {
                fieldBuilder.append(convertIfTagIgnored(fieldInfo, false));
                fieldBuilder.append(fieldInfo.getColumn()).append(",");
                fieldBuilder.append(convertIfTagIgnored(fieldInfo, true));
                placeholderBuilder.append(convertIfTagIgnored(fieldInfo, false));
                placeholderBuilder.append("#{").append(fieldInfo.getEl()).append("},");
                placeholderBuilder.append(convertIfTagIgnored(fieldInfo, true));
            } else {
                fieldBuilder.append(fieldInfo.getColumn()).append(",");
                placeholderBuilder.append("#{").append(fieldInfo.getEl()).append("},");
            }
        }
        fieldBuilder.append("\n</trim>");
        placeholderBuilder.append("\n</trim>");
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), fieldBuilder.toString(),
                placeholderBuilder.toString());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addInsertMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource, keyGenerator, keyProperty,
                keyColumn);
    }

    /**
     * <p>
     * 注入 entity 条件删除 SQL 语句
     * </p>
     *
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectDeleteSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.DELETE;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlWhereEntityWrapper(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addDeleteMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource);
    }

    /**
     * <p>
     * 注入 map 条件删除 SQL 语句
     * </p>
     *
     * @param mapperClass
     * @param table
     */
    protected void injectDeleteByMapSql(Class<?> mapperClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.DELETE_BY_MAP;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlWhereByMap(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
        this.addDeleteMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource);
    }

    /**
     * <p>
     * 注入删除 SQL 语句
     * </p>
     *
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectDeleteByIdSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.DELETE_BY_ID;
        SqlSource sqlSource;
        String idStr = table.getKeyColumn();
        if (batch) {
            sqlMethod = SqlMethod.DELETE_BATCH_BY_IDS;
            StringBuilder ids = new StringBuilder();
            ids.append("\n<foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\">");
            ids.append("#{item}");
            ids.append("\n</foreach>");
            idStr = ids.toString();
        }
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), table.getKeyColumn(), idStr);
        sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addDeleteMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource);
    }

    /**
     * <p>
     * 注入更新 SQL 语句
     * </p>
     *
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectUpdateByIdSql(boolean selective, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = selective ? SqlMethod.UPDATE_BY_ID : SqlMethod.UPDATE_ALL_COLUMN_BY_ID;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlSet(selective, table, null), table.getKeyColumn(),
                table.getKeyProperty());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }

    /**
     * <p>
     * 注入批量更新 SQL 语句
     * </p>
     *
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectUpdateSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.UPDATE;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlSet(true, table, "et."), sqlWhereEntityWrapper(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }

    /**
     * <p>
     * 注入查询 SQL 语句
     * </p>
     *
     * @param batch       是否为批量插入
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectSelectByIdSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.SELECT_BY_ID;
        SqlSource sqlSource;
        if (batch) {
            sqlMethod = SqlMethod.SELECT_BATCH_BY_IDS;
            StringBuilder ids = new StringBuilder();
            ids.append("\n<foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\">");
            ids.append("#{item}");
            ids.append("\n</foreach>");
            sqlSource = languageDriver.createSqlSource(configuration, String.format(sqlMethod.getSql(),
                    sqlSelectColumns(table, false), table.getTableName(), table.getKeyColumn(), ids.toString()), modelClass);
        } else {
            sqlSource = new RawSqlSource(configuration, String.format(sqlMethod.getSql(), sqlSelectColumns(table, false),
                    table.getTableName(), table.getKeyColumn(), table.getKeyProperty()), Object.class);
        }
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, table);
    }

    /**
     * <p>
     * 注入 map 查询 SQL 语句
     * </p>
     *
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectSelectByMapSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.SELECT_BY_MAP;
        String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table, false), table.getTableName(), sqlWhereByMap(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, table);
    }

    /**
     * <p>
     * 注入实体查询一条记录 SQL 语句
     * </p>
     *
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectSelectOneSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.SELECT_ONE;
        String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table, false), table.getTableName(), sqlWhere(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, table);
    }

    /**
     * <p>
     * 注入EntityWrapper方式查询记录列表 SQL 语句
     * </p>
     *
     * @param sqlMethod
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectSelectListSql(SqlMethod sqlMethod, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table, true), table.getTableName(),
                sqlWhereEntityWrapper(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, table);
    }

    /**
     * <p>
     * 注入EntityWrapper方式查询记录列表 SQL 语句
     * </p>
     *
     * @param sqlMethod
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectSelectMapsSql(SqlMethod sqlMethod, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table, true), table.getTableName(),
                sqlWhereEntityWrapper(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, Map.class, table);
    }

    /**
     * <p>
     * 注入EntityWrapper方式查询记录列表 SQL 语句
     * </p>
     *
     * @param sqlMethod
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectSelectObjsSql(SqlMethod sqlMethod, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        String sql = String.format(sqlMethod.getSql(), sqlSelectObjsColumns(table), table.getTableName(),
                sqlWhereEntityWrapper(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, Object.class, table);
    }

    /**
     * <p>
     * 注入EntityWrapper查询总记录数 SQL 语句
     * </p>
     *
     * @param mapperClass
     * @param modelClass
     * @param table       表信息
     */
    protected void injectSelectCountSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.SELECT_COUNT;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlWhereEntityWrapper(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, Integer.class, null);
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
        StringBuilder where = new StringBuilder("\n<if test=\"ew!=null\">");
        where.append("\n<if test=\"ew.entity!=null\">\n<where>");
        if (StringUtils.isNotEmpty(table.getKeyProperty())) {
            where.append("\n<if test=\"ew.entity.").append(table.getKeyProperty()).append("!=null\">\n");
            where.append(table.getKeyColumn()).append("=#{ew.entity.").append(table.getKeyProperty()).append("}");
            where.append("\n</if>");
        }
        List<TableFieldInfo> fieldList = table.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            where.append(convertIfTag(fieldInfo, "ew.entity.", false));
            where.append(" AND ").append(fieldInfo.getColumn()).append("=#{ew.entity.").append(fieldInfo.getEl()).append("}");
            where.append(convertIfTag(fieldInfo, true));
        }
        where.append("\n</where>\n</if>");
        where.append("\n<if test=\"ew.sqlSegment!=null\">\n${ew.sqlSegment}\n</if>");
        where.append("\n</if>");
        return where.toString();
    }

    /**
     * <p>
     * SQL 更新 set 语句
     * </p>
     *
     * @param selective 是否选择判断
     * @param table     表信息
     * @param prefix    前缀
     * @return
     */
    protected String sqlSet(boolean selective, TableInfo table, String prefix) {
        StringBuilder set = new StringBuilder();
        set.append("<trim prefix=\"SET\" suffixOverrides=\",\">");
        List<TableFieldInfo> fieldList = table.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            if (selective) {
                set.append(convertIfTag(true, fieldInfo, prefix, false));
                set.append(fieldInfo.getColumn()).append("=#{");
                if (null != prefix) {
                    set.append(prefix);
                }
                set.append(fieldInfo.getEl()).append("},");
                set.append(convertIfTag(true, fieldInfo, null, true));
            } else {
                set.append(fieldInfo.getColumn()).append("=#{");
                if (null != prefix) {
                    set.append(prefix);
                }
                set.append(fieldInfo.getEl()).append("},");
            }
        }
        set.append("\n</trim>");
        return set.toString();
    }

    /**
     * <p>
     * 获取需要转义的SQL字段
     * </p>
     *
     * @param convertStr
     * @return
     */
    protected String sqlWordConvert(String convertStr) {
        GlobalConfiguration globalConfig = GlobalConfiguration.getGlobalConfig(configuration);
        return SqlReservedWords.convert(globalConfig, convertStr);
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
            int _size = 0;
            if (null != fieldList) {
                _size = fieldList.size();
            }

            // 主键处理
            if (StringUtils.isNotEmpty(table.getKeyProperty())) {
                if (table.isKeyRelated()) {
                    columns.append(table.getKeyColumn()).append(" AS ").append(sqlWordConvert(table.getKeyProperty()));
                } else {
                    columns.append(sqlWordConvert(table.getKeyProperty()));
                }
                if (_size >= 1) {
                    // 判断其余字段是否存在
                    columns.append(",");
                }
            }

            if (_size >= 1) {
                // 字段处理
                int i = 0;
                Iterator<TableFieldInfo> iterator = fieldList.iterator();
                while (iterator.hasNext()) {
                    TableFieldInfo fieldInfo = iterator.next();
                    // 匹配转换内容
                    String wordConvert = sqlWordConvert(fieldInfo.getProperty());
                    if (fieldInfo.getColumn().equals(wordConvert)) {
                        columns.append(wordConvert);
                    } else {
                        // 字段属性不一致
                        columns.append(fieldInfo.getColumn());
                        columns.append(" AS ").append(wordConvert);
                    }
                    if (i + 1 < _size) {
                        columns.append(",");
                    }
                    i++;
                }
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
                    columns.append(fieldInfo.getColumn());
                    columns.append(" AS ").append(wordConvert);
                }
            }
        }
        columns.append("</otherwise></choose>");
        return columns.toString();
    }

    /**
     * <p>
     * SQL 查询条件
     * </p>
     */
    protected String sqlWhere(TableInfo table) {
        StringBuilder where = new StringBuilder();
        where.append("\n<where>");
        if (StringUtils.isNotEmpty(table.getKeyProperty())) {
            where.append("\n<if test=\"ew.").append(table.getKeyProperty()).append("!=null\">\n");
            where.append(table.getKeyColumn()).append("=#{ew.").append(table.getKeyProperty()).append("}");
            where.append("\n</if>");
        }
        List<TableFieldInfo> fieldList = table.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            where.append(convertIfTag(fieldInfo, "ew.", false));
            where.append(" AND ").append(fieldInfo.getColumn()).append("=#{ew.").append(fieldInfo.getEl()).append("}");
            where.append(convertIfTag(fieldInfo, true));
        }
        where.append("\n</where>");
        return where.toString();
    }

    /**
     * <p>
     * SQL map 查询条件
     * </p>
     */
    protected String sqlWhereByMap(TableInfo table) {
        StringBuilder where = new StringBuilder();
        where.append("\n<if test=\"cm!=null and !cm.isEmpty\">");
        where.append("\n<where>");
        where.append("\n<foreach collection=\"cm.keys\" item=\"k\" separator=\"AND\">");
        where.append("\n<if test=\"cm[k] != null\">");
        where.append(SqlReservedWords.convert(getGlobalConfig(), "\n${k}")).append(" = #{cm[${k}]}");
        where.append("\n</if>");
        where.append("\n</foreach>");
        where.append("\n</where>");
        where.append("\n</if>");
        return where.toString();
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
		/* 忽略策略 */
        FieldStrategy fieldStrategy = fieldInfo.getFieldStrategy();
        if (fieldStrategy == FieldStrategy.IGNORED) {
            if (ignored) {
                return "";
            }
            // 查询策略，使用全局策略
            fieldStrategy = this.getGlobalConfig().getFieldStrategy();
        }

        // 关闭标签
        if (close) {
            return "</if>";
        }

		/* 前缀处理 */
        String property = fieldInfo.getProperty();
        if (null != prefix) {
            property = prefix + property;
        }

        // 验证逻辑
        if (fieldStrategy == FieldStrategy.NOT_EMPTY) {
            String propertyType = fieldInfo.getPropertyType();
            if (StringUtils.isCharSequence(propertyType)) {
                return String.format("\n\t<if test=\"%s!=null and %s!=''\">", property, property);
            } else {
                return String.format("\n\t<if test=\"%s!=null \">", property);
            }
        } else {
            // FieldStrategy.NOT_NULL
            return String.format("\n\t<if test=\"%s!=null\">", property);
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

    /*
     * 查询
     */
    public MappedStatement addSelectMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource, Class<?> resultType,
                                                    TableInfo table) {
        if (null != table) {
            String resultMap = table.getResultMap();
            if (null != resultMap) {
				/* 返回 resultMap 映射结果集 */
                return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.SELECT, null, resultMap, null,
                        new NoKeyGenerator(), null, null);
            }
        }

		/* 普通查询 */
        return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.SELECT, null, null, resultType,
                new NoKeyGenerator(), null, null);
    }

    /*
     * 插入
     */
    public MappedStatement addInsertMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id, SqlSource sqlSource,
                                                    KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
        return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.INSERT, modelClass, null, Integer.class,
                keyGenerator, keyProperty, keyColumn);
    }

    /*
     * 删除
     */
    public MappedStatement addDeleteMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource) {
        return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.DELETE, null, null, Integer.class,
                new NoKeyGenerator(), null, null);
    }

    /*
     * 更新
     */
    public MappedStatement addUpdateMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id, SqlSource sqlSource) {
        return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.UPDATE, modelClass, null, Integer.class,
                new NoKeyGenerator(), null, null);
    }

    public MappedStatement addMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource,
                                              SqlCommandType sqlCommandType, Class<?> parameterClass, String resultMap, Class<?> resultType,
                                              KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
        String statementName = mapperClass.getName() + "." + id;
        if (configuration.hasStatement(statementName)) {
            System.err.println("{" + statementName
                    + "} Has been loaded by XML or SqlProvider, ignoring the injection of the SQL.");
            return null;
        }
		/* 缓存逻辑处理 */
        boolean isSelect = false;
        if (sqlCommandType == SqlCommandType.SELECT) {
            isSelect = true;
        }
        return builderAssistant.addMappedStatement(id, sqlSource, StatementType.PREPARED, sqlCommandType, null, null, null,
                parameterClass, resultMap, resultType, null, !isSelect, isSelect, false, keyGenerator, keyProperty, keyColumn,
                configuration.getDatabaseId(), languageDriver, null);
    }

    // --------------------------------------------------------SqlRunner------------------------------------------------------------
    public void injectSqlRunner(Configuration configuration) {
        this.configuration = configuration;
        this.languageDriver = configuration.getDefaultScriptingLanguageInstance();
        initSelectList();
        initSelectObjs();
        initInsert();
        initUpdate();
        initDelete();
        initCount();
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
     * 创建查询MappedStatement
     *
     * @param mappedStatement
     * @param sqlSource       执行的sqlSource
     * @param resultType      返回的结果类型
     */
    @SuppressWarnings("serial")
    private void createSelectMappedStatement(String mappedStatement, SqlSource sqlSource, final Class<?> resultType) {
        MappedStatement ms = new MappedStatement.Builder(configuration, mappedStatement, sqlSource, SqlCommandType.SELECT)
                .resultMaps(new ArrayList<ResultMap>() {
                    {
                        add(new ResultMap.Builder(configuration, "defaultResultMap", resultType, new ArrayList<ResultMapping>(0))
                                .build());
                    }
                }).build();
        // 缓存
        configuration.addMappedStatement(ms);
    }

    /**
     * 创建一个MappedStatement
     *
     * @param mappedStatement
     * @param sqlSource       执行的sqlSource
     * @param sqlCommandType  执行的sqlCommandType
     */
    @SuppressWarnings("serial")
    private void createUpdateMappedStatement(String mappedStatement, SqlSource sqlSource, SqlCommandType sqlCommandType) {
        MappedStatement ms = new MappedStatement.Builder(configuration, mappedStatement, sqlSource, sqlCommandType).resultMaps(
                new ArrayList<ResultMap>() {
                    {
                        add(new ResultMap.Builder(configuration, "defaultResultMap", int.class, new ArrayList<ResultMapping>(0))
                                .build());
                    }
                }).build();
        // 缓存
        configuration.addMappedStatement(ms);
    }

    /**
     * initSelectList
     */
    private void initSelectList() {
        if (hasMappedStatement(SqlRunner.SELECT_LIST)) {
            logger.warn("MappedStatement 'SqlRunner.SelectList' Already Exists");
            return;
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, SqlRunner.SQLScript, Map.class);
        createSelectMappedStatement(SqlRunner.SELECT_LIST, sqlSource, Map.class);
    }

    /**
     * initSelectObjs
     */
    private void initSelectObjs() {
        if (hasMappedStatement(SqlRunner.SELECT_OBJS)) {
            logger.warn("MappedStatement 'SqlRunner.SelectObjs' Already Exists");
            return;
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, SqlRunner.SQLScript, Object.class);
        createSelectMappedStatement(SqlRunner.SELECT_OBJS, sqlSource, Object.class);
    }

    /**
     * initCount
     */
    private void initCount() {
        if (hasMappedStatement(SqlRunner.COUNT)) {
            logger.warn("MappedStatement 'SqlRunner.Count' Already Exists");
            return;
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, SqlRunner.SQLScript, Map.class);
        createSelectMappedStatement(SqlRunner.COUNT, sqlSource, Integer.class);
    }

    /**
     * initInsert
     */
    private void initInsert() {
        if (hasMappedStatement(SqlRunner.INSERT)) {
            logger.warn("MappedStatement 'SqlRunner.Insert' Already Exists");
            return;
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, SqlRunner.SQLScript, Map.class);
        createUpdateMappedStatement(SqlRunner.INSERT, sqlSource, SqlCommandType.INSERT);
    }

    /**
     * initUpdate
     */
    private void initUpdate() {
        if (hasMappedStatement(SqlRunner.UPDATE)) {
            logger.warn("MappedStatement 'SqlRunner.Update' Already Exists");
            return;
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, SqlRunner.SQLScript, Map.class);
        createUpdateMappedStatement(SqlRunner.UPDATE, sqlSource, SqlCommandType.UPDATE);
    }

    /**
     * initDelete
     */
    private void initDelete() {
        if (hasMappedStatement(SqlRunner.DELETE)) {
            logger.warn("MappedStatement 'SqlRunner.Delete' Already Exists");
            return;
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, SqlRunner.SQLScript, Map.class);
        createUpdateMappedStatement(SqlRunner.DELETE, sqlSource, SqlCommandType.DELETE);
    }

    /**
     * <p>
     * 全局配置
     * </p>
     */
    protected GlobalConfiguration getGlobalConfig() {
        return GlobalConfiguration.getGlobalConfig(configuration);
    }

}
