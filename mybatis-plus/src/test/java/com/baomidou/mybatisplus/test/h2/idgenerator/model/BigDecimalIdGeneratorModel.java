package com.baomidou.mybatisplus.test.h2.idgenerator.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@TableName(value = "t_id_generator_bigdecimal")
public class BigDecimalIdGeneratorModel {

    private BigDecimal id;

    private String name;

    public BigDecimalIdGeneratorModel(String name) {
        this.name = name;
    }
}
