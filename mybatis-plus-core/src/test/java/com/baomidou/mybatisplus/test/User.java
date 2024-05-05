package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("sys_user")
@Data
public class User {

    private Integer id;

    @TableField("username")
    private String name;

    private Integer roleId;
}
