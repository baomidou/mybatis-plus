/**
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
package com.baomidou.mybatisplus.plugins.parser.logicdelete;

import com.baomidou.mybatisplus.entity.TableFieldInfo;
import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 租户处理器
 * </p>
 *
 * @author hubin
 * @since 2017-08-31
 */
public class LogicDeleteDefaultHandler implements LogicDeleteHandler {

    private static final Map<String, TableFieldInfo> tableLogicDeleteMap = new ConcurrentHashMap<>();

    public LogicDeleteDefaultHandler() {
        if (tableLogicDeleteMap.isEmpty()) {
            List<TableInfo> tableInfos = TableInfoHelper.getTableInfos();
            for (TableInfo tableInfo : tableInfos) {
                List<TableFieldInfo> tableFieldInfos = tableInfo.getFieldList();
                for (TableFieldInfo tableFieldInfo: tableFieldInfos) {
                    if (tableFieldInfo.isLogicDelete()) {
                        tableLogicDeleteMap.put(tableInfo.getTableName(), tableFieldInfo);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public Expression getValue(String tableName) {
        if (String.class.equals(tableLogicDeleteMap.get(tableName).getPropertyType())) {
            return new StringValue(tableLogicDeleteMap.get(tableName).getLogicNotDeleteValue());
        } else {
            return new LongValue(tableLogicDeleteMap.get(tableName).getLogicNotDeleteValue());
        }
    }

    @Override
    public String getColumn(String tableName) {
        return tableLogicDeleteMap.get(tableName).getColumn();
    }

    @Override
    public boolean doTableFilter(String tableName) {
        return tableLogicDeleteMap.containsKey(tableName);
    }
}
