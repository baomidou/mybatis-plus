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
import com.baomidou.mybatisplus.core.toolkit.StringPool;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigBuilder.class);

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
     * 数据库表信息
     */
    private final List<TableInfo> tableInfoList = new ArrayList<>();
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
    private static final Pattern REGX = Pattern.compile("[~!/@#$%^&*()+\\\\\\[\\]|{};:'\",<.>?]+");
    /**
     * 表数据查询
     */
    private final DecoratorDbQuery dbQuery;
    /**
     * 包配置信息
     */
    private final PackageConfig packageConfig;

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
        this.strategyConfig = Optional.ofNullable(strategyConfig).orElseGet(StrategyConfig::new);
        //TODO 先把验证插在这里，后续改成build构建的话在build的时候验证
        this.strategyConfig.validate();
        this.dataSourceConfig = dataSourceConfig;
        this.dbQuery = new DecoratorDbQuery(dataSourceConfig.getDbQuery(), dataSourceConfig, strategyConfig);
        this.globalConfig = Optional.ofNullable(globalConfig).orElseGet(GlobalConfig::new);
        this.template = Optional.ofNullable(template).orElseGet(TemplateConfig::new);
        this.packageConfig = Optional.ofNullable(packageConfig).orElseGet(PackageConfig::new);
        this.pathInfo.putAll(new PathInfoHandler(this.globalConfig, this.template, this.packageConfig).getPathInfo());
        this.tableInfoList.addAll(getTablesInfo());
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
            dbQuery.query(dbQuery.tablesSql(), result -> {
                String tableName = result.getStringResult(dbQuery.tableName());
                if (StringUtils.isNotBlank(tableName)) {
                    TableInfo tableInfo = new TableInfo();
                    tableInfo.setName(tableName);
                    String tableComment = result.getTableComment();
                    // 跳过视图
                    if (!(strategyConfig.isSkipView() && "VIEW".equals(tableComment))) {
                        tableInfo.setComment(tableComment);
                        if (isInclude && strategyConfig.matchIncludeTable(tableName)) {
                            includeTableList.add(tableInfo);
                        } else if (isExclude && strategyConfig.matchExcludeTable(tableName)) {
                            excludeTableList.add(tableInfo);
                        }
                        tableList.add(tableInfo);
                    }
                }
            });
            //TODO 我要把这个打印不存在表的功能和正则匹配功能删掉，就算是苗老板来了也拦不住的那种
            if (isExclude || isInclude) {
                Map<String, String> notExistTables = new HashSet<>(isExclude ? strategyConfig.getExclude() : strategyConfig.getInclude())
                    .stream()
                    .filter(s -> !matcherRegTable(s))
                    .collect(Collectors.toMap(String::toLowerCase, s -> s, (o, n) -> n));
                // 将已经存在的表移除，获取配置中数据库不存在的表
                for (TableInfo tabInfo : tableList) {
                    if (notExistTables.isEmpty()) {
                        break;
                    }
                    //解决可能大小写不敏感的情况导致无法移除掉
                    notExistTables.remove(tabInfo.getName().toLowerCase());
                }
                if (notExistTables.size() > 0) {
                    LOGGER.warn("表[{}]在数据库中不存在！！！", String.join(StringPool.COMMA, notExistTables.values()));
                }
                // 需要反向生成的表信息
                if (isExclude) {
                    tableList.removeAll(excludeTableList);
                } else {
                    tableList.clear();
                    tableList.addAll(includeTableList);
                }
            }
            // 性能优化，只处理需执行表字段 github issues/219
            tableList.forEach(this::convertTableFields);
            // 数据库操作完成,释放连接对象
            dbQuery.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tableList;
    }

    /**
     * 将字段信息与表信息关联
     *
     * @param tableInfo 表信息
     */
    private void convertTableFields(TableInfo tableInfo) {
        List<TableField> fieldList = new ArrayList<>();
        List<TableField> commonFieldList = new ArrayList<>();
        DbType dbType = this.dataSourceConfig.getDbType();
        String tableName = tableInfo.getName();
        try {
            String tableFieldsSql = dbQuery.tableFieldsSql(tableName);
            Set<String> h2PkColumns = new HashSet<>();
            if (DbType.H2 == dbType) {
                dbQuery.query(String.format(H2Query.PK_QUERY_SQL, tableName), result -> {
                    String primaryKey = result.getStringResult(dbQuery.fieldKey());
                    if (Boolean.parseBoolean(primaryKey)) {
                        h2PkColumns.add(result.getStringResult(dbQuery.fieldName()));
                    }
                });
            }
            dbQuery.query(tableFieldsSql, result -> {
                TableField field = new TableField();
                String columnName = result.getStringResult(dbQuery.fieldName());
                // 避免多重主键设置，目前只取第一个找到ID，并放到list中的索引为0的位置
                boolean isId = DbType.H2 == dbType ? h2PkColumns.contains(columnName) : result.isPrimaryKey();
                // 处理ID
                if (isId) {
                    field.setKeyFlag(true).setKeyIdentityFlag(dbQuery.isKeyIdentity(result.getResultSet()));
                    tableInfo.setHavePrimaryKey(true);
                }
                String newColumnName = columnName;
                IKeyWordsHandler keyWordsHandler = dataSourceConfig.getKeyWordsHandler();
                if (keyWordsHandler != null && keyWordsHandler.isKeyWords(columnName)) {
                    LOGGER.warn("当前表[{}]存在字段[{}]为数据库关键字或保留字!", tableName, columnName);
                    field.setKeyWords(true);
                    newColumnName = keyWordsHandler.formatColumn(columnName);
                }
                field.setName(columnName).setColumnName(newColumnName)
                    .setType(result.getStringResult(dbQuery.fieldType()))
                    .setPropertyName(strategyConfig.getNameConvert().propertyNameConvert(field), this.strategyConfig, dataSourceConfig.getTypeConvert().processTypeConvert(globalConfig, field))
                    .setComment(result.getFiledComment())
                    .setCustomMap(dbQuery.getCustomFields(result.getResultSet()));
                // 填充逻辑判断
                strategyConfig.getTableFillList().stream()
                    //忽略大写字段问题
                    .filter(tf -> tf.getFieldName().equalsIgnoreCase(field.getName()))
                    .findFirst().ifPresent(tf -> field.setFill(tf.getFieldFill().name()));
                if (strategyConfig.matchSuperEntityColumns(field.getName())) {
                    // 跳过公共字段
                    commonFieldList.add(field);
                } else {
                    fieldList.add(field);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        tableInfo.addFields(fieldList);
        tableInfo.addCommonFields(commonFieldList);
        tableInfo.processTable(strategyConfig, globalConfig);
    }

    /**
     * 格式化数据库注释内容
     *
     * @param comment 注释
     * @return 注释
     * @see DecoratorDbQuery.ResultSetWrapper#formatComment(java.lang.String)
     * @since 3.4.0
     * @deprecated 3.4.1
     */
    @Deprecated
    public String formatComment(String comment) {
        return StringUtils.isBlank(comment) ? StringPool.EMPTY : comment.replaceAll("\r\n", "\t");
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
        this.tableInfoList.clear(); //保持语义
        this.tableInfoList.addAll(tableInfoList);
        return this;
    }

    /**
     * 判断表名是否为正则表名(这表名规范比较随意,只能尽量匹配上特殊符号)
     *
     * @param tableName 表名
     * @return 是否正则
     * @since 3.4.1
     */
    public static boolean matcherRegTable(String tableName) {
        return REGX.matcher(tableName).find();
    }

    /**
     * 获取包配置信息
     *
     * @return 包配置信息
     * @see PackageConfig#getPackageInfo()
     * @deprecated 3.4.1
     */
    @Deprecated
    public Map<String, String> getPackageInfo() {
        return packageConfig.getPackageInfo();
    }

}
