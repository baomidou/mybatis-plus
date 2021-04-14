package com.baomidou.mybatisplus.test.strategy;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author miemie
 * @since 2021-01-27
 */
@Data
public class Entity {

    private Long id;

    private String name;

    @TableField(insertStrategy = FieldStrategy.NOT_EMPTY)
    private String insertStr;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String updateStr;
}
