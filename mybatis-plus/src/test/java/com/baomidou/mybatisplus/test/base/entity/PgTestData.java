package com.baomidou.mybatisplus.test.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author miemie
 * @since 2018-08-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@TableName(value = "tb_only_pg_test_data")
public class PgTestData extends BaseEntity {

    @TableField(value = "age", el = "dataAge, jdbcType=INTEGER")
    private Integer dataAge;
    @TableField(value = "c_datetime", fill = FieldFill.INSERT)
    private LocalDateTime createDatetime;
    @TableField(value = "u_datetime", fill = FieldFill.UPDATE)
    private LocalDateTime updateDatetime;
}
