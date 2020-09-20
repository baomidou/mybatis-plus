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

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.File;
import java.util.Map;

/**
 * 模板路径配置项
 *
 * @author tzg hubin
 * @since 2017-06-17
 */
@Data
@Accessors(chain = true)
public class TemplateConfig {

    @Getter(AccessLevel.NONE)
    private String entity = ConstVal.TEMPLATE_ENTITY_JAVA;

    private String entityKt = ConstVal.TEMPLATE_ENTITY_KT;

    private String service = ConstVal.TEMPLATE_SERVICE;

    private String serviceImpl = ConstVal.TEMPLATE_SERVICE_IMPL;

    private String mapper = ConstVal.TEMPLATE_MAPPER;

    private String xml = ConstVal.TEMPLATE_XML;

    private String controller = ConstVal.TEMPLATE_CONTROLLER;

    public String getEntity(boolean kotlin) {
        return kotlin ? entityKt : entity;
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
                        setXml(null);
                        break;
                    case ENTITY:
                        setEntity(null).setEntityKt(null);
                        break;
                    case MAPPER:
                        setMapper(null);
                        break;
                    case SERVICE:
                        setService(null).setServiceImpl(null);
                        break;
                    case CONTROLLER:
                        setController(null);
                        break;
                }
            }
        }
        return this;
    }

    public Map<String, String> initTemplate(GlobalConfig globalConfig, Map<String, String> packageInfo) {
        Map<String, String> initMap = CollectionUtils.newHashMapWithExpectedSize(6);
        String outputDir = globalConfig.getOutputDir();
        setPathInfo(initMap, packageInfo, this.getEntity(globalConfig.isKotlin()), outputDir, ConstVal.ENTITY_PATH, ConstVal.ENTITY);
        setPathInfo(initMap, packageInfo, this.getMapper(), outputDir, ConstVal.MAPPER_PATH, ConstVal.MAPPER);
        setPathInfo(initMap, packageInfo, this.getXml(), outputDir, ConstVal.XML_PATH, ConstVal.XML);
        setPathInfo(initMap, packageInfo, this.getService(), outputDir, ConstVal.SERVICE_PATH, ConstVal.SERVICE);
        setPathInfo(initMap, packageInfo, this.getServiceImpl(), outputDir, ConstVal.SERVICE_IMPL_PATH, ConstVal.SERVICE_IMPL);
        setPathInfo(initMap, packageInfo, this.getController(), outputDir, ConstVal.CONTROLLER_PATH, ConstVal.CONTROLLER);
        return initMap;
    }

    private void setPathInfo(Map<String, String> pathInfo, Map<String, String> packageInfo, String template, String outputDir, String path, String module) {
        if (StringUtils.isNotBlank(template)) {
            pathInfo.put(path, joinPath(outputDir, packageInfo.get(module)));
        }
    }

    /**
     * 连接路径字符串
     *
     * @param parentDir   路径常量字符串
     * @param packageName 包名
     * @return 连接后的路径
     */
    private String joinPath(String parentDir, String packageName) {
        if (StringUtils.isBlank(parentDir)) {
            parentDir = System.getProperty(ConstVal.JAVA_TMPDIR);
        }
        if (!StringUtils.endsWith(parentDir, File.separator)) {
            parentDir += File.separator;
        }
        packageName = packageName.replaceAll("\\.", StringPool.BACK_SLASH + File.separator);
        return parentDir + packageName;
    }

}
