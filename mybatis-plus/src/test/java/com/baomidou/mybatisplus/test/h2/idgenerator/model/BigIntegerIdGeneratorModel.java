package com.baomidou.mybatisplus.test.h2.idgenerator.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@TableName(value = "t_id_generator_biginteger")
public class BigIntegerIdGeneratorModel {

    private BigInteger id;

    private String name;

    public BigIntegerIdGeneratorModel(String name) {
        this.name = name;
    }
}
