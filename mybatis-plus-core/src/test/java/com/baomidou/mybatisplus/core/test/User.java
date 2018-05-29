package com.baomidou.mybatisplus.core.test;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
