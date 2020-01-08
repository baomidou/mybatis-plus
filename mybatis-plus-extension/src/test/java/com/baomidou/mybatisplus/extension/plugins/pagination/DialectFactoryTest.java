/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension.plugins.pagination;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.MySqlDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author nieqiuqiu
 */
class DialectFactoryTest {

    @Test
    void test() {
        DialectModel dialectModel;
        IPage<Object> page = new Page<>();
        //使用默认方言
        dialectModel = DialectFactory.buildPaginationSql(page, "select * from test ", com.baomidou.mybatisplus.annotation.DbType.MYSQL, null);
        Assertions.assertEquals(dialectModel.getDialectSql(), "select * from test  LIMIT ?,?");
        dialectModel = DialectFactory.buildPaginationSql(page, "select * from test ", DbType.MYSQL, null);
        Assertions.assertEquals(dialectModel.getDialectSql(), "select * from test  LIMIT ?,?");
        //使用默认自定义方言
        dialectModel = DialectFactory.buildPaginationSql(page, "select * from test ", com.baomidou.mybatisplus.annotation.DbType.H2, MySqlDialect.class.getName());
        Assertions.assertEquals(dialectModel.getDialectSql(), "select * from test  LIMIT ?,?");
        dialectModel = DialectFactory.buildPaginationSql(page, "select * from test ", DbType.H2, MySqlDialect.class.getName());
        Assertions.assertEquals(dialectModel.getDialectSql(), "select * from test  LIMIT ?,?");
    }
}
