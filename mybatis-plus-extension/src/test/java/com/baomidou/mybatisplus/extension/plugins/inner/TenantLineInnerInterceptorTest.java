package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-07-30
 */
class TenantLineInnerInterceptorTest {

    private final TenantLineInnerInterceptor interceptor = new TenantLineInnerInterceptor(new TenantLineHandler() {
        @Override
        public Expression getTenantId() {
            return new LongValue(1);
        }

        @Override
        public boolean ignoreTable(String tableName) {
            return tableName.startsWith("with_as");
        }
    });

    @Test
    void insert() {
        // plain
        assertSql("insert into entity (id,name) value (?,?)",
            "INSERT INTO entity (id, name, tenant_id) VALUES (?, ?, 1)");
        // 无 insert的列
        assertSql("insert into entity value (?,?)",
            "INSERT INTO entity VALUES (?, ?)");
        // 自己加了insert的列
        assertSql("insert into entity (id,name,tenant_id) value (?,?,?)",
            "INSERT INTO entity (id, name, tenant_id) VALUES (?, ?, ?)");
        // insert into select
        assertSql("insert into entity (id,name) select id,name from entity2",
            "INSERT INTO entity (id, name, tenant_id) SELECT id, name, tenant_id FROM entity2 WHERE tenant_id = 1");

        assertSql("insert into entity (id,name) select * from entity2",
            "INSERT INTO entity (id, name, tenant_id) SELECT * FROM entity2 WHERE tenant_id = 1");

        assertSql("insert into entity (id,name) select id,name from (select id,name from entity3) t",
            "INSERT INTO entity (id, name, tenant_id) SELECT id, name, tenant_id FROM (SELECT id, name, tenant_id FROM entity3 WHERE tenant_id = 1) t");

        assertSql("insert into entity (id,name) select * from (select id,name from entity3) t",
            "INSERT INTO entity (id, name, tenant_id) SELECT * FROM (SELECT id, name, tenant_id FROM entity3 WHERE tenant_id = 1) t");

        assertSql("insert into entity (id,name) select t.* from (select id,name from entity3) t",
            "INSERT INTO entity (id, name, tenant_id) SELECT t.* FROM (SELECT id, name, tenant_id FROM entity3 WHERE tenant_id = 1) t");
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
    void selectBodySubSelect(){
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
    }

    @Test
    void selectInnerJoin() {
        // inner join
        assertSql("SELECT * FROM entity e " +
                "inner join entity1 e1 on e1.id = e.id " +
                "WHERE e.id = ? OR e.name = ?",
            "SELECT * FROM entity e " +
                "INNER JOIN entity1 e1 ON e1.id = e.id AND e1.tenant_id = 1 " +
                "WHERE (e.id = ? OR e.name = ?) AND e.tenant_id = 1");

        assertSql("SELECT * FROM entity e " +
                "inner join entity1 e1 on e1.id = e.id " +
                "WHERE (e.id = ? OR e.name = ?)",
            "SELECT * FROM entity e " +
                "INNER JOIN entity1 e1 ON e1.id = e.id AND e1.tenant_id = 1 " +
                "WHERE (e.id = ? OR e.name = ?) AND e.tenant_id = 1");

        // 垃圾 inner join todo
//        assertSql("SELECT * FROM entity,entity1 " +
//                "WHERE entity.id = entity1.id",
//            "SELECT * FROM entity e " +
//                "INNER JOIN entity1 e1 ON e1.id = e.id AND e1.tenant_id = 1 " +
//                "WHERE (e.id = ? OR e.name = ?) AND e.tenant_id = 1");
    }

    @Test
    void selectWithAs() {
        assertSql("with with_as_A as (select * from entity) select * from with_as_A",
            "WITH with_as_A AS (SELECT * FROM entity WHERE tenant_id = 1) SELECT * FROM with_as_A");
    }

    void assertSql(String sql, String targetSql) {
        assertThat(interceptor.parserSingle(sql, null)).isEqualTo(targetSql);
    }
}
