package com.baomidou.mybatisplus.test.base.entity.pg;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author miemie
 * @since 2018-08-06
 */
@Data
@Accessors(chain = true)
public class PgData {

    private Long id;
    @TableField(value = "age", el = "dataAge, jdbcType=INTEGER")
    private Integer dataAge;
    @TableField(value = "c_datetime", fill = FieldFill.INSERT)
    private LocalDateTime createDatetime;
    @TableField(value = "u_datetime", fill = FieldFill.UPDATE)
    private LocalDateTime updateDatetime;
}
