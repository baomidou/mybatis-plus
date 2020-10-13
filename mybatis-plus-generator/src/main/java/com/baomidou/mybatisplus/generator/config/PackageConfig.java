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


import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 跟包相关的配置项
 *
 * @author YangHu, tangguo, hubin
 * @since 2016-08-30
 */

@Data
@Accessors(chain = true)
public class PackageConfig {

    /**
     * 父包名。如果为空，将下面子包名必须写全部， 否则就只需写子包名
     */
    private String parent = "com.baomidou";
    /**
     * 父包模块名
     */
    private String moduleName = "";
    /**
     * Entity包名
     */
    private String entity = "entity";
    /**
     * Service包名
     */
    private String service = "service";
    /**
     * Service Impl包名
     */
    private String serviceImpl = "service.impl";
    /**
     * Mapper包名
     */
    private String mapper = "mapper";
    /**
     * Mapper XML包名
     */
    private String xml = "mapper.xml";
    /**
     * Controller包名
     */
    private String controller = "controller";
    /**
     * 路径配置信息
     */
    private Map<String, String> pathInfo;

    /**
     * 包配置信息
     *
     * @since 3.4.1
     */
    private final Map<String, String> packageInfo = new HashMap<>();

    /**
     * 父包名
     */
    public String getParent() {
        if (StringUtils.isNotBlank(moduleName)) {
            return parent + StringPool.DOT + moduleName;
        }
        return parent;
    }


    /**
     * 连接父子包名
     *
     * @return 连接后的包名
     * @since 3.4.1
     */
    public String joinPackage(String subPackage) {
        String parent = getParent();
        return StringUtils.isBlank(parent) ? subPackage : (parent + StringPool.DOT + subPackage);
    }

    /**
     * 获取包配置信息
     *
     * @return 包配置信息
     * @since 3.4.1
     */
    public Map<String, String> getPackageInfo() {
        if (packageInfo.isEmpty()) {
            packageInfo.put(ConstVal.MODULE_NAME, this.getModuleName());
            packageInfo.put(ConstVal.ENTITY, this.joinPackage(this.getEntity()));
            packageInfo.put(ConstVal.MAPPER, this.joinPackage(this.getMapper()));
            packageInfo.put(ConstVal.XML, this.joinPackage(this.getXml()));
            packageInfo.put(ConstVal.SERVICE, this.joinPackage(this.getService()));
            packageInfo.put(ConstVal.SERVICE_IMPL, this.joinPackage(this.getServiceImpl()));
            packageInfo.put(ConstVal.CONTROLLER, this.joinPackage(this.getController()));
        }
        return Collections.unmodifiableMap(this.packageInfo);
    }

    /**
     * 后续不再公开此构造方法
     *
     * @see Builder
     * @deprecated 3.4.1
     */
    public PackageConfig() {
    }

    /**
     * @param parent 父包名
     * @return this
     * @see Builder#parent(String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public PackageConfig setParent(String parent) {
        this.parent = parent;
        return this;
    }

    /**
     * @param moduleName 模块名
     * @return this
     * @see Builder#moduleName(String)
     * @deprecated 3.4.1
     */
    @Deprecated
    public PackageConfig setModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    /**
     * @param entity 实体名
     * @return this
     * @see Builder#entity(String)
     * @deprecated 3.4.1
     */
    public PackageConfig setEntity(String entity) {
        this.entity = entity;
        return this;
    }

    /**
     * @param service service接口包名
     * @return this
     * @see Builder#service(String)
     * @deprecated 3.4.1
     */
    public PackageConfig setService(String service) {
        this.service = service;
        return this;
    }

    /**
     * @param serviceImpl service实现类包名
     * @return this
     * @see Builder#serviceImpl(String)
     * @deprecated 3.4.1
     */
    public PackageConfig setServiceImpl(String serviceImpl) {
        this.serviceImpl = serviceImpl;
        return this;
    }

    /**
     * @param mapper mapper包名
     * @return this
     * @see Builder#mapper(String)
     * @deprecated 3.4.1
     */
    public PackageConfig setMapper(String mapper) {
        this.mapper = mapper;
        return this;
    }

    /**
     * @param xml xml包名
     * @return this
     * @see Builder#xml(String)
     * @deprecated 3.4.1
     */
    public PackageConfig setXml(String xml) {
        this.xml = xml;
        return this;
    }

    /**
     * @param controller 控制器包名
     * @return this
     * @see Builder#controller(String)
     * @deprecated 3.4.1
     */
    public PackageConfig setController(String controller) {
        this.controller = controller;
        return this;
    }

    /**
     * @param pathInfo 路径信息
     * @return this
     * @see Builder#pathInfo(Map)
     * @deprecated 3.4.1
     */
    public PackageConfig setPathInfo(Map<String, String> pathInfo) {
        this.pathInfo = pathInfo;
        return this;
    }

    /**
     * 获取包配置信息
     *
     * @param module 模块
     * @return 配置信息
     * @since 3.4.1
     */
    public String getPackageInfo(String module) {
        return getPackageInfo().get(module);
    }

    /**
     * 构建者
     *
     * @author nieqiurong 2020/10/13.
     * @since 3.4.1
     */
    public static class Builder {

        private final PackageConfig packageConfig = new PackageConfig();

        /**
         * 指定父包名
         *
         * @param parent 父包名
         * @return this
         */
        public Builder parent(String parent) {
            this.packageConfig.parent = parent;
            return this;
        }

        /**
         * 指定模块名称
         *
         * @param moduleName 模块名
         * @return this
         */
        public Builder moduleName(String moduleName) {
            this.packageConfig.moduleName = moduleName;
            return this;
        }

        /**
         * 指定实体包名
         *
         * @param entity 实体包名
         * @return this
         */
        public Builder entity(String entity) {
            this.packageConfig.entity = entity;
            return this;
        }

        /**
         * 指定service接口包名
         *
         * @param service service包名
         * @return this
         */
        public Builder service(String service) {
            this.packageConfig.service = service;
            return this;
        }

        /**
         * service实现类包名
         *
         * @param serviceImpl service实现类包名
         * @return this
         */
        public Builder serviceImpl(String serviceImpl) {
            this.packageConfig.serviceImpl = serviceImpl;
            return this;
        }

        /**
         * 指定mapper接口包名
         *
         * @param mapper mapper包名
         * @return this
         */
        public Builder mapper(String mapper) {
            this.packageConfig.mapper = mapper;
            return this;
        }

        /**
         * 指定xml包名
         *
         * @param xml xml包名
         * @return this
         */
        public Builder xml(String xml) {
            this.packageConfig.xml = xml;
            return this;
        }

        /**
         * 指定控制器包名
         *
         * @param controller 控制器包名
         * @return this
         */
        public Builder controller(String controller) {
            this.packageConfig.controller = controller;
            return this;
        }

        /**
         * 路径配置信息
         *
         * @param pathInfo 路径配置信息
         * @return this
         */
        public Builder pathInfo(Map<String, String> pathInfo) {
            this.packageConfig.pathInfo = pathInfo;
            return this;
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
        public PackageConfig build() {
            //TODO 后面考虑把那些entity包名挂到Entity上去
            return this.packageConfig;
        }
    }
}
