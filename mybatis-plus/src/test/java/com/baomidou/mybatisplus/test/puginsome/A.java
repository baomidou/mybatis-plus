package com.baomidou.mybatisplus.test.puginsome;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author miemie
 * @since 2022-05-11
 */
@Data
public class A {
    private int id;
    private String name;

    @TableField(exist = false)
    private B b;
}
