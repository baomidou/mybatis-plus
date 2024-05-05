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
package com.baomidou.mybatisplus.autoconfigure;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author nieqiurong
 * @since 3.5.4
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(InnerInterceptor.class)
@ConditionalOnMissingBean(MybatisPlusInterceptor.class)
public class MybatisPlusInnerInterceptorAutoConfiguration {

    @Bean
    public MybatisPlusInterceptor defaultMybatisPlusInterceptor(List<InnerInterceptor> innerInterceptorList) {
        MybatisPlusInterceptor plusInterceptor = new MybatisPlusInterceptor();
        plusInterceptor.setInterceptors(innerInterceptorList);
        return plusInterceptor;
    }

}
