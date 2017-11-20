package com.baomidou.mybatisplus.test.oracle.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户表
 */
@Data
@Accessors(chain = true)
@TableName("TEST_SEQUSER")
//@KeySequence("SEQ_TEST")
public class TestSequser extends BaseTestEntity implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
//    @TableId(value = "TEST_ID", type = IdType.INPUT)
//    private Long id;
    /**
     * 主键ID
     */
    // @TableId(value = "ID")
    // private Long id;


    /**
     * 名称
     */
    @TableField(value = "NAME")
    private String name;

    /**
     * 年龄
     */
    @TableField(value = "age")
    private Integer age;

    /**
     * 测试下划线字段命名类型
     */
    @TableField(value = "TEST_TYPE")
    private Integer testType;

    public TestSequser() {

    }

    public TestSequser(String name, Integer age, Integer testType) {
        this.name = name;
        this.age = age;
        this.testType = testType;
    }

    //public Long getId() {
    //	return id;
    //}

    //public void setId(Long id) {
    //	this.id = id;
    //}

}
