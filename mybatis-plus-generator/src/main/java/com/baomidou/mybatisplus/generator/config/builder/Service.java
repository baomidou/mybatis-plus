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
package com.baomidou.mybatisplus.generator.config.builder;

import com.baomidou.mybatisplus.generator.ITemplate;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.function.ConverterFileName;
import com.baomidou.mybatisplus.generator.util.ClassUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Service属性配置
 *
 * @author nieqiurong 2020/10/11.
 * @since 3.5.0
 */
public class Service implements ITemplate {

    private final static Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private Service() {
    }

    /**
     * 是否生成serviceImpl
     *
     * @since 3.5.6
     */
    @Getter
    private boolean generateServiceImpl = true;


    /**
     * 是否生成service
     *
     * @since 3.5.6
     */
    @Getter
    private boolean generateService = true;

    /**
     * @since 3.5.6
     */
    @Getter
    private String serviceTemplate = ConstVal.TEMPLATE_SERVICE;

    /**
     * @since 3.5.6
     */
    @Getter
    private String serviceImplTemplate = ConstVal.TEMPLATE_SERVICE_IMPL;

    /**
     * 自定义继承的Service类全称，带包名
     */
    private String superServiceClass = ConstVal.SUPER_SERVICE_CLASS;

    /**
     * 自定义继承的ServiceImpl类全称，带包名
     */
    private String superServiceImplClass = ConstVal.SUPER_SERVICE_IMPL_CLASS;

    @NotNull
    public String getSuperServiceClass() {
        return superServiceClass;
    }

    @NotNull
    public String getSuperServiceImplClass() {
        return superServiceImplClass;
    }

    /**
     * 转换输出Service文件名称
     *
     * @since 3.5.0
     */
    private ConverterFileName converterServiceFileName = (entityName -> "I" + entityName + ConstVal.SERVICE);

    /**
     * 转换输出ServiceImpl文件名称
     *
     * @since 3.5.0
     */
    private ConverterFileName converterServiceImplFileName = (entityName -> entityName + ConstVal.SERVICE_IMPL);

    /**
     * 是否覆盖已有文件（默认 false）
     *
     * @since 3.5.2
     */
    @Getter
    private boolean fileOverride;

    @NotNull
    public ConverterFileName getConverterServiceFileName() {
        return converterServiceFileName;
    }

    @NotNull
    public ConverterFileName getConverterServiceImplFileName() {
        return converterServiceImplFileName;
    }

    @Override
    @NotNull
    public Map<String, Object> renderData(@NotNull TableInfo tableInfo) {
        Map<String, Object> data = new HashMap<>();
        data.put("superServiceClassPackage", this.superServiceClass);
        data.put("superServiceClass", ClassUtils.getSimpleName(this.superServiceClass));
        data.put("superServiceImplClassPackage", this.superServiceImplClass);
        data.put("superServiceImplClass", ClassUtils.getSimpleName(this.superServiceImplClass));
        data.put("generateServiceImpl", this.generateServiceImpl);
        data.put("generateService", this.generateService);
        return data;
    }

    public static class Builder extends BaseBuilder {

        private final Service service = new Service();

        public Builder(@NotNull StrategyConfig strategyConfig) {
            super(strategyConfig);
        }

        /**
         * Service接口父类
         *
         * @param clazz 类
         * @return this
         */
        public Builder superServiceClass(@NotNull Class<?> clazz) {
            return superServiceClass(clazz.getName());
        }

        /**
         * Service接口父类
         *
         * @param superServiceClass 类名
         * @return this
         */
        public Builder superServiceClass(@NotNull String superServiceClass) {
            this.service.superServiceClass = superServiceClass;
            return this;
        }

        /**
         * Service实现类父类
         *
         * @param clazz 类
         * @return this
         */
        public Builder superServiceImplClass(@NotNull Class<?> clazz) {
            return superServiceImplClass(clazz.getName());
        }

        /**
         * Service实现类父类
         *
         * @param superServiceImplClass 类名
         * @return this
         */
        public Builder superServiceImplClass(@NotNull String superServiceImplClass) {
            this.service.superServiceImplClass = superServiceImplClass;
            return this;
        }

        /**
         * 转换输出service接口文件名称
         *
         * @param converter 　转换处理
         * @return this
         * @since 3.5.0
         */
        public Builder convertServiceFileName(@NotNull ConverterFileName converter) {
            this.service.converterServiceFileName = converter;
            return this;
        }

        /**
         * 转换输出service实现类文件名称
         *
         * @param converter 　转换处理
         * @return this
         * @since 3.5.0
         */
        public Builder convertServiceImplFileName(@NotNull ConverterFileName converter) {
            this.service.converterServiceImplFileName = converter;
            return this;
        }

        /**
         * 格式化service接口文件名称
         *
         * @param format 　格式
         * @return this
         * @since 3.5.0
         */
        public Builder formatServiceFileName(@NotNull String format) {
            return convertServiceFileName((entityName) -> String.format(format, entityName));
        }

        /**
         * 格式化service实现类文件名称
         *
         * @param format 　格式
         * @return this
         * @since 3.5.0
         */
        public Builder formatServiceImplFileName(@NotNull String format) {
            return convertServiceImplFileName((entityName) -> String.format(format, entityName));
        }

        /**
         * 覆盖已有文件（该方法后续会删除，替代方法为enableFileOverride方法）
         *
         * @see #enableFileOverride()
         */
        @Deprecated
        public Builder fileOverride() {
            LOGGER.warn("fileOverride方法后续会删除，替代方法为enableFileOverride方法");
            this.service.fileOverride = true;
            return this;
        }

        /**
         * 覆盖已有文件
         */
        public Builder enableFileOverride() {
            this.service.fileOverride = true;
            return this;
        }

        /**
         * 禁用生成Service
         *
         * @return this
         * @since 3.5.6
         */
        public Builder disable() {
            this.service.generateService = false;
            this.service.generateServiceImpl = false;
            return this;
        }

        /**
         * 禁用生成
         *
         * @return this
         * @since 3.5.6
         */
        public Builder disableService() {
            this.service.generateService = false;
            return this;
        }

        /**
         * 禁用生成ServiceImpl
         *
         * @return this
         * @since 3.5.6
         */
        public Builder disableServiceImpl() {
            this.service.generateServiceImpl = false;
            return this;
        }

        /**
         * Service模板路径
         *
         * @return this
         * @since 3.5.6
         */
        public Builder serviceTemplate(@NotNull String template) {
            this.service.serviceTemplate = template;
            return this;
        }

        /**
         * ServiceImpl模板路径
         *
         * @return this
         * @since 3.5.6
         */
        public Builder serviceImplTemplate(@NotNull String template) {
            this.service.serviceImplTemplate = template;
            return this;
        }

        @NotNull
        public Service get() {
            return this.service;
        }
    }
}
