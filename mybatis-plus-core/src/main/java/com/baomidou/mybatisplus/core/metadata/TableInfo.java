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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.session.Configuration;

import java.util.List;

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
     * todo 秋秋来把注释写上
     */
    private Class<?> parentClass;

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

    public void setFieldList(GlobalConfig globalConfig, List<TableFieldInfo> fieldList) {
        this.fieldList = fieldList;
        /*
         * 启动逻辑删除注入、判断该表是否启动
         */
        if (null != globalConfig.getDbConfig().getLogicDeleteValue()) {
            for (TableFieldInfo tfi : fieldList) {
                if (tfi.isLogicDelete()) {
                    this.setLogicDelete(true);
                    break;
                }
            }
        }
    }

    public void setFieldList(List<TableFieldInfo> fieldList) {
        throw ExceptionUtils.mpe("you can't use this method to set fieldList !");
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
}
