package com.baomidou.mybatisplus.test.base.entity.pg;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author miemie
 * @since 2018-08-06
 */
@Data
@Accessors(chain = true)
public class PgData {

    private Long id;
    @TableField("\"pgInt\"")
    private Integer pgInt;
    private Integer pgInt2;
    @TableField("\"group\"")
    private Integer group;
}
