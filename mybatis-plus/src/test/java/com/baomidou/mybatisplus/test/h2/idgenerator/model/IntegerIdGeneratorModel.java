package com.baomidou.mybatisplus.test.h2.idgenerator.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName(value = "t_id_generator_int")
public class IntegerIdGeneratorModel {

    private Integer id;

    private String name;

    public IntegerIdGeneratorModel(String name) {
        this.name = name;
    }
}
