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
package com.baomidou.mybatisplus.generator;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * 生成文件
 *
 * @author YangHu, tangguo, hubin
 * @since 2016-08-30
 */
@Data
@Accessors(chain = true)
public class AutoGenerator {
    private static final Logger logger = LoggerFactory.getLogger(AutoGenerator.class);

    /**
     * 配置信息
     */
    protected ConfigBuilder config;
    /**
     * 注入配置
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    protected InjectionConfig injectionConfig;
    /**
     * 数据源配置
     */
    private DataSourceConfig dataSource;
    /**
     * 数据库表配置
     */
    private StrategyConfig strategy;
    /**
     * 包 相关配置
     */
    private PackageConfig packageInfo;
    /**
     * 模板 相关配置
     */
    private TemplateConfig template;
    /**
     * 全局 相关配置
     */
    private GlobalConfig globalConfig;
    /**
     * 模板引擎
     */
    private AbstractTemplateEngine templateEngine;

    /**
     * 生成代码
     */
    public void execute() {
        logger.debug("==========================准备生成文件...==========================");
        // 初始化配置
        if (null == config) {
            config = new ConfigBuilder(packageInfo, dataSource, strategy, template, globalConfig);
            if (null != injectionConfig) {
                injectionConfig.setConfig(config);
            }
        }
        if (null == templateEngine) {
            // 为了兼容之前逻辑，采用 Velocity 引擎 【 默认 】
            templateEngine = new VelocityTemplateEngine();
        }
        // 模板引擎初始化执行文件输出
        templateEngine.init(this.pretreatmentConfigBuilder(config)).mkdirs().batchOutput().open();
        logger.debug("==========================文件生成完成！！！==========================");
    }

    /**
     * 开放表信息、预留子类重写
     *
     * @param config 配置信息
     * @return ignore
     */
    protected List<TableInfo> getAllTableInfoList(ConfigBuilder config) {
        return config.getTableInfoList();
    }

    /**
     * 预处理配置
     *
     * @param config 总配置信息
     * @return 解析数据结果集
     */
    protected ConfigBuilder pretreatmentConfigBuilder(ConfigBuilder config) {
        /*
         * 注入自定义配置
         */
        if (null != injectionConfig) {
            injectionConfig.initMap();
            config.setInjectionConfig(injectionConfig);
        }
        /*
         * 表信息列表
         */
        List<TableInfo> tableList = this.getAllTableInfoList(config);
        for (TableInfo tableInfo : tableList) {
            /* ---------- 添加导入包 ---------- */
            if (config.getGlobalConfig().isActiveRecord()) {
                // 开启 ActiveRecord 模式
                tableInfo.setImportPackages(Model.class.getCanonicalName());
            }
            if (tableInfo.isConvert()) {
                // 表注解
                tableInfo.setImportPackages(TableName.class.getCanonicalName());
            }
            if (config.getStrategyConfig().getLogicDeleteFieldName() != null && tableInfo.isLogicDelete(config.getStrategyConfig().getLogicDeleteFieldName())) {
                // 逻辑删除注解
                tableInfo.setImportPackages(TableLogic.class.getCanonicalName());
            }
            if (StringUtils.isNotBlank(config.getStrategyConfig().getVersionFieldName())) {
                // 乐观锁注解
                tableInfo.setImportPackages(Version.class.getCanonicalName());
            }
            boolean importSerializable = true;
            if (StringUtils.isNotBlank(config.getSuperEntityClass())) {
                // 父实体
                tableInfo.setImportPackages(config.getSuperEntityClass());
                importSerializable = false;
            }
            if (config.getGlobalConfig().isActiveRecord()) {
                importSerializable = true;
            }
            if (importSerializable) {
                tableInfo.setImportPackages(Serializable.class.getCanonicalName());
            }
            // Boolean类型is前缀处理
            if (config.getStrategyConfig().isEntityBooleanColumnRemoveIsPrefix()
                && CollectionUtils.isNotEmpty(tableInfo.getFields())) {
                tableInfo.getFields().stream().filter(field -> "boolean".equalsIgnoreCase(field.getPropertyType()))
                    .filter(field -> field.getPropertyName().startsWith("is"))
                    .forEach(field -> {
                        field.setConvert(true);
                        field.setPropertyName(StringUtils.removePrefixAfterPrefixToLower(field.getPropertyName(), 2));
                    });
            }
        }
        return config.setTableInfoList(tableList);
    }

    public InjectionConfig getCfg() {
        return injectionConfig;
    }

    public AutoGenerator setCfg(InjectionConfig injectionConfig) {
        this.injectionConfig = injectionConfig;
        return this;
    }
}
