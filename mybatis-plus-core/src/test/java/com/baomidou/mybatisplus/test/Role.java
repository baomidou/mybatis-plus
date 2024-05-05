package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Role {

    private Integer id;

    @TableField("roleName")
    private String name;
}
