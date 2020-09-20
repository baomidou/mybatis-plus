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
package com.baomidou.mybatisplus.generator.config.builder;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.querys.DecoratorDbQuery;
import com.baomidou.mybatisplus.generator.config.querys.H2Query;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 配置汇总 传递给文件生成工具
 *
 * @author YangHu, tangguo, hubin, Juzi
 * @since 2016-08-30
 */
@Data
@Accessors(chain = true)
public class ConfigBuilder {

    /**
     * 模板路径配置信息
     */
    @Setter(value = AccessLevel.NONE)
    private final TemplateConfig template;
    /**
     * 数据库配置
     */
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private final DataSourceConfig dataSourceConfig;
    /**
     * SQL连接
     */
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private final Connection connection;
    /**
     * 数据库表信息
     */
    private final List<TableInfo> tableInfoList = new ArrayList<>();
    /**
     * 包配置详情
     */
    @Setter(value = AccessLevel.NONE)
    private final Map<String, String> packageInfo = new HashMap<>();
    /**
     * 路径配置信息
     */
    @Setter(value = AccessLevel.NONE)
    private final Map<String, String> pathInfo = new HashMap<>();
    /**
     * 策略配置
     */
    private StrategyConfig strategyConfig;
    /**
     * 全局配置信息
     */
    private GlobalConfig globalConfig;
    /**
     * 注入配置信息
     */
    private InjectionConfig injectionConfig;
    /**
     * 过滤正则
     */
    private static final Pattern REGX = Pattern.compile("[~!/@#$%^&*()-=+\\\\|[{}];:'\",<.>?]+");
    /**
     * 表数据查询
     */
    private final DecoratorDbQuery dbQuery;
    /**
     * 在构造器中处理配置
     *
     * @param packageConfig    包配置
     * @param dataSourceConfig 数据源配置
     * @param strategyConfig   表配置
     * @param template         模板配置
     * @param globalConfig     全局配置
     */
    public ConfigBuilder(PackageConfig packageConfig, DataSourceConfig dataSourceConfig, StrategyConfig strategyConfig,
                         TemplateConfig template, GlobalConfig globalConfig) {
        this.connection = dataSourceConfig.getConn();
        this.dataSourceConfig = dataSourceConfig;
        this.dbQuery = (DecoratorDbQuery) dataSourceConfig.getDbQuery();
        this.globalConfig = globalConfig;
        this.template = template;
        this.packageInfo.putAll(packageConfig.initPackageInfo());
        this.pathInfo.putAll(Optional.ofNullable(packageConfig.getPathInfo()).orElseGet(() -> this.template.initTemplate(this.globalConfig, packageInfo)));
        this.strategyConfig = strategyConfig;
        this.strategyConfig.validate();
        this.tableInfoList.addAll(getTablesInfo());
        processTable();
    }

    /**
     * 处理表对应的类名称
     */
    private void processTable() {
        for (TableInfo tableInfo : tableInfoList) {
            String entityName = strategyConfig.getNameConvert().entityNameConvert(tableInfo);
            if (StringUtils.isNotBlank(globalConfig.getEntityName())) {
                tableInfo.setConvert(true);
                tableInfo.setEntityName(String.format(globalConfig.getEntityName(), entityName));
            } else {
                tableInfo.setEntityName(strategyConfig, entityName);
            }
            if (StringUtils.isNotBlank(globalConfig.getMapperName())) {
                tableInfo.setMapperName(String.format(globalConfig.getMapperName(), entityName));
            } else {
                tableInfo.setMapperName(entityName + ConstVal.MAPPER);
            }
            if (StringUtils.isNotBlank(globalConfig.getXmlName())) {
                tableInfo.setXmlName(String.format(globalConfig.getXmlName(), entityName));
            } else {
                tableInfo.setXmlName(entityName + ConstVal.MAPPER);
            }
            if (StringUtils.isNotBlank(globalConfig.getServiceName())) {
                tableInfo.setServiceName(String.format(globalConfig.getServiceName(), entityName));
            } else {
                tableInfo.setServiceName("I" + entityName + ConstVal.SERVICE);
            }
            if (StringUtils.isNotBlank(globalConfig.getServiceImplName())) {
                tableInfo.setServiceImplName(String.format(globalConfig.getServiceImplName(), entityName));
            } else {
                tableInfo.setServiceImplName(entityName + ConstVal.SERVICE_IMPL);
            }
            if (StringUtils.isNotBlank(globalConfig.getControllerName())) {
                tableInfo.setControllerName(String.format(globalConfig.getControllerName(), entityName));
            } else {
                tableInfo.setControllerName(entityName + ConstVal.CONTROLLER);
            }
            // 检测导入包
            checkImportPackages(tableInfo);
        }
    }

