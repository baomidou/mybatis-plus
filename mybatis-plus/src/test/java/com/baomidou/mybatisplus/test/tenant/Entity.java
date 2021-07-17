package com.baomidou.mybatisplus.test.tenant;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author miemie
 * @since 2020-06-24
 */
@Data
@Accessors(chain = true)
public class Entity implements Serializable {
    private static final long serialVersionUID = 6389385437936113455L;

    private Long id;

    private String name;

    private Integer tenantId;
}
