package com.baomidou.mybatisplys.extension.test.plugins.optimisticLocker.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;

@TableName("version_user")
public class LogicVersionUser implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    @Version
    private Short version;

    @TableLogic(value = "0", delval = "1")
    private Integer isDelete;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Short getVersion() {
        return version;
    }

    public void setVersion(Short version) {
        this.version = version;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

}
