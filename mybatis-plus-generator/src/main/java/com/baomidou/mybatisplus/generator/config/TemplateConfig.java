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

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 模板路径配置项
 *
 * @author tzg hubin
 * @since 2017-06-17
 */
@Data
@Accessors(chain = true)
public class TemplateConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateConfig.class);

    private static final Pattern FILE_TYPE = Pattern.compile("\\.[^.\\\\/:*?\"<>|\\r\\n]+$");

    /**
     * 由于需要支持kotlin与java两种模板,当不需要支持其中一种的话,可以使用方式一进行设置.
     * example:
     * 1.setEntity("/templates/entity.java") or setEntity("/templates/entity.kt")
     * 2.setEntity("/templates/entity")
     * 3.setEntity("/templates/entity%s")
     * 设置实体模板路径
     */
    @Getter(AccessLevel.NONE)
    private String entity;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean disableEntity;

    /**
     * 设置实体模板路径
     *
     * @see #entity
     * @deprecated 3.4.1
     */
    @Deprecated
    private String entityKt = ConstVal.TEMPLATE_ENTITY_KT;

    private String service;

    private String serviceImpl;

    private String mapper;

    private String xml;

    private String controller;

    /**
     * 后续不再公开此方法.
     *
     * @see Builder#all()
     * @deprecated 3.4.1
     */
    @Deprecated
    public TemplateConfig() {
        this.controller = ConstVal.TEMPLATE_CONTROLLER;
        this.entity = ConstVal.TEMPLATE_ENTITY;
        this.xml = ConstVal.TEMPLATE_XML;
        this.service = ConstVal.TEMPLATE_SERVICE;
        this.serviceImpl = ConstVal.TEMPLATE_SERVICE_IMPL;
        this.mapper = ConstVal.TEMPLATE_MAPPER;
    }

    /**
     * 设置实体模板
     *
     * @param entity 实体模板
     * @return this
     * @see Builder#entity(String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public TemplateConfig setEntity(String entity) {
        logger(entity, TemplateType.ENTITY);
        this.entity = entity;
        return this;
    }

    /**
     * 设置service接口模板
     *
     * @param service service接口模板
     * @return this
     * @see Builder#service(String, String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public TemplateConfig setService(String service) {
        logger(service, TemplateType.SERVICE);
        this.service = service;
        return this;
    }

    /**
     * 设置service实现类模板
     *
     * @param serviceImpl service实现类模板
     * @return this
     * @see Builder#service(String, String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public TemplateConfig setServiceImpl(String serviceImpl) {
        logger(serviceImpl, TemplateType.SERVICE);
        this.serviceImpl = serviceImpl;
        return this;
    }

    /**
     * 设置mapper模板
     *
     * @param mapper mapper模板
     * @return this
     * @see Builder#mapper(String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public TemplateConfig setMapper(String mapper) {
        logger(mapper, TemplateType.ENTITY);
        this.mapper = mapper;
        return this;
    }

    /**
     * 设置mapperXml模板
     *
     * @param xml mapperXml模板
     * @return this
     * @see Builder#mapperXml(String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public TemplateConfig setXml(String xml) {
        logger(xml, TemplateType.XML);
        this.xml = xml;
        return this;
    }

    /**
     * 设置控制器模板
     *
     * @param controller 控制器模板
     * @return this
     * @see Builder#controller(String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public TemplateConfig setController(String controller) {
        logger(controller, TemplateType.CONTROLLER);
        this.controller = controller;
        return this;
    }

    /**
     * 当模板赋值为空时进行日志提示打印
     *
     * @param value        模板值
     * @param templateType 模板类型
     */
    private void logger(String value, TemplateType templateType) {
        if (StringUtils.isBlank(value)) {
            LOGGER.warn("推荐使用disable(TemplateType.{})方法进行默认模板禁用.", templateType.name());
        }
    }

    /**
     * 设置实体模板路径
     *
     * @param entityKt 模板路径
     * @deprecated 3.4.1 {@link #setEntity(String)}
     */
    @Deprecated
    public TemplateConfig setEntityKt(String entityKt) {
        return setEntity(entityKt);
    }

    /**
     * @return 获取实体模板路径
     * @deprecated 3.4.1 {@link #getEntity(boolean)}
     */
    @Deprecated
    public String getEntityKt() {
        return getEntity(true);
    }

    /**
     * 获取实体模板路径
     *
     * @param kotlin 是否kotlin
     * @return 模板路径
     */
    public String getEntity(boolean kotlin) {
        if (!disableEntity) {
            if (StringUtils.isBlank(entity)) {
                // 默认情况
                return kotlin ? ConstVal.TEMPLATE_ENTITY_KT : ConstVal.TEMPLATE_ENTITY_JAVA;
            }
            // 用户自定义情况,尝试替换文件后缀进行加载
            Matcher matcher = FILE_TYPE.matcher(entity);
            if (matcher.find()) {
                return matcher.replaceAll(kotlin ? ".kt" : ".java");
            }
            if (entity.endsWith("%s")) {
                return kotlin ? String.format(entity, ".kt") : String.format(entity, ".java");
            } else {
                //支持无后缀情况,自动加后缀
                return kotlin ? entity + ".kt" : entity + ".java";
            }
        }
        return null;
    }

    /**
     * 禁用模板
     *
     * @param templateTypes 模板类型
     * @return this
     * @since 3.3.2
     */
    public TemplateConfig disable(TemplateType... templateTypes) {
        if (templateTypes != null && templateTypes.length > 0) {
            for (TemplateType templateType : templateTypes) {
                switch (templateType) {
                    case XML:
                        this.xml = null;
                        break;
                    case ENTITY:
                        this.entity = null;
                        this.entityKt = null;
                        //暂时没其他多的需求,使用一个单独的boolean变量进行支持一下.
                        this.disableEntity = true;
                        break;
                    case MAPPER:
                        this.mapper = null;
                        break;
                    case SERVICE:
                        this.service = null;
                        this.serviceImpl = null;
                        break;
                    case CONTROLLER:
                        this.controller = null;
                        break;
                    default:
                }
            }
        }
        return this;
    }

    /**
     * 禁用全部模板
     *
     * @return this
     * @since 3.4.1
     */
    public TemplateConfig disable() {
        return disable(TemplateType.values());
    }


    /**
     * 模板路径配置构建者
     *
     * @author nieqiurong 3.4.1
     */
    public static class Builder {

        private final TemplateConfig templateConfig = new TemplateConfig();

        /**
         * 默认生成一个空的
         */
        public Builder() {
            // 后续去除构造之后去除此方法调用
            templateConfig.disable(TemplateType.values());
        }

        /**
         * 激活所有默认配置模板
         *
         * @return 默认配置模板
         */
        public Builder all() {
            this.templateConfig.controller = ConstVal.TEMPLATE_CONTROLLER;
            this.templateConfig.entity = ConstVal.TEMPLATE_ENTITY;
            this.templateConfig.xml = ConstVal.TEMPLATE_XML;
            this.templateConfig.service = ConstVal.TEMPLATE_SERVICE;
            this.templateConfig.serviceImpl = ConstVal.TEMPLATE_SERVICE_IMPL;
            this.templateConfig.mapper = ConstVal.TEMPLATE_MAPPER;
            this.templateConfig.disableEntity = false;
            return this;
        }

        /**
         * 使用默认实体模板
         *
         * @return this
         */
        public Builder entity() {
            return entity(ConstVal.TEMPLATE_ENTITY);
        }

        /**
         * 设置实体模板路径
         *
         * @param entityTemplate 实体模板
         * @return this
         */
        public Builder entity(String entityTemplate) {
            this.templateConfig.disableEntity = false;
            this.templateConfig.setEntity(entityTemplate);
            return this;
        }

        /**
         * 使用默认service模板
         *
         * @return this
         */
        public Builder service() {
            return service(ConstVal.TEMPLATE_SERVICE, ConstVal.TEMPLATE_SERVICE_IMPL);
        }

        /**
         * 设置service模板路径
         *
         * @param serviceTemplate     service接口模板路径
         * @param serviceImplTemplate service实现类模板路径
         * @return this
         */
        public Builder service(String serviceTemplate, String serviceImplTemplate) {
            this.templateConfig.setService(serviceTemplate).setServiceImpl(serviceImplTemplate);
            return this;
        }

        /**
         * 使用默认mapper模板
         *
         * @return this
         */
        public Builder mapper() {
            return mapper(ConstVal.TEMPLATE_MAPPER);
        }

        /**
         * 设置mapper模板路径
         *
         * @param mapperTemplate mapper模板路径
         * @return this
         */
        public Builder mapper(String mapperTemplate) {
            this.templateConfig.setMapper(mapperTemplate);
            return this;
        }


        /**
         * 使用默认mapperXml模板
         *
         * @return this
         */
        public Builder mapperXml() {
            return mapperXml(ConstVal.TEMPLATE_XML);
        }

        /**
         * 设置mapperXml模板路径
         *
         * @param mapperXmlTemplate xml模板路径
         * @return this
         */
        public Builder mapperXml(String mapperXmlTemplate) {
            this.templateConfig.setXml(mapperXmlTemplate);
            return this;
        }

        /**
         * 使用默认控制器模板
         *
         * @return this
         */
        public Builder controller() {
            return controller(ConstVal.TEMPLATE_CONTROLLER);
        }

        /**
         * 设置控制器模板路径
         *
         * @param controllerTemplate 控制器模板路径
         * @return this
         */
        public Builder controller(String controllerTemplate) {
            this.templateConfig.setController(controllerTemplate);
            return this;
        }

        /**
         * 构建模板配置对象
         *
         * @return 模板配置对象
         */
        public TemplateConfig build() {
            return this.templateConfig;
        }

    }

}
