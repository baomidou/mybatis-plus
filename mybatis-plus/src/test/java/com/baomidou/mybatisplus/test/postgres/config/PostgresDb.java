package com.baomidou.mybatisplus.test.postgres.config;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.test.base.db.BaseDb;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgresDb extends BaseDb {

    public static void initPgData() throws SQLException {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:postgres/spring-test-postgres.xml");
        DataSource ds = context.getBean("dataSource", DataSource.class);
        try (Connection conn = ds.getConnection()) {
            initData(conn, "/postgres/", "test_data.ddl.sql");
        }
    }

    public static void initData(Connection conn, String path, String ddlFileName)
        throws SQLException {
        String createTableSql = readFile(path, ddlFileName);
        String[] sqls = createTableSql.split(";");
        Statement stmt = conn.createStatement();
        for (String sql : sqls) {
            if (StringUtils.isNotEmpty(sql)) {
                stmt.execute(sql);
            }
        }
    }
}
