package com.baomidou.mybatisplus.core.conditions.query;

import lombok.Data;

/**
 * 共享查询字段
 *
 * @author miemie
 * @since 2018-11-20
 */
@Data
public class SharedSqlSelect {

    /**
     * 查询字段
     */
    private String sqlSelect;
}
