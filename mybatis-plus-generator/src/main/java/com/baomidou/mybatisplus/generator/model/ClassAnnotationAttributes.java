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

import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;

/**
 * 类注解属性
 *
 * @author nieqiurong
 * @since 3.5.10
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class ClassAnnotationAttributes extends AnnotationAttributes {

    /**
     * 根据{@link TableInfo}信息加工处理显示注解
     * <p>显示名称处理函数(最终需要转换成{@link #setDisplayName(String)})<p/>
     */
    private Function<TableInfo, String> displayNameFunction;

    public ClassAnnotationAttributes(@NotNull Class<?> annotationClass) {
        super(annotationClass);
    }

    public ClassAnnotationAttributes(@NotNull Class<?> annotationClass, @NotNull Function<TableInfo, String> displayNameFunction, String... extraPkg) {
        super(annotationClass);
        this.displayNameFunction = displayNameFunction;
        if (extraPkg != null && extraPkg.length > 0) {
            super.getImportPackages().addAll(Arrays.asList(extraPkg));
        }
    }

    public ClassAnnotationAttributes(@NotNull Class<?> annotationClass, @NotNull String displayName, String... extraPkg) {
        super(annotationClass, displayName, extraPkg);
    }

    public ClassAnnotationAttributes(@NotNull String displayName, @NotNull String... importPackages) {
        super(displayName, importPackages);
    }

    public ClassAnnotationAttributes(@NotNull String importPackage, @NotNull Function<TableInfo, String> displayNameFunction) {
        super.getImportPackages().add(importPackage);
        this.displayNameFunction = displayNameFunction;
    }

    public ClassAnnotationAttributes(@NotNull Set<String> importPackages, @NotNull Function<TableInfo, String> displayNameFunction) {
        super.getImportPackages().addAll(importPackages);
        this.displayNameFunction = displayNameFunction;
    }

    public void handleDisplayName(TableInfo tableInfo) {
        if (displayNameFunction != null) {
            super.setDisplayName(displayNameFunction.apply(tableInfo));
        }
    }

}
