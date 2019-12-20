package com.baomidou.mp.h2.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author K神带你飞
 * @since 2019-09-25
 */
@TableName("h2user")
public class H2user implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * PK
     */
      @TableId(value = "test_id", type = IdType.AUTO)
    private Long testId;

    /**
     * USERNAME
     */
    @TableField("name")
    private String name;

    @TableField("age")
    private Integer age;

    @TableField("test_type")
    private Integer testType;

    @TableField("test_date")
    private LocalDateTime testDate;

    @TableField("price")
    private BigDecimal price;

    @TableField("desc")
    private String desc;

    @TableField("version")
    private Integer version;

    @TableField("last_updated_dt")
    private LocalDateTime lastUpdatedDt;

    @TableField("deleted")
    private Integer deleted;


    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

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

    public LocalDateTime getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDateTime testDate) {
        this.testDate = testDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LocalDateTime getLastUpdatedDt() {
        return lastUpdatedDt;
    }

    public void setLastUpdatedDt(LocalDateTime lastUpdatedDt) {
        this.lastUpdatedDt = lastUpdatedDt;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "H2user{" +
        "testId=" + testId +
        ", name=" + name +
        ", age=" + age +
        ", testType=" + testType +
        ", testDate=" + testDate +
        ", price=" + price +
        ", desc=" + desc +
        ", version=" + version +
        ", lastUpdatedDt=" + lastUpdatedDt +
        ", deleted=" + deleted +
        "}";
    }
}
