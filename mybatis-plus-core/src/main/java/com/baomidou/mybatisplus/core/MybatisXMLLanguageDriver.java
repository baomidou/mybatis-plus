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
package com.baomidou.mybatisplus.core;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.util.List;

/**
 * 继承 {@link XMLLanguageDriver} 重装构造函数, 使用自己的 MybatisParameterHandler
 *
 * @author hubin
 * @since 2016-03-11
 */
public class MybatisXMLLanguageDriver extends XMLLanguageDriver {

    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement,
                                                   Object parameterObject, BoundSql boundSql) {
        // TODO 使用 MybatisParameterHandler 而不是 ParameterHandler
        return new MybatisParameterHandler(mappedStatement, parameterObject, boundSql);
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        GlobalConfig.DbConfig config = GlobalConfigUtils.getDbConfig(configuration);
        if (config.isReplacePlaceholder()) {
            List<String> find = SqlUtils.findPlaceholder(script);
            if (CollectionUtils.isNotEmpty(find)) {
                try {
                    script = SqlUtils.replaceSqlPlaceholder(script, find, config.getEscapeSymbol());
                } catch (MybatisPlusException e) {
                    throw new IncompleteElementException();
                }
            }
        }
        return super.createSqlSource(configuration, script, parameterType);
    }
}
