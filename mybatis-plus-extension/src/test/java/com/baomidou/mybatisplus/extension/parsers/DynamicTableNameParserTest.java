package com.baomidou.mybatisplus.extension.parsers;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Create by hcl at 2020/6/30
 */
class DynamicTableNameParserTest {

    @Test
    void parser() {
        DynamicTableNameParser parser = new DynamicTableNameParser();
        Map<String, ITableNameHandler> tableNameHandlerMap = new HashMap<>();
        tableNameHandlerMap.put("t_user",
                // https://github.com/baomidou/mybatis-plus/issues/2411
                (metaObject, sql, tableName) -> {
                    if ("t_user".equals(tableName)) {
                        return "t_user_2019";
                    }
                    return tableName;
                });
        parser.setTableNameHandlerMap(tableNameHandlerMap);

        String before = "select a.* from t_user a join t_userrole b on b.userid = a.gid";
        String after = "select a.* from t_user_2019 a join t_userrole b on b.userid = a.gid";
        assertEquals(after, parser.parser(null, before).getSql());

        before = "select * from t_user";
        after = "select * from t_user_2019";
        assertEquals(after, parser.parser(null, before).getSql());

        before = "insert into t_user(id,name) values('1','zhangsan')";
        after = "insert into t_user_2019(id,name) values('1','zhangsan')";
        assertEquals(after, parser.parser(null, before).getSql());
        before = "select a.*,\n" +
                "        (select count(1) from t_user) as cnt\n" +
                "        from t_xxx a";
        after = "select a.*,\n" +
                "        (select count(1) from t_user_2019) as cnt\n" +
                "        from t_xxx a";
        assertEquals(after, parser.parser(null, before).getSql());
    }

}