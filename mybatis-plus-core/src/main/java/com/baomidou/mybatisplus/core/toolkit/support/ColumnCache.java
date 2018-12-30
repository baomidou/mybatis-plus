package com.baomidou.mybatisplus.core.toolkit.support;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author miemie
 * @since 2018-12-30
 */
@Data
@AllArgsConstructor
public class ColumnCache {

    /**
     * 使用 column
     */
    private String column;
    /**
     * 查询 column
     */
    private String columnSelect;
}
