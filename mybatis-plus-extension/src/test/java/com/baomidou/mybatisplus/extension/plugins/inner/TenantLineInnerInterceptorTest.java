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
