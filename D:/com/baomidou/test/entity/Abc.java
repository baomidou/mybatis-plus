package com.baomidou.test.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import java.io.Serializable;

/**
 * <p>
 * VIEW
 * </p>
 *
 * @author hubin
 * @since 2018-03-20
 */
public class Abc extends Model<Abc> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;
    /**
     * aa
     */
    private Date type;
    @TableField("abc_aa")
    private String abcAa;
    @TableField("A_as")
    private String as;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getType() {
        return type;
    }

    public void setType(Date type) {
        this.type = type;
    }

    public String getAbcAa() {
        return abcAa;
    }

    public void setAbcAa(String abcAa) {
        this.abcAa = abcAa;
    }

    public String getAs() {
        return as;
    }

    public void setAs(String as) {
        this.as = as;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Abc{" +
        ", id=" + id +
        ", type=" + type +
        ", abcAa=" + abcAa +
        ", as=" + as +
        "}";
    }
}
