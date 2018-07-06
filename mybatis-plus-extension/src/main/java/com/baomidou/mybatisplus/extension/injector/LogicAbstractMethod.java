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
package com.baomidou.mybatisplus.extension.injector;

import java.util.List;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

/**
 * <p>
 * 抽象的注入方法类
 * </p>
 *
 * @author hubin
 * @since 2018-04-06
 */
public abstract class LogicAbstractMethod extends AbstractMethod {

    public LogicAbstractMethod() {
        // TO DO NOTHING
    }

    /**
     * <p>
     * SQL 更新 set 语句
     * </p>
     *
     * @param table 表信息
     * @return sql and 片段
     */
    public String getLogicDeleteSql(boolean startWithAnd, TableInfo table) {
        StringBuilder sql = new StringBuilder();
        List<TableFieldInfo> fieldList = table.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            if (fieldInfo.isLogicDelete()) {
                if (startWithAnd) {
                    sql.append(" AND ");
                }
                sql.append(fieldInfo.getColumn());
                if (StringUtils.isCharSequence(fieldInfo.getPropertyType())) {
                    sql.append("='").append(fieldInfo.getLogicNotDeleteValue()).append("'");
                } else {
                    sql.append("=").append(fieldInfo.getLogicNotDeleteValue());
                }
            }
        }
        return sql.toString();
    }

    /**
     * <p>
     * SQL 更新 set 语句
     * </p>
     *
     * @param table 表信息
     * @return sql set 片段
     */
    protected String sqlLogicSet(TableInfo table) {
        List<TableFieldInfo> fieldList = table.getFieldList();
        StringBuilder set = new StringBuilder("SET ");
        int i = 0;
        for (TableFieldInfo fieldInfo : fieldList) {
            if (fieldInfo.isLogicDelete()) {
                if (++i > 1) {
                    set.append(",");
                }
                set.append(fieldInfo.getColumn()).append("=");
                if (StringUtils.isCharSequence(fieldInfo.getPropertyType())) {
                    set.append("'").append(fieldInfo.getLogicDeleteValue()).append("'");
                } else {
                    set.append(fieldInfo.getLogicDeleteValue());
                }
            }
        }
        return set.toString();
    }

    // ------------ 处理逻辑删除条件过滤 ------------

    @Override
    protected String sqlWhereEntityWrapper(TableInfo table) {
        if (table.isLogicDelete()) {
            StringBuilder where = new StringBuilder(128);
            where.append("<choose><when test=\"ew!=null and !ew.emptyOfWhere\">");
            where.append("<trim prefix=\"WHERE\" prefixOverrides=\"AND|OR\">");
            where.append("<if test=\"ew.entity!=null\">");
            if (StringUtils.isNotEmpty(table.getKeyProperty())) {
                where.append("<if test=\"ew.entity.").append(table.getKeyProperty()).append("!=null\">");
                where.append(" AND ").append(table.getKeyColumn()).append("=#{ew.entity.");
                where.append(table.getKeyProperty()).append("}");
                where.append("</if>");
            }
            List<TableFieldInfo> fieldList = table.getFieldList();
            for (TableFieldInfo fieldInfo : fieldList) {
                where.append(convertIfTag(fieldInfo, "ew.entity.", false));
                where.append(" AND ").append(sqlCondition(fieldInfo.getCondition(),
                    fieldInfo.getColumn(), "ew.entity." + fieldInfo.getEl()));
                where.append(convertIfTag(fieldInfo, true));
            }
            where.append("</if>");
            // 删除逻辑
            where.append(getLogicDeleteSql(true, table));
            // SQL 片段
            where.append("<if test=\"ew.sqlSegment!=null and ew.sqlSegment!=''\"> AND ${ew.sqlSegment}</if>");
            where.append("</trim>");
            where.append("</when><otherwise>WHERE ");
            // 删除逻辑
            where.append(getLogicDeleteSql(false, table));
            where.append("</otherwise></choose>");
            return where.toString();
        }
        // 正常逻辑
        return super.sqlWhereEntityWrapper(table);
    }

    @Override
    protected String sqlWhereByMap(TableInfo table) {
        if (table.isLogicDelete()) {
            return new StringBuilder()
                .append("<trim prefix=\"WHERE\" prefixOverrides=\"AND\">")
                .append("<if test=\"cm!=null and !cm.isEmpty\">")
                .append("<foreach collection=\"cm\" index=\"k\" item=\"v\" separator=\"AND\">")
                .append("<choose><when test=\"v==null\"> ${k} IS NULL </when>")
                .append("<otherwise> ${k}=#{v} </otherwise></choose>")
                .append("</foreach>")
                .append("</if>")
                .append(getLogicDeleteSql(true, table))
                .append("</trim>")
                .toString();
        }
        // 正常逻辑
        return super.sqlWhereByMap(table);
    }

}
