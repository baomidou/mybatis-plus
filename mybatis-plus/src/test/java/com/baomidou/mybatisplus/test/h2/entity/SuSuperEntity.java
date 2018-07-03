package com.baomidou.mybatisplus.test.h2.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 多层集成测试
 * github #170
 * </p>
 *
 * @author yuxiaobin
 * @since 2017/12/7
 */
@Data
@Accessors(chain = true)
public abstract class SuSuperEntity {

    @TableField(value = "last_updated_dt", fill = FieldFill.UPDATE)
    private Date lastUpdatedDt;

}
