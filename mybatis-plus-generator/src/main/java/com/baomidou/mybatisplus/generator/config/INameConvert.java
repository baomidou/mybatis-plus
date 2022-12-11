/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 名称转换接口类
 *
 * @author hubin
 * @since 2017-01-20
 */
public interface INameConvert {

    /**
     * 执行实体名称转换
     *
     * @param tableInfo 表信息对象
     * @return
     */
    @NotNull
    String entityNameConvert(@NotNull TableInfo tableInfo);

    /**
     * 执行属性名称转换
     *
     * @param field 表字段对象，如果属性表字段命名不一致注意 convert 属性的设置
     * @return
     */
    @NotNull
    String propertyNameConvert(@NotNull TableField field);

    /**
     * 默认名称转换接口类
     *
     * @author nieqiurong 2020/9/20.
     * @since 3.5.0
     */
    class DefaultNameConvert implements INameConvert {

        private final StrategyConfig strategyConfig;

        public DefaultNameConvert(StrategyConfig strategyConfig) {
            this.strategyConfig = strategyConfig;
        }

        @Override
        public @NotNull String entityNameConvert(@NotNull TableInfo tableInfo) {
            return NamingStrategy.capitalFirst(processName(tableInfo.getName(), strategyConfig.entity().getNaming(), strategyConfig.getTablePrefix(), strategyConfig.getTableSuffix()));
        }

        @Override
        public @NotNull String propertyNameConvert(@NotNull TableField field) {
            return processName(field.getName(), strategyConfig.entity().getColumnNaming(), strategyConfig.getFieldPrefix(), strategyConfig.getFieldSuffix());
        }

        private String processName(String name, NamingStrategy strategy, Set<String> prefix, Set<String> suffix) {
            String propertyName = name;
            // 删除前缀
            if (prefix.size() > 0) {
                propertyName = NamingStrategy.removePrefix(propertyName, prefix);
            }
            // 删除后缀
            if (suffix.size() > 0) {
                propertyName = NamingStrategy.removeSuffix(propertyName, suffix);
            }
            if (StringUtils.isBlank(propertyName)) {
                throw new RuntimeException(String.format("%s 的名称转换结果为空，请检查是否配置问题", name));
            }
            // 下划线转驼峰
            if (NamingStrategy.underline_to_camel.equals(strategy)) {
                return NamingStrategy.underlineToCamel(propertyName);
            }
            return propertyName;
        }
    }
}
