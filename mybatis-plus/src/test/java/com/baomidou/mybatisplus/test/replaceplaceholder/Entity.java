package com.baomidou.mybatisplus.test.replaceplaceholder;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author miemie
 * @since 2020-06-23
 */
@Data
public class Entity implements Serializable {
    private static final long serialVersionUID = 6962439201546719734L;

    private Long id;

    @TableField("`name`")
    private String name;

    private Integer age;

    @TableField(exist = false)
    private EntitySub es;

    @Data
    public static class EntitySub {
        private Long id;
        private String name;
    }
}
