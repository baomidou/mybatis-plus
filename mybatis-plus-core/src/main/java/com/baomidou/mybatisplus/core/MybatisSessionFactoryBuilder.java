/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.IOUtils;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.Closeable;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

/**
 * <p>
 * replace default SqlSessionFactoryBuilder class
 * </p>
 *
 * @author hubin
 * @since 2016-01-23
 */
@Deprecated
public class MybatisSessionFactoryBuilder extends SqlSessionFactoryBuilder {

    private GlobalConfig globalConfig = GlobalConfigUtils.defaults();

    @Override
    public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
        return build(new MybatisXMLConfigBuilder(reader, environment, properties), reader);
    }

    @Override
    public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
        return build(new MybatisXMLConfigBuilder(inputStream, environment, properties), inputStream);
    }

    protected SqlSessionFactory build(MybatisXMLConfigBuilder parser, Closeable closeable) {
        try {
            GlobalConfigUtils.setGlobalConfig(parser.getConfiguration(), this.globalConfig);
            return build(parser.parse());
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
            IOUtils.closeQuietly(closeable);
        }
    }

    /**
     * 注入全局配置
     */
    public void setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

}
