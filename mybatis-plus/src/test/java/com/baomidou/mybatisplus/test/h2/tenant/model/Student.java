package com.baomidou.mybatisplus.test.h2.tenant.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author nieqiuqiu 2019/12/8
 */
@Data
@TableName(value = "student")
@NoArgsConstructor
public class Student implements Serializable {

    private Long id;

    private String tenantId;

    private String name;

    public Student(String name) {
        this.name = name;
    }
}
