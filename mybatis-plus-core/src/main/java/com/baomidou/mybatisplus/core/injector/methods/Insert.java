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

import java.util.List;

import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;

/**
 * <p>
 * 根据 ID 删除
 * </p>
 *
 * @author hubin
 * @since 2018-04-06
 */
public class Insert extends AbstractMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        KeyGenerator keyGenerator = new NoKeyGenerator();
        StringBuilder fieldBuilder = new StringBuilder();
        StringBuilder placeholderBuilder = new StringBuilder();
        SqlMethod sqlMethod = SqlMethod.INSERT_ONE;

        fieldBuilder.append("\n<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        placeholderBuilder.append("\n<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        String keyProperty = null;
        String keyColumn = null;

        // 表包含主键处理逻辑,如果不包含主键当普通字段处理
        if (StringUtils.isNotEmpty(tableInfo.getKeyProperty())) {
            if (tableInfo.getIdType() == IdType.AUTO) {
                /** 自增主键 */
                keyGenerator = new Jdbc3KeyGenerator();
                keyProperty = tableInfo.getKeyProperty();
                keyColumn = tableInfo.getKeyColumn();
            } else {
                if (null != tableInfo.getKeySequence()) {
                    keyGenerator = TableInfoHelper.genKeyGenerator(tableInfo, builderAssistant, sqlMethod.getMethod(), languageDriver);
                    keyProperty = tableInfo.getKeyProperty();
                    keyColumn = tableInfo.getKeyColumn();
                    fieldBuilder.append(tableInfo.getKeyColumn()).append(",");
                    placeholderBuilder.append("#{").append(tableInfo.getKeyProperty()).append("},");
                } else {
                    /** 用户输入自定义ID */
                    fieldBuilder.append(tableInfo.getKeyColumn()).append(",");
                    // 正常自定义主键策略
                    placeholderBuilder.append("#{").append(tableInfo.getKeyProperty()).append("},");
                }
            }
        }

        // 是否 IF 标签判断
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            // 在FieldIgnore,INSERT_UPDATE,INSERT 时设置为false
            if (FieldFill.INSERT == fieldInfo.getFieldFill()
                || FieldFill.INSERT_UPDATE == fieldInfo.getFieldFill()) {
                fieldBuilder.append(fieldInfo.getColumn()).append(",");
                placeholderBuilder.append("#{").append(fieldInfo.getEl()).append("},");
            } else {
                fieldBuilder.append(convertIfTagIgnored(fieldInfo, false));
                fieldBuilder.append(fieldInfo.getColumn()).append(",");
                fieldBuilder.append(convertIfTagIgnored(fieldInfo, true));
                placeholderBuilder.append(convertIfTagIgnored(fieldInfo, false));
                placeholderBuilder.append("#{").append(fieldInfo.getEl()).append("},");
                placeholderBuilder.append(convertIfTagIgnored(fieldInfo, true));
            }
        }
        fieldBuilder.append("\n</trim>");
        placeholderBuilder.append("\n</trim>");
        String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), fieldBuilder.toString(), placeholderBuilder.toString());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource, keyGenerator, keyProperty, keyColumn);
    }
}
