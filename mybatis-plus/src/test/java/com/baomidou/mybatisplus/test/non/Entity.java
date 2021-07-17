package com.baomidou.mybatisplus.test.non;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author miemie
 * @since 2020-06-23
 */
@Data
public class Entity implements Serializable {
    private static final long serialVersionUID = 6962439201546719734L;

    @TableId("t_id")
    private Long id;

    @TableField("t_name")
    private String name;
}
