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
package com.baomidou.mybatisplus.autoconfigure;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDependsOnDatabaseInitializationDetector;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitializationDetector;

import java.util.Collections;
import java.util.Set;

/**
 * {@link DependsOnDatabaseInitializationDetector} for Mybatis.
 *
 * @author Eddú Meléndez
 *
 * @since 2.3.0
 */
class MybatisDependsOnDatabaseInitializationDetector
    extends AbstractBeansOfTypeDependsOnDatabaseInitializationDetector {

  @Override
  protected Set<Class<?>> getDependsOnDatabaseInitializationBeanTypes() {
    return Collections.singleton(SqlSessionTemplate.class);
  }

}
