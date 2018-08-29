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
package com.baomidou.mybatisplus.core.metadata;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.ibatis.session.Configuration;

import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * <p>
 * 数据库表反射信息
 * </p>
 *
 * @author hubin
 * @since 2016-01-23
 */
@Data
@Accessors(chain = true)
public class TableInfo {

    /**
     * 表主键ID 类型
     */
    private IdType idType = IdType.NONE;
    /**
     * 数据库类型
     */
    private DbType dbType;
    /**
     * 表名称
     */
    private String tableName;
    /**
     * 表映射结果集
     */
    private String resultMap;
    /**
     * 主键是否有存在字段名与属性名关联
     * true: 表示要进行 as
     */
    private boolean keyRelated = false;
    /**
     * 表主键ID 属性名
     */
    private String keyProperty;
    /**
     * 表主键ID 字段名
     */
    private String keyColumn;
    /**
     * 表主键ID Sequence
     */
    private KeySequence keySequence;
    /**
     * 表字段信息列表
     */
    private List<TableFieldInfo> fieldList;
    /**
     * 命名空间
     */
    private String currentNamespace;
    /**
     * MybatisConfiguration 标记 (Configuration内存地址值)
     */
    private String configMark;
    /**
     * 是否开启逻辑删除
     */
    private boolean logicDelete = false;
    /**
     * 是否开启下划线转驼峰
     */
    private boolean underCamel = true;
    /**
     * 标记该字段属于哪个类
     */
    private Class<?> clazz;
    /**
     * 缓存包含主键及字段的 sql select
     */
    @Setter(AccessLevel.NONE)
    private String allSqlSelect;
    /**
     * 缓存主键字段的 sql select
     */
    @Setter(AccessLevel.NONE)
    private String sqlSelect;

    /**
     * <p>
     * 获得注入的 SQL Statement
     * </p>
     *
     * @param sqlMethod MybatisPlus 支持 SQL 方法
     * @return SQL Statement
     */
    public String getSqlStatement(String sqlMethod) {
        return currentNamespace + StringPool.DOT + sqlMethod;
    }

    public void setConfigMark(Configuration configuration) {
        Assert.notNull(configuration, "Error: You need Initialize MybatisConfiguration !");
        this.configMark = configuration.toString();
    }

    public void setLogicDelete(boolean logicDelete) {
        if (logicDelete) {
            this.logicDelete = true;
        }
    }

    /**
     * 获取主键的 select sql 片段
     *
     * @return sql 片段
     */
    public String getKeySqlSelect() {
        if (sqlSelect != null) {
            return sqlSelect;
        }
        if (StringUtils.isNotEmpty(keyProperty)) {
            if (keyRelated) {
                sqlSelect = SqlUtils.sqlWordConvert(dbType, keyColumn, true) + " AS " +
                    SqlUtils.sqlWordConvert(dbType, keyProperty, false);
            } else {
                sqlSelect = SqlUtils.sqlWordConvert(dbType, keyColumn, true);
            }
        } else {
            sqlSelect = StringPool.EMPTY;
        }
        return sqlSelect;
    }

    /**
     * 获取包含主键及字段的 select sql 片段
     *
     * @return sql 片段
     */
    public String getAllSqlSelect() {
        if (allSqlSelect != null) {
            return allSqlSelect;
        }
        String sqlSelect = getKeySqlSelect();
        String fieldsSqlSelect = fieldList.stream().filter(TableFieldInfo::isSelect)
            .map(i -> i.getSqlSelect(dbType)).collect(joining(StringPool.COMMA));
        if (StringUtils.isNotEmpty(sqlSelect) && StringUtils.isNotEmpty(fieldsSqlSelect)) {
            allSqlSelect = sqlSelect + StringPool.COMMA + fieldsSqlSelect;
        } else if (StringUtils.isNotEmpty(fieldsSqlSelect)) {
            allSqlSelect = fieldsSqlSelect;
        } else {
            allSqlSelect = sqlSelect;
        }
        return allSqlSelect;
    }

    /**
     * 获取 inset 时候主键 sql 脚本片段
     * insert into table (字段) values (值)
     * 位于 "值" 部位
     *
     * @return sql 脚本片段
     */
    public String getKeyInsertSqlProperty() {
        if (StringUtils.isNotEmpty(keyProperty)) {
            if (idType == IdType.AUTO) {
                return StringPool.EMPTY;
            }
            return SqlScriptUtils.safeParam(keyProperty) + StringPool.COMMA + StringPool.NEWLINE;
        }
        return StringPool.EMPTY;
    }

