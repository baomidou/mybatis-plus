package com.baomidou.mybatisplus.core.conditions.query;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;

import java.io.Serializable;
import java.util.function.Predicate;

/**
 * @author miemie
 * @since 2018-12-12
 */
public interface Query<This, T, R> extends Serializable {

    This select(R... columns);

    This select(Predicate<TableFieldInfo> predicate);

    This select(Class<T> entityClass, Predicate<TableFieldInfo> predicate);

    /**
     * 查询条件 SQL 片段
     */
    String getSqlSelect();
}
