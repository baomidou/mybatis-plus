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
package com.baomidou.mybatisplus.generator.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 注解属性
 *
 * @author nieqiurong
 * @since 3.5.10
 */
@Getter
@ToString
public class AnnotationAttributes {

    /**
     * 显示名称
     */
    @Setter
    private String displayName;

    /**
     * 导入包路径
     */
    private final Set<String> importPackages = new HashSet<>();

    public AnnotationAttributes() {
    }

    public AnnotationAttributes(@NotNull Class<?> annotationClass) {
        this.displayName = "@" + annotationClass.getSimpleName();
        this.importPackages.add(annotationClass.getName());
    }
    public AnnotationAttributes(@NotNull Class<?> annotationClass, @NotNull String displayName, String... extraPkg) {
        this.displayName = displayName;
        this.importPackages.add(annotationClass.getName());
        if (extraPkg != null && extraPkg.length > 0) {
            this.importPackages.addAll(Arrays.asList(extraPkg));
        }
    }

    public AnnotationAttributes(@NotNull String displayName, @NotNull String... importPackages) {
        this.displayName = displayName;
        this.importPackages.addAll(Arrays.asList(importPackages));
    }

    /**
     * 添加导包
     *
     * @param importPackage 包路径
     */
    public void addImportPackage(@NotNull String importPackage) {
        this.importPackages.add(importPackage);
    }

}
