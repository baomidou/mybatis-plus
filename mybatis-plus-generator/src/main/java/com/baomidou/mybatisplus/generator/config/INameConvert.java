/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
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

import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

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
    String entityNameConvert(TableInfo tableInfo);

    /**
     * 执行属性名称转换
     *
     * @param field 表字段对象，如果属性表字段命名不一致注意 convert 属性的设置
     * @return
     */
    String propertyNameConvert(TableField field);


    /**
     * 默认名称转换接口类
     *
     * @author nieqiurong 2020/9/20.
     * @since 3.4.1
     */
    class DefaultNameConvert implements INameConvert {

        private final StrategyConfig strategyConfig;

        public DefaultNameConvert(StrategyConfig strategyConfig) {
            this.strategyConfig = strategyConfig;
        }

        @Override
        public String entityNameConvert(TableInfo tableInfo) {
            return NamingStrategy.capitalFirst(processName(tableInfo.getName(), strategyConfig.entity().getNaming(), strategyConfig.getTablePrefix()));
        }

        @Override
        public String propertyNameConvert(TableField field) {
            return processName(field.getName(), strategyConfig.entity().getNaming(), strategyConfig.getTablePrefix());
        }

        private String processName(String name, NamingStrategy strategy, Set<String> prefix) {
            String propertyName;
            if (prefix.size() > 0) {
                if (strategy == NamingStrategy.underline_to_camel) {
                    // 删除前缀、下划线转驼峰
                    propertyName = NamingStrategy.removePrefixAndCamel(name, prefix);
                } else {
                    // 删除前缀
                    propertyName = NamingStrategy.removePrefix(name, prefix);
                }
            } else if (strategy == NamingStrategy.underline_to_camel) {
                // 下划线转驼峰
                propertyName = NamingStrategy.underlineToCamel(name);
            } else {
                // 不处理
                propertyName = name;
            }
            return propertyName;
        }
    }
}
