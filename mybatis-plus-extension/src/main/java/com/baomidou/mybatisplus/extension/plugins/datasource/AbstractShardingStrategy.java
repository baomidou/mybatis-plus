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

import org.apache.ibatis.mapping.SqlCommandType;

import java.util.List;

/**
 * 表和库切分策略抽象类
 *
 * @author hubin
 * @since 3.5.18
 */
public abstract class AbstractShardingStrategy implements IShardingStrategy {

    @Override
    public void determineDatasourceKey(String group, String statementId, Object parameter, SqlCommandType commandType) {
        changeDatabaseKey(group, commandType, keys -> chooseKey(keys, statementId, parameter));
    }

    public abstract String chooseKey(List<String> keys, String statementId, Object parameter);
}
