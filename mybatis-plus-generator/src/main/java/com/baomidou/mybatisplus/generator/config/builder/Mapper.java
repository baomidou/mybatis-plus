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
package com.baomidou.mybatisplus.generator.config.builder;

import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import lombok.Getter;

/**
 * 控制器属性配置
 *
 * @author nieqiurong 2020/10/11.
 * @since 3.4.1
 */
@Getter
public class Mapper {

    private Mapper() {
    }

    /**
     * 自定义继承的Mapper类全称，带包名
     */
    private String superClass = ConstVal.SUPER_MAPPER_CLASS;

    /**
     * 开启 BaseResultMap
     *
     * @since 3.4.1
     */
    private boolean baseResultMap;

    /**
     * 开启 baseColumnList
     *
     * @since 3.4.1
     */
    private boolean baseColumnList;

    /**
     * 是否在xml中添加二级缓存配置
     *
     * @since 3.4.1
     */
    private boolean enableXmlCache;

    public static class Builder extends BaseBuilder {

        private final Mapper mapper = new Mapper();

        public Builder(StrategyConfig strategyConfig) {
            super(strategyConfig);
        }

        /**
         * 父类Mapper
         *
         * @param superClass 类名
         * @return this
         */
        public Builder superClass(String superClass) {
            this.mapper.superClass = superClass;
            return this;
        }

        /**
         * 父类Mapper
         *
         * @param superClass 类
         * @return this
         * @since 3.4.1
         */
        public Builder superClass(Class<?> superClass) {
            return superClass(superClass.getName());
        }

        /**
         * 开启baseResultMap
         *
         * @param baseResultMap 是否开启baseResultMap
         * @return this
         * @since 3.4.1
         */
        public Builder baseResultMap(boolean baseResultMap){
            this.mapper.baseResultMap = baseResultMap;
            return this;
        }

        /**
         * 开启baseColumnList
         *
         * @param baseColumnList 是否开启baseColumnList
         * @return this
         * @since 3.4.1
         */
        public Builder baseColumnList(boolean baseColumnList) {
            this.mapper.baseColumnList = baseColumnList;
            return this;
        }

        /**
         * 是否在xml中添加二级缓存配置
         *
         * @param enableXmlCache 是否开启
         * @return this
         */
        public Builder enableXmlCache(boolean enableXmlCache) {
            this.mapper.enableXmlCache = enableXmlCache;
            return this;
        }

        public Mapper get() {
            return this.mapper;
        }
    }
}
