package com.baomidou.mybatisplus.mapper;

import com.baomidou.mybatisplus.MybatisAbstractSQL;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import java.text.MessageFormat;

/**
 * <p>
 * 实现AbstractSQL ，实现WHERE条件自定义
 * </p>
 *
 * @author yanghu
 * @Date 2016-08-22
 */
public class TSQLPlus extends MybatisAbstractSQL<TSQLPlus> {

    private final String IS_NOT_NULL = " IS NOT NULL";
    private final String IS_NULL = " IS NULL";

    @Override
    public TSQLPlus getSelf() {
        return this;
    }

    /**
     * 将LIKE语句添加到WHERE条件中
     *
     * @param column 字段名
     * @param value  like值,无需前后%, MYSQL及ORACEL通用
     * @return
     */
    public TSQLPlus LIKE(String column, String value) {
        handerLike(column, value, false);
        return this;
    }

    /**
     * 将LIKE语句添加到WHERE条件中
     *
     * @param column 字段名
     * @param value  like值,无需前后%, MYSQL及ORACEL通用
     * @return
     */
    public TSQLPlus NOT_LIKE(String column, String value) {
        handerLike(column, value, true);
        return this;
    }

    /**
     * IS NOT NULL查询
     *
     * @param columns 以逗号分隔的字段名称
     * @return this
     */
    public TSQLPlus IS_NOT_NULL(String columns) {
        handerNull(columns, IS_NOT_NULL);
        return this;
    }

    /**
     * IS NULL查询
     *
     * @param columns 以逗号分隔的字段名称
     * @return
     */
    public TSQLPlus IS_NULL(String columns) {
        handerNull(columns, IS_NULL);
        return this;
    }

    /**
     * 处理LIKE操作
     *
     * @param column 字段名称
     * @param value  like匹配值
     * @param isNot  是否为NOT LIKE操作
     */
    private void handerLike(String column, String value, boolean isNot) {
        if (StringUtils.isNotEmpty(column) && StringUtils.isNotEmpty(value)) {
            String likeSql = " LIKE CONCAT(CONCAT({0},{1}),{2})";
            if (isNot) {
                likeSql = " NOT" + likeSql;
            }
            String percent = StringUtils.quotaMark("%");
            WHERE(column + MessageFormat.format(likeSql, percent, StringUtils.quotaMark(value), percent));
        }
    }

    /**
     * 以相同的方式处理null和notnull
     *
     * @param columns 以逗号分隔的字段名称
     * @param sqlPart SQL部分
     */
    private void handerNull(String columns, String sqlPart) {
        if (StringUtils.isNotEmpty(columns)) {
            String[] cols = columns.split(",");
            for (String col : cols) {
                if (StringUtils.isNotEmpty(col.trim())) {
                    WHERE(col + sqlPart);
                }
            }
        }
    }

}
