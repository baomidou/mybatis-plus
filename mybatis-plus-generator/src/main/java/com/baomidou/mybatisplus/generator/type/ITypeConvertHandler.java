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
package com.baomidou.mybatisplus.generator.type;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import org.jetbrains.annotations.NotNull;

/**
 * 类型转换处理器
 *
 * @author nieqiurong 2022/5/12.
 * @since 3.5.3
 */
public interface ITypeConvertHandler {

    /**
     * 转换字段类型
     *
     * @param globalConfig 全局配置
     * @param typeRegistry 类型注册信息
     * @param metaInfo     字段元数据信息
     * @return 子类类型
     */
    @NotNull
    IColumnType convert(GlobalConfig globalConfig, TypeRegistry typeRegistry, TableField.MetaInfo metaInfo);

}
