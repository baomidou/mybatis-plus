/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.plugins.parser.logicdelete;

import net.sf.jsqlparser.expression.Expression;

/**
 * <p>
 * 逻辑删除处理器
 * </p>
 *
 * @author willenfoo
 * @since 2018-03-09
 */
public interface LogicDeleteHandler {

    Expression getValue(String tableName);

    String getColumn(String tableName);

    boolean doTableFilter(String tableName);
}
