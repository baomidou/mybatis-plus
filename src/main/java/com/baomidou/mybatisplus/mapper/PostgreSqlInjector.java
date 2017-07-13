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

import java.util.Iterator;
import java.util.List;

import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.entity.TableFieldInfo;
import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * PostgreSql 自动注入器，处理字段大小写敏感，自动双引号转义。
 * </p>
 *
 * @author hubin
 * @Date 2017-07-11
 */
public class PostgreSqlInjector extends LogicSqlInjector {

    /**
     * <p>
     * select sql as 字段转换，默认原样返回，预留子类处理<br>
     * </p>
     *
     * @param columnStr 字段内容
     * @return
     */
    protected String sqlSelectAsColumnConvert(String columnStr) {
        GlobalConfiguration globalConfig = GlobalConfigUtils.getGlobalConfig(configuration);
        return String.format(globalConfig.getIdentifierQuote(), columnStr);
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
                    columns.append(table.getKeyColumn()).append(" AS ").append(sqlSelectAsColumnConvert(table.getKeyProperty()));
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
                        columns.append(" AS ").append(sqlSelectAsColumnConvert(wordConvert));
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
}
