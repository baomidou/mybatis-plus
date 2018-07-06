package com.baomidou.mybatisplus.test.base.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author miemie
 * @since 2018-07-06
 */
public class BaseDb {

    protected static void executeSql(Statement stmt, String path, String sqlFilename) throws SQLException, IOException {
        try (
            BufferedReader reader = new BufferedReader(new FileReader(filePath(path, sqlFilename)))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                stmt.execute(line.replace(";", ""));
            }
        }
    }

    protected static String readFile(String path, String fileName) {
        StringBuilder builder = new StringBuilder();
        try (
            BufferedReader reader = new BufferedReader(new FileReader(filePath(path, fileName)))
        ) {
            String line;
            while ((line = reader.readLine()) != null)
                builder.append(line).append(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private static String filePath(String path, String fileName) {
        return BaseDb.class.getResource(path + fileName).getPath();
    }
}
