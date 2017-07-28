/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 查询字段
 * </p>
 *
 * @author bliver
 * @Date 2017-07-28
 */
public class Columns implements Serializable {

    private static final long serialVersionUID = 1L;

    private Columns() {
    }

    /**
     * 获取实例
     */
    public static Columns create() {
        return new Columns();
    }

    public Columns column(String column) {
        Column oneColumn = Column.create().column(column);
        this.columns.add(oneColumn);
        return this;
    }

    public Columns column(String column, String as) {
        Column oneColumn = Column.create().column(column).as(as);
        this.columns.add(oneColumn);
        return this;
    }

    public Columns column(String column, String as, boolean escape) {
        Column oneColumn = Column.create().column(column).as(as);
        oneColumn.setEscape(escape);
        this.columns.add(oneColumn);
        return this;
    }

    //字段
    private List<Column> columns = new ArrayList<>();

    public Column[] getColumns() {
        Column[] columnArray = new Column[columns.size()];
        return columns.toArray(columnArray);
    }
}