    /**
     * 获取 inset 时候主键 sql 脚本片段
     * insert into table (字段) values (值)
     * 位于 "字段" 部位
     *
     * @return sql 脚本片段
     */
    public String getKeyInsertSqlColumn() {
        if (StringUtils.isNotEmpty(keyColumn)) {
            if (idType == IdType.AUTO) {
                return StringPool.EMPTY;
            }
            return keyColumn + StringPool.COMMA + StringPool.NEWLINE;
        }
        return StringPool.EMPTY;
    }


    /**
     * 获取所有 inset 时候插入值 sql 脚本片段
     * insert into table (字段) values (值)
     * 位于 "值" 部位
     *
     * @return sql 脚本片段
     */
    public String getAllInsertSqlProperty() {
        return getKeyInsertSqlProperty() + fieldList.stream().map(TableFieldInfo::getInsertSqlProperty)
            .collect(joining(StringPool.NEWLINE));
    }

    /**
     * 获取 inset 时候字段 sql 脚本片段
     * insert into table (字段) values (值)
     * 位于 "字段" 部位
     *
     * @return sql 脚本片段
     */
    public String getAllInsertSqlColumn() {
        return getKeyInsertSqlColumn() + fieldList.stream().map(TableFieldInfo::getInsertSqlColumn)
            .collect(joining(StringPool.NEWLINE));
    }

    /**
     * 获取所有的查询的 sql 片段
     *
     * @param ignoreLogicDelFiled 是否过滤掉逻辑删除字段
     * @param withId              是否包含 id 项
     * @param prefix              前缀
     * @return sql 脚本片段
     */
    public String getAllSqlWhere(boolean ignoreLogicDelFiled, boolean withId, final String prefix) {
        String newPrefix = prefix == null ? StringPool.EMPTY : prefix;
        String filedSqlScript = fieldList.stream()
            .filter(i -> {
                if (ignoreLogicDelFiled) {
                    return !(isLogicDelete() && i.isLogicDelete());
                }
                return true;
            })
            .map(i -> i.getSqlWhere(newPrefix)).collect(joining(StringPool.NEWLINE));
        if (!withId) {
            return filedSqlScript;
        }
        String newKeyProperty = newPrefix + keyProperty;
        String keySqlScript = keyColumn + StringPool.EQUALS + SqlScriptUtils.safeParam(newKeyProperty);
        return SqlScriptUtils.convertIf(keySqlScript, String.format("%s != null", newKeyProperty), false) +
            StringPool.NEWLINE + filedSqlScript;
    }

    /**
     * 获取所有的 sql set 片段
     *
     * @param ignoreLogicDelFiled 是否过滤掉逻辑删除字段
     * @param prefix              前缀
     * @return sql 脚本片段
     */
    public String getAllSqlSet(boolean ignoreLogicDelFiled, final String prefix) {
        String newPrefix = prefix == null ? StringPool.EMPTY : prefix;
        return fieldList.stream()
            .filter(i -> {
                if (ignoreLogicDelFiled) {
                    return !(isLogicDelete() && i.isLogicDelete());
                }
                return true;
            })
            .map(i -> i.getSqlSet(newPrefix)).collect(joining(StringPool.NEWLINE));
    }

    /**
     * 获取逻辑删除字段的 sql 脚本
     *
     * @param startWithAnd 是否以 and 开头
     * @param deleteValue  是否需要的是逻辑删除值
     * @return sql 脚本
     */
    public String getLogicDeleteSql(boolean startWithAnd, boolean deleteValue) {
        if (isLogicDelete()) {
            TableFieldInfo field = fieldList.stream().filter(TableFieldInfo::isLogicDelete).findFirst()
                .orElseThrow(() -> ExceptionUtils.mpe(String.format("can't find the logicFiled from table {%s}", tableName)));
            String formatStr = field.isCharSequence() ? "'%s'" : "%s";
            String logicDeleteSql = field.getColumn() + StringPool.EQUALS +
                String.format(formatStr, deleteValue ? field.getLogicDeleteValue() : field.getLogicNotDeleteValue());
            if (startWithAnd) {
                logicDeleteSql = " AND " + logicDeleteSql;
            }
            return logicDeleteSql;
        }
        return StringPool.EMPTY;
    }
}
