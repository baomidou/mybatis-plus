package com.baomidou.mybatisplus.test.h2.keygenerator.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName(value = "key_generator_model")
@KeySequence(value = "key_generator_model_seq", dbType = DbType.POSTGRE_SQL)
public class LongKeyGeneratorModel {

    @TableId(type = IdType.INPUT)
    private Long id;

    private String name;

}
