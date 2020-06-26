package com.baomidou.mybatisplus.test.enums;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author miemie
 * @since 2020-06-23
 */
@Data
@Accessors(chain = true)
public class Entity implements Serializable {
    private static final long serialVersionUID = 5094767605376915138L;

    private Long id;

    private EnumStr enumStr;

    private EnumInt enumInt;
}
