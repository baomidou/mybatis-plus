package com.baomidou.mybatisplus.test.h2.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.FieldFill;

/**
 * <p>
 * 多层集成测试
 * github #170
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/12/7
 */
public abstract class SuSuperEntity {

    @TableField(value = "last_updated_dt", fill = FieldFill.UPDATE)
    private Date lastUpdatedDt;

    public Date getLastUpdatedDt() {
        return lastUpdatedDt;
    }

    public void setLastUpdatedDt(Date lastUpdatedDt) {
        this.lastUpdatedDt = lastUpdatedDt;
    }
}
