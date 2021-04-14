/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.scripting;

import com.baomidou.mybatisplus.core.MybatisParameterHandler;
import lombok.NoArgsConstructor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.mybatis.scripting.thymeleaf.ThymeleafLanguageDriver;
import org.mybatis.scripting.thymeleaf.ThymeleafLanguageDriverConfig;
import org.thymeleaf.ITemplateEngine;

/**
 * @author miemie
 * @since 2020-06-18
 */
@NoArgsConstructor
public class MybatisThymeleafLanguageDriver extends ThymeleafLanguageDriver {

    public MybatisThymeleafLanguageDriver(ThymeleafLanguageDriverConfig config) {
        super(config);
    }

    public MybatisThymeleafLanguageDriver(ITemplateEngine templateEngine) {
        super(templateEngine);
    }

    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        return new MybatisParameterHandler(mappedStatement, parameterObject, boundSql);
    }
}
