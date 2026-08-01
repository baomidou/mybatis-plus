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
package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Explicit execution scope used to transfer a mapper page result to the executor interceptor.
 *
 * <p>The scope is intentionally statement-specific. This prevents nested count queries from
 * being treated as the data query and avoids reflection-based mapper method discovery.</p>
 */
public final class PageExecutionContext implements AutoCloseable {

    private static final ThreadLocal<PageExecutionContext> CURRENT = new ThreadLocal<>();

    private final String statementId;
    private final IPage<?> page;
    private final PageExecutionContext previous;
    private boolean closed;

    private PageExecutionContext(String statementId, IPage<?> page, PageExecutionContext previous) {
        this.statementId = statementId;
        this.page = page;
        this.previous = previous;
    }

    public static PageExecutionContext open(String statementId, IPage<?> page) {
        Objects.requireNonNull(statementId, "statementId must not be null");
        Objects.requireNonNull(page, "page must not be null");
        PageExecutionContext context = new PageExecutionContext(statementId, page, CURRENT.get());
        CURRENT.set(context);
        return context;
    }

    /**
     * Converts the raw query list to the selectList-compatible page envelope when this query
     * belongs to the active mapper page scope.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object processResult(String statementId, Object result) {
        PageExecutionContext context = CURRENT.get();
        if (context == null || !context.statementId.equals(statementId) || !(result instanceof List)) {
            return result;
        }
        List<?> records = (List<?>) result;
        if (records.size() == 1 && records.get(0) == context.page) {
            return result;
        }
        context.page.setRecords((List) records);
        return Collections.singletonList(context.page);
    }

    @Override
    public void close() {
        if (closed) {
            throw new IllegalStateException("Page execution context is already closed");
        }
        if (CURRENT.get() != this) {
            throw new IllegalStateException("Page execution context closed out of order");
        }
        closed = true;
        if (previous == null) {
            CURRENT.remove();
        } else {
            CURRENT.set(previous);
        }
    }
}
