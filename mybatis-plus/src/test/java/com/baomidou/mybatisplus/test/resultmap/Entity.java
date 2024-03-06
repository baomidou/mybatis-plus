package com.baomidou.mybatisplus.test.resultmap;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.GsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author miemie
 * @since 2020-06-23
 */
@Data
@Accessors(chain = true)
@TableName(resultMap = "resultMap")
public class Entity implements Serializable {
    private static final long serialVersionUID = 6962439201546719734L;

    private Long id;

    @TableField(typeHandler = GsonTypeHandler.class)
    private Gg gg1;

    @TableField(typeHandler = GsonTypeHandler.class)
    private Gg gg2;

    @TableField(typeHandler = GsonTypeHandler.class)
    private List<Gg> gg3;

    @TableField(typeHandler = GsonTypeHandler.class)
    private List<Gg4> gg4;

    @TableField(typeHandler = GsonTypeHandler.class)
    private String[] str;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Gg {
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Gg4 {
        private String name;
        private Gg gg;
        private List<Gg> ggList;
        private Map<String,Gg> ggMap;
    }
}
