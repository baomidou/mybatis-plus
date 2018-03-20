/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.h2.entity.persistent;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.Version;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.FieldStrategy;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 测试用户类
 * </p>
 *
 * @author hubin sjy
 */
/* 表名 value 注解【 驼峰命名可无 】, resultMap 注解测试【 映射 xml 的 resultMap 内容 】 */
@Data
@Accessors(chain = true)
@TableName("h2user")
public class H2UserIntVersionAR extends Model<H2UserIntVersionAR> {

    @TableId(value = "test_id")
    private Long id;

    @TableField(value = "last_updated_dt", fill = FieldFill.UPDATE)
    private Date lastUpdatedDt;

    /* 测试忽略验证 */
    private String name;

    private Integer age;

    /*BigDecimal 测试*/
    private BigDecimal price;

    /* 测试下划线字段命名类型, 字段填充 */
    @TableField(value = "test_type", strategy = FieldStrategy.IGNORED)
    private Integer testType;

    private String desc;

    @TableField
	private Date testDate;

    @Version
    private Integer version;


    public H2UserIntVersionAR() {

    }

    public H2UserIntVersionAR(String name) {
        this.name = name;
    }

    public H2UserIntVersionAR(Integer testType) {
        this.testType = testType;
    }

    public H2UserIntVersionAR(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public H2UserIntVersionAR(Long id, String name) {
        this.setId(id);
        this.name = name;
    }

    public H2UserIntVersionAR(Long id, Integer age) {
        this.setId(id);
        this.age = age;
    }

    public H2UserIntVersionAR(Long id, String name, Integer age, Integer testType) {
        this.setId(id);
        this.name = name;
        this.age = age;
        this.testType = testType;
    }

    public H2UserIntVersionAR(String name, Integer age, Integer testType) {
        this.name = name;
        this.age = age;
        this.testType = testType;
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
