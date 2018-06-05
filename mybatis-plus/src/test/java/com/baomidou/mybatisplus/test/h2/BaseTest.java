package com.baomidou.mybatisplus.test.h2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class BaseTest {

    protected final String NQQ = "聂秋秋";

    protected void log(Object object) {
        System.out.println(object);
    }

    public void initData(Connection conn, String ddlFileName, String insertFileName, String tableName) throws SQLException, IOException {
        String createTableSql = readFile(ddlFileName);
        Statement stmt = conn.createStatement();
        stmt.execute(createTableSql);
        stmt.execute("truncate table " + tableName);
        executeSql(stmt, insertFileName);
        conn.commit();
    }

    public void executeSql(Statement stmt, String sqlFilename) throws SQLException, IOException {
        try (
            BufferedReader reader = new BufferedReader(new FileReader(filePath(sqlFilename)))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                stmt.execute(line.replace(";", ""));
            }
        }
    }

    public String readFile(String fileName) {
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

    public String filePath(String fileName) {
        return H2UserTest.class.getResource("/h2/" + fileName).getPath();
    }
}
