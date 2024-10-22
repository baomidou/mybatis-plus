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
package com.baomidou.mybatisplus.core.metadata;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.session.Configuration;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static java.util.stream.Collectors.joining;

/**
 * 数据库表反射信息
 *
 * @author hubin
 * @since 2016-01-23
 */
@Data
@Setter(AccessLevel.PACKAGE)
@Accessors(chain = true)
public class TableInfo implements Constants {

    /**
     * 实体类型
     */
    private Class<?> entityType;
    /**
     * 表主键ID 类型
     */
    private IdType idType = IdType.NONE;
    /**
     * 表名称
     */
    private String tableName;
    /**
     * 表映射结果集
     */
    private String resultMap;
    /**
     * 是否是需要自动生成的 resultMap
     */
    private boolean autoInitResultMap;
    /**
     * 主键是否有存在字段名与属性名关联
     * <p>true: 表示要进行 as</p>
     */
    private boolean keyRelated;
    /**
     * 表主键ID 字段名
     */
    private String keyColumn;
    /**
     * 表主键ID 属性名
     */
    private String keyProperty;
    /**
     * 表主键ID 属性类型
     */
    private Class<?> keyType;
    /**
     * 表主键ID Sequence
     */
    private KeySequence keySequence;
    /**
     * 表字段信息列表
     */
    private List<TableFieldInfo> fieldList;
    /**
     * 命名空间 (对应的 mapper 接口的全类名)
     */
    private String currentNamespace;
    /**
     * MybatisConfiguration 标记 (Configuration内存地址值)
     *
     * @deprecated 3.5.3.2 初始化阶段可以使用一下,后期尽量避免在容器初始化完成之后再继续调用此方法
     */
    @Getter
    @Setter(AccessLevel.NONE)
    @Deprecated
    private Configuration configuration;
    /**
     * 是否开启下划线转驼峰
     * <p>
     * 未注解指定字段名的情况下,用于自动从 property 推算 column 的命名
     */
    private boolean underCamel;
    /**
     * 缓存包含主键及字段的 sql select
     */
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private String allSqlSelect;
    /**
     * 缓存主键字段的 sql select
     */
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private String sqlSelect;
    /**
     * 表字段是否启用了插入填充
     *
     * @since 3.3.0
     */
    @Getter
    @Setter(AccessLevel.NONE)
    private boolean withInsertFill;
    /**
     * 表字段是否启用了更新填充
     *
     * @since 3.3.0
     */
    @Getter
    @Setter(AccessLevel.NONE)
    private boolean withUpdateFill;
    /**
     * 表字段是否启用了逻辑删除
     *
     * @since 3.4.0
     */
    @Getter
    @Setter(AccessLevel.NONE)
    private boolean withLogicDelete;
    /**
     * 逻辑删除字段
     *
     * @since 3.4.0
     */
    @Getter
    @Setter(AccessLevel.NONE)
    private TableFieldInfo logicDeleteFieldInfo;
    /**
     * 表字段是否启用了乐观锁
     *
     * @since 3.3.1
     */
    @Getter
    @Setter(AccessLevel.NONE)
    private boolean withVersion;
    /**
     * 乐观锁字段
     *
     * @since 3.3.1
     */
    @Getter
    @Setter(AccessLevel.NONE)
    private TableFieldInfo versionFieldInfo;

    /**
     * 排序列表
     */
    @Setter
    private List<OrderFieldInfo> orderByFields;

    /**
     * @since 3.4.4
     */
    @Getter
    private Reflector reflector;

    /**
     * @param configuration 配置对象
     * @param entityType    实体类型
     * @since 3.4.4
     */
    public TableInfo(Configuration configuration, Class<?> entityType) {
        this.configuration = configuration;
        this.entityType = entityType;
        this.reflector = configuration.getReflectorFactory().findForClass(entityType);
        this.underCamel = configuration.isMapUnderscoreToCamelCase();
    }

    /**
     * 获得注入的 SQL Statement
     *
     * @param sqlMethod MybatisPlus 支持 SQL 方法
     * @return SQL Statement
     * @deprecated 3.4.0 如果存在的多mapper共用一个实体的情况，这里可能会出现获取命名空间错误的情况
     */
    @Deprecated
    public String getSqlStatement(String sqlMethod) {
        return currentNamespace + DOT + sqlMethod;
    }