    /**
     * 检测导入包
     *
     * @param tableInfo ignore
     */
    private void checkImportPackages(TableInfo tableInfo) {
        if (StringUtils.isNotBlank(strategyConfig.getSuperEntityClass())) {
            // 自定义父类
            tableInfo.getImportPackages().add(strategyConfig.getSuperEntityClass());
        } else if (globalConfig.isActiveRecord()) {
            // 无父类开启 AR 模式
            tableInfo.getImportPackages().add(com.baomidou.mybatisplus.extension.activerecord.Model.class.getCanonicalName());
        }
        if (null != globalConfig.getIdType() && tableInfo.isHavePrimaryKey()) {
            // 指定需要 IdType 场景
            tableInfo.getImportPackages().add(com.baomidou.mybatisplus.annotation.IdType.class.getCanonicalName());
            tableInfo.getImportPackages().add(com.baomidou.mybatisplus.annotation.TableId.class.getCanonicalName());
        }
        if (StringUtils.isNotBlank(strategyConfig.getVersionFieldName())
            && CollectionUtils.isNotEmpty(tableInfo.getFields())) {
            tableInfo.getFields().forEach(f -> {
                if (strategyConfig.getVersionFieldName().equals(f.getName())) {
                    tableInfo.getImportPackages().add(com.baomidou.mybatisplus.annotation.Version.class.getCanonicalName());
                }
            });
        }
    }


