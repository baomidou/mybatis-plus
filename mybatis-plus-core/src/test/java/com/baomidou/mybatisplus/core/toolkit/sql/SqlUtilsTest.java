package com.baomidou.mybatisplus.core.toolkit.sql;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-11-19
 */
class SqlUtilsTest {

    @Test
    void m1() {
        List<String> list = SqlUtils.findPlaceholder("select {@table},{@table:t},{@table:t:r} from table");
        assertThat(list).contains("{@table}", "{@table:t}", "{@table:t:r}");
    }

    @Test
    void getNewSelectBody() {
        String s = SqlUtils.getNewSelectBody("id,name", "d", null, null);
        assertThat(s).isEqualTo("d.id,d.name");

        s = SqlUtils.getNewSelectBody("`id`,`name`", "d", null, null);
        assertThat(s).isEqualTo("d.`id`,d.`name`");

        s = SqlUtils.getNewSelectBody("id,name", "d", "pp", "`");
        assertThat(s).isEqualTo("d.id AS `pp.id`,d.name AS `pp.name`");

        s = SqlUtils.getNewSelectBody("id AS t_id,name AS t_name", "d", null, null);
        assertThat(s).isEqualTo("d.id AS t_id,d.name AS t_name");

        s = SqlUtils.getNewSelectBody("`id` AS t_id,`name` AS t_name", "d", null, null);
        assertThat(s).isEqualTo("d.`id` AS t_id,d.`name` AS t_name");

        s = SqlUtils.getNewSelectBody("id AS `t_id`,name AS `t_name`", "d", "pp", "`");
        assertThat(s).isEqualTo("d.id AS `pp.t_id`,d.name AS `pp.t_name`");

        s = SqlUtils.getNewSelectBody("`id` AS `t_id`,`name` AS `t_name`", "d", "pp", "'");
        assertThat(s).isEqualTo("d.`id` AS 'pp.t_id',d.`name` AS 'pp.t_name'");
    }
}
