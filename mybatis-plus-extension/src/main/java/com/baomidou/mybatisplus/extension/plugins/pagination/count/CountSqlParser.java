package com.baomidou.mybatisplus.extension.plugins.pagination.count;

/**
 * @author miemie
 * @since 2020-06-16
 */
public interface CountSqlParser {

    String parser(String sql);

    default String defaultCount(String sql) {
        return String.format("SELECT COUNT(1) FROM ( %s ) TOTAL", sql);
    }
}
