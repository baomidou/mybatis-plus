package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.jsqlparser.enums.ExpressionAppendMode;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-07-30
 */
class TenantLineInnerInterceptorTest {

    private static final Map<String, String> FIRS_RESULT_TMAP = new HashMap<>();

    private static final Map<String, String> LAST_RESULT_TMAP = new HashMap<>();

    static {
        firstResultMap();
        lastResultMap();
    }

    static void firstResultMap() {
        FIRS_RESULT_TMAP.put("insert into entity (id) values (?)",
            "INSERT INTO entity (id, tenant_id) VALUES (?, 1)");
        FIRS_RESULT_TMAP.put("insert into entity (id,name) values (?,?)",
            "INSERT INTO entity (id, name, tenant_id) VALUES (?, ?, 1)");
        // batch
        FIRS_RESULT_TMAP.put("insert into entity (id) values (?),(?)",
            "INSERT INTO entity (id, tenant_id) VALUES (?, 1), (?, 1)");
        FIRS_RESULT_TMAP.put("insert into entity (id,name) values (?,?),(?,?)",
            "INSERT INTO entity (id, name, tenant_id) VALUES (?, ?, 1), (?, ?, 1)");
        // 无 insert的列
        FIRS_RESULT_TMAP.put("insert into entity value (?,?)",
            "INSERT INTO entity VALUES (?, ?)");
        // 自己加了insert的列
        FIRS_RESULT_TMAP.put("insert into entity (id,name,tenant_id) value (?,?,?)",
            "INSERT INTO entity (id, name, tenant_id) VALUES (?, ?, ?)");
        // insert into select
        FIRS_RESULT_TMAP.put("insert into entity (id,name) select id,name from entity2",
            "INSERT INTO entity (id, name, tenant_id) SELECT id, name, tenant_id FROM entity2 WHERE tenant_id = 1");
        FIRS_RESULT_TMAP.put("insert into entity (id,name) select * from entity2 e2",
            "INSERT INTO entity (id, name, tenant_id) SELECT * FROM entity2 e2 WHERE e2.tenant_id = 1");
        FIRS_RESULT_TMAP.put("insert into entity (id,name) select id,name from (select id,name from entity3 e3) t",
            "INSERT INTO entity (id, name, tenant_id) SELECT id, name, tenant_id FROM (SELECT id, name, tenant_id FROM entity3 e3 WHERE e3.tenant_id = 1) t");
        FIRS_RESULT_TMAP.put("insert into entity (id,name) select * from (select id,name from entity3 e3) t",
            "INSERT INTO entity (id, name, tenant_id) SELECT * FROM (SELECT id, name, tenant_id FROM entity3 e3 WHERE e3.tenant_id = 1) t");
        FIRS_RESULT_TMAP.put("insert into entity (id,name) select t.* from (select id,name from entity3 e3) t",
            "INSERT INTO entity (id, name, tenant_id) SELECT t.* FROM (SELECT id, name, tenant_id FROM entity3 e3 WHERE e3.tenant_id = 1) t");

        FIRS_RESULT_TMAP.put("delete from entity where id = ?", "DELETE FROM entity WHERE tenant_id = 1 AND id = ?");

        FIRS_RESULT_TMAP.put("update entity set name = ? where id = ?",
            "UPDATE entity SET name = ? WHERE tenant_id = 1 AND id = ?");

        // set subSelect
        FIRS_RESULT_TMAP.put("UPDATE entity e SET e.cq = (SELECT e1.total FROM entity e1 WHERE e1.id = ?) WHERE e.id = ?",
            "UPDATE entity e SET e.cq = (SELECT e1.total FROM entity e1 WHERE e1.tenant_id = 1 AND e1.id = ?) WHERE e.tenant_id = 1 AND e.id = ?");

        FIRS_RESULT_TMAP.put("UPDATE sys_user SET (name, age) = ('秋秋', 18), address = 'test'",
            "UPDATE sys_user SET (name, age) = ('秋秋', 18), address = 'test' WHERE tenant_id = 1");

        FIRS_RESULT_TMAP.put("UPDATE entity t1 INNER JOIN entity t2 ON t1.a= t2.a SET t1.b = t2.b, t1.c = t2.c",
            "UPDATE entity t1 INNER JOIN entity t2 ON t1.a = t2.a SET t1.b = t2.b, t1.c = t2.c WHERE t1.tenant_id = 1");

        // 单表
        FIRS_RESULT_TMAP.put("select * from entity where id = ?", "SELECT * FROM entity WHERE tenant_id = 1 AND id = ?");

        FIRS_RESULT_TMAP.put("select * from entity where id = ? or name = ?", "SELECT * FROM entity WHERE tenant_id = 1 AND (id = ? OR name = ?)");

        FIRS_RESULT_TMAP.put("SELECT * FROM entity WHERE (id = ? OR name = ?)", "SELECT * FROM entity WHERE tenant_id = 1 AND (id = ? OR name = ?)");

        /* not */
        FIRS_RESULT_TMAP.put("SELECT * FROM entity WHERE not (id = ? OR name = ?)", "SELECT * FROM entity WHERE tenant_id = 1 AND NOT (id = ? OR name = ?)");

        FIRS_RESULT_TMAP.put("SELECT * FROM entity u WHERE not (u.id = ? OR u.name = ?)", "SELECT * FROM entity u WHERE u.tenant_id = 1 AND NOT (u.id = ? OR u.name = ?)");

        /* in */
        FIRS_RESULT_TMAP.put("SELECT * FROM entity e WHERE e.id IN (select e1.id from entity1 e1 where e1.id = ?)", "SELECT * FROM entity e WHERE e.tenant_id = 1 AND e.id IN (SELECT e1.id FROM entity1 e1 WHERE e1.tenant_id = 1 AND e1.id = ?)");
        // 在最前
        FIRS_RESULT_TMAP.put("SELECT * FROM entity e WHERE e.id IN " +
                "(select e1.id from entity1 e1 where e1.id = ?) and e.id = ?",
            "SELECT * FROM entity e WHERE e.tenant_id = 1 AND e.id IN (SELECT e1.id FROM entity1 e1 WHERE e1.tenant_id = 1 AND e1.id = ?) AND e.id = ?");
        // 在最后
        FIRS_RESULT_TMAP.put("SELECT * FROM entity e WHERE e.id = ? and e.id IN " +
                "(select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.tenant_id = 1 AND e.id = ? AND e.id IN (SELECT e1.id FROM entity1 e1 WHERE e1.tenant_id = 1 AND e1.id = ?)");
        // 在中间
        FIRS_RESULT_TMAP.put("SELECT * FROM entity e WHERE e.id = ? and e.id IN " +
                "(select e1.id from entity1 e1 where e1.id = ?) and e.id = ?",
            "SELECT * FROM entity e WHERE e.tenant_id = 1 AND e.id = ? AND e.id IN (SELECT e1.id FROM entity1 e1 WHERE e1.tenant_id = 1 AND e1.id = ?) AND e.id = ?");

        FIRS_RESULT_TMAP.put("SELECT * FROM entity e WHERE e.id = (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.tenant_id = 1 AND e.id = (SELECT e1.id FROM entity1 e1 WHERE e1.tenant_id = 1 AND e1.id = ?)");

        /* inner not = */
        FIRS_RESULT_TMAP.put("SELECT * FROM entity e WHERE not (e.id = (select e1.id from entity1 e1 where e1.id = ?))",
            "SELECT * FROM entity e WHERE e.tenant_id = 1 AND NOT (e.id = (SELECT e1.id FROM entity1 e1 WHERE e1.tenant_id = 1 AND e1.id = ?))");

        FIRS_RESULT_TMAP.put("SELECT * FROM entity e WHERE not (e.id = (select e1.id from entity1 e1 where e1.id = ?) and e.id = ?)",
            "SELECT * FROM entity e WHERE e.tenant_id = 1 AND NOT (e.id = (SELECT e1.id FROM entity1 e1 WHERE e1.tenant_id = 1 AND e1.id = ?) AND e.id = ?)");

        FIRS_RESULT_TMAP.put("SELECT * FROM entity e WHERE EXISTS (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.tenant_id = 1 AND EXISTS (SELECT e1.id FROM entity1 e1 WHERE e1.tenant_id = 1 AND e1.id = ?)");

        FIRS_RESULT_TMAP.put("SELECT EXISTS (SELECT 1 FROM entity1 e WHERE e.id = ? LIMIT 1)", "SELECT EXISTS (SELECT 1 FROM entity1 e WHERE e.tenant_id = 1 AND e.id = ? LIMIT 1)");

        /* NOT EXISTS */
        FIRS_RESULT_TMAP.put("SELECT * FROM entity e WHERE NOT EXISTS (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.tenant_id = 1 AND NOT EXISTS (SELECT e1.id FROM entity1 e1 WHERE e1.tenant_id = 1 AND e1.id = ?)");

        /* >= */
        FIRS_RESULT_TMAP.put("SELECT * FROM entity e WHERE e.id >= (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.tenant_id = 1 AND e.id >= (SELECT e1.id FROM entity1 e1 WHERE e1.tenant_id = 1 AND e1.id = ?)");

        /* <= */
        FIRS_RESULT_TMAP.put("SELECT * FROM entity e WHERE e.id <= (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.tenant_id = 1 AND e.id <= (SELECT e1.id FROM entity1 e1 WHERE e1.tenant_id = 1 AND e1.id = ?)");

        /* <> */
        FIRS_RESULT_TMAP.put("SELECT * FROM entity e WHERE e.id <> (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.tenant_id = 1 AND e.id <> (SELECT e1.id FROM entity1 e1 WHERE e1.tenant_id = 1 AND e1.id = ?)");

        FIRS_RESULT_TMAP.put("SELECT * FROM (select e.id from entity e WHERE e.id = (select e1.id from entity1 e1 where e1.id = ?))",
            "SELECT * FROM (SELECT e.id FROM entity e WHERE e.tenant_id = 1 AND e.id = (SELECT e1.id FROM entity1 e1 WHERE e1.tenant_id = 1 AND e1.id = ?))");

        FIRS_RESULT_TMAP.put("select t1.col1,(select t2.col2 from t2 t2 where t1.col1=t2.col1) from t1 t1",
            "SELECT t1.col1, (SELECT t2.col2 FROM t2 t2 WHERE t2.tenant_id = 1 AND t1.col1 = t2.col1) FROM t1 t1 WHERE t1.tenant_id = 1");

        FIRS_RESULT_TMAP.put("SELECT e1.*, IF((SELECT e2.id FROM entity2 e2 WHERE e2.id = 1) = 1, e2.type, e1.type) AS type " +
                "FROM entity e1 WHERE e1.id = ?",
            "SELECT e1.*, IF((SELECT e2.id FROM entity2 e2 WHERE e2.tenant_id = 1 AND e2.id = 1) = 1, e2.type, e1.type) AS type FROM entity e1 WHERE e1.tenant_id = 1 AND e1.id = ?");

        // left join
        FIRS_RESULT_TMAP.put("SELECT * FROM entity e " +
                "left join entity1 e1 on e1.id = e.id " +
                "WHERE e.id = ? OR e.name = ?",
            "SELECT * FROM entity e LEFT JOIN entity1 e1 ON e1.tenant_id = 1 AND e1.id = e.id WHERE e.tenant_id = 1 AND (e.id = ? OR e.name = ?)");

        FIRS_RESULT_TMAP.put("SELECT * FROM entity e " +
                "left join entity1 e1 on e1.id = e.id " +
                "WHERE (e.id = ? OR e.name = ?)",
            "SELECT * FROM entity e LEFT JOIN entity1 e1 ON e1.tenant_id = 1 AND e1.id = e.id WHERE e.tenant_id = 1 AND (e.id = ? OR e.name = ?)");

        FIRS_RESULT_TMAP.put("SELECT * FROM entity e " +
                "left join entity1 e1 on e1.id = e.id " +
                "left join entity2 e2 on e1.id = e2.id",
            "SELECT * FROM entity e LEFT JOIN entity1 e1 ON e1.tenant_id = 1 AND e1.id = e.id LEFT JOIN entity2 e2 ON e2.tenant_id = 1 AND e1.id = e2.id WHERE e.tenant_id = 1");

        // right join
        FIRS_RESULT_TMAP.put("SELECT * FROM entity e " +
                "right join entity1 e1 on e1.id = e.id",
            "SELECT * FROM entity e RIGHT JOIN entity1 e1 ON e.tenant_id = 1 AND e1.id = e.id WHERE e1.tenant_id = 1");

        FIRS_RESULT_TMAP.put("SELECT * FROM with_as_1 e " +
                "right join entity1 e1 on e1.id = e.id",
            "SELECT * FROM with_as_1 e RIGHT JOIN entity1 e1 ON e1.id = e.id WHERE e1.tenant_id = 1");

        FIRS_RESULT_TMAP.put("SELECT * FROM entity e " +
                "right join entity1 e1 on e1.id = e.id " +
                "WHERE e.id = ? OR e.name = ?",
            "SELECT * FROM entity e RIGHT JOIN entity1 e1 ON e.tenant_id = 1 AND e1.id = e.id WHERE e1.tenant_id = 1 AND (e.id = ? OR e.name = ?)");

        FIRS_RESULT_TMAP.put("SELECT * FROM entity e " +
                "right join entity1 e1 on e1.id = e.id " +
                "right join entity2 e2 on e1.id = e2.id ",
            "SELECT * FROM entity e RIGHT JOIN entity1 e1 ON e.tenant_id = 1 AND e1.id = e.id RIGHT JOIN entity2 e2 ON e1.tenant_id = 1 AND e1.id = e2.id WHERE e2.tenant_id = 1");

        FIRS_RESULT_TMAP.put("SELECT * FROM entity e " +
                "right join entity1 e1 on e1.id = e.id " +
                "left join entity2 e2 on e1.id = e2.id",
            "SELECT * FROM entity e RIGHT JOIN entity1 e1 ON e.tenant_id = 1 AND e1.id = e.id LEFT JOIN entity2 e2 ON e2.tenant_id = 1 AND e1.id = e2.id WHERE e1.tenant_id = 1");

        FIRS_RESULT_TMAP.put("SELECT * FROM entity e " +
                "left join entity1 e1 on e1.id = e.id " +
                "right join entity2 e2 on e1.id = e2.id",
            "SELECT * FROM entity e LEFT JOIN entity1 e1 ON e1.tenant_id = 1 AND e1.id = e.id RIGHT JOIN entity2 e2 ON e.tenant_id = 1 AND e1.id = e2.id WHERE e2.tenant_id = 1");

        FIRS_RESULT_TMAP.put("SELECT * FROM entity e " +
                "left join entity1 e1 on e1.id = e.id " +
                "inner join entity2 e2 on e1.id = e2.id",
            "SELECT * FROM entity e LEFT JOIN entity1 e1 ON e1.tenant_id = 1 AND e1.id = e.id INNER JOIN entity2 e2 ON e.tenant_id = 1 AND e2.tenant_id = 1 AND e1.id = e2.id");

        FIRS_RESULT_TMAP.put("select * from (select * from entity e) e1 " +
                "left join entity2 e2 on e1.id = e2.id",
            "SELECT * FROM (SELECT * FROM entity e WHERE e.tenant_id = 1) e1 LEFT JOIN entity2 e2 ON e2.tenant_id = 1 AND e1.id = e2.id");

        FIRS_RESULT_TMAP.put("select * from entity1 e1 " +
                "left join (select * from entity2 e2) e22 " +
                "on e1.id = e22.id",
            "SELECT * FROM entity1 e1 " +
                "LEFT JOIN (SELECT * FROM entity2 e2 WHERE e2.tenant_id = 1) e22 " +
                "ON e1.id = e22.id " +
                "WHERE e1.tenant_id = 1");

        FIRS_RESULT_TMAP.put("select * FROM " +
                "(entity1 e1 right JOIN entity2 e2 ON e1.id = e2.id)",
            "SELECT * FROM (entity1 e1 RIGHT JOIN entity2 e2 ON e1.tenant_id = 1 AND e1.id = e2.id) WHERE e2.tenant_id = 1");

        FIRS_RESULT_TMAP.put("select * FROM " +
                "(entity1 e1 LEFT JOIN entity2 e2 ON e1.id = e2.id)",
            "SELECT * FROM (entity1 e1 LEFT JOIN entity2 e2 ON e2.tenant_id = 1 AND e1.id = e2.id) WHERE e1.tenant_id = 1");


        FIRS_RESULT_TMAP.put("select * FROM " +
                "(entity1 e1 LEFT JOIN entity2 e2 ON e1.id = e2.id) " +
                "right join entity3 e3 on e1.id = e3.id",
            "SELECT * FROM (entity1 e1 LEFT JOIN entity2 e2 ON e2.tenant_id = 1 AND e1.id = e2.id) RIGHT JOIN entity3 e3 ON e1.tenant_id = 1 AND e1.id = e3.id WHERE e3.tenant_id = 1");


        FIRS_RESULT_TMAP.put("select * FROM entity e " +
                "LEFT JOIN (entity1 e1 right join entity2 e2 ON e1.id = e2.id) " +
                "on e.id = e2.id",
            "SELECT * FROM entity e LEFT JOIN (entity1 e1 RIGHT JOIN entity2 e2 ON e1.tenant_id = 1 AND e1.id = e2.id) ON e2.tenant_id = 1 AND e.id = e2.id WHERE e.tenant_id = 1");

        FIRS_RESULT_TMAP.put("select * FROM entity e " +
                "LEFT JOIN (entity1 e1 left join entity2 e2 ON e1.id = e2.id) " +
                "on e.id = e2.id",
            "SELECT * FROM entity e LEFT JOIN (entity1 e1 LEFT JOIN entity2 e2 ON e2.tenant_id = 1 AND e1.id = e2.id) ON e1.tenant_id = 1 AND e.id = e2.id WHERE e.tenant_id = 1");

        FIRS_RESULT_TMAP.put("select * FROM entity e " +
                "RIGHT JOIN (entity1 e1 left join entity2 e2 ON e1.id = e2.id) " +
                "on e.id = e2.id",
            "SELECT * FROM entity e RIGHT JOIN (entity1 e1 LEFT JOIN entity2 e2 ON e2.tenant_id = 1 AND e1.id = e2.id) ON e.tenant_id = 1 AND e.id = e2.id WHERE e1.tenant_id = 1");

        // 多个 on 尾缀的
        FIRS_RESULT_TMAP.put("SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 " +
                "LEFT JOIN entity2 e2 ON e2.id = e1.id " +
                "ON e1.id = e.id " +
                "WHERE (e.id = ? OR e.NAME = ?)",
            "SELECT * FROM entity e LEFT JOIN entity1 e1 LEFT JOIN entity2 e2 ON e2.tenant_id = 1 AND e2.id = e1.id ON e1.tenant_id = 1 AND e1.id = e.id WHERE e.tenant_id = 1 AND (e.id = ? OR e.NAME = ?)");

        FIRS_RESULT_TMAP.put("SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 " +
                "LEFT JOIN with_as_A e2 ON e2.id = e1.id " +
                "ON e1.id = e.id " +
                "WHERE (e.id = ? OR e.NAME = ?)",
            "SELECT * FROM entity e LEFT JOIN entity1 e1 LEFT JOIN with_as_A e2 ON e2.id = e1.id ON e1.tenant_id = 1 AND e1.id = e.id WHERE e.tenant_id = 1 AND (e.id = ? OR e.NAME = ?)");

        // inner join
        FIRS_RESULT_TMAP.put("SELECT * FROM entity e " +
                "inner join entity1 e1 on e1.id = e.id " +
                "WHERE e.id = ? OR e.name = ?",
            "SELECT * FROM entity e INNER JOIN entity1 e1 ON e.tenant_id = 1 AND e1.tenant_id = 1 AND e1.id = e.id WHERE e.id = ? OR e.name = ?");

        FIRS_RESULT_TMAP.put("SELECT * FROM entity e " +
                "inner join entity1 e1 on e1.id = e.id " +
                "WHERE (e.id = ? OR e.name = ?)",
            "SELECT * FROM entity e INNER JOIN entity1 e1 ON e.tenant_id = 1 AND e1.tenant_id = 1 AND e1.id = e.id WHERE (e.id = ? OR e.name = ?)");

        // ignore table
        FIRS_RESULT_TMAP.put("SELECT * FROM entity e " +
                "inner join with_as_1 w1 on w1.id = e.id " +
                "WHERE (e.id = ? OR e.name = ?)",
            "SELECT * FROM entity e INNER JOIN with_as_1 w1 ON e.tenant_id = 1 AND w1.id = e.id WHERE (e.id = ? OR e.name = ?)");

        // 隐式内连接
        FIRS_RESULT_TMAP.put("SELECT * FROM entity e,entity1 e1 " +
                "WHERE e.id = e1.id",
            "SELECT * FROM entity e, entity1 e1 WHERE e.tenant_id = 1 AND e1.tenant_id = 1 AND e.id = e1.id");

        // 隐式内连接
        FIRS_RESULT_TMAP.put("SELECT * FROM entity a, with_as_entity1 b " +
                "WHERE a.id = b.id",
            "SELECT * FROM entity a, with_as_entity1 b WHERE a.tenant_id = 1 AND a.id = b.id");

        FIRS_RESULT_TMAP.put("SELECT * FROM with_as_entity a, with_as_entity1 b " +
                "WHERE a.id = b.id",
            "SELECT * FROM with_as_entity a, with_as_entity1 b WHERE a.id = b.id");

        // SubJoin with 隐式内连接
        FIRS_RESULT_TMAP.put("SELECT * FROM (entity e,entity1 e1) " +
                "WHERE e.id = e1.id",
            "SELECT * FROM (entity e, entity1 e1) WHERE e.tenant_id = 1 AND e1.tenant_id = 1 AND e.id = e1.id");

        FIRS_RESULT_TMAP.put("SELECT * FROM ((entity e,entity1 e1),entity2 e2) " +
                "WHERE e.id = e1.id and e.id = e2.id",
            "SELECT * FROM ((entity e, entity1 e1), entity2 e2) WHERE e.tenant_id = 1 AND e1.tenant_id = 1 AND e2.tenant_id = 1 AND e.id = e1.id AND e.id = e2.id");

        FIRS_RESULT_TMAP.put("SELECT * FROM (entity e,(entity1 e1,entity2 e2)) " +
                "WHERE e.id = e1.id and e.id = e2.id",
            "SELECT * FROM (entity e, (entity1 e1, entity2 e2)) WHERE e.tenant_id = 1 AND e1.tenant_id = 1 AND e2.tenant_id = 1 AND e.id = e1.id AND e.id = e2.id");

        // 沙雕的括号写法
        FIRS_RESULT_TMAP.put("SELECT * FROM (((entity e,entity1 e1))) " +
                "WHERE e.id = e1.id",
            "SELECT * FROM (((entity e, entity1 e1))) WHERE e.tenant_id = 1 AND e1.tenant_id = 1 AND e.id = e1.id");

        // join
        FIRS_RESULT_TMAP.put("SELECT * FROM entity e join entity1 e1 on e1.id = e.id WHERE e.id = ? OR e.name = ?",
            "SELECT * FROM entity e JOIN entity1 e1 ON e1.tenant_id = 1 AND e1.id = e.id WHERE e.tenant_id = 1 AND (e.id = ? OR e.name = ?)");

        FIRS_RESULT_TMAP.put("SELECT * FROM entity e join entity1 e1 on e1.id = e.id WHERE (e.id = ? OR e.name = ?)",
            "SELECT * FROM entity e JOIN entity1 e1 ON e1.tenant_id = 1 AND e1.id = e.id WHERE e.tenant_id = 1 AND (e.id = ? OR e.name = ?)");

        FIRS_RESULT_TMAP.put("with with_as_A as (select * from entity) select * from with_as_A",
            "WITH with_as_A AS (SELECT * FROM entity WHERE tenant_id = 1) SELECT * FROM with_as_A");

        FIRS_RESULT_TMAP.put("INSERT INTO entity (name,age) VALUES ('秋秋',18),('秋秋','22') ON DUPLICATE KEY UPDATE age=18",
            "INSERT INTO entity (name, age, tenant_id) VALUES ('秋秋', 18, 1), ('秋秋', '22', 1) ON DUPLICATE KEY UPDATE age = 18, tenant_id = 1");
    }

