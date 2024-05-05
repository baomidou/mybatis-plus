package com.baomidou.mybatisplus.test.h2.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.test.h2.enums.GenderEnum;
import com.baomidou.mybatisplus.test.h2.enums.GradeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 学生实体
 *
 * @author nieqiurong 2018/7/27.
 */
@Data
@Accessors(chain = true)
@TableName("h2student")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class H2Student extends Model<H2Student> {


    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1290051894415073936L;

    public H2Student(Long id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    @OrderBy
    private GradeEnum grade;

    private GenderEnum gender;

    private Integer age;

}
