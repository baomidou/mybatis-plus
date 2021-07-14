/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.h2.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 测试用户类
 *
 * @author hubin sjy
 */
/* 表名 value 注解【 驼峰命名可无 】, resultMap 注解测试【 映射 xml 的 resultMap 内容 】 */
@Data
@Accessors(chain = true)
@TableName("h2user")
@EqualsAndHashCode(callSuper = true)
public class H2User extends SuperEntity {

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2043176352335589747L;

	/* 测试忽略验证 */
    private String name;

    private AgeEnum age;

    /*BigDecimal 测试*/
    private BigDecimal price;

    /* 测试下划线字段命名类型, 字段填充 */
    @TableField
    private Integer testType;

    /**
     * 转义关键字测试
     */
    @TableField("`desc`")
    private String desc;

    /**
     * 该注解 select 默认不注入 select 查询
     */
    @TableField(select = false)
    private Date testDate;

    @Version
    private Integer version;

    @TableLogic
    private Integer deleted;


    public H2User() {

    }

    public H2User(String name) {
        this.name = name;
    }

    public H2User(Integer testType) {
        this.testType = testType;
    }

    public H2User(String name, AgeEnum age) {
        this.name = name;
        this.age = age;
    }

    public H2User(Long id, String name) {
        this.setTestId(id);
        this.name = name;
    }

    public H2User(Long id, AgeEnum age) {
        this.setTestId(id);
        this.age = age;
    }

    public H2User(Long id, String name, AgeEnum age, Integer testType) {
        this.setTestId(id);
        this.name = name;
        this.age = age;
        this.testType = testType;
    }

    public H2User(String name, AgeEnum age, Integer testType) {
        this.name = name;
        this.age = age;
        this.testType = testType;
    }

    public H2User(String name, Integer deleted) {
        this.name = name;
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "h2user:{name=" + name + "," +
            "age=" + age + "," +
            "price=" + price + "," +
            "testType=" + testType + "," +
            "desc=" + desc + "," +
            "testDate=" + testDate + "," +
            "version=" + version;
    }
}
