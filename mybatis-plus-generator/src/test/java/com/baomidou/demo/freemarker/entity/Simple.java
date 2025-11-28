package com.baomidou.demo.freemarker.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 测试表
 * </p>
 *
 * @author baomidou
 * @since 2025-03-27
 */
@TableName("T_SIMPLE")
public class Simple implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 删除标识1
     */
    private Byte deleteFlag;

    /**
     * 删除标识2
     */
    private Byte deleted;

    /**
     * 测试布尔类型
     */
    private Byte isOk;

    /**
     * 版本
     */
    private Long version;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Byte getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Byte deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Byte getDeleted() {
        return deleted;
    }

    public void setDeleted(Byte deleted) {
        this.deleted = deleted;
    }

    public Byte getIsOk() {
        return isOk;
    }

    public void setIsOk(Byte isOk) {
        this.isOk = isOk;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Simple{" +
            "id = " + id +
            ", name = " + name +
            ", age = " + age +
            ", deleteFlag = " + deleteFlag +
            ", deleted = " + deleted +
            ", isOk = " + isOk +
            ", version = " + version +
            ", createTime = " + createTime +
            ", updateTime = " + updateTime +
            "}";
    }
}
