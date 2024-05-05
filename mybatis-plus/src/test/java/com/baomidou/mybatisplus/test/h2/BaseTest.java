package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.test.h2.entity.H2User;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@DirtiesContext
public class BaseTest {

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
