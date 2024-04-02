package com.baomidou.mybatisplus.extension.ddl.history;

import org.springframework.stereotype.Component;

import java.util.function.Function;


@Component
public class SQLiteDdlGenerator implements IDdlGenerator {

    public static IDdlGenerator newInstance() {
        return new SQLiteDdlGenerator();
    }

    @Override
    public boolean existTable(String databaseName, Function<String, Boolean> executeFunction) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(1) FROM sqlite_master WHERE name='");
        sql.append(getDdlHistory()).append("' AND type='table'");
        return executeFunction.apply(sql.toString());
    }

    @Override
    public String createDdlHistory() {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE IF NOT EXISTS `").append(getDdlHistory()).append("` (");
        sql.append("script  TEXT primary key,");
        sql.append("type    TEXT,");
        sql.append("version TEXT");
        sql.append(");");
        return sql.toString();
    }
}
