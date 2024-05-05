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
package com.baomidou.mybatisplus.generator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * 运行工具类
 *
 * @author nieqiurong 2020/11/13.
 * @since 3.5.0
 */
public class RuntimeUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeUtils.class);

    /**
     * 打开指定输出文件目录
     *
     * @param outDir 输出文件目录
     */
    public static void openDir(String outDir) throws IOException {
        File file = new File(outDir);
        if (!file.isDirectory()) {
            LOGGER.error("illegal directory:{}", outDir);
            throw new IllegalArgumentException("Illegal directory " + outDir);
        }
        String osName = System.getProperty("os.name");
        if (osName != null) {
            if (osName.contains("Mac")) {
                Runtime.getRuntime().exec("open " + outDir);
            } else if (osName.contains("Windows")) {
                Runtime.getRuntime().exec(MessageFormat.format("cmd /c start \"\" \"{0}\"", outDir));
            } else {
                LOGGER.debug("file output directory:{}", outDir);
            }
        } else {
            LOGGER.warn("read operating system failed!");
        }
    }
}
