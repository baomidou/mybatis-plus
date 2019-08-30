package com.baomidou.mybatisplus.extension.plugins.pagination.optimize;

import org.apache.ibatis.reflection.MetaObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2019-05-08
 */
class JsqlParserCountOptimizeTest {

    private JsqlParserCountOptimize parser = new JsqlParserCountOptimize(true);
    private MetaObject metaObject = null;

    @Test
    void parserLeftJoin() {
        /* 能进行优化的 SQL */
        assertThat(parser.parser(metaObject, "select * from user u LEFT JOIN role r ON r.id = u.role_id WHERE u.xx = ?").getSql())
            .isEqualTo("SELECT COUNT(1) FROM user u WHERE u.xx = ?");

        assertThat(parser.parser(metaObject, "select * from user LEFT JOIN role ON role.id = user.role_id WHERE user.xx = ?").getSql())
            .isEqualTo("SELECT COUNT(1) FROM user WHERE user.xx = ?");

        /* 不能进行优化的 SQL */
        assertThat(parser.parser(metaObject, "select * from user u LEFT JOIN role r ON r.id = u.role_id AND r.name = ? where u.xx = ?").getSql())
            .isEqualTo("SELECT COUNT(1) FROM user u LEFT JOIN role r ON r.id = u.role_id AND r.name = ? WHERE u.xx = ?");

        assertThat(parser.parser(metaObject, "select * from user u LEFT JOIN role r ON r.id = u.role_id WHERE u.xax = ? AND r.cc = ? AND r.qq = ?").getSql())
            .isEqualTo("SELECT COUNT(1) FROM user u LEFT JOIN role r ON r.id = u.role_id WHERE u.xax = ? AND r.cc = ? AND r.qq = ?");
    }
}
