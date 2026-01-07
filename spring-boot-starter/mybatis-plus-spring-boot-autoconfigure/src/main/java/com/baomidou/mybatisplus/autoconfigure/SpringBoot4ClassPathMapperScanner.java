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
package com.baomidou.mybatisplus.autoconfigure;

import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.util.Set;

/**
 * Custom ClassPathMapperScanner for Spring Boot 4 / Spring Framework 7 compatibility.
 * <p>
 * This scanner fixes the issue where the factoryBeanObjectType attribute must be set
 * as a Class object instead of a String in Spring Framework 7.
 * </p>
 * <p>
 * The parent mybatis-spring ClassPathMapperScanner sets factoryBeanObjectType as a String
 * (the fully qualified class name), but Spring Framework 7 requires it to be a Class object.
 * This scanner post-processes the bean definitions to convert String values to Class objects.
 * </p>
 *
 * @author baomidou
 * @since 3.5.16
 */
public class SpringBoot4ClassPathMapperScanner extends ClassPathMapperScanner {

    private static final String FACTORY_BEAN_OBJECT_TYPE = "factoryBeanObjectType";

    public SpringBoot4ClassPathMapperScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        // Let the parent scanner do the scanning and registration
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

        // Post-process to fix factoryBeanObjectType for Spring Framework 7 compatibility
        if (!beanDefinitions.isEmpty()) {
            fixFactoryBeanObjectType(beanDefinitions);
        }

        return beanDefinitions;
    }

    /**
     * Fix the factoryBeanObjectType attribute for Spring Framework 7 compatibility.
     * <p>
     * In Spring Framework 7 (Spring Boot 4), the factoryBeanObjectType attribute must be
     * a Class object. MyBatis-Spring 4.0.0 sets it as a String, causing an error:
     * "Invalid value type for attribute 'factoryBeanObjectType': java.lang.String"
     * </p>
     * <p>
     * This method converts String values to Class objects.
     * </p>
     */
    private void fixFactoryBeanObjectType(Set<BeanDefinitionHolder> beanDefinitions) {
        for (BeanDefinitionHolder holder : beanDefinitions) {
            GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
            
            // Get the factoryBeanObjectType attribute
            Object factoryBeanObjectType = definition.getAttribute(FACTORY_BEAN_OBJECT_TYPE);
            
            // If it's a String, convert it to a Class
            if (factoryBeanObjectType instanceof String) {
                String className = (String) factoryBeanObjectType;
                try {
                    // Use the bean class loader if available, otherwise use the current class loader
                    ClassLoader classLoader = definition.getBeanClass() != null ? 
                            definition.getBeanClass().getClassLoader() : 
                            Thread.currentThread().getContextClassLoader();
                    
                    Class<?> clazz = Class.forName(className, false, classLoader);
                    definition.setAttribute(FACTORY_BEAN_OBJECT_TYPE, clazz);
                    
                    if (logger.isDebugEnabled()) {
                        logger.debug("Fixed factoryBeanObjectType for mapper: " + className);
                    }
                } catch (ClassNotFoundException e) {
                    // Log warning but don't fail - the mapper might still work in some cases
                    logger.warn("Cannot load mapper interface class: " + className + 
                            ". Unable to fix factoryBeanObjectType, which may cause issues in Spring Framework 7.", e);
                }
            }
        }
    }
}
