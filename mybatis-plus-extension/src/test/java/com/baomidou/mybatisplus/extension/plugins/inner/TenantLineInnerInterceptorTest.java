package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-07-30
 */
class TenantLineInnerInterceptorTest {

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
            if (Objects.equals(tableName, "sys_dict")) {
                return true;
            }
            return tableName.startsWith("with_as");
        }
    });

    @Test
    void insert() {
        // plain
        assertSql("insert into entity (id,name) values (?,?)",
            "INSERT INTO entity (id, name, tenant_id) VALUES (?, ?, 1)");
        // batch
        assertSql("insert into entity (id,name) values (?,?),(?,?)",
            "INSERT INTO entity (id, name, tenant_id) VALUES (?, ?, 1), (?, ?, 1)");
        // 无 insert的列
        assertSql("insert into entity value (?,?)",
            "INSERT INTO entity VALUES (?, ?)");
        // 自己加了insert的列
        assertSql("insert into entity (id,name,tenant_id) value (?,?,?)",
            "INSERT INTO entity (id, name, tenant_id) VALUES (?, ?, ?)");
        // insert into select
        assertSql("insert into entity (id,name) select id,name from entity2",
            "INSERT INTO entity (id, name, tenant_id) SELECT id, name, tenant_id FROM entity2 WHERE tenant_id = 1");

        assertSql("insert into entity (id,name) select * from entity2 e2",
            "INSERT INTO entity (id, name, tenant_id) SELECT * FROM entity2 e2 WHERE e2.tenant_id = 1");

        assertSql("insert into entity (id,name) select id,name from (select id,name from entity3 e3) t",
            "INSERT INTO entity (id, name, tenant_id) SELECT id, name, tenant_id FROM (SELECT id, name, tenant_id FROM entity3 e3 WHERE e3.tenant_id = 1) t");

        assertSql("insert into entity (id,name) select * from (select id,name from entity3 e3) t",
            "INSERT INTO entity (id, name, tenant_id) SELECT * FROM (SELECT id, name, tenant_id FROM entity3 e3 WHERE e3.tenant_id = 1) t");

        assertSql("insert into entity (id,name) select t.* from (select id,name from entity3 e3) t",
            "INSERT INTO entity (id, name, tenant_id) SELECT t.* FROM (SELECT id, name, tenant_id FROM entity3 e3 WHERE e3.tenant_id = 1) t");
    }

    @Test
    void delete() {
        assertSql("delete from entity where id = ?",
            "DELETE FROM entity WHERE tenant_id = 1 AND id = ?");
    }

    @Test
    void update() {
        assertSql("update entity set name = ? where id = ?",
            "UPDATE entity SET name = ? WHERE tenant_id = 1 AND id = ?");
    }

    @Test
    void selectSingle() {
        // 单表
        assertSql("select * from entity where id = ?",
            "SELECT * FROM entity WHERE id = ? AND tenant_id = 1");

        assertSql("select * from entity where id = ? or name = ?",
            "SELECT * FROM entity WHERE (id = ? OR name = ?) AND tenant_id = 1");

        assertSql("SELECT * FROM entity WHERE (id = ? OR name = ?)",
            "SELECT * FROM entity WHERE (id = ? OR name = ?) AND tenant_id = 1");

        /* not */
        assertSql("SELECT * FROM entity WHERE not (id = ? OR name = ?)",
            "SELECT * FROM entity WHERE NOT (id = ? OR name = ?) AND tenant_id = 1");
    }

    @Test
    void selectSubSelectIn() {
        /* in */
        assertSql("SELECT * FROM entity e WHERE e.id IN (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.id IN (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.tenant_id = 1");
        // 在最前
        assertSql("SELECT * FROM entity e WHERE e.id IN " +
                "(select e1.id from entity1 e1 where e1.id = ?) and e.id = ?",
            "SELECT * FROM entity e WHERE e.id IN " +
                "(SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.id = ? AND e.tenant_id = 1");
        // 在最后
        assertSql("SELECT * FROM entity e WHERE e.id = ? and e.id IN " +
                "(select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.id = ? AND e.id IN " +
                "(SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.tenant_id = 1");
        // 在中间
        assertSql("SELECT * FROM entity e WHERE e.id = ? and e.id IN " +
                "(select e1.id from entity1 e1 where e1.id = ?) and e.id = ?",
            "SELECT * FROM entity e WHERE e.id = ? AND e.id IN " +
                "(SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.id = ? AND e.tenant_id = 1");
    }

    @Test
    void selectSubSelectEq() {
        /* = */
        assertSql("SELECT * FROM entity e WHERE e.id = (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.id = (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.tenant_id = 1");
    }

    @Test
    void selectSubSelectInnerNotEq() {
        /* inner not = */
        assertSql("SELECT * FROM entity e WHERE not (e.id = (select e1.id from entity1 e1 where e1.id = ?))",
            "SELECT * FROM entity e WHERE NOT (e.id = (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1)) AND e.tenant_id = 1");

        assertSql("SELECT * FROM entity e WHERE not (e.id = (select e1.id from entity1 e1 where e1.id = ?) and e.id = ?)",
            "SELECT * FROM entity e WHERE NOT (e.id = (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.id = ?) AND e.tenant_id = 1");
    }

    @Test
    void selectSubSelectExists() {
        /* EXISTS */
        assertSql("SELECT * FROM entity e WHERE EXISTS (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE EXISTS (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.tenant_id = 1");


        /* NOT EXISTS */
        assertSql("SELECT * FROM entity e WHERE NOT EXISTS (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE NOT EXISTS (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.tenant_id = 1");
    }

    @Test
    void selectSubSelect() {
        /* >= */
        assertSql("SELECT * FROM entity e WHERE e.id >= (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.id >= (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.tenant_id = 1");


        /* <= */
        assertSql("SELECT * FROM entity e WHERE e.id <= (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.id <= (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.tenant_id = 1");


        /* <> */
        assertSql("SELECT * FROM entity e WHERE e.id <> (select e1.id from entity1 e1 where e1.id = ?)",
            "SELECT * FROM entity e WHERE e.id <> (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.tenant_id = 1");
    }

    @Test
    void selectFromSelect() {
        assertSql("SELECT * FROM (select e.id from entity e WHERE e.id = (select e1.id from entity1 e1 where e1.id = ?))",
            "SELECT * FROM (SELECT e.id FROM entity e WHERE e.id = (SELECT e1.id FROM entity1 e1 WHERE e1.id = ? AND e1.tenant_id = 1) AND e.tenant_id = 1)");
    }

    @Test
    void selectBodySubSelect() {
        assertSql("select t1.col1,(select t2.col2 from t2 t2 where t1.col1=t2.col1) from t1 t1",
            "SELECT t1.col1, (SELECT t2.col2 FROM t2 t2 WHERE t1.col1 = t2.col1 AND t2.tenant_id = 1) FROM t1 t1 WHERE t1.tenant_id = 1");
    }

    @Test
    void selectLeftJoin() {
        // left join
        assertSql("SELECT * FROM entity e " +
                "left join entity1 e1 on e1.id = e.id " +
                "WHERE e.id = ? OR e.name = ?",
            "SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 ON e1.id = e.id AND e1.tenant_id = 1 " +
                "WHERE (e.id = ? OR e.name = ?) AND e.tenant_id = 1");

        assertSql("SELECT * FROM entity e " +
                "left join entity1 e1 on e1.id = e.id " +
                "WHERE (e.id = ? OR e.name = ?)",
            "SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 ON e1.id = e.id AND e1.tenant_id = 1 " +
                "WHERE (e.id = ? OR e.name = ?) AND e.tenant_id = 1");

        assertSql("SELECT * FROM entity e " +
                "left join entity1 e1 on e1.id = e.id " +
                "left join entity2 e2 on e1.id = e2.id",
            "SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 ON e1.id = e.id AND e1.tenant_id = 1 " +
                "LEFT JOIN entity2 e2 ON e1.id = e2.id AND e2.tenant_id = 1 " +
                "WHERE e.tenant_id = 1");
    }

    @Test
    void selectRightJoin() {
        // right join
        assertSql("SELECT * FROM entity e " +
                "right join entity1 e1 on e1.id = e.id",
            "SELECT * FROM entity e " +
                "RIGHT JOIN entity1 e1 ON e1.id = e.id AND e.tenant_id = 1 " +
                "WHERE e1.tenant_id = 1");

        assertSql("SELECT * FROM with_as_1 e " +
                "right join entity1 e1 on e1.id = e.id",
            "SELECT * FROM with_as_1 e " +
                "RIGHT JOIN entity1 e1 ON e1.id = e.id " +
                "WHERE e1.tenant_id = 1");

        assertSql("SELECT * FROM entity e " +
                "right join entity1 e1 on e1.id = e.id " +
                "WHERE e.id = ? OR e.name = ?",
            "SELECT * FROM entity e " +
                "RIGHT JOIN entity1 e1 ON e1.id = e.id AND e.tenant_id = 1 " +
                "WHERE (e.id = ? OR e.name = ?) AND e1.tenant_id = 1");

        assertSql("SELECT * FROM entity e " +
                "right join entity1 e1 on e1.id = e.id " +
                "right join entity2 e2 on e1.id = e2.id ",
            "SELECT * FROM entity e " +
                "RIGHT JOIN entity1 e1 ON e1.id = e.id AND e.tenant_id = 1 " +
                "RIGHT JOIN entity2 e2 ON e1.id = e2.id AND e1.tenant_id = 1 " +
                "WHERE e2.tenant_id = 1");
    }

    @Test
    void selectMixJoin() {
        assertSql("SELECT * FROM entity e " +
                "right join entity1 e1 on e1.id = e.id " +
                "left join entity2 e2 on e1.id = e2.id",
            "SELECT * FROM entity e " +
                "RIGHT JOIN entity1 e1 ON e1.id = e.id AND e.tenant_id = 1 " +
                "LEFT JOIN entity2 e2 ON e1.id = e2.id AND e2.tenant_id = 1 " +
                "WHERE e1.tenant_id = 1");

        assertSql("SELECT * FROM entity e " +
                "left join entity1 e1 on e1.id = e.id " +
                "right join entity2 e2 on e1.id = e2.id",
            "SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 ON e1.id = e.id AND e1.tenant_id = 1 " +
                "RIGHT JOIN entity2 e2 ON e1.id = e2.id AND e1.tenant_id = 1 " +
                "WHERE e2.tenant_id = 1");

        assertSql("SELECT * FROM entity e " +
                "left join entity1 e1 on e1.id = e.id " +
                "inner join entity2 e2 on e1.id = e2.id",
            "SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 ON e1.id = e.id AND e1.tenant_id = 1 " +
                "INNER JOIN entity2 e2 ON e1.id = e2.id AND e.tenant_id = 1 AND e2.tenant_id = 1");
    }


    @Test
    void selectJoinSubSelect() {
        assertSql("select * from (select * from entity e) e1 " +
                "left join entity2 e2 on e1.id = e2.id",
            "SELECT * FROM (SELECT * FROM entity e WHERE e.tenant_id = 1) e1 " +
                "LEFT JOIN entity2 e2 ON e1.id = e2.id AND e2.tenant_id = 1");

        assertSql("select * from entity1 e1 " +
                "left join (select * from entity2 e2) e22 " +
                "on e1.id = e22.id",
            "SELECT * FROM entity1 e1 " +
                "LEFT JOIN (SELECT * FROM entity2 e2 WHERE e2.tenant_id = 1) e22 " +
                "ON e1.id = e22.id " +
                "WHERE e1.tenant_id = 1");
    }

    @Test
    void selectSubJoin() {

        assertSql("select * FROM " +
                "(entity1 e1 right JOIN entity2 e2 ON e1.id = e2.id)",
            "SELECT * FROM " +
                "(entity1 e1 RIGHT JOIN entity2 e2 ON e1.id = e2.id AND e1.tenant_id = 1) " +
                "WHERE e2.tenant_id = 1");

        assertSql("select * FROM " +
                "(entity1 e1 LEFT JOIN entity2 e2 ON e1.id = e2.id)",
            "SELECT * FROM " +
                "(entity1 e1 LEFT JOIN entity2 e2 ON e1.id = e2.id AND e2.tenant_id = 1) " +
                "WHERE e1.tenant_id = 1");


        assertSql("select * FROM " +
                "(entity1 e1 LEFT JOIN entity2 e2 ON e1.id = e2.id) " +
                "right join entity3 e3 on e1.id = e3.id",
            "SELECT * FROM " +
                "(entity1 e1 LEFT JOIN entity2 e2 ON e1.id = e2.id AND e2.tenant_id = 1) " +
                "RIGHT JOIN entity3 e3 ON e1.id = e3.id AND e1.tenant_id = 1 " +
                "WHERE e3.tenant_id = 1");


        assertSql("select * FROM entity e " +
                "LEFT JOIN (entity1 e1 right join entity2 e2 ON e1.id = e2.id) " +
                "on e.id = e2.id",
            "SELECT * FROM entity e " +
                "LEFT JOIN (entity1 e1 RIGHT JOIN entity2 e2 ON e1.id = e2.id AND e1.tenant_id = 1) " +
                "ON e.id = e2.id AND e2.tenant_id = 1 " +
                "WHERE e.tenant_id = 1");

        assertSql("select * FROM entity e " +
                "LEFT JOIN (entity1 e1 left join entity2 e2 ON e1.id = e2.id) " +
                "on e.id = e2.id",
            "SELECT * FROM entity e " +
                "LEFT JOIN (entity1 e1 LEFT JOIN entity2 e2 ON e1.id = e2.id AND e2.tenant_id = 1) " +
                "ON e.id = e2.id AND e1.tenant_id = 1 " +
                "WHERE e.tenant_id = 1");

        assertSql("select * FROM entity e " +
                "RIGHT JOIN (entity1 e1 left join entity2 e2 ON e1.id = e2.id) " +
                "on e.id = e2.id",
            "SELECT * FROM entity e " +
                "RIGHT JOIN (entity1 e1 LEFT JOIN entity2 e2 ON e1.id = e2.id AND e2.tenant_id = 1) " +
                "ON e.id = e2.id AND e.tenant_id = 1 " +
                "WHERE e1.tenant_id = 1");
    }


    @Test
    void selectLeftJoinMultipleTrailingOn() {
        // 多个 on 尾缀的
        assertSql("SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 " +
                "LEFT JOIN entity2 e2 ON e2.id = e1.id " +
                "ON e1.id = e.id " +
                "WHERE (e.id = ? OR e.NAME = ?)",
            "SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 " +
                "LEFT JOIN entity2 e2 ON e2.id = e1.id AND e2.tenant_id = 1 " +
                "ON e1.id = e.id AND e1.tenant_id = 1 " +
                "WHERE (e.id = ? OR e.NAME = ?) AND e.tenant_id = 1");

        assertSql("SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 " +
                "LEFT JOIN with_as_A e2 ON e2.id = e1.id " +
                "ON e1.id = e.id " +
                "WHERE (e.id = ? OR e.NAME = ?)",
            "SELECT * FROM entity e " +
                "LEFT JOIN entity1 e1 " +
                "LEFT JOIN with_as_A e2 ON e2.id = e1.id " +
                "ON e1.id = e.id AND e1.tenant_id = 1 " +
                "WHERE (e.id = ? OR e.NAME = ?) AND e.tenant_id = 1");
    }

    @Test
    void selectInnerJoin() {
        // inner join
        assertSql("SELECT * FROM entity e " +
                "inner join entity1 e1 on e1.id = e.id " +
                "WHERE e.id = ? OR e.name = ?",
            "SELECT * FROM entity e " +
                "INNER JOIN entity1 e1 ON e1.id = e.id AND e.tenant_id = 1 AND e1.tenant_id = 1 " +
                "WHERE e.id = ? OR e.name = ?");

        assertSql("SELECT * FROM entity e " +
                "inner join entity1 e1 on e1.id = e.id " +
                "WHERE (e.id = ? OR e.name = ?)",
            "SELECT * FROM entity e " +
                "INNER JOIN entity1 e1 ON e1.id = e.id AND e.tenant_id = 1 AND e1.tenant_id = 1 " +
                "WHERE (e.id = ? OR e.name = ?)");

        // 隐式内连接
        assertSql("SELECT * FROM entity e,entity1 e1 " +
                "WHERE e.id = e1.id",
            "SELECT * FROM entity e, entity1 e1 " +
                "WHERE e.id = e1.id AND e.tenant_id = 1 AND e1.tenant_id = 1");

        // 隐式内连接
        assertSql("SELECT * FROM entity a, with_as_entity1 b " +
                "WHERE a.id = b.id",
            "SELECT * FROM entity a, with_as_entity1 b " +
                "WHERE a.id = b.id AND a.tenant_id = 1");

        assertSql("SELECT * FROM with_as_entity a, with_as_entity1 b " +
                "WHERE a.id = b.id",
            "SELECT * FROM with_as_entity a, with_as_entity1 b " +
                "WHERE a.id = b.id");

        // SubJoin with 隐式内连接
        assertSql("SELECT * FROM (entity e,entity1 e1) " +
                "WHERE e.id = e1.id",
            "SELECT * FROM (entity e, entity1 e1) " +
                "WHERE e.id = e1.id " +
                "AND e.tenant_id = 1 AND e1.tenant_id = 1");

        assertSql("SELECT * FROM ((entity e,entity1 e1),entity2 e2) " +
                "WHERE e.id = e1.id and e.id = e2.id",
            "SELECT * FROM ((entity e, entity1 e1), entity2 e2) " +
                "WHERE e.id = e1.id AND e.id = e2.id " +
                "AND e.tenant_id = 1 AND e1.tenant_id = 1 AND e2.tenant_id = 1");

        assertSql("SELECT * FROM (entity e,(entity1 e1,entity2 e2)) " +
                "WHERE e.id = e1.id and e.id = e2.id",
            "SELECT * FROM (entity e, (entity1 e1, entity2 e2)) " +
                "WHERE e.id = e1.id AND e.id = e2.id " +
                "AND e.tenant_id = 1 AND e1.tenant_id = 1 AND e2.tenant_id = 1");

        // 沙雕的括号写法
        assertSql("SELECT * FROM (((entity e,entity1 e1))) " +
                "WHERE e.id = e1.id",
            "SELECT * FROM (((entity e, entity1 e1))) " +
                "WHERE e.id = e1.id " +
                "AND e.tenant_id = 1 AND e1.tenant_id = 1");

    }


    @Test
    void selectWithAs() {
        assertSql("with with_as_A as (select * from entity) select * from with_as_A",
            "WITH with_as_A AS (SELECT * FROM entity WHERE tenant_id = 1) SELECT * FROM with_as_A");
    }


    @Test
    void selectIgnoreTable() {
        assertSql(" SELECT dict.dict_code, item.item_text AS \"text\", item.item_value AS \"value\" FROM sys_dict_item item INNER JOIN sys_dict dict ON dict.id = item.dict_id WHERE dict.dict_code IN (1, 2, 3) AND item.item_value IN (1, 2, 3)",
            "SELECT dict.dict_code, item.item_text AS \"text\", item.item_value AS \"value\" FROM sys_dict_item item INNER JOIN sys_dict dict ON dict.id = item.dict_id AND item.tenant_id = 1 WHERE dict.dict_code IN (1, 2, 3) AND item.item_value IN (1, 2, 3)");
    }

    void assertSql(String sql, String targetSql) {
        assertThat(interceptor.parserSingle(sql, null)).isEqualTo(targetSql);
    }
}
