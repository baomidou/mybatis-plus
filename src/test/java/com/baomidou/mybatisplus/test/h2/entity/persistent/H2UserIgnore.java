package com.baomidou.mybatisplus.test.h2.entity.persistent;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.Version;
import com.baomidou.mybatisplus.enums.FieldIgnore;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.baomidou.mybatisplus.test.h2.entity.SuperEntity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 字段忽略 测试实体
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/6/28
 */
@Data
@Accessors(chain = true)
@TableName("h2user")
public class H2UserIgnore extends SuperEntity {

    /* 测试忽略验证 */
    private String name;

    private Integer age;

    /*BigDecimal 测试*/
    private BigDecimal price;

    /* 测试下划线字段命名类型, 字段填充 */
    @TableField(value = "test_type", strategy = FieldStrategy.IGNORED, ignore = FieldIgnore.UPDATE)
    private Integer testType;

    @TableField(value = "desc", ignore = FieldIgnore.INSERT)
    private String desc;

    @Version
    private Integer version;
}
