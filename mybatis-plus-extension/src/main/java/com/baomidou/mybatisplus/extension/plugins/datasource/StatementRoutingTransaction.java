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
package com.baomidou.mybatisplus.extension.plugins.datasource;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.transaction.Transaction;

import java.sql.SQLException;

/**
 * A transaction whose physical connection can change between statements.
 *
 * @author AMin
 */
public interface StatementRoutingTransaction extends Transaction {

    /**
     * Flush pending batches and release reusable statements before a route change. Implementations must not acquire a
     * connection solely for this check, since the query may be served from a cache.
     *
     * @param executor the executor owned by this transaction
     */
    void prepareStatement(Executor executor) throws SQLException;
}
