package com.baomidou.mybatisplus.test.orderby;

import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author nieqiurong
 */
@Data
@TableName(value = "t_order_test")
public class OrderByEntity {

    @OrderBy(sort = 3)
    @TableId(value = "oid")
    private Long id;

    private String name;

    @OrderBy(asc = true, sort = 2)
    @TableField("nl")
    private Long age;

    @OrderBy
    private String tel;

}
