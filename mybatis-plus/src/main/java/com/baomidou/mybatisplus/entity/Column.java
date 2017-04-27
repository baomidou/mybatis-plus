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

import com.baomidou.mybatisplus.mapper.SqlRunner;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * 查询字段
 * </p>
 *
 * @author Caratacus
 * @Date 2017-04-27
 */
public class Column implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String AS = " AS ";

    /**
     * 获取实例
     */
    public static Column create() {
        return new Column();
    }

    //转义
    private boolean escape = true;
    //字段
    private String column;
    //AS
    private String as;

    public String getColumn() {
        return column;
    }

    public Column column(String column) {
        this.column = column;
        return this;
    }

    public String getAs() {
        if (StringUtils.isEmpty(getColumn()) || StringUtils.isEmpty(as)) {
            return StringUtils.EMPTY;
        }
        String quote = null;
        if (isEscape() && SqlRunner.FACTORY != null) {
            GlobalConfiguration globalConfig = GlobalConfiguration.getGlobalConfig(SqlRunner.FACTORY.getConfiguration());
            quote = globalConfig.getIdentifierQuote() == null ? globalConfig.getDbType().getQuote() : globalConfig.getIdentifierQuote();
        }
        return AS + (StringUtils.isNotEmpty(quote) ? String.format(quote, as) : as);
    }

    public Column as(String as) {
        this.as = as;
        return this;
    }

    public boolean isEscape() {
        return escape;
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }
}