    static void lastResultMap() {
        LAST_RESULT_TMAP.put("insert into entity (id) values (?)",
            "INSERT INTO entity (id, tenant_id) VALUES (?, 1)");
        LAST_RESULT_TMAP.put("insert into entity (id,name) values (?,?)",
            "INSERT INTO entity (id, name, tenant_id) VALUES (?, ?, 1)");
        // batch
        LAST_RESULT_TMAP.put("insert into entity (id) values (?),(?)",
            "INSERT INTO entity (id, tenant_id) VALUES (?, 1), (?, 1)");
        LAST_RESULT_TMAP.put("insert into entity (id,name) values (?,?),(?,?)",
            "INSERT INTO entity (id, name, tenant_id) VALUES (?, ?, 1), (?, ?, 1)");
        // 无 insert的列
        LAST_RESULT_TMAP.put("insert into entity value (?,?)",
            "INSERT INTO entity VALUES (?, ?)");
        // 自己加了insert的列
        LAST_RESULT_TMAP.put("insert into entity (id,name,tenant_id) value (?,?,?)",
            "INSERT INTO entity (id, name, tenant_id) VALUES (?, ?, ?)");
        // insert into select
        LAST_RESULT_TMAP.put("insert into entity (id,name) select id,name from entity2",
            "INSERT INTO entity (id, name, tenant_id) SELECT id, name, tenant_id FROM entity2 WHERE tenant_id = 1");
        LAST_RESULT_TMAP.put("insert into entity (id,name) select * from entity2 e2",
            "INSERT INTO entity (id, name, tenant_id) SELECT * FROM entity2 e2 WHERE e2.tenant_id = 1");
        LAST_RESULT_TMAP.put("insert into entity (id,name) select id,name from (select id,name from entity3 e3) t",
            "INSERT INTO entity (id, name, tenant_id) SELECT id, name, tenant_id FROM (SELECT id, name, tenant_id FROM entity3 e3 WHERE e3.tenant_id = 1) t");
        LAST_RESULT_TMAP.put("insert into entity (id,name) select * from (select id,name from entity3 e3) t",
            "INSERT INTO entity (id, name, tenant_id) SELECT * FROM (SELECT id, name, tenant_id FROM entity3 e3 WHERE e3.tenant_id = 1) t");
        LAST_RESULT_TMAP.put("insert into entity (id,name) select t.* from (select id,name from entity3 e3) t",
            "INSERT INTO entity (id, name, tenant_id) SELECT t.* FROM (SELECT id, name, tenant_id FROM entity3 e3 WHERE e3.tenant_id = 1) t");

        LAST_RESULT_TMAP.put("delete from entity where id = ?", "DELETE FROM entity WHERE id = ? AND tenant_id = 1");

        LAST_RESULT_TMAP.put("update entity set name = ? where id = ?",
            "UPDATE entity SET name = ? WHERE id = ? AND tenant_id = 1");

        // set subSelect
        LAST_RESULT_TMAP.put("UPDATE entity e SET e.cq = (SELECT e1.total FROM entity e1 WHERE e1.id = ?) WHERE e.id = ?",
            "UPDATE entity e SET e.cq = (SELECT e1.total FROM entity e1 WHERE e1.id = ? AND e1.tenant_id = 1) " +
                "WHERE e.id = ? AND e.tenant_id = 1");

        LAST_RESULT_TMAP.put("UPDATE sys_user SET (name, age) = ('秋秋', 18), address = 'test'",
            "UPDATE sys_user SET (name, age) = ('秋秋', 18), address = 'test' WHERE tenant_id = 1");

        LAST_RESULT_TMAP.put("UPDATE entity t1 INNER JOIN entity t2 ON t1.a= t2.a SET t1.b = t2.b, t1.c = t2.c",
            "UPDATE entity t1 INNER JOIN entity t2 ON t1.a = t2.a SET t1.b = t2.b, t1.c = t2.c WHERE t1.tenant_id = 1");

        // 单表
        LAST_RESULT_TMAP.put("select * from entity where id = ?", "SELECT * FROM entity WHERE id = ? AND tenant_id = 1");

        LAST_RESULT_TMAP.put("select * from entity where id = ? or name = ?", "SELECT * FROM entity WHERE (id = ? OR name = ?) AND tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM entity WHERE (id = ? OR name = ?)", "SELECT * FROM entity WHERE (id = ? OR name = ?) AND tenant_id = 1");

        /* not */
        LAST_RESULT_TMAP.put("SELECT * FROM entity WHERE not (id = ? OR name = ?)", "SELECT * FROM entity WHERE NOT (id = ? OR name = ?) AND tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM entity u WHERE not (u.id = ? OR u.name = ?)", "SELECT * FROM entity u WHERE NOT (u.id = ? OR u.name = ?) AND u.tenant_id = 1");

        /* in */
        LAST_RESULT_TMAP.put("SELECT * FROM entity e WHERE e.id IN (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.id IN (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.tenant_id = 1");
        // 在最前
        LAST_RESULT_TMAP.put("SELECT * FROM entity e WHERE e.id IN " +
                "(select e1.id from entity1 e1 where e1.id = ?) and e.id = ?",
            "SELECT * FROM entity e WHERE e.id IN " +
                "(SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.id = ? AND e.tenant_id = 1");
        // 在最后
        LAST_RESULT_TMAP.put("SELECT * FROM entity e WHERE e.id = ? and e.id IN " +
                "(select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.id = ? AND e.id IN " +
                "(SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.tenant_id = 1");
        // 在中间
        LAST_RESULT_TMAP.put("SELECT * FROM entity e WHERE e.id = ? and e.id IN " +
                "(select e1.id from entity1 e1 where e1.id = ?) and e.id = ?",
            "SELECT * FROM entity e WHERE e.id = ? AND e.id IN " +
                "(SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.id = ? AND e.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM entity e WHERE e.id = (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.id = (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.tenant_id = 1");

        /* inner not = */
        LAST_RESULT_TMAP.put("SELECT * FROM entity e WHERE not (e.id = (select e1.id from entity1 e1 where e1.id = ?))",
            "SELECT * FROM entity e WHERE NOT (e.id = (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1)) AND e.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM entity e WHERE not (e.id = (select e1.id from entity1 e1 where e1.id = ?) and e.id = ?)",
            "SELECT * FROM entity e WHERE NOT (e.id = (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.id = ?) AND e.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM entity e WHERE EXISTS (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE EXISTS (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT EXISTS (SELECT 1 FROM entity1 e WHERE e.id = ? LIMIT 1)", "SELECT EXISTS (SELECT 1 FROM entity1 e WHERE e.id = ? AND e.tenant_id = 1 LIMIT 1)");

        /* NOT EXISTS */
        LAST_RESULT_TMAP.put("SELECT * FROM entity e WHERE NOT EXISTS (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE NOT EXISTS (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.tenant_id = 1");

        /* >= */
        LAST_RESULT_TMAP.put("SELECT * FROM entity e WHERE e.id >= (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.id >= (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.tenant_id = 1");


        /* <= */
        LAST_RESULT_TMAP.put("SELECT * FROM entity e WHERE e.id <= (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.id <= (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.tenant_id = 1");

        /* <> */
        LAST_RESULT_TMAP.put("SELECT * FROM entity e WHERE e.id <> (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.id <> (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM (select e.id from entity e WHERE e.id = (select e1.id from entity1 e1 where e1.id = ?))",
            "SELECT * FROM (SELECT e.id FROM entity e WHERE e.id = (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.tenant_id = 1)");

        LAST_RESULT_TMAP.put("select t1.col1,(select t2.col2 from t2 t2 where t1.col1=t2.col1) from t1 t1",
            "SELECT t1.col1, (SELECT t2.col2 FROM t2 t2 WHERE t1.col1 = t2.col1 AND t2.tenant_id = 1) FROM t1 t1 WHERE t1.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT e1.*, IF((SELECT e2.id FROM entity2 e2 WHERE e2.id = 1) = 1, e2.type, e1.type) AS type " +
                "FROM entity e1 WHERE e1.id = ?",
            "SELECT e1.*, IF((SELECT e2.id FROM entity2 e2 WHERE e2.id = 1 AND e2.tenant_id = 1) = 1, e2.type, e1.type) AS type " +
                "FROM entity e1 WHERE e1.id = ? AND e1.tenant_id = 1");

        // left join
        LAST_RESULT_TMAP.put("SELECT * FROM entity e " +
                "left join entity1 e1 on e1.id = e.id " +
                "WHERE e.id = ? OR e.name = ?",
            "SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 ON e1.id = e.id AND e1.tenant_id = 1 " +
                "WHERE (e.id = ? OR e.name = ?) AND e.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM entity e " +
                "left join entity1 e1 on e1.id = e.id " +
                "WHERE (e.id = ? OR e.name = ?)",
            "SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 ON e1.id = e.id AND e1.tenant_id = 1 " +
                "WHERE (e.id = ? OR e.name = ?) AND e.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM entity e " +
                "left join entity1 e1 on e1.id = e.id " +
                "left join entity2 e2 on e1.id = e2.id",
            "SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 ON e1.id = e.id AND e1.tenant_id = 1 " +
                "LEFT JOIN entity2 e2 ON e1.id = e2.id AND e2.tenant_id = 1 " +
                "WHERE e.tenant_id = 1");

        // right join
        LAST_RESULT_TMAP.put("SELECT * FROM entity e " +
                "right join entity1 e1 on e1.id = e.id",
            "SELECT * FROM entity e " +
                "RIGHT JOIN entity1 e1 ON e1.id = e.id AND e.tenant_id = 1 " +
                "WHERE e1.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM with_as_1 e " +
                "right join entity1 e1 on e1.id = e.id",
            "SELECT * FROM with_as_1 e " +
                "RIGHT JOIN entity1 e1 ON e1.id = e.id " +
                "WHERE e1.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM entity e " +
                "right join entity1 e1 on e1.id = e.id " +
                "WHERE e.id = ? OR e.name = ?",
            "SELECT * FROM entity e " +
                "RIGHT JOIN entity1 e1 ON e1.id = e.id AND e.tenant_id = 1 " +
                "WHERE (e.id = ? OR e.name = ?) AND e1.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM entity e " +
                "right join entity1 e1 on e1.id = e.id " +
                "right join entity2 e2 on e1.id = e2.id ",
            "SELECT * FROM entity e " +
                "RIGHT JOIN entity1 e1 ON e1.id = e.id AND e.tenant_id = 1 " +
                "RIGHT JOIN entity2 e2 ON e1.id = e2.id AND e1.tenant_id = 1 " +
                "WHERE e2.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM entity e " +
                "right join entity1 e1 on e1.id = e.id " +
                "left join entity2 e2 on e1.id = e2.id",
            "SELECT * FROM entity e " +
                "RIGHT JOIN entity1 e1 ON e1.id = e.id AND e.tenant_id = 1 " +
                "LEFT JOIN entity2 e2 ON e1.id = e2.id AND e2.tenant_id = 1 " +
                "WHERE e1.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM entity e " +
                "left join entity1 e1 on e1.id = e.id " +
                "right join entity2 e2 on e1.id = e2.id",
            "SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 ON e1.id = e.id AND e1.tenant_id = 1 " +
                "RIGHT JOIN entity2 e2 ON e1.id = e2.id AND e.tenant_id = 1 " +
                "WHERE e2.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM entity e " +
                "left join entity1 e1 on e1.id = e.id " +
                "inner join entity2 e2 on e1.id = e2.id",
            "SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 ON e1.id = e.id AND e1.tenant_id = 1 " +
                "INNER JOIN entity2 e2 ON e1.id = e2.id AND e.tenant_id = 1 AND e2.tenant_id = 1");

        LAST_RESULT_TMAP.put("select * from (select * from entity e) e1 " +
                "left join entity2 e2 on e1.id = e2.id",
            "SELECT * FROM (SELECT * FROM entity e WHERE e.tenant_id = 1) e1 " +
                "LEFT JOIN entity2 e2 ON e1.id = e2.id AND e2.tenant_id = 1");

        LAST_RESULT_TMAP.put("select * from entity1 e1 " +
                "left join (select * from entity2 e2) e22 " +
                "on e1.id = e22.id",
            "SELECT * FROM entity1 e1 " +
                "LEFT JOIN (SELECT * FROM entity2 e2 WHERE e2.tenant_id = 1) e22 " +
                "ON e1.id = e22.id " +
                "WHERE e1.tenant_id = 1");

        LAST_RESULT_TMAP.put("select * FROM " +
                "(entity1 e1 right JOIN entity2 e2 ON e1.id = e2.id)",
            "SELECT * FROM " +
                "(entity1 e1 RIGHT JOIN entity2 e2 ON e1.id = e2.id AND e1.tenant_id = 1) " +
                "WHERE e2.tenant_id = 1");

        LAST_RESULT_TMAP.put("select * FROM " +
                "(entity1 e1 LEFT JOIN entity2 e2 ON e1.id = e2.id)",
            "SELECT * FROM " +
                "(entity1 e1 LEFT JOIN entity2 e2 ON e1.id = e2.id AND e2.tenant_id = 1) " +
                "WHERE e1.tenant_id = 1");


        LAST_RESULT_TMAP.put("select * FROM " +
                "(entity1 e1 LEFT JOIN entity2 e2 ON e1.id = e2.id) " +
                "right join entity3 e3 on e1.id = e3.id",
            "SELECT * FROM " +
                "(entity1 e1 LEFT JOIN entity2 e2 ON e1.id = e2.id AND e2.tenant_id = 1) " +
                "RIGHT JOIN entity3 e3 ON e1.id = e3.id AND e1.tenant_id = 1 " +
                "WHERE e3.tenant_id = 1");


        LAST_RESULT_TMAP.put("select * FROM entity e " +
                "LEFT JOIN (entity1 e1 right join entity2 e2 ON e1.id = e2.id) " +
                "on e.id = e2.id",
            "SELECT * FROM entity e " +
                "LEFT JOIN (entity1 e1 RIGHT JOIN entity2 e2 ON e1.id = e2.id AND e1.tenant_id = 1) " +
                "ON e.id = e2.id AND e2.tenant_id = 1 " +
                "WHERE e.tenant_id = 1");

        LAST_RESULT_TMAP.put("select * FROM entity e " +
                "LEFT JOIN (entity1 e1 left join entity2 e2 ON e1.id = e2.id) " +
                "on e.id = e2.id",
            "SELECT * FROM entity e " +
                "LEFT JOIN (entity1 e1 LEFT JOIN entity2 e2 ON e1.id = e2.id AND e2.tenant_id = 1) " +
                "ON e.id = e2.id AND e1.tenant_id = 1 " +
                "WHERE e.tenant_id = 1");

        LAST_RESULT_TMAP.put("select * FROM entity e " +
                "RIGHT JOIN (entity1 e1 left join entity2 e2 ON e1.id = e2.id) " +
                "on e.id = e2.id",
            "SELECT * FROM entity e " +
                "RIGHT JOIN (entity1 e1 LEFT JOIN entity2 e2 ON e1.id = e2.id AND e2.tenant_id = 1) " +
                "ON e.id = e2.id AND e.tenant_id = 1 " +
                "WHERE e1.tenant_id = 1");

        // 多个 on 尾缀的
        LAST_RESULT_TMAP.put("SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 " +
                "LEFT JOIN entity2 e2 ON e2.id = e1.id " +
                "ON e1.id = e.id " +
                "WHERE (e.id = ? OR e.NAME = ?)",
            "SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 " +
                "LEFT JOIN entity2 e2 ON e2.id = e1.id AND e2.tenant_id = 1 " +
                "ON e1.id = e.id AND e1.tenant_id = 1 " +
                "WHERE (e.id = ? OR e.NAME = ?) AND e.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 " +
                "LEFT JOIN with_as_A e2 ON e2.id = e1.id " +
                "ON e1.id = e.id " +
                "WHERE (e.id = ? OR e.NAME = ?)",
            "SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 " +
                "LEFT JOIN with_as_A e2 ON e2.id = e1.id " +
                "ON e1.id = e.id AND e1.tenant_id = 1 " +
                "WHERE (e.id = ? OR e.NAME = ?) AND e.tenant_id = 1");

        // inner join
        LAST_RESULT_TMAP.put("SELECT * FROM entity e " +
                "inner join entity1 e1 on e1.id = e.id " +
                "WHERE e.id = ? OR e.name = ?",
            "SELECT * FROM entity e " +
                "INNER JOIN entity1 e1 ON e1.id = e.id AND e.tenant_id = 1 AND e1.tenant_id = 1 " +
                "WHERE e.id = ? OR e.name = ?");

        LAST_RESULT_TMAP.put("SELECT * FROM entity e " +
                "inner join entity1 e1 on e1.id = e.id " +
                "WHERE (e.id = ? OR e.name = ?)",
            "SELECT * FROM entity e " +
                "INNER JOIN entity1 e1 ON e1.id = e.id AND e.tenant_id = 1 AND e1.tenant_id = 1 " +
                "WHERE (e.id = ? OR e.name = ?)");

        // ignore table
        LAST_RESULT_TMAP.put("SELECT * FROM entity e " +
                "inner join with_as_1 w1 on w1.id = e.id " +
                "WHERE (e.id = ? OR e.name = ?)",
            "SELECT * FROM entity e " +
                "INNER JOIN with_as_1 w1 ON w1.id = e.id AND e.tenant_id = 1 " +
                "WHERE (e.id = ? OR e.name = ?)");

        // 隐式内连接
        LAST_RESULT_TMAP.put("SELECT * FROM entity e,entity1 e1 " +
                "WHERE e.id = e1.id",
            "SELECT * FROM entity e, entity1 e1 " +
                "WHERE e.id = e1.id AND e.tenant_id = 1 AND e1.tenant_id = 1");

        // 隐式内连接
        LAST_RESULT_TMAP.put("SELECT * FROM entity a, with_as_entity1 b " +
                "WHERE a.id = b.id",
            "SELECT * FROM entity a, with_as_entity1 b " +
                "WHERE a.id = b.id AND a.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM with_as_entity a, with_as_entity1 b " +
                "WHERE a.id = b.id",
            "SELECT * FROM with_as_entity a, with_as_entity1 b " +
                "WHERE a.id = b.id");

        // SubJoin with 隐式内连接
        LAST_RESULT_TMAP.put("SELECT * FROM (entity e,entity1 e1) " +
                "WHERE e.id = e1.id",
            "SELECT * FROM (entity e, entity1 e1) " +
                "WHERE e.id = e1.id " +
                "AND e.tenant_id = 1 AND e1.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM ((entity e,entity1 e1),entity2 e2) " +
                "WHERE e.id = e1.id and e.id = e2.id",
            "SELECT * FROM ((entity e, entity1 e1), entity2 e2) " +
                "WHERE e.id = e1.id AND e.id = e2.id " +
                "AND e.tenant_id = 1 AND e1.tenant_id = 1 AND e2.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM (entity e,(entity1 e1,entity2 e2)) " +
                "WHERE e.id = e1.id and e.id = e2.id",
            "SELECT * FROM (entity e, (entity1 e1, entity2 e2)) " +
                "WHERE e.id = e1.id AND e.id = e2.id " +
                "AND e.tenant_id = 1 AND e1.tenant_id = 1 AND e2.tenant_id = 1");

        // 沙雕的括号写法
        LAST_RESULT_TMAP.put("SELECT * FROM (((entity e,entity1 e1))) " +
                "WHERE e.id = e1.id",
            "SELECT * FROM (((entity e, entity1 e1))) " +
                "WHERE e.id = e1.id " +
                "AND e.tenant_id = 1 AND e1.tenant_id = 1");

        // join
        LAST_RESULT_TMAP.put("SELECT * FROM entity e join entity1 e1 on e1.id = e.id WHERE e.id = ? OR e.name = ?",
            "SELECT * FROM entity e JOIN entity1 e1 ON e1.id = e.id AND e1.tenant_id = 1 WHERE (e.id = ? OR e.name = ?) AND e.tenant_id = 1");

        LAST_RESULT_TMAP.put("SELECT * FROM entity e join entity1 e1 on e1.id = e.id WHERE (e.id = ? OR e.name = ?)",
            "SELECT * FROM entity e JOIN entity1 e1 ON e1.id = e.id AND e1.tenant_id = 1 WHERE (e.id = ? OR e.name = ?) AND e.tenant_id = 1");

        LAST_RESULT_TMAP.put("with with_as_A as (select * from entity) select * from with_as_A",
            "WITH with_as_A AS (SELECT * FROM entity WHERE tenant_id = 1) SELECT * FROM with_as_A");

        LAST_RESULT_TMAP.put("INSERT INTO entity (name,age) VALUES ('秋秋',18),('秋秋','22') ON DUPLICATE KEY UPDATE age=18",
            "INSERT INTO entity (name, age, tenant_id) VALUES ('秋秋', 18, 1), ('秋秋', '22', 1) ON DUPLICATE KEY UPDATE age = 18, tenant_id = 1");
    }


