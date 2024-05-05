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

import com.baomidou.mybatisplus.extension.ddl.history.IDdlGenerator;

import javax.sql.DataSource;
import java.util.List;
import java.util.function.Consumer;

/**
 * DDL 处理器
 *
 * @author hubin
 * @since 2021-06-22
 */
public interface IDdl {

    /**
     * 执行 SQL 脚本
     *
     * @param consumer 指定数据源执行
     */
    void runScript(Consumer<DataSource> consumer);

    /**
     * DDL 生成器
     */
    default IDdlGenerator getDdlGenerator() {
        return null;
    }

    /**
     * 执行 SQL 脚本
     * <p>Resources.getResourceAsReader("db/test.sql")</p>
     *
     * @return SQL 脚本文件列表
     */
    List<String> getSqlFiles();
}
