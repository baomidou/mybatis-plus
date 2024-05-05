package com.baomidou.mybatisplus.test.h2.keygenerator.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName(value = "key_generator_model")
@EqualsAndHashCode(callSuper = true)
public class ExtendKeyGeneratorModel extends BaseMode {

    @TableId(type = IdType.INPUT, value = "id")
    private Long uid;

    private String name;
}
