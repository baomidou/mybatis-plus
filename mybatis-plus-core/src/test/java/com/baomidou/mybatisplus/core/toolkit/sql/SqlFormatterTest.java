package com.baomidou.mybatisplus.core.toolkit.sql;

import org.junit.Test;

/**
 * @author miemie
 * @since 2019-01-09
 */
public class SqlFormatterTest {

    @Test
    public void format() {
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
