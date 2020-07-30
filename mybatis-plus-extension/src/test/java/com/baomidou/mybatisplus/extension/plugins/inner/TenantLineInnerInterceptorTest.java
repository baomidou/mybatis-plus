package com.baomidou.mybatisplus.extension.plugins.inner;

import net.sf.jsqlparser.expression.LongValue;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-07-30
 */
class TenantLineInnerInterceptorTest {

    private final TenantLineInnerInterceptor interceptor = new TenantLineInnerInterceptor(() -> new LongValue(1));

    @Test
    void insert() {
        assertSql("insert into entity (id,name) value (?,?)",
            "INSERT INTO entity (id, name, tenant_id) VALUES (?, ?, 1)");
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

    void assertSql(String sql, String targetSql) {
        assertThat(interceptor.parserSingle(sql, null)).isEqualTo(targetSql);
    }
}
