package com.baomidou.mybatisplus.test.h2.base;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;

import com.baomidou.mybatisplus.test.h2.H2UserNoOptLockTest;

/**
 * <p>
 * Mybatis Plus H2 Junit Test
 * </p>
 *
 * @author hubin
 * @since 2017-06-15
 */
public class H2Test {

    public static void executeSql(Statement stmt, String sqlFilename) throws SQLException, IOException {
        String filePath = H2Test.class.getResource("/h2/" + sqlFilename).getPath();
        try (
            BufferedReader reader = new BufferedReader(new FileReader(filePath))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                stmt.execute(line.replace(";", ""));
            }
        }
    }

    public static void initData(Connection conn) throws SQLException, IOException {
//        String createTableSql = readFile("user.ddl.sql");
//        Statement stmt = conn.createStatement();
//        stmt.execute(createTableSql);
//        stmt.execute("truncate table h2user");
//        executeSql(stmt, "user.insert.sql");
//        conn.commit();
        initData(conn, "user.ddl.sql", "user.insert.sql", "h2user");
    }

    public static void initData(Connection conn, String ddlFileName, String insertFileName, String tableName) throws SQLException, IOException {
        String createTableSql = readFile(ddlFileName);
        Statement stmt = conn.createStatement();
        stmt.execute(createTableSql);
        stmt.execute("truncate table " + tableName);
        executeSql(stmt, insertFileName);
        conn.commit();
    }


    public static String readFile(String filename) {
        StringBuilder builder = new StringBuilder();
        String filePath = H2UserNoOptLockTest.class.getResource("/h2/" + filename).getPath();
        try (
            BufferedReader reader = new BufferedReader(new FileReader(filePath))
        ) {
            String line;
            while ((line = reader.readLine()) != null)
                builder.append(line).append(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    protected void assertUpdateFill(Date lastUpdatedDt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        System.out.println("after update: testDate=" + lastUpdatedDt);
        String versionDateStr = sdf.format(lastUpdatedDt);
        //MyMetaObjectHandler.updateFill() : set lastUpdatedDt=currentTimestamp
        Assert.assertEquals("lastUpdateDt will be updated by H2MetaObjectHandler.updateFill()", sdf.format(new Date()), versionDateStr);//before update: lastUpdatedDt=currentTimestamp-1day
    }
}
