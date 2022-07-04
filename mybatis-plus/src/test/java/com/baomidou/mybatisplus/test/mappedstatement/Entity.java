package com.baomidou.mybatisplus.test.mappedstatement;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author 16K
 * @since 2022/07/03
 */

@Data
@EqualsAndHashCode
public class Entity implements Serializable {

    private static final long serialVersionUID = 0L;

    private String id;

    private String name;

}
