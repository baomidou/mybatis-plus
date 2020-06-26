package com.baomidou.mybatisplus.extension.parsers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Create by hcl at 2020/6/24
 */
class ITableNameHandlerTest {

    /**
     * 测试 process 方法
     */
    @Test
    void process() {
        // https://github.com/baomidou/mybatis-plus/issues/2411
        ITableNameHandler handler = (metaObject, sql, tableName) -> {
            if ("t_user".equals(tableName)) {
                return "t_user_2019";
            }
            return null;
        };
        String before = "select a.* from t_user a join t_userrole b on b.userid = a.gid";
        String after = "select a.* from t_user_2019 a join t_userrole b on b.userid = a.gid";
        assertEquals(after, handler.process(null, before, "t_user"));

        before = "select * from t_user";
        after = "select * from t_user_2019";
        assertEquals(after, handler.process(null, before, "t_user"));

        before = "insert into t_user(id,name) values('1','zhangsan')";
        after = "insert into t_user_2019(id,name) values('1','zhangsan')";
        assertEquals(after, handler.process(null, before, "t_user"));
        before = "select a.*,\n" +
                "        (select count(1) from t_user) as cnt\n" +
                "        from t_xxx a";
        after = "select a.*,\n" +
                "        (select count(1) from t_user_2019) as cnt\n" +
                "        from t_xxx a";
        assertEquals(after, handler.process(null, before, "t_user"));
    }

}