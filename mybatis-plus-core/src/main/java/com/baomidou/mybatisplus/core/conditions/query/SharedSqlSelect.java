package com.baomidou.mybatisplus.core.conditions.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 共享查询字段
 *
 * @author miemie
 * @since 2018-11-20
 */
@Data
public class SharedSqlSelect implements Serializable {
    private static final long serialVersionUID = -1536422416594422874L;

    /**
     * 查询字段
     */
    private String sqlSelect;
}
