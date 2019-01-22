package com.baomidou.mybatisplus.test.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @author miemie
 * @since 2019-01-19
 */
@Data
@Accessors(chain = true)
@TableName(resultMap = "resultChildren1")
public class ResultMapEntity {

    private Long id;

    private String column1;
    private String column2;
    private String column3;
    private String column4;
    @TableField(el = "list, typeHandler=com.baomidou.mybatisplus.test.base.type.ListTypeHandler")
    private List<String> list;
    @TableField(el = "map, typeHandler=com.baomidou.mybatisplus.test.base.type.MapTypeHandler")
    private Map<String, Object> map;
}
