package com.baomidou.mybatisplus.test.association;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 子实体类
 * @author yangbo
 * @since 2023-01-07
 */
@Data
@TableName(value = "sub_entity", autoResultMap = true)
public class SubEntity {

    private Long id;
    private String name;

    @TableField(value = "parent_id", propertyIn = "parent.id")
    private Entity parent;
}
