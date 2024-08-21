package com.baomidou.mybatisplus.test.autoconfigure.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Jam804
 * @since 2024-08-22
 */
@Data
@TableName("test")
public class TestEntity {

    private Long id;

    private String name;
}
