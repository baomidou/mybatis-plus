package com.baomidou.mybatisplus.test.h2.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 * 多层集成测试
 * <p>
 * github #170
 * </p>
 *
 * @author yuxiaobin
 * @since 2017/12/7
 */
@Data
public abstract class SuSuperEntityCamel {

    @TableField(value = "lastUpdatedDt", fill = FieldFill.UPDATE)
    private Date lastUpdatedDt;

}
