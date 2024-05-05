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
package com.baomidou.mybatisplus.extension.ddl;

import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.List;
import java.util.function.Consumer;

/**
 * 非多数据源 DDL 实现
 *
 * @author hubin
 * @since 2021-09-23
 */
public class SimpleDdl implements IDdl {

    @Autowired
    private DataSource dataSource;

    @Override
    public void runScript(Consumer<DataSource> consumer) {
        consumer.accept(dataSource);
    }

    @Override
    public List<String> getSqlFiles() {
        return null;
    }

}
