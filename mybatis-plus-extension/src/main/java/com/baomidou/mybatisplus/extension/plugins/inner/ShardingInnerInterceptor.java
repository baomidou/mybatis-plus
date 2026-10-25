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
package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.datasource.DefaultShardingProcessor;
import com.baomidou.mybatisplus.extension.plugins.datasource.IShardingProcessor;
import com.baomidou.mybatisplus.extension.plugins.datasource.ShardingHelper;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;

/**
 * 多数据源
 *
 * @author hubin
 * @since 3.5.18
 */
public class ShardingInnerInterceptor implements InnerInterceptor {

    private final IShardingProcessor processor;

    public ShardingInnerInterceptor() {
        this(new DefaultShardingProcessor());
    }

    public ShardingInnerInterceptor(IShardingProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement statement, Object parameter, RowBounds rowBounds,
                            ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        changeDatasource(statement, parameter);
    }

    @Override
    public void beforeUpdate(Executor executor, MappedStatement statement, Object parameter) throws SQLException {
        changeDatasource(statement, parameter);
    }

    private void changeDatasource(MappedStatement statement, Object parameter) {
        SqlCommandType commandType = statement.getSqlCommandType();
        if (commandType == SqlCommandType.UNKNOWN || commandType == SqlCommandType.FLUSH) {
            return;
        }
        boolean changed = processor != null
            && processor.changeDatasource(statement, parameter, ShardingHelper.getDatabaseKey());
        if (!changed) {
            ShardingHelper.changeDatasource(statement.getId(), parameter, commandType);
        }
    }
}
