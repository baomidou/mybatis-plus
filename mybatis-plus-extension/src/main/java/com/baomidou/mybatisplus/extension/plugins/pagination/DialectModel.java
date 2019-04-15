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
package com.baomidou.mybatisplus.extension.plugins.pagination;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;

import com.baomidou.mybatisplus.core.toolkit.Assert;

import lombok.Getter;

/**
 * 分页参数动态化所需 model
 *
 * @author miemie
 * @since 2018-10-31
 */
public class DialectModel {
    private static final String FIRST_PARAM_NAME = "mybatis_plus_first";
    private static final String SECOND_PARAM_NAME = "mybatis_plus_second";

    /**
     * 分页方言 sql
     */
    @Getter
    private final String dialectSql;
    /**
     * 提供 Configuration
     */
    private Configuration configuration;
    /**
     * 用 List<ParameterMapping> 消费第一个值
     */
    private Consumer<List<ParameterMapping>> firstParamConsumer = i -> {
    };
    /**
     * 用 Map<String, Object> 消费第一个值
     */
    private Consumer<Map<String, Object>> firstParamMapConsumer = i -> {
    };
    /**
     * 用 List<ParameterMapping> 消费第二个值
     */
    private Consumer<List<ParameterMapping>> secondParamConsumer = i -> {
    };
    /**
     * 用 Map<String, Object> 消费第二个值
     */
    private Consumer<Map<String, Object>> secondParamMapConsumer = i -> {
    };
    /**
     * 提供 第一个值
     */
    private final long firstParam;
    /**
     * 提供 第二个值
     */
    private final long secondParam;

    public DialectModel(String dialectSql, long firstParam, long secondParam) {
        this.dialectSql = dialectSql;
        this.firstParam = firstParam;
        this.secondParam = secondParam;
    }

    /**
     * 设置消费 List<ParameterMapping> 的方式
     * <p>带下标的</p>
     * <p>mark: 标记一下,暂时没看到哪个数据库的分页方言会存在使用该方法</p>
     *
     * @return this
     */
    @SuppressWarnings("unused")
    public DialectModel setConsumer(boolean isFirstParam, Function<List<ParameterMapping>, Integer> function) {
        if (isFirstParam) {
            this.firstParamConsumer = i -> i.add(function.apply(i), new ParameterMapping
                .Builder(configuration, FIRST_PARAM_NAME, long.class).build());
        } else {
            this.secondParamConsumer = i -> i.add(function.apply(i), new ParameterMapping
                .Builder(configuration, SECOND_PARAM_NAME, long.class).build());
        }
        this.setParamMapConsumer(isFirstParam);
        return this;
    }

    /**
     * 设置消费 List<ParameterMapping> 的方式
     * <p>不带下标的</p>
     *
     * @return this
     */
    public DialectModel setConsumer(boolean isFirstParam) {
        if (isFirstParam) {
            this.firstParamConsumer = i -> i.add(new ParameterMapping.Builder(configuration, FIRST_PARAM_NAME, long.class).build());
        } else {
            this.secondParamConsumer = i -> i.add(new ParameterMapping.Builder(configuration, SECOND_PARAM_NAME, long.class).build());
        }
        this.setParamMapConsumer(isFirstParam);
        return this;
    }

    /**
     * 设置消费 List<ParameterMapping> 的方式
     * <p>不带下标的,两个值都有</p>
     *
     * @return this
     */
    public DialectModel setConsumerChain() {
        return setConsumer(true).setConsumer(false);
    }

    /**
     * 把内部所有需要消费的都消费掉
     *
     * @param parameterMappings    ParameterMapping 集合
     * @param configuration        Configuration
     * @param additionalParameters additionalParameters map
     */
    public void consumers(List<ParameterMapping> parameterMappings, Configuration configuration,
                          Map<String, Object> additionalParameters) {
        Assert.notNull(configuration, "configuration must notNull !");
        Assert.notNull(parameterMappings, "parameterMappings must notNull !");
        Assert.notNull(additionalParameters, "additionalParameters must notNull !");
        this.configuration = configuration;
        this.firstParamConsumer.accept(parameterMappings);
        this.secondParamConsumer.accept(parameterMappings);
        this.firstParamMapConsumer.accept(additionalParameters);
        this.secondParamMapConsumer.accept(additionalParameters);
    }

    /**
     * 设置消费 Map<String, Object> 的方式
     */
    private void setParamMapConsumer(boolean isFirstParam) {
        if (isFirstParam) {
            this.firstParamMapConsumer = i -> i.put(FIRST_PARAM_NAME, firstParam);
        } else {
            this.secondParamMapConsumer = i -> i.put(SECOND_PARAM_NAME, secondParam);
        }
    }
}
