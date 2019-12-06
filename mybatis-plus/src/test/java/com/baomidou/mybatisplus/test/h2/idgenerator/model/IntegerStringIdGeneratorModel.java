package com.baomidou.mybatisplus.test.h2.idgenerator.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName(value = "t_id_generator_int_string")
public class IntegerStringIdGeneratorModel {

    @TableId(type = IdType.ID_WORKER_STR)
    private String id;

    private String name;

    public IntegerStringIdGeneratorModel(String name) {
        this.name = name;
    }
}