    /**
     * 获取所有的数据库表信息
     */
    private List<TableInfo> getTablesInfo() {
        boolean isInclude = strategyConfig.getInclude().size() > 0;
        boolean isExclude = strategyConfig.getExclude().size() > 0;
        //所有的表信息
        List<TableInfo> tableList = new ArrayList<>();

        //需要反向生成或排除的表信息
        List<TableInfo> includeTableList = new ArrayList<>();
        List<TableInfo> excludeTableList = new ArrayList<>();

        try {
            String tablesSql = dbQuery.tablesSql();
            StringBuilder sql = new StringBuilder(tablesSql);
            if (strategyConfig.isEnableSqlFilter()) {
                if (strategyConfig.getLikeTable() != null) {
                    sql.append(" AND ").append(dbQuery.tableName()).append(" LIKE '").append(strategyConfig.getLikeTable().getValue()).append("'");
                } else if (strategyConfig.getNotLikeTable() != null) {
                    sql.append(" AND ").append(dbQuery.tableName()).append(" NOT LIKE '").append(strategyConfig.getNotLikeTable().getValue()).append("'");
                }
                if (isInclude) {
                    sql.append(" AND ").append(dbQuery.tableName()).append(" IN (")
                        .append(strategyConfig.getInclude().stream().map(tb -> "'" + tb + "'").collect(Collectors.joining(","))).append(")");
                } else if (isExclude) {
                    sql.append(" AND ").append(dbQuery.tableName()).append(" NOT IN (")
                        .append(strategyConfig.getExclude().stream().map(tb -> "'" + tb + "'").collect(Collectors.joining(","))).append(")");
                }
            }
            TableInfo tableInfo;
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());
                 ResultSet results = preparedStatement.executeQuery()) {
                while (results.next()) {
                    final String tableName = results.getString(dbQuery.tableName());
                    if (StringUtils.isBlank(tableName)) {
                        System.err.println("当前数据库为空！！！");
                        continue;
                    }
                    tableInfo = new TableInfo();
                    tableInfo.setName(tableName);
                    String tableComment = dbQuery.getTableComment(results);
                    if (strategyConfig.isSkipView() && "VIEW".equals(tableComment)) {
                        // 跳过视图
                        continue;
                    }
                    tableInfo.setComment(tableComment);
                    if (isInclude && strategyConfig.matchIncludeTable(tableName)) {
                        includeTableList.add(tableInfo);
                    } else if (isExclude && strategyConfig.matchExcludeTable(tableName)) {
                        excludeTableList.add(tableInfo);
                    }
                    tableList.add(tableInfo);
                }
            }
            //TODO 我要把这个打印不存在表的功能和正则匹配功能删掉，就算是苗老板来了也拦不住的那种
            if (isExclude || isInclude) {
                Set<String> notExistTables = new HashSet<>();
                notExistTables.addAll(strategyConfig.getExclude());
                notExistTables.addAll(strategyConfig.getInclude());
                notExistTables = notExistTables.stream().filter(s -> !REGX.matcher(s).find()).collect(Collectors.toSet());
                // 将已经存在的表移除，获取配置中数据库不存在的表
                for (TableInfo tabInfo : tableList) {
                    if (notExistTables.isEmpty()) {
                        break;
                    }
                    notExistTables.remove(tabInfo.getName());
                }
                if (notExistTables.size() > 0) {
                    System.err.println("表 " + notExistTables + " 在数据库中不存在！！！");
                }
                // 需要反向生成的表信息
                if (isExclude) {
                    tableList.removeAll(excludeTableList);
                } else {
                    tableList = includeTableList;
                }
            }
            // 性能优化，只处理需执行表字段 github issues/219
            tableList.forEach(this::convertTableFields);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tableList;
    }

    /**
     * 将字段信息与表信息关联
     *
     * @param tableInfo 表信息
     * @return ignore
     */
    private TableInfo convertTableFields(TableInfo tableInfo) {
        List<TableField> fieldList = new ArrayList<>();
        List<TableField> commonFieldList = new ArrayList<>();
        DbType dbType = this.dataSourceConfig.getDbType();
        String tableName = tableInfo.getName();
        try {
            String tableFieldsSql = dbQuery.tableFieldsSql(tableName);
            Set<String> h2PkColumns = new HashSet<>();
            if (DbType.H2 == dbType) {
                try (PreparedStatement pkQueryStmt = connection.prepareStatement(String.format(H2Query.PK_QUERY_SQL, tableName));
                     ResultSet pkResults = pkQueryStmt.executeQuery()) {
                    while (pkResults.next()) {
                        String primaryKey = pkResults.getString(dbQuery.fieldKey());
                        if (Boolean.parseBoolean(primaryKey)) {
                            h2PkColumns.add(pkResults.getString(dbQuery.fieldName()));
                        }
                    }
                }
            }
            try (
                PreparedStatement preparedStatement = connection.prepareStatement(tableFieldsSql);
                ResultSet results = preparedStatement.executeQuery()) {
                while (results.next()) {
                    TableField field = new TableField();
                    String columnName = results.getString(dbQuery.fieldName());
                    // 避免多重主键设置，目前只取第一个找到ID，并放到list中的索引为0的位置
                    boolean isId;
                    if (DbType.H2 == dbType) {
                        isId = h2PkColumns.contains(columnName);
                    } else {
                        String key = results.getString(dbQuery.fieldKey());
                        if (DbType.DB2 == dbType || DbType.SQLITE == dbType) {
                            isId = StringUtils.isNotBlank(key) && "1".equals(key);
                        } else {
                            isId = StringUtils.isNotBlank(key) && "PRI".equals(key.toUpperCase());
                        }
                    }

                    // 处理ID
                    if (isId) {
                        field.setKeyFlag(true);
                        tableInfo.setHavePrimaryKey(true);
                        field.setKeyIdentityFlag(dbQuery.isKeyIdentity(results));
                    } else {
                        field.setKeyFlag(false);
                    }
                    // 自定义字段查询
                    String[] fcs = dbQuery.fieldCustom();
                    if (null != fcs) {
                        Map<String, Object> customMap = CollectionUtils.newHashMapWithExpectedSize(fcs.length);
                        for (String fc : fcs) {
                            customMap.put(fc, results.getObject(fc));
                        }
                        field.setCustomMap(customMap);
                    }
                    // 处理其它信息
                    field.setName(columnName);
                    String newColumnName = columnName;
                    IKeyWordsHandler keyWordsHandler = dataSourceConfig.getKeyWordsHandler();
                    if (keyWordsHandler != null && keyWordsHandler.isKeyWords(columnName)) {
                        System.err.printf("当前表[%s]存在字段[%s]为数据库关键字或保留字!%n", tableName, columnName);
                        field.setKeyWords(true);
                        newColumnName = keyWordsHandler.formatColumn(columnName);
                    }
                    field.setColumnName(newColumnName);
                    field.setType(results.getString(dbQuery.fieldType()));
                    field.setPropertyName(strategyConfig.getNameConvert().propertyNameConvert(field));
                    field.setColumnType(dataSourceConfig.getTypeConvert().processTypeConvert(globalConfig, field));
                    field.setComment(dbQuery.getFiledComment(results));
                    // 填充逻辑判断
                    strategyConfig.getTableFillList()
                        .stream()
                        //忽略大写字段问题
                        .filter(tf -> tf.getFieldName().equalsIgnoreCase(field.getName()))
                        .findFirst().ifPresent(tf -> field.setFill(tf.getFieldFill().name()));
                    if (strategyConfig.includeSuperEntityColumns(field.getName())) {
                        // 跳过公共字段
                        commonFieldList.add(field);
                        continue;
                    }
                    fieldList.add(field);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        tableInfo.setFields(fieldList);
        tableInfo.setCommonFields(commonFieldList);
        return tableInfo;
    }

    /**
     * 格式化数据库注释内容
     *
     * @param comment 注释
     * @return 注释
     * @see DecoratorDbQuery#formatComment(java.lang.String)
     * @since 3.4.0
     * @deprecated 3.4.1
     */
    @Deprecated
    public String formatComment(String comment) {
        return dbQuery.formatComment(comment);
    }

    public void close() {
        //暂时只有数据库连接需要关闭
        Optional.ofNullable(connection).ifPresent((con) -> {
            try {
                con.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        });
    }


    /**
     * 不再建议调用此方法，后续不再公开此方法.
     *
     * @param tableInfoList tableInfoList
     * @return configBuild
     * @deprecated 3.4.1 {@link #getTableInfoList()} 返回引用，如果有需要请直接操作
     */
    @Deprecated
    public ConfigBuilder setTableInfoList(List<TableInfo> tableInfoList) {
        this.tableInfoList.addAll(tableInfoList);
        return this;
    }
}
