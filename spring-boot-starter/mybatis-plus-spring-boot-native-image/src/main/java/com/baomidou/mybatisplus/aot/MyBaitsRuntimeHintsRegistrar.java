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
package com.baomidou.mybatisplus.aot;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

import java.io.IOException;

/**
 * mybatis aot 运行时提示注册器
 *
 * @author xiaochen
 * @since 2026-01-12
 */
class MyBaitsRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        AotUtils aotUtils = new AotUtils(hints, classLoader);
        registerXml(aotUtils);
    }

    private void registerXml(AotUtils aotUtils) {
        try {
            aotUtils.registerPattern(
                    aotUtils.findResources("", CollectUtils::isMapperXmlResource).toArray(AotUtils.EMPTY_STRING_ARRAY));
        } catch (IOException e) {
            throw new RuntimeException("注册用户资源目录的xml文件失败", e);
        }
    }

}
