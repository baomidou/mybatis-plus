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
package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.config.builder.Entity;
import com.baomidou.mybatisplus.generator.config.builder.Mapper;
import com.baomidou.mybatisplus.generator.config.rules.DateType;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 全局配置
 *
 * @author hubin
 * @since 2016-12-02
 */
@Data
@Accessors(chain = true)
public class GlobalConfig {

    /**
     * 生成文件的输出目录【 windows:D://  linux or mac:/tmp 】
     */
    private String outputDir = System.getProperty("os.name").toLowerCase().contains("windows") ? "D://" : "/tmp";

    /**
     * 是否覆盖已有文件
     */
    private boolean fileOverride = false;

    /**
     * 是否打开输出目录
     */
    private boolean open = true;

    /**
     * 是否在xml中添加二级缓存配置
     */
    @Deprecated
    private boolean enableCache = false;

    /**
     * 开发人员
     */
    private String author;

    /**
     * 开启 Kotlin 模式
     */
    private boolean kotlin = false;

    /**
     * 开启 swagger2 模式
     */
    private boolean swagger2 = false;

    /**
     * 开启 ActiveRecord 模式
     */
    @Deprecated
    private boolean activeRecord = false;

    /**
     * 开启 BaseResultMap
     */
    @Deprecated
    private boolean baseResultMap = false;

    /**
     * 时间类型对应策略
     */
    private DateType dateType = DateType.TIME_PACK;

    /**
     * 开启 baseColumnList
     */
    @Deprecated
    private boolean baseColumnList = false;
    /**
     * 各层文件名称方式，例如： %sAction 生成 UserAction
     * %s 为占位符
     */
    private String entityName;
    private String mapperName;
    private String xmlName;
    private String serviceName;
    private String serviceImplName;
    private String controllerName;

    /**
     * 后续不再公开此构造方法
     *
     * @see Builder
     * @deprecated 3.4.1
     */
    @Deprecated
    public GlobalConfig() {
    }

    /**
     * 指定生成的主键的ID类型
     */
    @Deprecated
    private IdType idType;

    /**
     * 是否开启 ActiveRecord 模式
     *
     * @return 是否开启
     * @see Entity#isActiveRecord()
     * @deprecated 3.4.1
     */
    public boolean isActiveRecord() {
        return activeRecord;
    }

    /**
     * 开启 ActiveRecord 模式
     *
     * @param activeRecord 是否开启
     * @return this
     * @see com.baomidou.mybatisplus.generator.config.builder.Entity.Builder#activeRecord(boolean)
     * @deprecated 3.4.1
     */
    @Deprecated
    public GlobalConfig setActiveRecord(boolean activeRecord) {
        this.activeRecord = activeRecord;
        return this;
    }

    /**
     * @return this
     * @see Entity#getIdType()
     * @deprecated 3.4.1
     */
    @Deprecated
    public IdType getIdType() {
        return idType;
    }

    /**
     * 指定生成的主键的ID类型
     *
     * @param idType 主键类型
     * @return this
     * @see Entity.Builder#idType(IdType)
     * @deprecated 3.4.1
     */
    @Deprecated
    public GlobalConfig setIdType(IdType idType) {
        this.idType = idType;
        return this;
    }

    /**
     * @return 是否开启
     * @see Mapper#isBaseResultMap() a
     * @deprecated 3.4.1
     */
    @Deprecated
    public boolean isBaseResultMap() {
        return baseResultMap;
    }

    /**
     * @param baseResultMap 是否开启
     * @return this
     * @see Mapper.Builder#baseResultMap(boolean)
     * @deprecated 3.4.1
     */
    @Deprecated
    public GlobalConfig setBaseResultMap(boolean baseResultMap) {
        this.baseResultMap = baseResultMap;
        return this;
    }

    /**
     * @return 是否开启
     * @see Mapper#isBaseColumnList()
     * @deprecated 3.4.1
     */
    @Deprecated
    public boolean isBaseColumnList() {
        return baseColumnList;
    }

    /**
     * @see Mapper.Builder#baseColumnList(boolean)
     * @param baseColumnList 是否开启
     * @return this
     */
    @Deprecated
    public GlobalConfig setBaseColumnList(boolean baseColumnList) {
        this.baseColumnList = baseColumnList;
        return this;
    }

    /**
     * @return 是否开启
     * @see Mapper#isEnableXmlCache()
     * @deprecated 3.4.1
     */
    @Deprecated
    public boolean isEnableCache() {
        return enableCache;
    }

    /**
     * @param enableCache 是否开启
     * @return this
     * @see Mapper.Builder#enableXmlCache(boolean)
     * @deprecated 3.4.1
     */
    @Deprecated
    public GlobalConfig setEnableCache(boolean enableCache) {
        this.enableCache = enableCache;
        return this;
    }

