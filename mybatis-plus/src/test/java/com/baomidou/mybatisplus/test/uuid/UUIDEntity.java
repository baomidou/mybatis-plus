package com.baomidou.mybatisplus.test.uuid;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.UUID;

@Data
@TableName("entity")
public class UUIDEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private UUID id;

    private String name;
}
