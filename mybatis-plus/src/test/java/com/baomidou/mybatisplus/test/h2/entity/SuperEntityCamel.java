package com.baomidou.mybatisplus.test.h2.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 测试父类情况
 *
 * @author hubin
 * @since 2016-06-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SuperEntityCamel extends SuSuperEntityCamel implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -531147777357149891L;

    /* 主键ID 注解，value 字段名，type 用户输入ID */
    @TableId(value = "testId")
    private Long id;

}
