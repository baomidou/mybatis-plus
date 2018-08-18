package com.baomidou.mybatisplus.test.base.entity.mysql;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author miemie
 * @since 2018-08-18
 */
@Data
@Accessors(chain = true)
public class MysqlData {

    private Long id;
    @TableField("`order`")
    private Integer order;
    @TableField("`group`")
    private Integer group;
}
