package com.baomidou.mybatisplus.test.autoresultmap;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.GsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author miemie
 * @since 2020-06-23
 */
@Data
@Accessors(chain = true)
@TableName(autoResultMap = true)
public class Entity implements Serializable {
    private static final long serialVersionUID = 6962439201546719734L;

    private Long id;

    @TableField("x_name")
    private String name;

    @TableField(typeHandler = GsonTypeHandler.class)
    private Gg gg;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Gg {
        private String name;
    }
}
