package com.baomidou.mybatisplus.test.multisqlsessionfactory.b.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author nieqiurong
 */
@Data
@TableName("t_entity_b")
public class BEntity {

    private Long id;

    private String name;

    public BEntity() {
    }

    public BEntity(String name) {
        this.name = name;
    }

}
