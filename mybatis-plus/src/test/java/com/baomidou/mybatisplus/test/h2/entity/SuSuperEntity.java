package com.baomidou.mybatisplus.test.h2.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 多层集成测试
 * <p>github #170</p>
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
