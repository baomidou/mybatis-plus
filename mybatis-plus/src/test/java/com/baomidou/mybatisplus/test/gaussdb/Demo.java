package com.baomidou.mybatisplus.test.gaussdb;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author nieqiurong
 * @since 3.5.13
 */
@Data
@ToString
public class Demo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer age;

    private LocalDate birthday;

    private LocalDateTime createTime;
}
