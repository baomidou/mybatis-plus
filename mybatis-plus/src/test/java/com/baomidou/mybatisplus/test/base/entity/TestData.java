package com.baomidou.mybatisplus.test.base.entity;

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
public class TestData {

    private Long id;
    private Integer testInt;
    private String testStr;
    private Double testDouble;
    private Boolean testBoolean;
    private LocalDate testDate;
    private LocalTime testTime;
    private LocalDateTime testDateTime;
    private LocalDateTime testTimestamp;
}
