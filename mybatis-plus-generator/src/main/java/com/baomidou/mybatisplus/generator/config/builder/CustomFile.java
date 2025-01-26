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
package com.baomidou.mybatisplus.generator.config.builder;

import com.baomidou.mybatisplus.generator.config.IConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import lombok.Getter;

import java.util.function.Function;

/**
 * 自定义模板文件配置
 *
 * @author xusimin
 * @since 3.5.3
 */
@Getter
public class CustomFile {

    /**
     * 文件名称格式化函数
     */
    private Function<TableInfo, String> formatNameFunction;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 模板路径
     */
    private String templatePath;

    /**
     * 自定义文件包名
     */
    private String packageName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 是否覆盖已有文件（默认 false）
     */
    private boolean fileOverride;

    /**
     * 获取文件短名称(去除后缀)
     *
     * @return 文件名
     * @since 3.5.10
     */
    public String getShortName() {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    /**
     * 构建者
     */
    public static class Builder implements IConfigBuilder<CustomFile> {

        private final CustomFile customFile;

        public Builder() {
            this.customFile = new CustomFile();
        }

        /**
         * 文件名称格式化函数
         */
        public CustomFile.Builder formatNameFunction(Function<TableInfo, String> formatNameFunction) {
            this.customFile.formatNameFunction = formatNameFunction;
            return this;
        }

        /**
         * 文件名称
         */
        public CustomFile.Builder fileName(String fileName) {
            this.customFile.fileName = fileName;
            return this;
        }

        /**
         * 模板路径
         */
        public CustomFile.Builder templatePath(String templatePath) {
            this.customFile.templatePath = templatePath;
            return this;
        }

        /**
         * 包路径
         */
        public CustomFile.Builder packageName(String packageName) {
            this.customFile.packageName = packageName;
            return this;
        }

        /**
         * 文件路径，默认为 PackageConfig.parent 路径
         */
        public CustomFile.Builder filePath(String filePath) {
            this.customFile.filePath = filePath;
            return this;
        }

        /**
         * 覆盖已有文件
         */
        public CustomFile.Builder enableFileOverride() {
            this.customFile.fileOverride = true;
            return this;
        }

        @Override
        public CustomFile build() {
            return this.customFile;
        }
    }
}
