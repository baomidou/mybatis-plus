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
package com.baomidou.mybatisplus.generator;

import com.baomidou.mybatisplus.generator.config.builder.Entity;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.model.ClassAnnotationAttributes;

import java.util.List;

/**
 * 表注解处理器
 *
 * @author nieqiurong
 * @since 3.5.10
 */
public interface ITableAnnotationHandler {

    /**
     * 处理字段级注解
     *
     * @param tableInfo 表信息
     * @return 注解信息
     */
    List<ClassAnnotationAttributes> handle(TableInfo tableInfo, Entity entity);

}
