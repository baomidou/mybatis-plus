package com.baomidou.mybatisplus.test.h2.cache.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_cache")
public class CacheModel implements Serializable {

    private Long id;

    private String name;

    public CacheModel(String name) {
        this.name = name;
    }
}