    private final TenantLineInnerInterceptor interceptor = new TenantLineInnerInterceptor(new TenantLineHandler() {
        private boolean ignoreFirst;// 需要执行 getTenantId 前必须先执行 ignoreTable

        @Override
        public Expression getTenantId() {
            assertThat(ignoreFirst).isEqualTo(true);
            ignoreFirst = false;
            return new LongValue(1);
        }

        @Override
        public boolean ignoreTable(String tableName) {
            ignoreFirst = true;
            return tableName.startsWith("with_as");
        }
    });

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    public void test(ExpressionAppendMode appendMode) {
        Map<String, String> assertMap = ExpressionAppendMode.LAST == appendMode ? LAST_RESULT_TMAP : FIRS_RESULT_TMAP;
        assertMap.forEach((k, v) -> assertSql(k, appendMode));
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void insert(ExpressionAppendMode appendMode) {
        // plain
        assertSql("insert into entity (id) values (?)", appendMode);
        assertSql("insert into entity (id,name) values (?,?)", appendMode);
        // batch
        assertSql("insert into entity (id) values (?),(?)", appendMode);
        assertSql("insert into entity (id,name) values (?,?),(?,?)", appendMode);
        // 无 insert的列
        assertSql("insert into entity value (?,?)", appendMode);
        // 自己加了insert的列
        assertSql("insert into entity (id,name,tenant_id) value (?,?,?)", appendMode);
        // insert into select
        assertSql("insert into entity (id,name) select id,name from entity2", appendMode);
        assertSql("insert into entity (id,name) select * from entity2 e2", appendMode);
        assertSql("insert into entity (id,name) select id,name from (select id,name from entity3 e3) t", appendMode);
        assertSql("insert into entity (id,name) select * from (select id,name from entity3 e3) t", appendMode);
        assertSql("insert into entity (id,name) select t.* from (select id,name from entity3 e3) t", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void delete(ExpressionAppendMode appendMode) {
        assertSql("delete from entity where id = ?", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void update(ExpressionAppendMode appendMode) {
        assertSql("update entity set name = ? where id = ?", appendMode);
        // set subSelect
        assertSql("UPDATE entity e SET e.cq = (SELECT e1.total FROM entity e1 WHERE e1.id = ?) WHERE e.id = ?", appendMode);

        assertSql("UPDATE sys_user SET (name, age) = ('秋秋', 18), address = 'test'", appendMode);

        assertSql("UPDATE entity t1 INNER JOIN entity t2 ON t1.a= t2.a SET t1.b = t2.b, t1.c = t2.c", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectSingle(ExpressionAppendMode appendMode) {
        // 单表
        assertSql("select * from entity where id = ?", appendMode);

        assertSql("select * from entity where id = ? or name = ?", appendMode);

        assertSql("SELECT * FROM entity WHERE (id = ? OR name = ?)", appendMode);

        /* not */
        assertSql("SELECT * FROM entity WHERE not (id = ? OR name = ?)", appendMode);

        assertSql("SELECT * FROM entity u WHERE not (u.id = ? OR u.name = ?)", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectSubSelectIn(ExpressionAppendMode appendMode) {
        /* in */
        assertSql("SELECT * FROM entity e WHERE e.id IN (select e1.id from entity1 e1 where e1.id = ?)", appendMode);
        // 在最前
        assertSql("SELECT * FROM entity e WHERE e.id IN " +
            "(select e1.id from entity1 e1 where e1.id = ?) and e.id = ?", appendMode);
        // 在最后
        assertSql("SELECT * FROM entity e WHERE e.id = ? and e.id IN " +
            "(select e1.id from entity1 e1 where e1.id = ?)", appendMode);
        // 在中间
        assertSql("SELECT * FROM entity e WHERE e.id = ? and e.id IN " +
            "(select e1.id from entity1 e1 where e1.id = ?) and e.id = ?", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectSubSelectEq(ExpressionAppendMode appendMode) {
        /* = */
        assertSql("SELECT * FROM entity e WHERE e.id = (select e1.id from entity1 e1 where e1.id = ?)", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectSubSelectInnerNotEq(ExpressionAppendMode appendMode) {
        /* inner not = */
        assertSql("SELECT * FROM entity e WHERE not (e.id = (select e1.id from entity1 e1 where e1.id = ?))", appendMode);

        assertSql("SELECT * FROM entity e WHERE not (e.id = (select e1.id from entity1 e1 where e1.id = ?) and e.id = ?)", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectSubSelectExists(ExpressionAppendMode appendMode) {
        /* EXISTS */
        assertSql("SELECT * FROM entity e WHERE EXISTS (select e1.id from entity1 e1 where e1.id = ?)", appendMode);

        assertSql("SELECT EXISTS (SELECT 1 FROM entity1 e WHERE e.id = ? LIMIT 1)", appendMode);

        /* NOT EXISTS */
        assertSql("SELECT * FROM entity e WHERE NOT EXISTS (select e1.id from entity1 e1 where e1.id = ?)", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectWhereSubSelect(ExpressionAppendMode appendMode) {
        /* >= */
        assertSql("SELECT * FROM entity e WHERE e.id >= (select e1.id from entity1 e1 where e1.id = ?)", appendMode);
        /* <= */
        assertSql("SELECT * FROM entity e WHERE e.id <= (select e1.id from entity1 e1 where e1.id = ?)", appendMode);
        /* <> */
        assertSql("SELECT * FROM entity e WHERE e.id <> (select e1.id from entity1 e1 where e1.id = ?)", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectFromSelect(ExpressionAppendMode appendMode) {
        assertSql("SELECT * FROM (select e.id from entity e WHERE e.id = (select e1.id from entity1 e1 where e1.id = ?))", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectBodySubSelect(ExpressionAppendMode appendMode) {
        assertSql("select t1.col1,(select t2.col2 from t2 t2 where t1.col1=t2.col1) from t1 t1", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectBodyFuncSubSelect(ExpressionAppendMode appendMode) {
        assertSql("SELECT e1.*, IF((SELECT e2.id FROM entity2 e2 WHERE e2.id = 1) = 1, e2.type, e1.type) AS type " +
            "FROM entity e1 WHERE e1.id = ?", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectLeftJoin(ExpressionAppendMode appendMode) {
        // left join
        assertSql("SELECT * FROM entity e " +
            "left join entity1 e1 on e1.id = e.id " +
            "WHERE e.id = ? OR e.name = ?", appendMode);

        assertSql("SELECT * FROM entity e " +
            "left join entity1 e1 on e1.id = e.id " +
            "WHERE (e.id = ? OR e.name = ?)", appendMode);

        assertSql("SELECT * FROM entity e " +
            "left join entity1 e1 on e1.id = e.id " +
            "left join entity2 e2 on e1.id = e2.id", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectRightJoin(ExpressionAppendMode appendMode) {
        // right join
        assertSql("SELECT * FROM entity e " +
                "right join entity1 e1 on e1.id = e.id",
            appendMode);

        assertSql("SELECT * FROM with_as_1 e " +
                "right join entity1 e1 on e1.id = e.id",
            appendMode);

        assertSql("SELECT * FROM entity e " +
                "right join entity1 e1 on e1.id = e.id " +
                "WHERE e.id = ? OR e.name = ?",
            appendMode);

        assertSql("SELECT * FROM entity e " +
                "right join entity1 e1 on e1.id = e.id " +
                "right join entity2 e2 on e1.id = e2.id ",
            appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectMixJoin(ExpressionAppendMode appendMode) {
        assertSql("SELECT * FROM entity e " +
                "right join entity1 e1 on e1.id = e.id " +
                "left join entity2 e2 on e1.id = e2.id",
            appendMode);

        assertSql("SELECT * FROM entity e " +
                "left join entity1 e1 on e1.id = e.id " +
                "right join entity2 e2 on e1.id = e2.id",
            appendMode);

        assertSql("SELECT * FROM entity e " +
                "left join entity1 e1 on e1.id = e.id " +
                "inner join entity2 e2 on e1.id = e2.id",
            appendMode);
    }


    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectJoinSubSelect(ExpressionAppendMode appendMode) {
        assertSql("select * from (select * from entity e) e1 " +
            "left join entity2 e2 on e1.id = e2.id", appendMode);

        assertSql("select * from entity1 e1 " +
            "left join (select * from entity2 e2) e22 " +
            "on e1.id = e22.id", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectSubJoin(ExpressionAppendMode appendMode) {
        assertSql("select * FROM " +
            "(entity1 e1 right JOIN entity2 e2 ON e1.id = e2.id)", appendMode);

        assertSql("select * FROM " +
            "(entity1 e1 LEFT JOIN entity2 e2 ON e1.id = e2.id)", appendMode);


        assertSql("select * FROM " +
            "(entity1 e1 LEFT JOIN entity2 e2 ON e1.id = e2.id) " +
            "right join entity3 e3 on e1.id = e3.id", appendMode);


        assertSql("select * FROM entity e " +
            "LEFT JOIN (entity1 e1 right join entity2 e2 ON e1.id = e2.id) " +
            "on e.id = e2.id", appendMode);

        assertSql("select * FROM entity e " +
            "LEFT JOIN (entity1 e1 left join entity2 e2 ON e1.id = e2.id) " +
            "on e.id = e2.id", appendMode);

        assertSql("select * FROM entity e " +
            "RIGHT JOIN (entity1 e1 left join entity2 e2 ON e1.id = e2.id) " +
            "on e.id = e2.id", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectLeftJoinMultipleTrailingOn(ExpressionAppendMode appendMode) {
        // 多个 on 尾缀的
        assertSql("SELECT * FROM entity e " +
            "LEFT JOIN entity1 e1 " +
            "LEFT JOIN entity2 e2 ON e2.id = e1.id " +
            "ON e1.id = e.id " +
            "WHERE (e.id = ? OR e.NAME = ?)", appendMode);

        assertSql("SELECT * FROM entity e " +
            "LEFT JOIN entity1 e1 " +
            "LEFT JOIN with_as_A e2 ON e2.id = e1.id " +
            "ON e1.id = e.id " +
            "WHERE (e.id = ? OR e.NAME = ?)", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectInnerJoin(ExpressionAppendMode appendMode) {
        // inner join
        assertSql("SELECT * FROM entity e " +
            "inner join entity1 e1 on e1.id = e.id " +
            "WHERE e.id = ? OR e.name = ?", appendMode);

        assertSql("SELECT * FROM entity e " +
            "inner join entity1 e1 on e1.id = e.id " +
            "WHERE (e.id = ? OR e.name = ?)", appendMode);

        // ignore table
        assertSql("SELECT * FROM entity e " +
            "inner join with_as_1 w1 on w1.id = e.id " +
            "WHERE (e.id = ? OR e.name = ?)", appendMode);

        // 隐式内连接
        assertSql("SELECT * FROM entity e,entity1 e1 " +
            "WHERE e.id = e1.id", appendMode);

        // 隐式内连接
        assertSql("SELECT * FROM entity a, with_as_entity1 b " +
            "WHERE a.id = b.id", appendMode);

        assertSql("SELECT * FROM with_as_entity a, with_as_entity1 b " +
            "WHERE a.id = b.id", appendMode);

        // SubJoin with 隐式内连接
        assertSql("SELECT * FROM (entity e,entity1 e1) " +
            "WHERE e.id = e1.id", appendMode);

        assertSql("SELECT * FROM ((entity e,entity1 e1),entity2 e2) " +
            "WHERE e.id = e1.id and e.id = e2.id", appendMode);

        assertSql("SELECT * FROM (entity e,(entity1 e1,entity2 e2)) " +
            "WHERE e.id = e1.id and e.id = e2.id", appendMode);

        // 沙雕的括号写法
        assertSql("SELECT * FROM (((entity e,entity1 e1))) WHERE e.id = e1.id", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectSingleJoin(ExpressionAppendMode appendMode) {
        // join
        assertSql("SELECT * FROM entity e join entity1 e1 on e1.id = e.id WHERE e.id = ? OR e.name = ?", appendMode);

        assertSql("SELECT * FROM entity e join entity1 e1 on e1.id = e.id WHERE (e.id = ? OR e.name = ?)", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void selectWithAs(ExpressionAppendMode appendMode) {
        assertSql("with with_as_A as (select * from entity) select * from with_as_A", appendMode);
    }

    @ParameterizedTest
    @EnumSource(ExpressionAppendMode.class)
    void testDuplicateKeyUpdate(ExpressionAppendMode appendMode) {
        assertSql("INSERT INTO entity (name,age) VALUES ('秋秋',18),('秋秋','22') ON DUPLICATE KEY UPDATE age=18", appendMode);
    }

    void assertSql(String sql, ExpressionAppendMode appendMode) {
        interceptor.setExpressionAppendMode(appendMode);
        Map<String, String> assertMap = ExpressionAppendMode.LAST == interceptor.getExpressionAppendMode() ? LAST_RESULT_TMAP : FIRS_RESULT_TMAP;
        assertThat(interceptor.parserSingle(sql, null)).isEqualTo(assertMap.get(sql));
    }

}
