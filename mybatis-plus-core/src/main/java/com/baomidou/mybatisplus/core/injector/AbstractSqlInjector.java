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
package com.baomidou.mybatisplus.core.injector;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;

import com.baomidou.mybatisplus.core.parser.SqlParserHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;


/**
 * <p>
 * SQL 自动注入器
 * </p>
 *
 * @author hubin
 * @since 2018-04-07
 */
public abstract class AbstractSqlInjector implements ISqlInjector {

    @Override
    public void inspectInject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
        String className = mapperClass.toString();
        Set<String> mapperRegistryCache = GlobalConfigUtils.getMapperRegistryCache(builderAssistant.getConfiguration());
        if (!mapperRegistryCache.contains(className)) {
            List<AbstractMethod> methodList = this.getMethodList();
            Assert.notEmpty(methodList, "No effective injection method was found.");
            // 循环注入自定义方法
            methodList.forEach(m -> m.inject(builderAssistant, mapperClass));
            mapperRegistryCache.add(className);
            /**
             * 初始化 SQL 解析
             */
            if (GlobalConfigUtils.getGlobalConfig(builderAssistant.getConfiguration()).isSqlParserCache()) {
                SqlParserHelper.initSqlParserInfoCache(mapperClass);
            }
        }
    }

    @Override
    public void injectSqlRunner(Configuration configuration) {
        if (isInjectSqlRunner()) {
            new SqlRunnerInjector().inject(configuration);
        }
    }

    /**
     * <p>
     * 获取 注入的方法
     * </p>
     *
     * @return 注入的方法集合
     */
    public abstract List<AbstractMethod> getMethodList();

    /**
     * <p>
     * 是否注入SqlRunner,抽象类默认注入,如果不需要重写该方法
     * </p>
     *
     * @return 注入的方法集合
     */
    public boolean isInjectSqlRunner() {
        return true;
    }
}
