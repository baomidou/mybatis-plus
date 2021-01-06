package com.baomidou.mybatisplus.test.batch;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author miemie
 * @since 2020-06-23
 */
@Data
@NoArgsConstructor
public class Entity implements Serializable {
    private static final long serialVersionUID = 6962439201546719734L;

    private Long id;

    private String name;

    public Entity(String name) {
        this.name = name;
    }
}
