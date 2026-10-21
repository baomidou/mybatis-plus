package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.test.h2.entity.H2User;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * h2 集成测试的 Spring 基类：注入 {@link JdbcTemplate}、带 {@code @DirtiesContext}， 再给子类一个按名字查 h2user 的助手。本身不含用例。
 */
@DirtiesContext
public class H2TestSupport {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected static final String NQQ = "聂秋秋";

    protected void log(Object object) {
        System.out.println(object);
    }

    protected List<H2User> queryByName(String name) {
        String sql = "select TEST_ID, NAME, AGE,LAST_UPDATED_DT from h2user ";
        if (name != null) {
            sql += "where name='" + name + "'";
        }
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            H2User u = new H2User();
            u.setTestId(rs.getLong("TEST_ID"));
            u.setName(rs.getString("NAME"));
            u.setAge(AgeEnum.parseValue(rs.getInt("AGE")));
            u.setLastUpdatedDt(rs.getDate("LAST_UPDATED_DT"));
            return u;
        });
    }

}
