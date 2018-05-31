package com.baomidou.mybatisplus.core.conditions.update;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.enums.SqlKeyword.EQ;

/**
 * <p>
 * Update 条件封装
 * </p>
 *
 * @author hubin miemie HCL
 * @since 2018-05-30
 */
public abstract class AbstractUpdateWrapper<T, R, This extends AbstractUpdateWrapper<T, R, This>> extends AbstractWrapper<T, R, This> {

//    /**
//     * SQL 更新字段内容，例如：name='1',age=2
//     */
//    protected String sqlSet;

    private List<ISqlSegment> setExpression = new ArrayList<>();

    /**
     * 例: setSql("name='1',age=2")
     */
    public This setSql(String sql) {
        return setSql(true, sql);
    }

    /**
     * 例: setSql("name='1',age=2")
     */
    public This setSql(boolean condition, String sql) {
        return doItUpd(condition, () -> sql);
    }

    /**
     * 字段 = 值
     */
    public This set(R column, Object val) {
        return set(true, column, val);
    }

    /**
     * 字段 = 值
     */
    public This set(boolean condition, R column, Object val) {
        return doItUpd(condition, () -> columnToString(column), EQ, () -> formatSql("{0}", val));
    }

    protected This doItUpd(boolean condition, ISqlSegment... sqlSegments) {
        if (condition) {
            setExpression.addAll(Arrays.asList(sqlSegments));
            setExpression.add(() -> ",");
        }
        return typedThis();
    }

    @Override
    public String getSqlSet() {
        String sqlSet = String.join("", setExpression.stream()
            .map(ISqlSegment::getSqlSegment)
            .collect(Collectors.toList()));
        if (StringUtils.isNotEmpty(sqlSet)) {
            sqlSet = sqlSet.substring(0, sqlSet.length() - 1);
        }
        return sqlSet;
    }
}
