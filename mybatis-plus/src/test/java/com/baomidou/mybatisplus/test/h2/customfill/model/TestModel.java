package com.baomidou.mybatisplus.test.h2.customfill.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.test.h2.customfill.annotation.InsertUpdateFill;
import lombok.Data;

@Data
@TableName(value = "t_fill_test")
public class TestModel {

    private Long id;
    @InsertUpdateFill
    private String a;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String b;
}
