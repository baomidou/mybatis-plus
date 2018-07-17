package com.baomidou.mybatisplus.test.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author meimie
 * @since 2018/6/7
 */
@Data
@Accessors(chain = true)
@TableName(value = "tb_test_data_logic")
public class LogicTestData {

    private Long id;
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
    @TableLogic
    private Boolean deleted;

    @Version
    private Long version;
}
