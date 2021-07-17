package com.baomidou.mybatisplus.test.version;

import com.baomidou.mybatisplus.annotation.Version;
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
    private static final long serialVersionUID = 6962439201546719734L;

    private Long id;

    private String name;

    @Version
    private Integer version;
}
