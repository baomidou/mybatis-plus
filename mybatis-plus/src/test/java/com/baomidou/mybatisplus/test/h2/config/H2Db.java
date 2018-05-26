package com.baomidou.mybatisplus.test.h2.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.baomidou.mybatisplus.test.h2.H2UserTest;

public class H2Db {

    public static void initH2User() throws SQLException, IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:h2/spring-test-h2.xml");
        DataSource ds = (DataSource) context.getBean("dataSource");
        try (Connection conn = ds.getConnection()) {
            H2Db.initData(conn, "user.ddl.sql", "user.insert.sql", "h2user");
        }
    }

    public static void initData(Connection conn, String ddlFileName, String insertFileName, String tableName) throws SQLException, IOException {
        String createTableSql = readFile(ddlFileName);
        Statement stmt = conn.createStatement();
        stmt.execute(createTableSql);
        stmt.execute("truncate table " + tableName);
        executeSql(stmt, insertFileName);
        conn.commit();
    }

    public static void executeSql(Statement stmt, String sqlFilename) throws SQLException, IOException {
        try (
            BufferedReader reader = new BufferedReader(new FileReader(filePath(sqlFilename)))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                stmt.execute(line.replace(";", ""));
            }
        }
    }

    public static String readFile(String fileName) {
        StringBuilder builder = new StringBuilder();
        try (
            BufferedReader reader = new BufferedReader(new FileReader(filePath(fileName)))
        ) {
            String line;
            while ((line = reader.readLine()) != null)
                builder.append(line).append(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static String filePath(String fileName) {
        return H2UserTest.class.getResource("/h2/" + fileName).getPath();
    }

}
