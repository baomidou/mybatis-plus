/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 包相关的配置项
 *
 * @author YangHu, tangguo, hubin
 * @since 2016-08-30
 */
public class PackageConfig {

    private PackageConfig() {
    }

    /**
     * 父包名。如果为空，将下面子包名必须写全部， 否则就只需写子包名
     */
    private String parent = "com.baomidou";

    /**
     * 父包模块名
     */
    @Getter
    private String moduleName = "";

    /**
     * Entity包名
     */
    @Getter
    private String entity = "entity";

    /**
     * Service包名
     */
    @Getter
    private String service = "service";

    /**
     * Service Impl包名
     */
    @Getter
    private String serviceImpl = "service.impl";

    /**
     * Mapper包名
     */
    @Getter
    private String mapper = "mapper";

    /**
     * Mapper XML包名
     */
    @Getter
    private String xml = "mapper.xml";

    /**
     * Controller包名
     */
    @Getter
    private String controller = "controller";

    /**
     * 路径配置信息
     */
    @Getter
    private Map<OutputFile, String> pathInfo;

    /**
     * 包配置信息
     *
     * @since 3.5.0
     */
    private final Map<String, String> packageInfo = new HashMap<>();

    /**
     * 父包名
     */
    @NotNull
    public String getParent() {
        if (StringUtils.isNotBlank(moduleName)) {
            return parent + StringPool.DOT + moduleName;
        }
        return parent;
    }

    /**
     * 连接父子包名
     *
     * @param subPackage 子包名
     * @return 连接后的包名
     */
    @NotNull
    public String joinPackage(String subPackage) {
        String parent = getParent();
        return StringUtils.isBlank(parent) ? subPackage : (parent + StringPool.DOT + subPackage);
    }

    /**
     * 获取包配置信息
     *
     * @return 包配置信息
     * @see #getPackageInfo(InjectionConfig)
     * @since 3.5.0
     * @deprecated 3.5.10
     */
    @NotNull
    @Deprecated
    public Map<String, String> getPackageInfo() {
        return getPackageInfo((InjectionConfig) null);
    }

    /**
     * 获取包配置信息
     *
     * @param injectionConfig 配置文件信息
     * @return 包配置信息
     * @since 3.5.10
     */
    @NotNull
    public Map<String, String> getPackageInfo(InjectionConfig injectionConfig) {
        if (packageInfo.isEmpty()) {
            packageInfo.put(ConstVal.MODULE_NAME, this.getModuleName());
            packageInfo.put(ConstVal.ENTITY, this.joinPackage(this.getEntity()));
            packageInfo.put(ConstVal.MAPPER, this.joinPackage(this.getMapper()));
            packageInfo.put(ConstVal.XML, this.joinPackage(this.getXml()));
            packageInfo.put(ConstVal.SERVICE, this.joinPackage(this.getService()));
            packageInfo.put(ConstVal.SERVICE_IMPL, this.joinPackage(this.getServiceImpl()));
            packageInfo.put(ConstVal.CONTROLLER, this.joinPackage(this.getController()));
            packageInfo.put(ConstVal.PARENT, this.getParent());
            if (injectionConfig != null) {
                List<CustomFile> customFiles = injectionConfig.getCustomFiles();
                for (CustomFile customFile : customFiles) {
                    if (StringUtils.isNotBlank(customFile.getPackageName())) {
                        String name = customFile.getShortName();
                        if (StringUtils.isNotBlank(this.parent)
                            && customFile.getPackageName().startsWith(this.parent)) {
                            packageInfo.put(name, customFile.getPackageName());
                        } else {
                            packageInfo.put(name, this.joinPackage(customFile.getPackageName()));
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableMap(this.packageInfo);
    }

    /**
     * 获取包配置信息
     *
     * @param module 模块
     * @return 配置信息
     * @see #getPackageInfo(InjectionConfig, String)
     * @since 3.5.0
     */
    @Deprecated
    public String getPackageInfo(String module) {
        return getPackageInfo().get(module);
    }

    /**
     * @since 3.5.10
     * @param injectionConfig 注入配置
     * @param module 模块
     * @return 配置信息
     */
    public String getPackageInfo(InjectionConfig injectionConfig, String module) {
        return getPackageInfo(injectionConfig).get(module);
    }

    /**
     * 构建者
     *
     * @author nieqiurong
     * @since 3.5.0
     */
    public static class Builder implements IConfigBuilder<PackageConfig> {

        private final PackageConfig packageConfig;

        public Builder() {
            this.packageConfig = new PackageConfig();
        }

        public Builder(@NotNull String parent, @NotNull String moduleName) {
            this();
            this.packageConfig.parent = parent;
            this.packageConfig.moduleName = moduleName;
        }

        /**
         * 指定父包名
         *
         * @param parent 父包名
         * @return this
         */
        public Builder parent(@NotNull String parent) {
            this.packageConfig.parent = parent;
            return this;
        }

        /**
         * 指定模块名称
         *
         * @param moduleName 模块名
         * @return this
         */
        public Builder moduleName(@NotNull String moduleName) {
            this.packageConfig.moduleName = moduleName;
            return this;
        }

        /**
         * 指定实体包名
         *
         * @param entity 实体包名
         * @return this
         */
        public Builder entity(@NotNull String entity) {
            this.packageConfig.entity = entity;
            return this;
        }

        /**
         * 指定service接口包名
         *
         * @param service service包名
         * @return this
         */
        public Builder service(@NotNull String service) {
            this.packageConfig.service = service;
            return this;
        }

        /**
         * service实现类包名
         *
         * @param serviceImpl service实现类包名
         * @return this
         */
        public Builder serviceImpl(@NotNull String serviceImpl) {
            this.packageConfig.serviceImpl = serviceImpl;
            return this;
        }

        /**
         * 指定mapper接口包名
         *
         * @param mapper mapper包名
         * @return this
         */
        public Builder mapper(@NotNull String mapper) {
            this.packageConfig.mapper = mapper;
            return this;
        }

        /**
         * 指定xml包名
         *
         * @param xml xml包名
         * @return this
         */
        public Builder xml(@NotNull String xml) {
            this.packageConfig.xml = xml;
            return this;
        }

        /**
         * 指定控制器包名
         *
         * @param controller 控制器包名
         * @return this
         */
        public Builder controller(@NotNull String controller) {
            this.packageConfig.controller = controller;
            return this;
        }

        /**
         * 路径配置信息
         *
         * @param pathInfo 路径配置信息
         * @return this
         */
        public Builder pathInfo(@NotNull Map<OutputFile, String> pathInfo) {
            this.packageConfig.pathInfo = pathInfo;
            return this;
        }

        /**
         * 连接父子包名
         *
         * @param subPackage 子包名
         * @return 连接后的包名
         */
        @NotNull
        public String joinPackage(@NotNull String subPackage) {
            return this.packageConfig.joinPackage(subPackage);
        }

        /**
         * 构建包配置对象
         * <p>当指定{@link #parent(String)} 与 {@link #moduleName(String)}时,其他模块名字会加上这两个作为前缀</p>
         * <p>
         * 例如:
         * <p>当设置 {@link #parent(String)},那么entity的配置为 {@link #getParent()}.{@link #getEntity()}</p>
         * <p>当设置 {@link #parent(String)}与{@link #moduleName(String)},那么entity的配置为 {@link #getParent()}.{@link #getModuleName()}.{@link #getEntity()} </p>
         * </p>
         *
         * @return 包配置对象
         */
        @Override
        public PackageConfig build() {
            return this.packageConfig;
        }
    }
}
