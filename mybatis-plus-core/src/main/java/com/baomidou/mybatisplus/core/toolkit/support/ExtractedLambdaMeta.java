/*
 * Copyright (c) 2011-2022, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.toolkit.support;

import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;

import java.lang.invoke.SerializedLambda;

/**
 * 使用 {@link com.baomidou.mybatisplus.core.toolkit.SerializedLambdaExtractor} 创建的元信息
 *
 * @author WangHeng
 */
public class ExtractedLambdaMeta implements LambdaMeta {
    private final SerializedLambda lambda;

    public ExtractedLambdaMeta(SerializedLambda lambda) {
        this.lambda = lambda;
    }

    @Override
    public String getImplMethodName() {
        return lambda.getImplMethodName();
    }

    @Override
    public Class<?> getInstantiatedClass() {
        // ex. (Lcom/baomidou/mybatisplus/test/toolkit/LambdaUtilsTest$TestModel;)Ljava/lang/Object;
        String instantiatedMethodType = lambda.getInstantiatedMethodType();
        // ex. com.baomidou.mybatisplus.test.toolkit.LambdaUtilsTest$TestModel
        String instantiatedType = instantiatedMethodType
            .substring(2, instantiatedMethodType.indexOf(StringPool.SEMICOLON))
            .replace(StringPool.SLASH, StringPool.DOT);

        return ClassUtils.toClassConfident(instantiatedType);
    }

}
