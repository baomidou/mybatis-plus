package com.baomidou.mybatisplus.test.association;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

/**
 * 父实体类。
 * @author yangbo
 * @since 2023-01-07
 */
@Data
public class Entity {

    private Long id;

    private String name;

    /**
     * 需要手工编写 mapper 来获取
     */
    @TableField(exist = false)
    private List<SubEntity> subEntityList;
}