    /**
     * @param entityName
     * @return this
     * @see Builder#entityName(String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public GlobalConfig setEntityName(String entityName) {
        this.entityName = entityName;
        return this;
    }

    /**
     * @param mapperName
     * @return this
     * @see Builder#mapperName(String)
     * @deprecated 3.4.1
     */
    public GlobalConfig setMapperName(String mapperName) {
        this.mapperName = mapperName;
        return this;
    }

    /**
     * @param xmlName
     * @return this
     * @see Builder#xmlName(String)
     * @deprecated 3.4.1
     */
    public GlobalConfig setXmlName(String xmlName) {
        this.xmlName = xmlName;
        return this;
    }

    /**
     * @param serviceName
     * @return this
     * @see Builder#serviceName(String)
     * @deprecated 3.4.1
     */
    public GlobalConfig setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    /**
     * @param serviceImplName
     * @return this
     * @see Builder#serviceImplName(String)
     * @deprecated 3.4.1
     */
    public GlobalConfig setServiceImplName(String serviceImplName) {
        this.serviceImplName = serviceImplName;
        return this;
    }

    /**
     * @param controllerName
     * @return this
     * @see Builder#controllerName(String)
     * @deprecated 3.4.1
     */
    public GlobalConfig setControllerName(String controllerName) {
        this.controllerName = controllerName;
        return this;
    }

    /**
     * @param dateType
     * @return this
     * @see Builder#dateType(DateType)
     * @deprecated 3.4.1
     */
    public GlobalConfig setDateType(DateType dateType) {
        this.dateType = dateType;
        return this;
    }

    /**
     * 全局配置构建
     *
     * @author nieqiurong 2020/10/11.
     * @since 3.4.1
     */
    public static class Builder {

        private final GlobalConfig globalConfig = new GlobalConfig();

        /**
         * 开启 ActiveRecord 模式
         *
         * @param activeRecord 是否开启
         * @return this
         * @see com.baomidou.mybatisplus.generator.config.builder.Entity.Builder#activeRecord(boolean)
         * @deprecated 3.4.1
         */
        @Deprecated
        public Builder activeRecord(boolean activeRecord) {
            this.globalConfig.activeRecord = activeRecord;
            return this;
        }

        /**
         * 指定生成的主键的ID类型
         *
         * @param idType 主键类型
         * @return this
         * @see Entity.Builder#idType(IdType)
         * @deprecated 3.4.1
         */
        @Deprecated
        public Builder idType(IdType idType) {
            this.globalConfig.idType = idType;
            return this;
        }

        /**
         * 开启baseResultMap
         *
         * @param baseResultMap 是否开启
         * @return this
         * @see Mapper.Builder#baseResultMap(boolean)
         * @deprecated 3.4.1
         */
        @Deprecated
        public Builder baseResultMap(boolean baseResultMap) {
            this.globalConfig.baseResultMap = baseResultMap;
            return this;
        }

        /**
         * 开启baseColumnList
         *
         * @param baseColumnList 是否开启
         * @return this
         * @see Mapper.Builder#baseColumnList(boolean)
         * @deprecated 3.4.1
         */
        @Deprecated
        public Builder baseColumnList(boolean baseColumnList) {
            this.globalConfig.baseColumnList = baseColumnList;
            return this;
        }

        /**
         * xml中添加二级缓存配置
         *
         * @param enableCache 是否开启
         * @return this
         * @see Mapper.Builder#enableXmlCache(boolean)
         * @deprecated 3.4.1
         */
        @Deprecated
        public Builder enableCache(boolean enableCache) {
            this.globalConfig.enableCache = enableCache;
            return this;
        }

        public Builder fileOverride(boolean fileOverride) {
            this.globalConfig.fileOverride = fileOverride;
            return this;
        }

        public Builder openDir(boolean open){
            this.globalConfig.open = open;
            return this;
        }

        public Builder outputDir(String outputDir) {
            this.globalConfig.outputDir = outputDir;
            return this;
        }

        public Builder author(String author){
            this.globalConfig.author = author;
            return this;
        }

        public Builder kotlin(boolean kotlin){
            this.globalConfig.kotlin = kotlin;
            return this;
        }

        public Builder swagger2(boolean swagger2){
            this.globalConfig.swagger2 = swagger2;
            return this;
        }

        public Builder entityName(String entityName){
            this.globalConfig.entityName = entityName;
            return this;
        }

        public Builder xmlName(String xmlName) {
            this.globalConfig.xmlName = xmlName;
            return this;
        }

        public Builder serviceName(String serviceName) {
            this.globalConfig.serviceName = serviceName;
            return this;
        }

        public Builder serviceImplName(String serviceImplName) {
            this.globalConfig.serviceImplName = serviceImplName;
            return this;
        }

        public Builder controllerName(String controllerName) {
            this.globalConfig.controllerName = controllerName;
            return this;
        }

        public Builder mapperName(String mapperName) {
            this.globalConfig.mapperName = mapperName;
            return this;
        }

        public Builder dateType(DateType dateType) {
            this.globalConfig.dateType = dateType;
            return this;
        }

        public GlobalConfig build() {
            return this.globalConfig;
        }
    }
}
