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
package com.baomidou.mybatisplus.extension.spring;

import com.baomidou.mybatisplus.core.spi.CompatibleHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring容器访问
 *
 * @author nieqiurong
 * @since 3.5.12
 * @deprecated 3.5.13 初始化顺序不太好兼容Bean初始化方法执行逻辑，使用{@link MybatisSqlSessionFactoryBean#setApplicationContext(ApplicationContext)}替代.
 */
@Deprecated
public class MybatisPlusApplicationContextAware implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisPlusApplicationContextAware.class);

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        LOGGER.info("Register ApplicationContext instances {}", applicationContext.getDisplayName());
        MybatisPlusApplicationContextAware.applicationContext = applicationContext;
        if (CompatibleHelper.hasCompatibleSet()) {
            CompatibleHelper.getCompatibleSet().setContext(applicationContext);
        }
    }

    public static boolean hasApplicationContext() {
        return applicationContext != null;
    }

    public static ApplicationContext getApplicationContext() {
        Assert.isTrue(hasApplicationContext(), "applicationContext is null");
        return applicationContext;
    }

}
