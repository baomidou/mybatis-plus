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
package com.baomidou.mybatisplus.generator.config;

import java.io.File;

/**
 * 输出文件接口
 *
 * @author hubin
 * @since 2023-08-04
 */
public interface IOutputFile {

    /**
     * 创建文件
     *
     * @param filePath   默认文件路径
     * @param outputFile 输出文件类型
     * @return {@link File}
     */
    File createFile(String filePath, OutputFile outputFile);
}
