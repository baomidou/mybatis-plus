/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.test;

import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SQL 解析测试
 */
class JSqlParserTest {

    @Test
    void parser() throws Exception {
        Select select = (Select) CCJSqlParserUtil.parse("SELECT a,b,c FROM tableName t WHERE t.col = 9 and b=c LIMIT 3, ?");

        PlainSelect ps = (PlainSelect) select.getSelectBody();

        System.out.println(ps.getWhere().toString());
        System.out.println(ps.getSelectItems().get(1).toString());

        AndExpression e = (AndExpression) ps.getWhere();
        System.out.println(e.getLeftExpression());
    }

    @Test
    void notLikeParser() throws Exception {
        final String targetSql = "SELECT * FROM tableName WHERE id NOT LIKE ?";
        Select select = (Select) CCJSqlParserUtil.parse(targetSql);
        assertThat(select.toString()).isEqualTo(targetSql);
    }


    @Test
    void updateWhereParser() throws Exception {
        Update update = (Update) CCJSqlParserUtil.parse("Update tableName t SET t.a=(select c from tn where tn.id=t.id),b=2,c=3 ");
        Assertions.assertNull(update.getWhere());
    }


    @Test
    void deleteWhereParser() throws Exception {
        Delete delete = (Delete) CCJSqlParserUtil.parse("delete from tableName t");
        Assertions.assertNull(delete.getWhere());
    }
}
