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

import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import lombok.Getter;

/**
 * 控制器属性配置
 *
 * @author nieqiurong 2020/10/11.
 * @since 3.4.1
 */
@Getter
public class Controller {

    private Controller() {
    }

    /**
     * 生成 <code>@RestController</code> 控制器
     * <pre>
     *      <code>@Controller</code> -> <code>@RestController</code>
     * </pre>
     */
    private boolean restStyle = false;
    /**
     * 驼峰转连字符
     * <pre>
     *      <code>@RequestMapping("/managerUserActionHistory")</code> -> <code>@RequestMapping("/manager-user-action-history")</code>
     * </pre>
     */
    private boolean hyphenStyle = false;

    /**
     * 自定义继承的Controller类全称，带包名
     */
    private String superClass;


    public static class Builder extends BaseBuilder {

        private final Controller controller = new Controller();

        public Builder(StrategyConfig strategyConfig) {
            super(strategyConfig);
        }

        /**
         * 父类控制器
         *
         * @param clazz 父类控制器
         * @return this
         */
        public Builder superClass(Class<?> clazz) {
            return superClass(clazz.getName());
        }

        /**
         * 父类控制器
         *
         * @param superClass 父类控制器类名
         * @return this
         */
        public Builder superClass(String superClass) {
            this.controller.superClass = superClass;
            return this;
        }

        /**
         * 是否驼峰转连字符
         *
         * @return this
         */
        public Builder hyphenStyle(boolean hyphenStyle) {
            this.controller.hyphenStyle = hyphenStyle;
            return this;
        }

        /**
         * 生成@RestController控制器
         *
         * @param restStyle 是否生成@RestController控制器
         * @return this
         */
        public Builder restStyle(boolean restStyle) {
            this.controller.restStyle = restStyle;
            return this;
        }

        public Controller get() {
            return this.controller;
        }
    }
}
