package com.baomidou.mybatisplus.test.h2.keygenerator.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "key_generator_model")
@KeySequence(value = "key_generator_model_seq")
public class StringKeyGeneratorModel {

    @TableId(type = IdType.INPUT)
    private String id;

    private String name;

}
