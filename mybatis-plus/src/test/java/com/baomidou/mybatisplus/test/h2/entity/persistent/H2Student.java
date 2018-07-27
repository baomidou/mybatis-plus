package com.baomidou.mybatisplus.test.h2.entity.persistent;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 学生实体
 * @author nieqiurong 2018/7/27.
 */
@Data
@TableName("h2student")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class H2Student extends Model<H2Student> {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer age;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
