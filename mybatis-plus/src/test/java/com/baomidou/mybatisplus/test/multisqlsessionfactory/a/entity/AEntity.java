package com.baomidou.mybatisplus.test.multisqlsessionfactory.a.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author nieqiurong
 */
@Data
@TableName("t_entity_a")
public class AEntity {

    private Long id;

    private String name;

    public AEntity() {
    }

    public AEntity(String name) {
        this.name = name;
    }

}
