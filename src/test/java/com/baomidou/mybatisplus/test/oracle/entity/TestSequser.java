package com.baomidou.mybatisplus.test.oracle.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

/**
 * 用户表
 */
@TableName("TEST_SEQUSER")
//@KeySequence("SEQ_TEST")
public class TestSequser  extends BaseTestEntity implements Serializable{

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
   // @TableId(value = "ID")
   // private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;
    
    /**
     * 测试下划线字段命名类型
     */
    //@TableField(value = "TEST_TYPE")
    private Integer testType;

    public TestSequser() {

    }
    
    public TestSequser(String name,Integer age,Integer testType){
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getTestType() {
		return testType;
	}

	public void setTestType(Integer testType) {
		this.testType = testType;
	}

}
