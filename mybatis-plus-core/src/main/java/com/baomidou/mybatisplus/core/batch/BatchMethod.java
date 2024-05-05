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
package com.baomidou.mybatisplus.core.batch;

import org.apache.ibatis.mapping.MappedStatement;

/**
 * 批量执行方法
 *
 * @author nieqiurong
 * @since 3.5.4
 */
public class BatchMethod<T> {

    /**
     * 执行的{@link MappedStatement#getId()}
     */
    private final String statementId;

    /**
     * 方法参数转换器,默认传递批量的entity的参数
     */
    private ParameterConvert<T> parameterConvert = (entity) -> entity;

    public BatchMethod(String statementId) {
        this.statementId = statementId;
    }

    public BatchMethod(String statementId, ParameterConvert<T> parameterConvert) {
        this.statementId = statementId;
        this.parameterConvert = parameterConvert;
    }

    public String getStatementId() {
        return statementId;
    }

    public ParameterConvert<T> getParameterConvert() {
        return parameterConvert;
    }

}
