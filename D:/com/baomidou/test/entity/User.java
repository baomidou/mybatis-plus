package com.baomidou.test.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author hubin
 * @since 2018-03-20
 */
@TableName("mp_user")
public class User extends Model<User> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;
    /**
     * 用户名
     */
    private String name;
    /**
     * 创建时间
     */
    private Date ctime;
    /**
     * 密码
     */
    @TableField("abc_password")
    private String abcPassword;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public String getAbcPassword() {
        return abcPassword;
    }

    public void setAbcPassword(String abcPassword) {
        this.abcPassword = abcPassword;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "User{" +
        ", id=" + id +
        ", name=" + name +
        ", ctime=" + ctime +
        ", abcPassword=" + abcPassword +
        "}";
    }
}
