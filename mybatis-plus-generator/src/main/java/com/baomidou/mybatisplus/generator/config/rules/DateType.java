package com.baomidou.mybatisplus.generator.config.rules;

/**
 * <p>
 * 数据库时间类型 到 实体类时间类型 对应策略
 * </p>
 *
 * @author miemie
 * @since 2018/5/22
 */
public enum DateType {
    /**
     * 只使用 java.util.date 代替
     */
    ONLY_DATE,
    /**
     * 使用 java.sql 包下的
     */
    SQL_PACK,
    /**
     * 使用 java.time 包下的
     * java8 新的时间类型
     */
    TIME_PACK
}
