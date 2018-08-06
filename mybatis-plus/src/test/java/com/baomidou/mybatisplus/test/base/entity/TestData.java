package com.baomidou.mybatisplus.test.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author meimie
 * @since 2018/6/7
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "tb_test_data")
public class TestData extends BaseEntity {

    private Integer testInt;
    private String testStr;
    private Double testDouble;
    private Boolean testBoolean;
    private LocalDate testDate;
    private LocalTime testTime;
    private LocalDateTime testDateTime;
    private LocalDateTime testTimestamp;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDatetime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateDatetime;

    @Version
    private Integer version;
}
