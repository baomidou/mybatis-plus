package com.baomidou.mybatisplus.test.h2.idgenerator.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName(value = "t_id_generator_long")
public class LongIdGeneratorModel {

    private Long id;

    private String name;

    public LongIdGeneratorModel(String name) {
        this.name = name;
    }
}
