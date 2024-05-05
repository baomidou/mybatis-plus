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
package com.baomidou.mybatisplus.core.injector;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * SQL 自动注入器
 *
 * @author hubin
 * @since 2018-04-07
 */
public abstract class AbstractSqlInjector implements ISqlInjector {

    protected final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public void inspectInject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
        Class<?> modelClass = ReflectionKit.getSuperClassGenericType(mapperClass, Mapper.class, 0);
        if (modelClass != null) {
            String className = mapperClass.toString();
            Set<String> mapperRegistryCache = GlobalConfigUtils.getMapperRegistryCache(builderAssistant.getConfiguration());
            if (!mapperRegistryCache.contains(className)) {
                TableInfo tableInfo = TableInfoHelper.initTableInfo(builderAssistant, modelClass);
                List<AbstractMethod> methodList = this.getMethodList(mapperClass, tableInfo);
                // 兼容旧代码
                if (CollectionUtils.isEmpty(methodList)) {
                    methodList = this.getMethodList(builderAssistant.getConfiguration(), mapperClass, tableInfo);
                }
                if (CollectionUtils.isNotEmpty(methodList)) {
                    // 循环注入自定义方法
                    methodList.forEach(m -> m.inject(builderAssistant, mapperClass, modelClass, tableInfo));
                } else {
                    logger.debug(className + ", No effective injection method was found.");
                }
                mapperRegistryCache.add(className);
            }
        }
    }

    /**
     * <p>
     * 获取 注入的方法
     * </p>
     *
     * @param mapperClass 当前mapper
     * @param tableInfo   表信息
     * @return 注入的方法集合
     * @since 3.1.2 add  mapperClass
     * @deprecated 3.5.6 {@link #getMethodList(Configuration, Class, TableInfo)}
     */
    @Deprecated
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        return getMethodList(tableInfo.getConfiguration(), mapperClass, tableInfo);
    }

    /**
     * 获取注入的方法
     *
     * @param configuration 配置对象
     * @param mapperClass   当前mapper
     * @param tableInfo     表信息
     * @return 注入方法集合
     * @since 3.5.6
     */
    public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
        return new ArrayList<>();
    }

}
