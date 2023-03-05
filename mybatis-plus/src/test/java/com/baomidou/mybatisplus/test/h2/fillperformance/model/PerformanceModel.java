package com.baomidou.mybatisplus.test.h2.fillperformance.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName(value = "t_fill_performance")
public class PerformanceModel {

    private Long id;
    private String a;
    private String b;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String c;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String d;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String e;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String f;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String g;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String h;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String i;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String j;
    private String k;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String l;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String m;
    private String n;

}
