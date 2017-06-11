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
package com.baomidou.mybatisplus.entity;

import java.util.List;

import org.apache.ibatis.session.Configuration;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;

/**
 * <p>
 * 数据库表反射信息
 * </p>
 *
 * @author hubin
 * @Date 2016-01-23
 */
public class TableInfo {

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
     * <p>
     * 主键是否有存在字段名与属性名关联
     * </p>
     * true , false
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
     * <p>
     * 表主键ID Sequence
     * </p>
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
     * <p>
     * 获得注入的 SQL Statement
     * </p>
     *
     * @param sqlMethod
     *            MybatisPlus 支持 SQL 方法
     * @return
     */
    public String getSqlStatement(String sqlMethod) {
        StringBuilder statement = new StringBuilder();
        statement.append(currentNamespace);
        statement.append(".");
        statement.append(sqlMethod);
        return statement.toString();
    }

    public IdType getIdType() {
        return idType;
    }

    public void setIdType(IdType idType) {
        this.idType = idType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getResultMap() {
        return resultMap;
    }

    public void setResultMap(String resultMap) {
        this.resultMap = resultMap;
    }

    public boolean isKeyRelated() {
        return keyRelated;
    }

    public void setKeyRelated(boolean keyRelated) {
        this.keyRelated = keyRelated;
    }

    public String getKeyProperty() {
        return keyProperty;
    }

    public void setKeyProperty(String keyProperty) {
        this.keyProperty = keyProperty;
    }

    public String getKeyColumn() {
        return keyColumn;
    }

    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }

    public KeySequence getKeySequence() {
        return keySequence;
    }

    public void setKeySequence(KeySequence keySequence) {
        this.keySequence = keySequence;
    }

    public List<TableFieldInfo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(GlobalConfiguration globalConfig, List<TableFieldInfo> fieldList) {
        this.fieldList = fieldList;
        /*
         * 启动逻辑删除注入、判断该表是否启动
         */
        if (null != globalConfig.getLogicDeleteValue()) {
            for (TableFieldInfo tfi: fieldList) {
                if (tfi.isLogicDelete()) {
                    this.setLogicDelete(true);
                    break;
                }
            }
        }
    }

    public String getCurrentNamespace() {
        return currentNamespace;
    }

    public void setCurrentNamespace(String currentNamespace) {
        this.currentNamespace = currentNamespace;
    }

    public String getConfigMark() {
        return configMark;
    }

    public void setConfigMark(Configuration configuration) {
        if (configuration == null) {
            throw new MybatisPlusException("Error: You need Initialize MybatisConfiguration !");
        }
        this.configMark = configuration.toString();
    }

    public boolean isLogicDelete() {
        return logicDelete;
    }

    public void setLogicDelete(boolean logicDelete) {
        if (logicDelete) {
            // 非 true 不设置，默认 false
            this.logicDelete = logicDelete;
        }
    }
}
