package com.baomidou.mybatisplus.test.optimisticlocker;

import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author miemie
 * @since 2020-06-24
 */
@Data
@Accessors(chain = true)
public class Entity {

    private Long id;

    private String name;

    @Version
    private Integer version;
}