    /**
     * 是否有主键
     *
     * @return 是否有
     */
    public boolean havePK() {
        return StringUtils.isNotBlank(keyColumn);
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
        if (havePK()) {
            sqlSelect = keyColumn;
            if (resultMap == null && keyRelated) {
                sqlSelect += (AS + keyProperty);
            }
        } else {
            sqlSelect = EMPTY;
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
        allSqlSelect = chooseSelect(TableFieldInfo::isSelect);
        return allSqlSelect;
    }

    /**
     * 获取需要进行查询的 select sql 片段
     *
     * @param predicate 过滤条件
     * @return sql 片段
     */
    public String chooseSelect(Predicate<TableFieldInfo> predicate) {
        String sqlSelect = getKeySqlSelect();
        String fieldsSqlSelect = fieldList.stream().filter(predicate)
            .map(TableFieldInfo::getSqlSelect).collect(joining(COMMA));
        if (StringUtils.isNotBlank(sqlSelect) && StringUtils.isNotBlank(fieldsSqlSelect)) {
            return sqlSelect + COMMA + fieldsSqlSelect;
        } else if (StringUtils.isNotBlank(fieldsSqlSelect)) {
            return fieldsSqlSelect;
        }
        return sqlSelect;
    }

    /**
     * 获取需要进行查询的 select sql 片段
     *
     * @param predicate 过滤条件
     * @return sql 片段
     */
    public String chooseSelect(Predicate<TableFieldInfo> predicate, List<String> noSelectProperty) {
        if (CollectionUtils.isEmpty(noSelectProperty)) {
            return chooseSelect(predicate);
        }
        String fieldsSqlSelect = fieldList.stream().filter(predicate)
            .filter(i -> !noSelectProperty.contains(i.getProperty()))
            .map(TableFieldInfo::getSqlSelect).collect(joining(COMMA));
        if (!havePK() || noSelectProperty.contains(keyProperty)) {
            return fieldsSqlSelect;
        } else {
            return getKeySqlSelect() + COMMA + fieldsSqlSelect;
        }
    }

    /**
     * 获取 insert 时候主键 sql 脚本片段
     * <p>insert into table (字段) values (值)</p>
     * <p>位于 "值" 部位</p>
     *
     * @return sql 脚本片段
     */
    public String getKeyInsertSqlProperty(final boolean batch, final String prefix, final boolean newLine) {
        final String newPrefix = prefix == null ? EMPTY : prefix;
        if (havePK()) {
            final String prefixKeyProperty = newPrefix + keyProperty;
            String keyColumn = SqlScriptUtils.safeParam(prefixKeyProperty) + COMMA;
            if (idType == IdType.AUTO) {
                if (batch) {
                    // 批量插入必须返回空自增情况下
                    return EMPTY;
                }
                return SqlScriptUtils.convertIf(keyColumn, String.format("%s != null", prefixKeyProperty), newLine);
            }
            return keyColumn + (newLine ? NEWLINE : EMPTY);
        }
        return EMPTY;
    }

    /**
     * 获取 insert 时候主键 sql 脚本片段
     * <p>insert into table (字段) values (值)</p>
     * <p>位于 "字段" 部位</p>
     *
     * @return sql 脚本片段
     */
    public String getKeyInsertSqlColumn(final boolean batch, final String prefix, final boolean newLine) {
        if (havePK()) {
            final String newPrefix = prefix == null ? EMPTY : prefix;
            final String prefixKeyProperty = newPrefix + keyProperty;
            if (idType == IdType.AUTO) {
                if (batch) {
                    // 批量插入必须返回空自增情况下
                    return EMPTY;
                }
                return SqlScriptUtils.convertIf(keyColumn + COMMA, String.format("%s != null", prefixKeyProperty), newLine);
            }
            return keyColumn + COMMA + (newLine ? NEWLINE : EMPTY);
        }
        return EMPTY;
    }

    /**
     * 获取所有 insert 时候插入值 sql 脚本片段
     * <p>insert into table (字段) values (值)</p>
     * <p>位于 "值" 部位</p>
     *
     * <li> 自动选部位,根据规则会生成 if 标签 </li>
     *
     * @return sql 脚本片段
     */
    public String getAllInsertSqlPropertyMaybeIf(final String prefix) {
        return getAllInsertSqlPropertyMaybeIf(prefix, false);
    }

    /**
     * 获取所有 insert 时候插入值 sql 脚本片段
     *
     * @param prefix                    前缀
     * @param ignoreAutoIncrementColumn 是否忽略自增主键字段
     * @return sql 脚本片段
     * @since 3.5.4
     */
    public String getAllInsertSqlPropertyMaybeIf(final String prefix, boolean ignoreAutoIncrementColumn) {
        final String newPrefix = prefix == null ? EMPTY : prefix;
        if (ignoreAutoIncrementColumn && idType == IdType.AUTO) {
            return fieldList.stream()
                .map(i -> i.getInsertSqlPropertyMaybeIf(newPrefix)).filter(Objects::nonNull).collect(joining(NEWLINE));
        }
        return getKeyInsertSqlProperty(false, newPrefix, true) + fieldList.stream()
            .map(i -> i.getInsertSqlPropertyMaybeIf(newPrefix)).filter(Objects::nonNull).collect(joining(NEWLINE));
    }

    /**
     * 获取 insert 时候字段 sql 脚本片段
     * <p>insert into table (字段) values (值)</p>
     * <p>位于 "字段" 部位</p>
     *
     * <li> 自动选部位,根据规则会生成 if 标签 </li>
     *
     * @return sql 脚本片段
     */
    public String getAllInsertSqlColumnMaybeIf(final String prefix) {
        return getAllInsertSqlColumnMaybeIf(prefix, false);
    }

    /**
     * 获取 insert 时候字段 sql 脚本片段
     *
     * @param prefix                    前缀
     * @param ignoreAutoIncrementColumn 是否忽略自增主键字段
     * @return sql脚本内容
     * @since 3.5.4
     */
    public String getAllInsertSqlColumnMaybeIf(final String prefix, boolean ignoreAutoIncrementColumn) {
        final String newPrefix = prefix == null ? EMPTY : prefix;
        if (ignoreAutoIncrementColumn && idType == IdType.AUTO) {
            return fieldList.stream().map(i -> i.getInsertSqlColumnMaybeIf(newPrefix))
                .filter(Objects::nonNull).collect(joining(NEWLINE));
        }
        return getKeyInsertSqlColumn(false, newPrefix, true) + fieldList.stream().map(i -> i.getInsertSqlColumnMaybeIf(newPrefix))
            .filter(Objects::nonNull).collect(joining(NEWLINE));
    }

    /**
     * 获取所有的查询的 sql 片段
     *
     * @param fistAnd             首个条件是否添加 AND 关键字
     * @param ignoreLogicDelFiled 是否过滤掉逻辑删除字段
     * @param withId              是否包含 id 项
     * @param prefix              前缀
     * @return sql 脚本片段
     */
    public String getAllSqlWhere(boolean fistAnd, boolean ignoreLogicDelFiled, boolean withId, final String prefix) {
        final String newPrefix = prefix == null ? EMPTY : prefix;
        String filedSqlScript = fieldList.stream()
            .filter(i -> {
                if (ignoreLogicDelFiled) {
                    return !(isWithLogicDelete() && i.isLogicDelete());
                }
                return true;
            })
            .map(i -> i.getSqlWhere(newPrefix)).filter(Objects::nonNull).collect(joining(NEWLINE));
        if (!withId || StringUtils.isBlank(keyProperty)) {
            return filedSqlScript;
        }
        String newKeyProperty = newPrefix + keyProperty;
        String keySqlScript = keyColumn + EQUALS + SqlScriptUtils.safeParam(newKeyProperty);
        return SqlScriptUtils.convertIf(fistAnd ? " AND " + keySqlScript : keySqlScript, String.format("%s != null", newKeyProperty), false)
            + NEWLINE + filedSqlScript;
    }

    /**
     * 获取所有的 sql set 片段
     *
     * @param ignoreLogicDelFiled 是否过滤掉逻辑删除字段
     * @param prefix              前缀
     * @return sql 脚本片段
     */
    public String getAllSqlSet(boolean ignoreLogicDelFiled, final String prefix) {
        final String newPrefix = prefix == null ? EMPTY : prefix;
        return fieldList.stream()
            .filter(i -> {
                if (ignoreLogicDelFiled) {
                    return !(isWithLogicDelete() && i.isLogicDelete());
                }
                return true;
            }).map(i -> i.getSqlSet(newPrefix)).filter(Objects::nonNull).collect(joining(NEWLINE));
    }

    /**
     * 获取逻辑删除字段的 sql 脚本
     *
     * @param startWithAnd 是否以 and 开头
     * @param isWhere      是否需要的是逻辑删除值
     * @return sql 脚本
     */
    public String getLogicDeleteSql(boolean startWithAnd, boolean isWhere) {
        if (withLogicDelete) {
            String logicDeleteSql = formatLogicDeleteSql(isWhere);
            if (startWithAnd) {
                logicDeleteSql = " AND " + logicDeleteSql;
            }
            return logicDeleteSql;
        }
        return EMPTY;
    }

    /**
     * format logic delete SQL, can be overrided by subclass
     * github #1386
     *
     * @param isWhere true: logicDeleteValue, false: logicNotDeleteValue
     * @return sql
     */
    protected String formatLogicDeleteSql(boolean isWhere) {
        final String value = isWhere ? logicDeleteFieldInfo.getLogicNotDeleteValue() : logicDeleteFieldInfo.getLogicDeleteValue();
        if (isWhere) {
            if (NULL.equalsIgnoreCase(value)) {
                return logicDeleteFieldInfo.getColumn() + " IS NULL";
            } else {
                return logicDeleteFieldInfo.getColumn() + EQUALS + String.format(logicDeleteFieldInfo.isCharSequence() ? "'%s'" : "%s", value);
            }
        }
        final String targetStr = logicDeleteFieldInfo.getColumn() + EQUALS;
        if (NULL.equalsIgnoreCase(value)) {
            return targetStr + NULL;
        } else {
            return targetStr + String.format(logicDeleteFieldInfo.isCharSequence() ? "'%s'" : "%s", value);
        }
    }

    /**
     * 自动构建 resultMap 并注入(如果条件符合的话)
     */
    void initResultMapIfNeed() {
        if (autoInitResultMap && null == resultMap) {
            String id = currentNamespace + DOT + MYBATIS_PLUS + UNDERSCORE + entityType.getSimpleName();
            List<ResultMapping> resultMappings = new ArrayList<>();
            if (havePK()) {
                ResultMapping idMapping = new ResultMapping.Builder(configuration, keyProperty, StringUtils.getTargetColumn(keyColumn), keyType)
                    .flags(Collections.singletonList(ResultFlag.ID)).build();
                resultMappings.add(idMapping);
            }
            if (CollectionUtils.isNotEmpty(fieldList)) {
                fieldList.forEach(i -> resultMappings.add(i.getResultMapping(configuration)));
            }
            ResultMap resultMap = new ResultMap.Builder(configuration, id, entityType, resultMappings).build();
            configuration.addResultMap(resultMap);
            this.resultMap = id;
        }
    }

    void setFieldList(List<TableFieldInfo> fieldList) {
        this.fieldList = fieldList;
        AtomicInteger logicDeleted = new AtomicInteger();
        AtomicInteger version = new AtomicInteger();
        fieldList.forEach(i -> {
            if (i.isLogicDelete()) {
                this.withLogicDelete = true;
                this.logicDeleteFieldInfo = i;
                logicDeleted.getAndAdd(1);
            }
            if (i.isWithInsertFill()) {
                this.withInsertFill = true;
            }
            if (i.isWithUpdateFill()) {
                this.withUpdateFill = true;
            }
            if (i.isVersion()) {
                this.withVersion = true;
                this.versionFieldInfo = i;
                version.getAndAdd(1);
            }
        });
        /* 校验字段合法性 */
        Assert.isTrue(logicDeleted.get() <= 1, "@TableLogic not support more than one in Class: \"%s\"", entityType.getName());
        Assert.isTrue(version.get() <= 1, "@Version not support more than one in Class: \"%s\"", entityType.getName());
    }

    public List<TableFieldInfo> getFieldList() {
        return Collections.unmodifiableList(fieldList);
    }

    public List<OrderFieldInfo> getOrderByFields() {
        if (null == this.orderByFields) {
            this.orderByFields = new LinkedList<>();
        }
        return this.orderByFields;
    }

    @Deprecated
    public boolean isLogicDelete() {
        return withLogicDelete;
    }

    /**
     * 获取对象属性值
     *
     * @param entity   对象
     * @param property 属性名
     * @return 属性值
     * @since 3.4.4
     */
    public Object getPropertyValue(Object entity, String property) {
        try {
            return this.reflector.getGetInvoker(property).invoke(entity, null);
        } catch (ReflectiveOperationException e) {
            throw ExceptionUtils.mpe("Error: Cannot read property in %s.  Cause:", e, entity.getClass().getSimpleName());
        }
    }

    /**
     * 设置对象属性值
     *
     * @param entity   实体对象
     * @param property 属性名
     * @param values   参数
     * @since 3.4.4
     */
    public void setPropertyValue(Object entity, String property, Object... values) {
        try {
            this.reflector.getSetInvoker(property).invoke(entity, values);
        } catch (ReflectiveOperationException e) {
            throw ExceptionUtils.mpe("Error: Cannot write property in %s.  Cause:", e, entity.getClass().getSimpleName());
        }
    }

    /**
     * 创建实例
     *
     * @param <T> 泛型
     * @return 初始化实例
     * @since 3.5.0
     */
    @SuppressWarnings("unchecked")
    public <T> T newInstance() {
        return (T) configuration.getObjectFactory().create(entityType);
    }

}
