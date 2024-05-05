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
package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.core.toolkit.StringPool;

import java.nio.charset.StandardCharsets;

/**
 * 定义常量
 *
 * @author YangHu, tangguo, hubin
 * @since 2016-08-31
 */
public interface ConstVal {

    String MODULE_NAME = "ModuleName";

    String ENTITY = "Entity";
    String SERVICE = "Service";
    String SERVICE_IMPL = "ServiceImpl";
    String MAPPER = "Mapper";
    String XML = "Xml";
    String CONTROLLER = "Controller";
    String PARENT = "Parent";

    String JAVA_TMPDIR = "java.io.tmpdir";
    String UTF8 = StandardCharsets.UTF_8.name();
    String UNDERLINE = "_";

    String JAVA_SUFFIX = StringPool.DOT_JAVA;
    String KT_SUFFIX = ".kt";
    String XML_SUFFIX = ".xml";

    /**
     * 实体模板路径
     */
    String TEMPLATE_ENTITY_JAVA = "/templates/entity.java";

    /**
     * 实体模板路径(kotlin模板)
     */
    String TEMPLATE_ENTITY_KT = "/templates/entity.kt";

    /**
     * 控制器模板路径
     */
    String TEMPLATE_CONTROLLER = "/templates/controller.java";

    /**
     * Mapper模板路径
     */
    String TEMPLATE_MAPPER = "/templates/mapper.java";

    /**
     * MapperXml模板路径
     */
    String TEMPLATE_XML = "/templates/mapper.xml";

    /**
     * Service模板路径
     */
    String TEMPLATE_SERVICE = "/templates/service.java";

    /**
     * ServiceImpl模板路径
     */
    String TEMPLATE_SERVICE_IMPL = "/templates/serviceImpl.java";

    String VM_LOAD_PATH_KEY = "file.resource.loader.class";
    String VM_LOAD_PATH_VALUE = "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader";

    String SUPER_MAPPER_CLASS = "com.baomidou.mybatisplus.core.mapper.BaseMapper";
    String SUPER_SERVICE_CLASS = "com.baomidou.mybatisplus.extension.service.IService";
    String SUPER_SERVICE_IMPL_CLASS = "com.baomidou.mybatisplus.extension.service.impl.ServiceImpl";

    /**
     * @see com.baomidou.mybatisplus.core.metadata.TableInfoHelper.DEFAULT_ID_NAME
     */
    String DEFAULT_ID_NAME = "id";
}
