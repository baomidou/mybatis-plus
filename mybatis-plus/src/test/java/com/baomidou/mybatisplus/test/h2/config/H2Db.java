package com.baomidou.mybatisplus.test.h2.config;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.baomidou.mybatisplus.test.base.db.BaseDb;

public class H2Db extends BaseDb {

    public static void initH2User() throws SQLException, IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:h2/spring-test-h2.xml");
        DataSource ds = context.getBean("dataSource", DataSource.class);
        try (Connection conn = ds.getConnection()) {
            H2Db.initData(conn, "/h2/", "user.ddl.sql", "user.insert.sql", "h2user");
        }
    }

    public static void initData(Connection conn, String path, String ddlFileName, String insertFileName, String tableName)
        throws SQLException, IOException {
        String createTableSql = readFile(path, ddlFileName);
        Statement stmt = conn.createStatement();
        stmt.execute(createTableSql);
        stmt.execute("truncate table " + tableName);
        executeSql(stmt, path, insertFileName);
        conn.commit();
    }
}
