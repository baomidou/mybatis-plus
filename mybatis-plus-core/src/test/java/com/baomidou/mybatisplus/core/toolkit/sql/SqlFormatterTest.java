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
package com.baomidou.mybatisplus.core.toolkit.sql;

import org.junit.jupiter.api.Test;

/**
 * @author miemie
 * @since 2019-01-09
 */
class SqlFormatterTest {

    @Test
    void format() {
        String format = new SqlFormatter().format(" INSERT INTO bianla_easemob_chat_record \n" +
            " ( msg_id,\n" +
            "\tmsg_send_time,\n" +
            "\tdirection,\n" +
            "\tsend_from,\n" +
            "\tsend_to,\n" +
            "\tchat_type,\n" +
            "\tbodies,\n" +
            "\tmsg,\n" +
            "\text,\n" +
            "\t`type`,\n" +
            "\tcreated )  VALUES \n" +
            " ( '555794876462008332',\n" +
            "\t'2019-01-09 02:07:58.848',\n" +
            "\t'outgoing',\n" +
            "\t'bianla_67248',\n" +
            "\t'bianla_5197773',\n" +
            "\t'chat',\n" +
            "\t'\\'dfsfsfsdf',\n" +
            "\t'[):][):]',\n" +
            "\t'{}',\n" +
            "\t'txt',\n" +
            "\t'2019-01-09 17:45:33.992' )");
        System.out.println(format);
    }
}
