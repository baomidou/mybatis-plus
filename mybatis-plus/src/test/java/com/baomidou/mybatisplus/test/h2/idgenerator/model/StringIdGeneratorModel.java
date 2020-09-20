package com.baomidou.mybatisplus.test.h2.idgenerator.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName(value = "t_id_generator_string")
public class StringIdGeneratorModel {

    @TableId(type = IdType.UUID)
    private String id;

    private String name;

    public StringIdGeneratorModel(String name) {
        this.name = name;
    }
}
