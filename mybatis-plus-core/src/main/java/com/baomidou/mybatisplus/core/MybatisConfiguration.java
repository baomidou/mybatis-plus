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
package com.baomidou.mybatisplus.core;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;

/**
 * <p>
 * replace default Configuration class
 * </p>
 * <p>
 * Caratacus 2016/9/25 replace mapperRegistry
 * </p>
 *
 * @author hubin
 * @since 2016-01-23
 */
public class MybatisConfiguration extends Configuration {

    private static final Log logger = LogFactory.getLog(MybatisConfiguration.class);

    /**
     * Mapper 注册
     */
    public final MybatisMapperRegistry mybatisMapperRegistry = new MybatisMapperRegistry(this);

    /**
     * 初始化调用
     */
    public MybatisConfiguration() {
        setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        System.out.println("  _ _     /_ _ _/_. ____  /    _");
        System.out.println(" / / //_//_//_|/ /_\\  /_///_/_\\ ");
        System.out.println("      _/             /             V.3.0-RC");
    }

    /**
     * <p>
     * MybatisPlus 加载 SQL 顺序：
     * </p>
     * 1、加载XML中的SQL<br>
     * 2、加载sqlProvider中的SQL<br>
     * 3、xmlSql 与 sqlProvider不能包含相同的SQL<br>
     * <br>
     * 调整后的SQL优先级：xmlSql > sqlProvider > curdSql <br>
     */
    @Override
    public void addMappedStatement(MappedStatement ms) {
        MybatisConfiguration.logger.debug("addMappedStatement: " + ms.getId());
        if (GlobalConfigUtils.isRefresh(ms.getConfiguration())) {
            /*
             * 支持是否自动刷新 XML 变更内容，开发环境使用【 注：生产环境勿用！】
             */
            mappedStatements.remove(ms.getId());
        } else {
            if (mappedStatements.containsKey(ms.getId())) {
                /*
                 * 说明已加载了xml中的节点； 忽略mapper中的SqlProvider数据
                 */
                MybatisConfiguration.logger.error("mapper[" + ms.getId() + "] is ignored, because it's exists, maybe from xml file");
                return;
            }
        }
        super.addMappedStatement(ms);
    }

    @Override
    public void setDefaultScriptingLanguage(Class<?> driver) {
        if (driver == null) {
            /* 设置自定义 driver */
            driver = MybatisXMLLanguageDriver.class;
        }
        super.setDefaultScriptingLanguage(driver);
    }

    @Override
    public MapperRegistry getMapperRegistry() {
        return mybatisMapperRegistry;
    }

    @Override
    public <T> void addMapper(Class<T> type) {
        mybatisMapperRegistry.addMapper(type);
    }

    @Override
    public void addMappers(String packageName, Class<?> superType) {
        mybatisMapperRegistry.addMappers(packageName, superType);
    }

    @Override
    public void addMappers(String packageName) {
        mybatisMapperRegistry.addMappers(packageName);
    }

    @Override
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mybatisMapperRegistry.getMapper(type, sqlSession);
    }

    @Override
    public boolean hasMapper(Class<?> type) {
        return mybatisMapperRegistry.hasMapper(type);
    }

}
