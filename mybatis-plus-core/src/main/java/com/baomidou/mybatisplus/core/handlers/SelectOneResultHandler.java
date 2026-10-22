/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.handlers;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.util.List;

/**
 * Internal result handler for BaseMapper.selectOne. Its type lets the cache interceptor identify internal queries
 * without changing user-supplied result handlers.
 */
public final class SelectOneResultHandler<T> implements ResultHandler<T> {

    private final List<T> results;

    public SelectOneResultHandler(List<T> results) {
        this.results = results;
    }

    @Override
    public void handleResult(ResultContext<? extends T> context) {
        results.add(context.getResultObject());
        if (results.size() == 2) {
            context.stop();
        }
    }
}
