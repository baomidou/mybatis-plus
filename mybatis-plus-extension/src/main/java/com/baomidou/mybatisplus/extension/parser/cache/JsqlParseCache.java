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
package com.baomidou.mybatisplus.extension.parser.cache;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;

/**
 * jsqlparser 缓存接口
 *
 * @author miemie
 * @since 2023-08-05
 */
public interface JsqlParseCache {

    void putStatement(String sql, Statement value);

    void putStatements(String sql, Statements value);

    Statement getStatement(String sql);

    Statements getStatements(String sql);
}
