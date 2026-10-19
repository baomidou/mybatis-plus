package com.baomidou.mybatisplus.test.uuid;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.UUID;

@Data
@TableName("entity")
public class UUIDLogicEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private UUID id;

    private String name;

    @TableField(fill = FieldFill.UPDATE)
    private String deleteBy;

    @TableField(fill = FieldFill.UPDATE)
    @TableLogic(delval = "true", value = "false")
    private Boolean deleted;

}
